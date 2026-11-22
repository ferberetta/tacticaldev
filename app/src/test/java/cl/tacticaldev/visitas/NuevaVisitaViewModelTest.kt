package cl.tacticaldev.visitas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NuevaVisitaViewModelTest {

    private lateinit var viewModel: NuevaVisitaViewModel

    @Before
    fun setup() {
        viewModel = NuevaVisitaViewModel()
    }

    @Test
    fun `formulario vacio no es valido`() {
        assertFalse(viewModel.state.value.esValido)
        assertFalse(viewModel.guardar())
        assertTrue(viewModel.state.value.intentoGuardar)
    }

    @Test
    fun `formulario completo es valido`() {
        viewModel.cambiarEquipo("Equipo 1")
        viewModel.cambiarEstado("Operativo")
        viewModel.cambiarProcedencia("Lugar test")
        viewModel.cambiarCargo("Técnico")
        viewModel.cambiarTelefono("123456789")
        viewModel.cambiarRut("123456785")
        viewModel.cambiarObservaciones("Observaciones largas de prueba")

        assertTrue(viewModel.state.value.esValido)
        assertTrue(viewModel.guardar())
    }

    @Test
    fun `cambiarTelefono limpia no digitos y corta en 9`() {
        viewModel.cambiarTelefono("123abc456def789000")
        assertEquals("123456789", viewModel.state.value.telefono)
    }

    @Test
    fun `cambiarRut permite k y numeros y pasa a mayusculas limitando a 9`() {
        viewModel.cambiarRut("1111111k2")
        assertEquals("1111111K", viewModel.state.value.rut)
    }
}
