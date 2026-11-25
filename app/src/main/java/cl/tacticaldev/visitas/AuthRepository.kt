package cl.tacticaldev.visitas

import android.content.SharedPreferences
import android.util.Base64
import cl.tacticaldev.visitas.dto.LoginRequest
import cl.tacticaldev.visitas.dto.UsuarioRequest
import cl.tacticaldev.visitas.network.UsuariosApi
import java.security.MessageDigest
import java.security.SecureRandom

data class Usuario(
    val rut: String,
    val cargo: String,
    val nombre: String = ""
)

class AuthRepository(
    private val prefs: SharedPreferences,
    private val api: UsuariosApi
) {
    companion object {
        const val ADMIN_RUT = "111111111"
        const val ADMIN_PASSWORD = "admin"
        const val ADMIN_NOMBRE = "Administrador principal"
        const val ADMIN_CARGO = "Administrador"
    }

    init {
        crearAdministradorInicial()
    }

    private fun crearAdministradorInicial() {
        val clave = "user_$ADMIN_RUT"

        if (!prefs.contains(clave)) {
            val sal = nuevaSal()
            val valor = "$ADMIN_CARGO|$ADMIN_NOMBRE|$sal|${hash(ADMIN_PASSWORD, sal)}"

            prefs.edit().putString(clave, valor).apply()
        }
    }

    private fun iniciarSesionAdministrador(
        rut: String,
        password: String
    ): Usuario? {
        if (rut != ADMIN_RUT) return null

        val valor = prefs.getString("user_$rut", null) ?: return null
        val partes = valor.split("|")

        if (partes.size != 4) return null

        val iguales = MessageDigest.isEqual(
            hash(password, partes[2]).toByteArray(),
            partes[3].toByteArray()
        )

        return if (iguales) Usuario(rut, ADMIN_CARGO, partes[1]) else null
    }

    suspend fun iniciarSesion(rut: String, password: String): Usuario? {
        iniciarSesionAdministrador(rut, password)?.let {
            return it
        }

        val respuesta = api.iniciarSesion(LoginRequest(rut, password))

        if (respuesta.activo != true) return null

        val cargo = when (respuesta.rol.uppercase()) {
            "ADMINISTRADOR" -> "Administrador"
            "TECNICO" -> "Técnico"
            "VENDEDOR" -> "Vendedor"
            else -> respuesta.rol
        }

        return Usuario(
            rut = respuesta.rut,
            cargo = cargo,
            nombre = "${respuesta.nombre} ${respuesta.apellido}".trim()
        )
    }

    suspend fun registrar(
        nombre: String,
        apellido: String,
        rut: String,
        password: String,
        cargo: String
    ) = api.registrar(
        UsuarioRequest(
            nombre = nombre,
            apellido = apellido,
            rut = rut,
            password = password,
            rol = when (cargo) {
                "Técnico" -> "TECNICO"
                "Vendedor" -> "VENDEDOR"
                else -> throw IllegalArgumentException("Perfil no válido")
            }
        )
    )

    private fun nuevaSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun hash(password: String, sal: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest((sal + password).toByteArray())

        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}