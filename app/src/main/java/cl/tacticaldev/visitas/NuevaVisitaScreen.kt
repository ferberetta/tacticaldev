package cl.tacticaldev.visitas

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.tacticaldev.visitas.model.Equipo
import cl.tacticaldev.visitas.ui.components.CameraSection
import cl.tacticaldev.visitas.ui.components.UbicacionSection

import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaVisitaScreen(
    equipos: List<Equipo>,
    viewModel: NuevaVisitaViewModel = viewModel(),
    onFotoCapturada: (Uri) -> Unit = {},
    onFotoEliminada: () -> Unit = {},
    onUbicacionObtenida: (Double, Double) -> Unit = { _, _ -> },
    onUbicacionEliminada: () -> Unit = {},
    onGuardar: (equipo: String, estado: String, procedencia: String, telefono: String, cargo: String, rut: String, observaciones: String) -> Unit =
        { _, _, _, _, _, _, _ -> }
) {
    val estados = listOf("Operativo", "Con falla", "Sin conexión")
    val cargos = listOf("Técnico", "Vendedor")
    val form by viewModel.state.collectAsState()

    var menuEquipo by remember { mutableStateOf(false) }
    var menuEstado by remember { mutableStateOf(false) }
    var menuCargo by remember { mutableStateOf(false) }
    var guardada by remember { mutableStateOf(false) }


    LaunchedEffect(guardada) {
        if (guardada) {
            delay(2500)
            guardada = false
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Nueva visita",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )


            ExposedDropdownMenuBox(expanded = menuEquipo, onExpandedChange = { menuEquipo = it }) {
                OutlinedTextField(
                    value = form.equipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Equipo") },
                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuEquipo) },
                    isError = form.errorEquipo != null,
                    supportingText = { form.errorEquipo?.let { Text(it) } },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuEquipo,
                    onDismissRequest = { menuEquipo = false }) {
                    equipos.forEach { equipo ->
                        DropdownMenuItem(
                            text = { Text(equipo.nombre) },
                            onClick = { viewModel.cambiarEquipo(equipo.nombre); menuEquipo = false }
                        )
                    }
                }
            }



            ExposedDropdownMenuBox(expanded = menuEstado, onExpandedChange = { menuEstado = it }) {
                OutlinedTextField(
                    value = form.estado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado del equipo") },
                    leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuEstado) },
                    isError = form.errorEstado != null,
                    supportingText = { form.errorEstado?.let { Text(it) } },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuEstado,
                    onDismissRequest = { menuEstado = false }) {
                    estados.forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado) },
                            onClick = { viewModel.cambiarEstado(estado); menuEstado = false }
                        )
                    }
                }
            }


            OutlinedTextField(
                value = form.procedencia,
                onValueChange = { viewModel.cambiarProcedencia(it) },
                label = { Text("Procedencia del lugar") },
                placeholder = { Text("Ej. Almacén Don Pepe") },
                leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                isError = form.errorProcedencia != null,
                supportingText = { form.errorProcedencia?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = form.telefono,
                onValueChange = { viewModel.cambiarTelefono(it) },
                label = { Text("Teléfono de contacto") },
                placeholder = { Text("Ej. 912345678") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                isError = form.errorTelefono != null,
                supportingText = { form.errorTelefono?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            Text(
                text = "Responsable de la visita",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ExposedDropdownMenuBox(expanded = menuCargo, onExpandedChange = { menuCargo = it }) {
                OutlinedTextField(
                    value = form.cargo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cargo (Vendedor o Técnico)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuCargo) },
                    isError = form.errorCargo != null,
                    supportingText = { form.errorCargo?.let { Text(it) } },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuCargo,
                    onDismissRequest = { menuCargo = false }) {
                    cargos.forEach { cargo ->
                        DropdownMenuItem(
                            text = { Text(cargo) },
                            onClick = { viewModel.cambiarCargo(cargo); menuCargo = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = formatearRut(form.rut),
                onValueChange = { viewModel.cambiarRut(it) },
                label = { Text("RUT") },
                placeholder = { Text("Ej. 12.345.678-5") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                isError = form.errorRut != null,
                supportingText = { form.errorRut?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = form.observaciones,
                onValueChange = { viewModel.cambiarObservaciones(it) },
                label = { Text("Observaciones") },
                placeholder = { Text("Ej. Temperatura estable...") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                isError = form.errorObservaciones != null,
                supportingText = { form.errorObservaciones?.let { Text(it) } },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            // Cámara (trabajo de Eduardo): botón para tomar la foto, vista previa y eliminar.
            CameraSection(
                onPhotoCaptured = onFotoCapturada,
                onPhotoRemoved = onFotoEliminada
            )

            // Geolocalización: botón para obtener la latitud y longitud del teléfono.
            UbicacionSection(
                onUbicacionObtenida = onUbicacionObtenida,
                onUbicacionEliminada = onUbicacionEliminada
            )




            AnimatedVisibility(
                visible = guardada,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.size(8.dp))
                        Text("Visita guardada", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }




            Button(
                onClick = {
                    if (viewModel.guardar()) {
                        guardada = true
                        onGuardar(
                            form.equipo,
                            form.estado,
                            form.procedencia.trim(),
                            form.telefono,
                            form.cargo,
                            formatearRut(form.rut),
                            form.observaciones.trim()
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text("Guardar visita")
            }
        }
    }
}