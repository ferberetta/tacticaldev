package cl.tacticaldev.visitas

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

data class Usuario(
    val rut: String,
    val cargo: String,
    val nombre: String = ""
)

class AuthRepository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("usuarios", Context.MODE_PRIVATE)

    // Guarda "cargo|nombre|sal|hash" bajo la clave "user_<rut>".
    fun registrar(rut: String, cargo: String, nombre: String, password: String): Boolean {
        val clave = "user_$rut"
        if (prefs.contains(clave)) return false   // ese RUT ya tiene cuenta
        val sal = nuevaSal()
        prefs.edit().putString(clave, "$cargo|$nombre|$sal|${hash(password, sal)}").apply()
        return true
    }

    fun iniciarSesion(rut: String, password: String): Usuario? {
        val valor = prefs.getString("user_$rut", null) ?: return null
        val partes = valor.split("|")

        val cargo: String
        val nombre: String
        val sal: String
        val hashGuardado: String

        if (partes.size == 4) {
            cargo = partes[0]
            nombre = partes[1]
            sal = partes[2]
            hashGuardado = partes[3]
        } else if (partes.size == 3) {
            // Compatibilidad con cuentas registradas anteriormente sin campo nombre
            cargo = partes[0]
            nombre = ""
            sal = partes[1]
            hashGuardado = partes[2]
        } else {
            return null
        }

        val iguales = MessageDigest.isEqual(
            hash(password, sal).toByteArray(), hashGuardado.toByteArray()
        )
        return if (iguales) Usuario(rut, cargo, nombre) else null
    }

    private fun nuevaSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun hash(password: String, sal: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest((sal + password).toByteArray())
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
