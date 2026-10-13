package cl.tacticaldev.visitas

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.tacticaldev.visitas.data.visitasFalsas
import cl.tacticaldev.visitas.ui.historial.DetalleVisitaScreen
import cl.tacticaldev.visitas.ui.historial.HistorialScreen

object Rutas {
    const val HISTORIAL = "historial"
    const val DETALLE = "detalle/{visitaId}"
    const val NUEVA_VISITA = "nueva_visita" // la implementa Benjamín
    const val EQUIPOS = "equipos"           // la implementa Diego

    fun detalle(id: Int) = "detalle/$id"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.HISTORIAL) {
        composable(Rutas.HISTORIAL) {
            HistorialScreen(
                onVolver = { navController.popBackStack() },
                onNuevaVisita = { /* Benjamín: navController.navigate(Rutas.NUEVA_VISITA) */ },
                onVisitaClick = { visita ->
                    navController.navigate(Rutas.detalle(visita.id))
                }
            )
        }
        composable(Rutas.DETALLE) { entrada ->
            val id = entrada.arguments?.getString("visitaId")?.toIntOrNull()
            val visita = visitasFalsas.firstOrNull { it.id == id }
            if (visita != null) {
                DetalleVisitaScreen(
                    visita = visita,
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}