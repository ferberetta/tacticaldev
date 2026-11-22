package cl.tacticaldev.visitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import cl.tacticaldev.visitas.network.ApiClient
import cl.tacticaldev.visitas.repository.EquipoRepository
import cl.tacticaldev.visitas.repository.VisitaRepository
import cl.tacticaldev.visitas.repository.ClimaRepository
import cl.tacticaldev.visitas.ui.theme.TacticalDEVTheme
import cl.tacticaldev.visitas.viewmodel.EquipoViewModel
import cl.tacticaldev.visitas.viewmodel.EquipoViewModelFactory
import cl.tacticaldev.visitas.viewmodel.HistorialViewModel
import cl.tacticaldev.visitas.viewmodel.HistorialViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val equipoRepository = EquipoRepository(ApiClient.equiposApi)
        val visitaRepository = VisitaRepository(ApiClient.visitasApi)
        val climaRepository = ClimaRepository(ApiClient.climaApi)

        val equipoViewModel = ViewModelProvider(
            this, EquipoViewModelFactory(equipoRepository)
        )[EquipoViewModel::class.java]

        val historialViewModel = ViewModelProvider(
            this, HistorialViewModelFactory(visitaRepository, equipoRepository, climaRepository)
        )[HistorialViewModel::class.java]

        setContent {
            TacticalDEVTheme {
                AppNavigation(
                    equipoViewModel = equipoViewModel,
                    historialViewModel = historialViewModel
                )
            }
        }
    }
}
