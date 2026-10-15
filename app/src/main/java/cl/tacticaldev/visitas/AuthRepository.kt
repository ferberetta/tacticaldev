package cl.tacticaldev.visitas

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

data class Usuario(val rut: String, val cargo: String)

class AuthRepository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("usuarios", Context.MODE_PRIVATE)

    // Guarda "cargo|sal|hash" bajo la clave "user_<rut>".
    fun registrar(rut: String, cargo: String, password: String): Boolean {
        val clave = "user_$rut"
        if (prefs.contains(clave)) return false   // ese RUT ya tiene cuenta
        val sal = nuevaSal()
        prefs.edit().putString(clave, "$cargo|$sal|${hash(password, sal)}").apply()
        return true
    }

    fun iniciarSesion(rut: String, password: String): Usuario? {
        val partes = prefs.getString("user_$rut", null)?.split("|") ?: return null
        if (partes.size != 3) return null
        val (cargo, sal, hashGuardado) = partes
        val iguales = MessageDigest.isEqual(
            hash(password, sal).toByteArray(), hashGuardado.toByteArray()
        )
        return if (iguales) Usuario(rut, cargo) else null
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