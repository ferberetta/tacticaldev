package cl.tacticaldev.visitas.ui.historial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.tacticaldev.visitas.model.Visita
import cl.tacticaldev.visitas.viewmodel.FiltroHistorial
import cl.tacticaldev.visitas.viewmodel.HistorialViewModel
import cl.tacticaldev.visitas.viewmodel.VisitaUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    viewModel: HistorialViewModel,
    onVolver: (() -> Unit)? = null,          // null = es la pantalla de inicio, sin flecha
    onNuevaVisita: () -> Unit = {},
    onVerEquipos: (() -> Unit)? = null,      // null = no se muestra el acceso a equipos
    onVisitaClick: (VisitaUi) -> Unit = {}
) {
    val filtro by viewModel.filtro.collectAsState()
    val lista by viewModel.visitasFiltradas.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial de visitas") },
                navigationIcon = {
                    if (onVolver != null) {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    if (onVerEquipos != null) {
                        IconButton(onClick = onVerEquipos) {
                            Icon(Icons.Filled.Settings, contentDescription = "Ver equipos")
                        }
                    }
                    // Botón + azul, arriba a la derecha, con animación al apretarlo
                    BotonAgregar(onClick = onNuevaVisita)
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroHistorial.values().forEach { opcion ->
                    FilterChip(
                        selected = filtro == opcion,
                        onClick = { viewModel.cambiarFiltro(opcion) },
                        label = {
                            Text(
                                when (opcion) {
                                    FiltroHistorial.TODAS -> "Todas"
                                    FiltroHistorial.COMPLETADAS -> "Completadas"
                                    FiltroHistorial.PENDIENTES -> "Pendientes"
                                }
                            )
                        }
                    )
                }
            }

            if (lista.isEmpty()) {
                var visibleVacio by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { visibleVacio = true }
                val alfaVacio by animateFloatAsState(
                    targetValue = if (visibleVacio) 1f else 0f,
                    animationSpec = tween(400),
                    label = "alfaVacio"
                )
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no hay visitas", modifier = Modifier.alpha(alfaVacio))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lista, key = { it.visita.id }) { item ->
                        TarjetaVisita(
                            item = item,
                            onClick = { onVisitaClick(item) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

// Botón + redondo y azul. Al apretarlo: se achica un poco al presionar y luego
// "rebota" mientras el + gira 90° (unos 250 ms); recién entonces abre el formulario.
@Composable
fun BotonAgregar(onClick: () -> Unit) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    var animando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val escala by animateFloatAsState(
        targetValue = when {
            animando -> 1.25f
            presionado -> 0.85f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "escalaBoton"
    )
    val giro by animateFloatAsState(
        targetValue = if (animando) 90f else 0f,
        animationSpec = tween(250),
        label = "giroBoton"
    )

    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(40.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
                rotationZ = giro
            }
            .background(Color(0xFF1976D2), CircleShape)
            .clickable(interactionSource = interaccion, indication = null) {
                if (!animando) {
                    animando = true
                    scope.launch {
                        delay(250)
                        onClick()
                        animando = false
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Nueva visita", tint = Color.White)
    }
}

@Composable
fun TarjetaVisita(
    item: VisitaUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 4 }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onClick() },
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFFB0BEC5), RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.visita.fecha, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(item.equipoNombre, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    EtiquetaEstado(sincronizada = item.visita.sincronizada)
                }
            }
        }
    }
}

@Composable
fun EtiquetaEstado(sincronizada: Boolean) {
    val fondo by animateColorAsState(
        targetValue = if (sincronizada) Color(0xFFD7F5E0) else Color(0xFFFFE3CC),
        animationSpec = tween(400),
        label = "fondoEstado"
    )
    val punto by animateColorAsState(
        targetValue = if (sincronizada) Color(0xFF2E9E5B) else Color(0xFFE8721C),
        animationSpec = tween(400),
        label = "puntoEstado"
    )
    val texto = if (sincronizada) "Sincronizada" else "Pendiente"

    Row(
        modifier = Modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).background(punto, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(texto, fontSize = 12.sp, color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun HistorialScreenPreview() {
    val ejemplo = listOf(
        VisitaUi(Visita(1, 1, "12-08-2026 10:24", "Todo bien", "Juan Perez", true), "Vitrina Freezer A-102", "Minimarket Maipú"),
        VisitaUi(Visita(2, 2, "10-08-2026 15:37", "Ruido", "Fernanda", false), "Conservadora B-07", "Lider Cerrillos")
    )
    MaterialTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ejemplo.forEach { TarjetaVisita(it, {}) }
        }
    }
}