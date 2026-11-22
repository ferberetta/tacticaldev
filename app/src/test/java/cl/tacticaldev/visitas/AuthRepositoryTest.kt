package cl.tacticaldev.visitas

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var fakePrefs: SharedPreferences
    private lateinit var fakeEditor: SharedPreferences.Editor
    private lateinit var map: MutableMap<String, String>
    private lateinit var repo: AuthRepository

    @Before
    fun setup() {
        map = mutableMapOf()
        fakePrefs = mockk()
        fakeEditor = mockk()

        every { fakePrefs.edit() } returns fakeEditor
        every { fakePrefs.contains(any()) } answers { map.containsKey(firstArg()) }
        every { fakePrefs.getString(any(), any()) } answers {
            map[firstArg<String>()] ?: secondArg<String?>()
        }
        every { fakeEditor.putString(any(), any()) } answers {
            map[firstArg()] = secondArg<String>()
            fakeEditor
        }
        every { fakeEditor.apply() } returns Unit

        repo = AuthRepository(fakePrefs)
    }

    @Test
    fun `registrar usuario nuevo exitoso`() {
        assertTrue(repo.registrar("123456785", "Técnico", "Juan", "pass123"))
        assertTrue(map.containsKey("user_123456785"))
    }

    @Test
    fun `registrar usuario repetido devuelve false`() {
        repo.registrar("123456785", "Técnico", "Juan", "pass123")
        assertFalse(repo.registrar("123456785", "Vendedor", "Pedro", "otraPass"))
    }

    @Test
    fun `login correcto devuelve usuario`() {
        repo.registrar("12345670K", "Vendedor", "Ana", "secreta")
        val usuario = repo.iniciarSesion("12345670K", "secreta")
        assertNotNull(usuario)
        assertEquals("12345670K", usuario?.rut)
        assertEquals("Vendedor", usuario?.cargo)
        assertEquals("Ana", usuario?.nombre)
    }

    @Test
    fun `login incorrecto devuelve null`() {
        repo.registrar("12345670K", "Vendedor", "Ana", "secreta")
        assertNull(repo.iniciarSesion("12345670K", "mala"))
        assertNull(repo.iniciarSesion("no_existe", "secreta"))
    }
}
