package cl.tacticaldev.visitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import cl.tacticaldev.visitas.model.AppDatabase
import cl.tacticaldev.visitas.repository.EquipoRepository
import cl.tacticaldev.visitas.ui.theme.TacticalDEVTheme
import cl.tacticaldev.visitas.viewmodel.EquipoViewModel
import cl.tacticaldev.visitas.viewmodel.EquipoViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Base de datos -> DAO -> Repositorio -> ViewModel (parte de Diego)
        val baseDeDatos = AppDatabase.obtenerBase(applicationContext)
        val repositorio = EquipoRepository(baseDeDatos.equipoDao(), baseDeDatos.visitaDao())
        val factory = EquipoViewModelFactory(repositorio)
        val equipoViewModel = ViewModelProvider(this, factory)[EquipoViewModel::class.java]

        setContent {
            TacticalDEVTheme {
                AppNavigation(equipoViewModel = equipoViewModel)
            }
        }
    }
}