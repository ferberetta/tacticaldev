
package cl.tacticaldev.visitas

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.tacticaldev.visitas.network.ApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuariosScreen(
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    val prefs = remember(context) {
        context.getSharedPreferences(
            "usuarios",
            Context.MODE_PRIVATE
        )
    }

    val authRepository = remember(prefs) {
        AuthRepository(prefs, ApiClient.usuariosApi)
    }

    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf("Técnico") }

    var menuAbierto by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Gestión de usuarios")
                },
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Registrar nuevo usuario",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Desde aquí puedes crear cuentas para los técnicos y vendedores.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    mensaje = null
                },
                label = {
                    Text("Nombre completo")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rut,
                onValueChange = { valor ->
                    rut = valor
                        .filter {
                            it.isDigit() || it == 'k' || it == 'K'
                        }
                        .uppercase()
                        .take(9)

                    mensaje = null
                },
                label = {
                    Text("RUT")
                },
                placeholder = {
                    Text("Ej. 123456785")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Text(
                    text = "Perfil del usuario",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = cargo,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Cargo")
                    },
                    trailingIcon = {
                        TextButton(
                            onClick = {
                                menuAbierto = true
                            }
                        ) {
                            Text("Cambiar")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = {
                        menuAbierto = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Técnico")
                        },
                        onClick = {
                            cargo = "Técnico"
                            menuAbierto = false
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Vendedor")
                        },
                        onClick = {
                            cargo = "Vendedor"
                            menuAbierto = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    mensaje = null
                },
                label = {
                    Text("Contraseña")
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            mensaje?.let {
                Text(
                    text = it,
                    color = if (error)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    val rutLimpio = rut.trim()
                    val partesNombre = nombre.trim()
                        .split(Regex("\\s+"), limit = 2)

                    when {
                        nombre.isBlank() -> {
                            error = true
                            mensaje = "Ingresa el nombre del usuario"
                        }

                        partesNombre.size < 2 ||
                                partesNombre[1].isBlank() -> {
                            error = true
                            mensaje = "Ingresa nombre y apellido"
                        }

                        rutLimpio.isBlank() -> {
                            error = true
                            mensaje = "Ingresa el RUT"
                        }

                        !rutValido(rutLimpio) -> {
                            error = true
                            mensaje = "El RUT ingresado no es válido"
                        }

                        password.length < 8 -> {
                            error = true
                            mensaje = "La contraseña debe tener al menos 8 caracteres"
                        }

                        else -> {
                            scope.launch {
                                cargando = true

                                try {
                                    authRepository.registrar(
                                        nombre = partesNombre[0],
                                        apellido = partesNombre[1],
                                        rut = rutLimpio,
                                        password = password,
                                        cargo = cargo
                                    )

                                    error = false
                                    mensaje = "Usuario registrado correctamente en el servidor"

                                    nombre = ""
                                    rut = ""
                                    password = ""
                                    cargo = "Técnico"

                                } catch (e: HttpException) {
                                    error = true

                                    val detalle = e.response()
                                        ?.errorBody()
                                        ?.string()

                                    mensaje = "Error HTTP ${e.code()}: ${
                                        detalle ?: "Sin detalles del servidor"
                                    }"

                                } catch (e: IOException) {
                                    error = true
                                    mensaje = "No se pudo conectar con el servidor"

                                } catch (e: Exception) {
                                    error = true
                                    mensaje = e.message
                                        ?: "Error al registrar usuario"

                                } finally {
                                    cargando = false
                                }
                            }
                        }
                    }
                },
                enabled = !cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Text(
                    if (cargando) "Registrando..."
                    else "Registrar usuario"
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "Importante: las cuentas creadas aquí podrán utilizarse desde la pantalla de inicio de sesión.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
