package cl.tacticaldev.visitas.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Pantalla de bienvenida: aparece "TacticalDev" con animación y, debajo, la versión.
// Cuando termina la animación llama a onTerminar() para pasar al historial.
@Composable
fun SplashScreen(onTerminar: () -> Unit) {
    val context = LocalContext.current

    // La versión se lee de build.gradle.kts (versionName), así no hay que cambiarla aquí.
    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0"
    }

    val alfaTitulo = remember { Animatable(0f) }
    val escalaTitulo = remember { Animatable(0.7f) }
    val alfaVersion = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 1) El título aparece creciendo suavemente.
        coroutineScope {
            launch { alfaTitulo.animateTo(1f, tween(800)) }
            launch { escalaTitulo.animateTo(1f, tween(800)) }
        }
        // 2) Luego aparece la versión, subiendo desde abajo.
        alfaVersion.animateTo(1f, tween(500))
        // 3) Pausa corta y pasa a la pantalla siguiente.
        delay(800)
        onTerminar()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D47A1)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TacticalDev",
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.graphicsLayer {
                    alpha = alfaTitulo.value
                    scaleX = escalaTitulo.value
                    scaleY = escalaTitulo.value
                }
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Versión $version",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = alfaVersion.value
                    translationY = (1f - alfaVersion.value) * 30f
                }
            )
        }
    }
}