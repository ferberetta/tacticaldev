package cl.tacticaldev.visitas

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<AuthRepository>()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        viewModel = AuthViewModel(repo)
    }

    @Test
    fun `enviar registro correcto llama al repositorio`() {
        every { repo.registrar(any(), any(), any(), any()) } returns true

        viewModel.cambiarModo() // a registro
        viewModel.cambiarRut("123456785")
        viewModel.cambiarNombre("Juan")
        viewModel.cambiarCargo("Técnico")
        viewModel.cambiarPassword("pass123")
        viewModel.cambiarConfirmar("pass123")

        viewModel.enviar()

        val state = viewModel.state.value
        assertNotNull(state.usuario)
        assertEquals("123456785", state.usuario?.rut)
        assertNull(state.mensaje)
    }

    @Test
    fun `enviar registro repetido muestra error`() {
        every { repo.registrar(any(), any(), any(), any()) } returns false

        viewModel.cambiarModo() // a registro
        viewModel.cambiarRut("123456785")
        viewModel.cambiarNombre("Juan")
        viewModel.cambiarCargo("Técnico")
        viewModel.cambiarPassword("pass123")
        viewModel.cambiarConfirmar("pass123")

        viewModel.enviar()

        val state = viewModel.state.value
        assertNull(state.usuario)
        assertEquals("Ese RUT ya tiene una cuenta", state.mensaje)
    }

    @Test
    fun `enviar login correcto devuelve usuario`() {
        every { repo.iniciarSesion("123456785", "pass") } returns Usuario("123456785", "Técnico")

        viewModel.cambiarRut("123456785")
        viewModel.cambiarPassword("pass")
        viewModel.enviar()

        val state = viewModel.state.value
        assertNotNull(state.usuario)
        assertNull(state.mensaje)
    }

    @Test
    fun `enviar login incorrecto muestra error`() {
        every { repo.iniciarSesion("123456785", "pass") } returns null

        viewModel.cambiarRut("123456785")
        viewModel.cambiarPassword("pass")
        viewModel.enviar()

        val state = viewModel.state.value
        assertNull(state.usuario)
        assertEquals("RUT o contraseña incorrectos", state.mensaje)
    }
}
