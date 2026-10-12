package cl.tacticaldev.visitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import cl.tacticaldev.visitas.model.AppDatabase
import cl.tacticaldev.visitas.repository.EquipoRepository
import cl.tacticaldev.visitas.ui.EquiposScreen
import cl.tacticaldev.visitas.viewmodel.EquipoViewModel
import cl.tacticaldev.visitas.viewmodel.EquipoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creamos todo A MANO, de abajo hacia arriba (sin Hilt ni Dagger):
        // 1) Base de datos  ->  2) DAO  ->  3) Repositorio  ->  4) ViewModel
        val baseDeDatos = AppDatabase.obtenerBase(applicationContext)
        val repositorio = EquipoRepository(baseDeDatos.equipoDao(), baseDeDatos.visitaDao())
        val factory = EquipoViewModelFactory(repositorio)
        val viewModel = ViewModelProvider(this, factory)[EquipoViewModel::class.java]

        setContent {
            // Uso MaterialTheme directo para que compile seguro.
            // Si quieren usar el tema del proyecto (carpeta ui/theme),
            // cambien MaterialTheme por el nombre de su tema, ej: VisitasTheme.
            MaterialTheme {
                EquiposScreen(viewModel = viewModel)
            }
        }
    }
}