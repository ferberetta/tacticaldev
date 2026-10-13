package cl.tacticaldev.visitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.tacticaldev.visitas.ui.theme.TacticalDEVTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TacticalDEVTheme {
                AppNavigation()
            }
        }
    }
}