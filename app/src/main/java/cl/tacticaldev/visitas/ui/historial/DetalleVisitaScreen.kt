package cl.tacticaldev.visitas.ui.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.tacticaldev.visitas.data.Visita
import cl.tacticaldev.visitas.data.visitasFalsas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleVisitaScreen(visita: Visita, onVolver: () -> Unit = {}) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de visita") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(visita.equipo, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(visita.puntoVenta, fontSize = 16.sp)
            EtiquetaEstado(sincronizada = visita.sincronizada)

            DatoDetalle("Fecha y hora", visita.fechaHora)
            DatoDetalle("Técnico", visita.tecnico)
            DatoDetalle("Observaciones", visita.observaciones)

            Text("Evidencia fotográfica", fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFB0BEC5), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Text("Foto de la visita") }

            Text("Ubicación", fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFCFD8DC), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Text("Lat: ${visita.latitud}  Lon: ${visita.longitud}") }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun DatoDetalle(titulo: String, valor: String) {
    Column {
        Text(titulo, fontSize = 12.sp, color = Color.Gray)
        Text(valor, fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleVisitaPreview() {
    MaterialTheme { DetalleVisitaScreen(visita = visitasFalsas.first()) }
}