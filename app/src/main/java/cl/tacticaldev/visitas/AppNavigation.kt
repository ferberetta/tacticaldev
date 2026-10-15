package cl.tacticaldev.visitas

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.tacticaldev.visitas.model.Visita
import cl.tacticaldev.visitas.model.VisitaDao
import cl.tacticaldev.visitas.ui.EquiposScreen
import cl.tacticaldev.visitas.ui.HomeScreen
import cl.tacticaldev.visitas.ui.SplashScreen
import cl.tacticaldev.visitas.ui.historial.DetalleVisitaScreen
import cl.tacticaldev.visitas.ui.historial.HistorialScreen
import cl.tacticaldev.visitas.viewmodel.EquipoViewModel
import cl.tacticaldev.visitas.viewmodel.HistorialViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Rutas {
    const val SPLASH = "splash"           // pantalla de bienvenida
    const val LOGIN = "login"            // pantalla inicio sesion Jonathan
    const val HOME = "home"              // pantalla principal con accesos y resumen
    const val HISTORIAL = "historial"
    const val DETALLE = "detalle/{visitaId}"
    const val NUEVA_VISITA = "nueva_visita" // formulario de Benjamín
    const val EQUIPOS = "equipos"           // pantalla de Diego

    fun detalle(id: Int) = "detalle/$id"
}

@Composable
fun AppNavigation(
    equipoViewModel: EquipoViewModel,
    historialViewModel: HistorialViewModel,
    visitaDao: VisitaDao
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val todas by historialViewModel.todas.collectAsState()
    val equipos by equipoViewModel.equipos.collectAsState()

    var usuarioLogueado by remember { mutableStateOf<Usuario?>(null) }

    NavHost(navController = navController, startDestination = Rutas.LOGIN) {

        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = { usuario ->
                    usuarioLogueado = usuario
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.HOME) {
            val pendientesCount = todas.count { !it.visita.sincronizada }
            val equiposFallaCount = equipos.count { it.estado.equals("Con falla", ignoreCase = true) }

            HomeScreen(
                usuario = usuarioLogueado,
                visitasPendientesCount = pendientesCount,
                equiposConFallaCount = equiposFallaCount,
                onRegistrarVisita = { navController.navigate(Rutas.NUEVA_VISITA) },
                onVerEquipos = { navController.navigate(Rutas.EQUIPOS) },
                onVerHistorial = { navController.navigate(Rutas.HISTORIAL) },
                onCerrarSesion = {
                    usuarioLogueado = null
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.SPLASH) {
            SplashScreen(
                onTerminar = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.NUEVA_VISITA) {
            // Foto tomada con la cámara en este formulario (null = sin foto).
            var fotoUri by remember { mutableStateOf<Uri?>(null) }
            // Ubicación obtenida con el botón de geolocalización (null = sin ubicación).
            var latitud by remember { mutableStateOf<Double?>(null) }
            var longitud by remember { mutableStateOf<Double?>(null) }

            NuevaVisitaScreen(
                equipos = equipos,
                onFotoCapturada = { fotoUri = it },
                onFotoEliminada = { fotoUri = null },
                onUbicacionObtenida = { lat, lon -> latitud = lat; longitud = lon },
                onUbicacionEliminada = { latitud = null; longitud = null },
                onGuardar = { equipo, estado, procedencia, telefono, cargo, rut, observaciones ->
                    val equipoId = equipos.firstOrNull { it.nombre == equipo }?.id
                    val rutaFoto = fotoUri?.toString()
                    val lat = latitud
                    val lon = longitud
                    scope.launch {
                        if (equipoId != null) {
                            val fecha = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                                .format(Date())
                            visitaDao.insertar(
                                Visita(
                                    equipoId = equipoId,
                                    fecha = fecha,
                                    observaciones = observaciones,
                                    tecnico = "$cargo $rut",
                                    rutaFoto = rutaFoto,
                                    latitud = lat,
                                    longitud = lon
                                )
                            )
                        }
                        // Deja ver la tarjeta verde y luego vuelve a la pantalla anterior,
                        // donde la visita nueva ya aparece en la lista.
                        delay(1500)
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(Rutas.EQUIPOS) {
            EquiposScreen(
                viewModel = equipoViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.HISTORIAL) {
            HistorialScreen(
                viewModel = historialViewModel,
                onVolver = { navController.popBackStack() },
                onNuevaVisita = { navController.navigate(Rutas.NUEVA_VISITA) },
                onVerEquipos = { navController.navigate(Rutas.EQUIPOS) },
                onVisitaClick = { item ->
                    navController.navigate(Rutas.detalle(item.visita.id))
                }
            )
        }

        composable(Rutas.DETALLE) { entrada ->
            val id = entrada.arguments?.getString("visitaId")?.toIntOrNull()
            val item = todas.firstOrNull { it.visita.id == id }
            if (item != null) {
                DetalleVisitaScreen(
                    item = item,
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}
