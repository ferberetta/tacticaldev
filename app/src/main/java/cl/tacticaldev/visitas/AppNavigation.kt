package cl.tacticaldev.visitas

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.tacticaldev.visitas.model.Visita
import cl.tacticaldev.visitas.model.VisitaDao
import cl.tacticaldev.visitas.ui.EquiposScreen
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

    NavHost(navController = navController, startDestination = Rutas.SPLASH) {
        composable(Rutas.SPLASH) {
            SplashScreen(
                onTerminar = {
                    // Pasa al historial y saca el splash de la pila (el botón atrás no vuelve a él).
                    navController.navigate(Rutas.HISTORIAL) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.NUEVA_VISITA) {
            // Foto tomada con la cámara en este formulario (null = sin foto).
            var fotoUri by remember { mutableStateOf<Uri?>(null) }

            NuevaVisitaScreen(
                equipos = equipos,
                onFotoCapturada = { fotoUri = it },
                onFotoEliminada = { fotoUri = null },
                onGuardar = { equipo, estado, procedencia, telefono, cargo, rut, observaciones ->
                    val equipoId = equipos.firstOrNull { it.nombre == equipo }?.id
                    val rutaFoto = fotoUri?.toString()
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
                                    rutaFoto = rutaFoto
                                )
                            )
                        }
                        // Deja ver la tarjeta verde y luego vuelve al historial,
                        // donde la visita nueva ya aparece en la lista.
                        delay(1500)
                        navController.popBackStack()
                    }
                }
            )
        }
        composable(Rutas.EQUIPOS) {
            EquiposScreen(viewModel = equipoViewModel)
        }
        composable(Rutas.HISTORIAL) {
            HistorialScreen(
                viewModel = historialViewModel,
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