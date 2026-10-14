package cl.tacticaldev.visitas

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import cl.tacticaldev.visitas.model.AppDatabase
import cl.tacticaldev.visitas.repository.EquipoRepository
import cl.tacticaldev.visitas.ui.components.CameraSection
import cl.tacticaldev.visitas.ui.theme.TacticalDEVTheme
import cl.tacticaldev.visitas.viewmodel.EquipoViewModel
import cl.tacticaldev.visitas.viewmodel.EquipoViewModelFactory
import cl.tacticaldev.visitas.viewmodel.HistorialViewModel
import cl.tacticaldev.visitas.viewmodel.HistorialViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val baseDeDatos = AppDatabase.obtenerBase(applicationContext)


        val repositorio = EquipoRepository(baseDeDatos.equipoDao(), baseDeDatos.visitaDao())
        val equipoViewModel = ViewModelProvider(
            this, EquipoViewModelFactory(repositorio)
        )[EquipoViewModel::class.java]


        val historialViewModel = ViewModelProvider(
            this, HistorialViewModelFactory(baseDeDatos.visitaDao(), baseDeDatos.equipoDao())
        )[HistorialViewModel::class.java]

        setContent {
            TacticalDEVTheme {
                AppNavigation(
                    equipoViewModel = equipoViewModel,
                    historialViewModel = historialViewModel,
                    visitaDao = baseDeDatos.visitaDao()
                )
            }
        }
    }
}