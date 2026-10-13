package cl.tacticaldev.visitas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.tacticaldev.visitas.ui.EquiposScreen
import cl.tacticaldev.visitas.ui.historial.DetalleVisitaScreen
import cl.tacticaldev.visitas.ui.historial.HistorialScreen
import cl.tacticaldev.visitas.viewmodel.EquipoViewModel
import cl.tacticaldev.visitas.viewmodel.HistorialViewModel

object Rutas {
    const val HISTORIAL = "historial"
    const val DETALLE = "detalle/{visitaId}"
    const val NUEVA_VISITA = "nueva_visita" // la implementa Benjamín
    const val EQUIPOS = "equipos"           // pantalla de Diego

    fun detalle(id: Int) = "detalle/$id"
}

@Composable
fun AppNavigation(
    equipoViewModel: EquipoViewModel,
    historialViewModel: HistorialViewModel
) {
    val navController = rememberNavController()
    val todas by historialViewModel.todas.collectAsState()

    NavHost(navController = navController, startDestination = Rutas.HISTORIAL) {
        composable(Rutas.HISTORIAL) {
            HistorialScreen(
                viewModel = historialViewModel,
                onVolver = { navController.popBackStack() },
                onNuevaVisita = { /* Benjamín: navController.navigate(Rutas.NUEVA_VISITA) */ },
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
        composable(Rutas.EQUIPOS) {
            EquiposScreen(viewModel = equipoViewModel)
        }
    }
}