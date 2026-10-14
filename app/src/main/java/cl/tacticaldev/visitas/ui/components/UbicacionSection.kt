package cl.tacticaldev.visitas.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.CancellationSignal
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

// Estados posibles de la ubicación en el formulario.
private sealed class EstadoUbicacion {
    object Inactivo : EstadoUbicacion()
    object Buscando : EstadoUbicacion()
    data class Lista(val latitud: Double, val longitud: Double) : EstadoUbicacion()
    data class Error(val mensaje: String) : EstadoUbicacion()
}

// Botón "Obtener ubicación": pide el permiso, busca la posición del teléfono (GPS o red)
// y avisa con onUbicacionObtenida(latitud, longitud). No necesita librerías extra.
@Composable
fun UbicacionSection(
    onUbicacionObtenida: (Double, Double) -> Unit = { _, _ -> },
    onUbicacionEliminada: () -> Unit = {}
) {
    val context = LocalContext.current
    var estado by remember { mutableStateOf<EstadoUbicacion>(EstadoUbicacion.Inactivo) }

    fun buscar() {
        estado = EstadoUbicacion.Buscando
        pedirUbicacion(
            context = context,
            onResultado = { lat, lon ->
                estado = EstadoUbicacion.Lista(lat, lon)
                onUbicacionObtenida(lat, lon)
            },
            onError = { mensaje -> estado = EstadoUbicacion.Error(mensaje) }
        )
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) buscar()
        else estado = EstadoUbicacion.Error("Permiso de ubicación denegado")
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedButton(
            onClick = {
                val tienePermiso =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED
                if (tienePermiso) buscar()
                else permisoLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            enabled = estado !is EstadoUbicacion.Buscando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estado is EstadoUbicacion.Buscando) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.size(8.dp))
                Text("Buscando ubicación...")
            } else {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text(if (estado is EstadoUbicacion.Lista) "Actualizar ubicación" else "Obtener ubicación")
            }
        }

        when (val e = estado) {
            is EstadoUbicacion.Lista -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Ubicación obtenida",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Abre la ubicación en la app Google Maps
                    OutlinedButton(
                        onClick = { abrirEnGoogleMaps(context, e.latitud, e.longitud) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Ver en Google Maps")
                    }
                    TextButton(onClick = {
                        estado = EstadoUbicacion.Inactivo
                        onUbicacionEliminada()
                    }) { Text("Quitar") }
                }
            }
            is EstadoUbicacion.Error -> Text(
                text = e.mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            else -> Unit
        }
    }
}

// Abre la ubicación en la app Google Maps (con un marcador).
// Si Google Maps no está instalada, la abre en el navegador. Se usa también en el detalle de la visita.
fun abrirEnGoogleMaps(
    context: Context,
    latitud: Double,
    longitud: Double,
    etiqueta: String = "Ubicación de la visita"
) {
    val geo = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud(${Uri.encode(etiqueta)})")
    val enApp = Intent(Intent.ACTION_VIEW, geo).setPackage("com.google.android.apps.maps")
    try {
        context.startActivity(enApp)
    } catch (e: ActivityNotFoundException) {
        val web = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitud,$longitud")
        )
        try {
            context.startActivity(web)
        } catch (e2: ActivityNotFoundException) {
            Toast.makeText(context, "No hay ninguna app para abrir el mapa", Toast.LENGTH_SHORT).show()
        }
    }
}

// Pide la posición actual. Prueba primero el GPS y, si no responde, la red.
// (Antes de llamarla ya hay que tener el permiso concedido.)
@SuppressLint("MissingPermission")
private fun pedirUbicacion(
    context: Context,
    onResultado: (Double, Double) -> Unit,
    onError: (String) -> Unit
) {
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val proveedores = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        .filter { runCatching { manager.isProviderEnabled(it) }.getOrDefault(false) }

    if (proveedores.isEmpty()) {
        onError("Activa la ubicación (GPS) del teléfono")
        return
    }

    fun intentar(indice: Int) {
        if (indice >= proveedores.size) {
            onError("No se pudo obtener la ubicación. Inténtalo de nuevo en un lugar abierto")
            return
        }
        try {
            LocationManagerCompat.getCurrentLocation(
                manager,
                proveedores[indice],
                CancellationSignal(),                   // la API ya trae un tiempo límite propio
                ContextCompat.getMainExecutor(context)
            ) { ubicacion ->
                if (ubicacion != null) onResultado(ubicacion.latitude, ubicacion.longitude)
                else intentar(indice + 1)
            }
        } catch (e: SecurityException) {
            onError("Permiso de ubicación denegado")
        }
    }
    intentar(0)
}