package cl.tacticaldev.visitas.ui.historial

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.tacticaldev.visitas.model.Visita
import cl.tacticaldev.visitas.ui.components.abrirEnGoogleMaps
import cl.tacticaldev.visitas.viewmodel.VisitaUi
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleVisitaScreen(item: VisitaUi, onVolver: () -> Unit = {}) {
    val visita = item.visita
    val context = LocalContext.current
    // Carga la foto guardada en la visita (reducida para no gastar memoria).
    val foto = remember(visita.rutaFoto) {
        visita.rutaFoto?.let { ruta ->
            runCatching {
                val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
                context.contentResolver.openInputStream(ruta.toUri())?.use { entrada ->
                    BitmapFactory.decodeStream(entrada, null, opciones)
                }
            }.getOrNull()
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de visita") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(item.equipoNombre, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(item.puntoVenta, fontSize = 16.sp)
            EtiquetaEstado(sincronizada = visita.sincronizada)

            DatoDetalle("Fecha y hora", visita.fecha)
            DatoDetalle("Técnico", visita.tecnico)
            DatoDetalle("Observaciones", visita.observaciones)

            Text("Evidencia fotográfica", fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFB0BEC5), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (foto != null) {
                    Image(
                        bitmap = foto.asImageBitmap(),
                        contentDescription = "Foto de la visita",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Text(if (visita.rutaFoto == null) "Sin foto" else "No se pudo cargar la foto")
                }
            }

            Text("Ubicación", fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Transparent, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                val lat = visita.latitud
                val lon = visita.longitud
                if (lat == null || lon == null) {
                    Text("Ubicación no registrada")
                } else {
                    // Abre el lugar de la visita en la app Google Maps
                    Button(onClick = { abrirEnGoogleMaps(context, lat, lon, item.equipoNombre) }) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Abrir en Google Maps")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun DatoDetalle(titulo: String, valor: String) {
    Column {
        Text(titulo, fontSize = 12.sp, color = Color.Gray)
        Text(valor, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleVisitaPreview() {
    MaterialTheme {
        DetalleVisitaScreen(
            item = VisitaUi(
                Visita(1, 1, "12-08-2026 10:24", "Temperatura estable", "Juan Perez", true, null, -33.4901, -70.7336),
                "Vitrina Freezer A-102",
                "Minimarket Maipú"
            )
        )
    }
}