package cl.tacticaldev.visitas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AuthState(
    val modoRegistro: Boolean = false,
    val rut: String = "",
    val nombre: String = "",
    val cargo: String = "",
    val password: String = "",
    val confirmar: String = "",
    val intento: Boolean = false,
    val mensaje: String? = null,     // error general (credenciales incorrectas, RUT repetido)
    val usuario: Usuario? = null     // se llena cuando el ingreso fue exitoso
) {
    val errorRut: String?
        get() = when {
            !intento -> null
            rut.isBlank() -> "Ingresa tu RUT"
            !rutValido(rut) -> "RUT no válido"
            else -> null
        }

    val errorNombre: String?
        get() = if (intento && modoRegistro && nombre.isBlank()) "Ingresa tu nombre" else null

    val errorCargo: String?
        get() = if (intento && modoRegistro && cargo.isBlank()) "Selecciona tu cargo" else null

    val errorPassword: String?
        get() = when {
            !intento -> null
            password.isBlank() -> "Ingresa tu contraseña"
            modoRegistro && password.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }

    val errorConfirmar: String?
        get() = if (intento && modoRegistro && confirmar != password)
            "Las contraseñas no coinciden" else null

    val esValido: Boolean
        get() = rutValido(rut) &&
                password.isNotBlank() &&
                (!modoRegistro || (nombre.isNotBlank() && cargo.isNotBlank() && password.length >= 6 && confirmar == password))
}

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AuthRepository(app)
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    // Cambia entre "Iniciar sesión" y "Crear cuenta" y limpia los campos.
    fun cambiarModo() = _state.update { AuthState(modoRegistro = !it.modoRegistro) }

    fun cambiarRut(valor: String) {
        val limpio = valor.filter { it.isDigit() || it == 'k' || it == 'K' }
            .uppercase()
            .take(9)
        _state.update { it.copy(rut = limpio, mensaje = null) }
    }

    fun cambiarNombre(valor: String) = _state.update { it.copy(nombre = valor, mensaje = null) }
    fun cambiarCargo(valor: String) = _state.update { it.copy(cargo = valor) }
    fun cambiarPassword(valor: String) = _state.update { it.copy(password = valor, mensaje = null) }
    fun cambiarConfirmar(valor: String) = _state.update { it.copy(confirmar = valor) }

    fun enviar() {
        _state.update { it.copy(intento = true, mensaje = null) }
        val s = _state.value
        if (!s.esValido) return

        if (s.modoRegistro) {
            if (repo.registrar(s.rut, s.cargo, s.nombre, s.password)) {
                _state.update { it.copy(usuario = Usuario(s.rut, s.cargo, s.nombre)) }
            } else {
                _state.update { it.copy(mensaje = "Ese RUT ya tiene una cuenta") }
            }
        } else {
            val usuario = repo.iniciarSesion(s.rut, s.password)
            if (usuario != null) {
                _state.update { it.copy(usuario = usuario) }
            } else {
                _state.update { it.copy(mensaje = "RUT o contraseña incorrectos") }
            }
        }
    }
}
