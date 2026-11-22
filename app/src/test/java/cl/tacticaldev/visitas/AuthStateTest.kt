package cl.tacticaldev.visitas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthStateTest {

    @Test
    fun `modo login valido solo con rut y password`() {
        val state = AuthState(
            modoRegistro = false,
            rut = "123456785",
            password = "123"
        )
        assertTrue(state.esValido)
        assertNull(state.errorRut)
        assertNull(state.errorPassword)
    }

    @Test
    fun `modo registro invalido si faltan campos`() {
        val state = AuthState(
            modoRegistro = true,
            rut = "123456785",
            password = "password123",
            confirmar = "password123",
            nombre = "", // falta
            cargo = "Técnico",
            intento = true
        )
        assertFalse(state.esValido)
        assertEquals("Ingresa tu nombre", state.errorNombre)
    }

    @Test
    fun `modo registro password corto`() {
        val state = AuthState(
            modoRegistro = true,
            password = "123",
            intento = true
        )
        assertFalse(state.esValido)
        assertEquals("Mínimo 6 caracteres", state.errorPassword)
    }

    @Test
    fun `modo registro confirmacion distinta`() {
        val state = AuthState(
            modoRegistro = true,
            password = "password123",
            confirmar = "password321",
            intento = true
        )
        assertFalse(state.esValido)
        assertEquals("Las contraseñas no coinciden", state.errorConfirmar)
    }
}
