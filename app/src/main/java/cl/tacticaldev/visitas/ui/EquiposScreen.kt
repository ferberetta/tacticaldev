package cl.tacticaldev.visitas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.tacticaldev.visitas.model.Equipo
import cl.tacticaldev.visitas.viewmodel.EquipoViewModel

// Pantalla principal de Equipos (pantalla 02 del diseño).
// Recibe el ViewModel como parámetro: la pantalla NO tiene lógica,
// solo dibuja el estado y avisa al ViewModel cuando el usuario hace algo.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquiposScreen(
    viewModel: EquipoViewModel,
    onVolver: (() -> Unit)? = null
) {

    // collectAsState() "escucha" el StateFlow del ViewModel.
    // Cada vez que el valor cambia, Compose vuelve a dibujar la pantalla.
    // "by" nos permite usar la variable directamente (sin escribir .value).
    val equipos by viewModel.equipos.collectAsState()
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val filtroActual by viewModel.filtroActual.collectAsState()
    
    var showDialog by remember { mutableStateOf(false) }
    var equipoAEditar by remember { mutableStateOf<Equipo?>(null) }

    // Scaffold nos entrega "padding" para que el contenido no quede
    // debajo de la barra de estado del celular.
    Scaffold(
        topBar = {
            if (onVolver != null) {
                TopAppBar(
                    title = { Text("Equipos", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onVolver) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                equipoAEditar = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Equipo")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            if (onVolver == null) {
                // ---------- TÍTULO ----------
                Text(
                    text = "Equipos",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.padding(top = 12.dp))
            }

            // ---------- BARRA DE BÚSQUEDA ----------
            OutlinedTextField(
                value = textoBusqueda,                       // lo que se muestra viene del ViewModel
                onValueChange = { textoNuevo ->
                    viewModel.cambiarBusqueda(textoNuevo)    // avisamos al ViewModel que cambió
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar equipo...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.padding(top = 8.dp))

            // ---------- FILTROS (3 chips en una fila) ----------
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // selected = true si este chip es el filtro activo (se pinta resaltado)
                FilterChip(
                    selected = filtroActual == "Todos",
                    onClick = { viewModel.cambiarFiltro("Todos") },
                    label = { Text("Todos") }
                )

                // OJO: el texto del chip dice "Operativos" pero el estado
                // guardado en la BD es "Operativo" (singular).
                FilterChip(
                    selected = filtroActual == "Operativo",
                    onClick = { viewModel.cambiarFiltro("Operativo") },
                    label = { Text("Operativos") }
                )

                FilterChip(
                    selected = filtroActual == "Con falla",
                    onClick = { viewModel.cambiarFiltro("Con falla") },
                    label = { Text("Con falla") }
                )
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))

            // ---------- LISTA DE EQUIPOS ----------
            // LazyColumn solo dibuja las tarjetas visibles (más eficiente que Column).
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // items recorre la lista y dibuja una tarjeta por cada equipo
                items(equipos) { equipo ->
                    TarjetaEquipo(
                        equipo = equipo,
                        onClick = {
                            equipoAEditar = equipo
                            showDialog = true
                        }
                    )
                }
            }
        }
        
        if (showDialog) {
            EquipoDialog(
                equipoInicial = equipoAEditar,
                onDismiss = { showDialog = false },
                onConfirm = { nombre, ubicacion, estado ->
                    if (equipoAEditar == null) {
                        viewModel.crearEquipo(nombre, ubicacion, estado)
                    } else {
                        viewModel.actualizarEquipo(equipoAEditar!!.copy(nombre = nombre, ubicacion = ubicacion, estado = estado))
                    }
                    showDialog = false
                },
                onDelete = {
                    equipoAEditar?.let {
                        viewModel.eliminarEquipo(it.id)
                    }
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoDialog(
    equipoInicial: Equipo?,
    onDismiss: () -> Unit,
    onConfirm: (nombre: String, ubicacion: String, estado: String) -> Unit,
    onDelete: () -> Unit
) {
    var nombre by remember { mutableStateOf(equipoInicial?.nombre ?: "") }
    var ubicacion by remember { mutableStateOf(equipoInicial?.ubicacion ?: "") }
    var estado by remember { mutableStateOf(equipoInicial?.estado ?: "") }
    var menuEstado by remember { mutableStateOf(false) }

    val estados = listOf("Operativo", "Con falla", "Sin conexión")
    val esValido = nombre.isNotBlank() && ubicacion.isNotBlank() && estado.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (equipoInicial == null) "Nuevo Equipo" else "Editar Equipo")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { ubicacion = it },
                    label = { Text("Ubicación") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = menuEstado,
                    onExpandedChange = { menuEstado = it }
                ) {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuEstado) },
                        modifier = Modifier
                            .menuAnchor(type = androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = menuEstado,
                        onDismissRequest = { menuEstado = false }
                    ) {
                        estados.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    estado = opcion
                                    menuEstado = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nombre, ubicacion, estado) },
                enabled = esValido
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Row {
                if (equipoInicial != null) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        }
    )
}

// Tarjeta de UN equipo. La separé en su propio Composable para que
// el código quede ordenado y se pueda reutilizar.
@Composable
fun TarjetaEquipo(equipo: Equipo, onClick: () -> Unit) {

    // Elegimos el color según el estado (estructura de control: when).
    val colorEstado = when (equipo.estado) {
        "Operativo" -> Color(0xFF2E7D32)   // verde
        "Con falla" -> Color(0xFFE65100)   // naranja/rojo
        else -> Color.Gray                 // "Sin conexión" u otro
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Nombre del equipo (en negrita)
            Text(
                text = equipo.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Ubicación (punto de venta)
            Text(
                text = equipo.ubicacion,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.padding(top = 6.dp))

            // Indicador de estado: un circulito de color + el texto del estado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color = colorEstado, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = equipo.estado,
                    color = colorEstado,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}