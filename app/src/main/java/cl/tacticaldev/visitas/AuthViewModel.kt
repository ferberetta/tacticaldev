
package cl.tacticaldev.visitas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthState(
    val rut: String = "",
    val password: String = "",
    val intento: Boolean = false,
    val mensaje: String? = null,
    val usuario: Usuario? = null
) {

    val errorRut: String?
        get() = when {
            !intento -> null
            rut.isBlank() -> "Ingresa tu RUT"
            !rutValido(rut) -> "RUT no válido"
            else -> null
        }

    val errorPassword: String?
        get() = if (intento && password.isBlank()) {
            "Ingresa tu contraseña"
        } else {
            null
        }

    val esValido: Boolean
        get() = rutValido(rut) && password.isNotBlank()
}

class AuthViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun cambiarRut(valor: String) {
        val limpio = valor
            .filter { it.isDigit() || it == 'k' || it == 'K' }
            .uppercase()
            .take(9)

        _state.update {
            it.copy(rut = limpio, mensaje = null)
        }
    }

    fun cambiarPassword(valor: String) {
        _state.update {
            it.copy(password = valor, mensaje = null)
        }
    }

    fun enviar() {

        _state.update {
            it.copy(intento = true, mensaje = null)
        }

        val s = _state.value

        if (!s.esValido) return

        val usuario = repo.iniciarSesion(s.rut, s.password)

        if (usuario != null) {
            _state.update {
                it.copy(usuario = usuario)
            }
        } else {
            _state.update {
                it.copy(mensaje = "RUT o contraseña incorrectos")
            }
        }
    }
}

class AuthViewModelFactory(
    private val repo: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repo) as T
    }
}
