
package cl.tacticaldev.visitas

import android.content.Context
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginExitoso: (Usuario) -> Unit
) {

    val context = LocalContext.current

    val prefs = context.getSharedPreferences(
        "usuarios",
        Context.MODE_PRIVATE
    )

    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            AuthRepository(prefs)
        )
    )

    val form by viewModel.state.collectAsState()

    var verPassword by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(form.usuario) {
        form.usuario?.let {
            onLoginExitoso(it)
        }
    }

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "TacticalDEV",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = form.rut,
                onValueChange = {
                    viewModel.cambiarRut(it)
                },
                label = {
                    Text("RUT")
                },
                placeholder = {
                    Text("Ej. 11.111.111-1")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Badge,
                        contentDescription = null
                    )
                },
                isError = form.errorRut != null,
                supportingText = {
                    form.errorRut?.let {
                        Text(it)
                    }
                },
                visualTransformation = RutVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.password,
                onValueChange = {
                    viewModel.cambiarPassword(it)
                },
                label = {
                    Text("Contraseña")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            verPassword = !verPassword
                        }
                    ) {
                        Icon(
                            imageVector = if (verPassword)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = "Mostrar u ocultar contraseña"
                        )
                    }
                },
                visualTransformation = if (verPassword)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

                isError = form.errorPassword != null,

                supportingText = {
                    form.errorPassword?.let {
                        Text(it)
                    }
                },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),

                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            form.mensaje?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    viewModel.enviar()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }
        }
    }
}
