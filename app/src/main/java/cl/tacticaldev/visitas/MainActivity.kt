package cl.tacticaldev.visitas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cl.tacticaldev.visitas.ui.theme.TacticalDEVTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TacticalDEVTheme {
                NuevaVisitaScreen(
                    equipos = listOf("Vitrina Freezer A-102", "Conservadora B-07", "Vitrina Freezer C-15")
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NuevaVisitaPreview() {
    TacticalDEVTheme {
        NuevaVisitaScreen(
            equipos = listOf("Vitrina Freezer A-102", "Conservadora B-07")
        )
    }
}