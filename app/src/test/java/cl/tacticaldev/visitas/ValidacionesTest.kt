package cl.tacticaldev.visitas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {
    @Test
    fun `rutValido con rut valido devuelve true`() {
        // Ejemplo rut válido 12345678-5 -> sin guion 123456785
        assertTrue(rutValido("123456785"))
    }

    @Test
    fun `rutValido con digito K devuelve true`() {
        assertTrue(rutValido("12345670K"))
    }

    @Test
    fun `rutValido con digito incorrecto devuelve false`() {
        assertFalse(rutValido("123456786")) // digito deberia ser 5
    }

    @Test
    fun `rutValido con rut muy corto devuelve false`() {
        assertFalse(rutValido("12345"))
    }

    @Test
    fun `rutValido con letras en el cuerpo devuelve false`() {
        assertFalse(rutValido("123A56785"))
    }

    @Test
    fun `formatearRut aplica puntos y guion correctamente`() {
        assertEquals("12.345.678-5", formatearRut("123456785"))
        assertEquals("12.345.670-K", formatearRut("12345670K"))
    }
}
