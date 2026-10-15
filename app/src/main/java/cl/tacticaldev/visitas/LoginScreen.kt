package cl.tacticaldev.visitas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginExitoso: (Usuario) -> Unit
) {
    val form by viewModel.state.collectAsState()
    val cargos = listOf("Técnico", "Vendedor")

    var menuCargo by remember { mutableStateOf(false) }
    var verPassword by remember { mutableStateOf(false) }

    // Cuando el ingreso es exitoso, avisa a la navegación.
    LaunchedEffect(form.usuario) {
        form.usuario?.let { onLoginExitoso(it) }
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
                text = if (form.modoRegistro) "Crear cuenta" else "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = form.rut,
                onValueChange = { viewModel.cambiarRut(it) },
                label = { Text("RUT") },
                placeholder = { Text("Ej. 12.345.678-5") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                isError = form.errorRut != null,
                supportingText = { form.errorRut?.let { Text(it) } },
                visualTransformation = RutVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // El cargo solo se elige al crear la cuenta.
            if (form.modoRegistro) {
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
            }

            OutlinedTextField(
                value = form.password,
                onValueChange = { viewModel.cambiarPassword(it) },
                label = { Text(if (form.modoRegistro) "Elige tu contraseña" else "Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { verPassword = !verPassword }) {
                        Icon(
                            if (verPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar u ocultar contraseña"
                        )
                    }
                },
                visualTransformation =
                    if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
                isError = form.errorPassword != null,
                supportingText = { form.errorPassword?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (form.modoRegistro) {
                OutlinedTextField(
                    value = form.confirmar,
                    onValueChange = { viewModel.cambiarConfirmar(it) },
                    label = { Text("Repite la contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = form.errorConfirmar != null,
                    supportingText = { form.errorConfirmar?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            form.mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.enviar() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (form.modoRegistro) "Crear cuenta" else "Ingresar")
            }

            TextButton(
                onClick = { viewModel.cambiarModo() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (form.modoRegistro) "¿Ya tienes cuenta? Inicia sesión"
                    else "¿Primera vez? Crea tu cuenta"
                )
            }
        }
    }
}