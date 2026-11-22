package cl.tacticaldev.visitas

import cl.tacticaldev.visitas.model.Equipo
import cl.tacticaldev.visitas.repository.EquipoRepository
import cl.tacticaldev.visitas.viewmodel.EquipoViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class EquipoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo = mockk<EquipoRepository>()

    private val freezer = Equipo(id = 1, nombre = "Freezer A", ubicacion = "Minimarket Maipú", estado = "Operativo")
    private val conservadora = Equipo(id = 2, nombre = "Conservadora B", ubicacion = "Local Centro", estado = "Con falla")
    private val vitrina = Equipo(id = 3, nombre = "Vitrina C", ubicacion = "Local Norte", estado = "Sin conexión")

    private fun crearViewModel(lista: List<Equipo> = listOf(freezer, conservadora, vitrina)): EquipoViewModel {
        coEvery { repo.obtenerTodos() } returns lista
        return EquipoViewModel(repo)
    }

    @Test
    fun `al iniciar carga los equipos desde el servidor`() {
        val viewModel = crearViewModel()

        assertEquals(listOf(freezer, conservadora, vitrina), viewModel.equipos.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `si el servidor devuelve lista vacia muestra los equipos por defecto`() {
        val viewModel = crearViewModel(emptyList())

        assertEquals(2, viewModel.equipos.value.size)
        assertEquals("Refrigeradores", viewModel.equipos.value[0].nombre)
        assertEquals("Conservadoras", viewModel.equipos.value[1].nombre)
    }

    @Test
    fun `si falla la carga muestra error y equipos por defecto`() {
        coEvery { repo.obtenerTodos() } throws RuntimeException("sin red")

        val viewModel = EquipoViewModel(repo)

        assertNotNull(viewModel.error.value)
        assertEquals(2, viewModel.equipos.value.size)
    }

    @Test
    fun `buscar por nombre filtra la lista`() {
        val viewModel = crearViewModel()

        viewModel.cambiarBusqueda("freezer")

        assertEquals(listOf(freezer), viewModel.equipos.value)
        assertEquals("freezer", viewModel.textoBusqueda.value)
    }

    @Test
    fun `buscar por ubicacion filtra la lista`() {
        val viewModel = crearViewModel()

        viewModel.cambiarBusqueda("centro")

        assertEquals(listOf(conservadora), viewModel.equipos.value)
    }

    @Test
    fun `buscar un texto que no existe deja la lista vacia`() {
        val viewModel = crearViewModel()

        viewModel.cambiarBusqueda("zzz")

        assertEquals(emptyList<Equipo>(), viewModel.equipos.value)
    }

    @Test
    fun `filtrar por estado muestra solo esos equipos`() {
        val viewModel = crearViewModel()

        viewModel.cambiarFiltro("Con falla")

        assertEquals(listOf(conservadora), viewModel.equipos.value)
        assertEquals("Con falla", viewModel.filtroActual.value)
    }

    @Test
    fun `filtro Todos vuelve a mostrar todos los equipos`() {
        val viewModel = crearViewModel()

        viewModel.cambiarFiltro("Operativo")
        viewModel.cambiarFiltro("Todos")

        assertEquals(3, viewModel.equipos.value.size)
    }

    @Test
    fun `busqueda y filtro de estado se combinan`() {
        val viewModel = crearViewModel()

        viewModel.cambiarBusqueda("local")
        viewModel.cambiarFiltro("Sin conexión")

        assertEquals(listOf(vitrina), viewModel.equipos.value)
    }

    @Test
    fun `crear equipo llama al repositorio y recarga la lista`() {
        val nuevo = Equipo(id = 4, nombre = "Freezer D", ubicacion = "Local Sur", estado = "Operativo")
        coEvery { repo.obtenerTodos() } returnsMany listOf(listOf(freezer), listOf(freezer, nuevo))
        coEvery { repo.crear(any()) } returns nuevo

        val viewModel = EquipoViewModel(repo)
        viewModel.crearEquipo("Freezer D", "Local Sur", "Operativo")

        coVerify(exactly = 1) { repo.crear(any()) }
        assertEquals(listOf(freezer, nuevo), viewModel.equipos.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `crear equipo con error de servidor muestra mensaje`() {
        val viewModel = crearViewModel()
        coEvery { repo.crear(any()) } throws RuntimeException("falla")

        viewModel.crearEquipo("Freezer D", "Local Sur", "Operativo")

        assertEquals("No se pudo conectar con el servidor", viewModel.error.value)
    }

    @Test
    fun `actualizar equipo llama al repositorio y recarga la lista`() {
        val modificado = freezer.copy(estado = "Con falla")
        coEvery { repo.obtenerTodos() } returnsMany listOf(listOf(freezer), listOf(modificado))
        coEvery { repo.actualizar(modificado) } returns modificado

        val viewModel = EquipoViewModel(repo)
        viewModel.actualizarEquipo(modificado)

        coVerify(exactly = 1) { repo.actualizar(modificado) }
        assertEquals(listOf(modificado), viewModel.equipos.value)
    }

    @Test
    fun `actualizar equipo con error de servidor muestra mensaje`() {
        val viewModel = crearViewModel()
        coEvery { repo.actualizar(any()) } throws RuntimeException("falla")

        viewModel.actualizarEquipo(freezer)

        assertEquals("No se pudo conectar con el servidor", viewModel.error.value)
    }

    @Test
    fun `eliminar equipo llama al repositorio y recarga la lista`() {
        coEvery { repo.obtenerTodos() } returnsMany listOf(listOf(freezer, conservadora), listOf(conservadora))
        coEvery { repo.eliminar(1) } returns Unit

        val viewModel = EquipoViewModel(repo)
        viewModel.eliminarEquipo(1)

        coVerify(exactly = 1) { repo.eliminar(1) }
        assertEquals(listOf(conservadora), viewModel.equipos.value)
    }

    @Test
    fun `eliminar equipo con error de servidor muestra mensaje`() {
        val viewModel = crearViewModel()
        coEvery { repo.eliminar(any()) } throws RuntimeException("falla")

        viewModel.eliminarEquipo(1)

        assertEquals("No se pudo conectar con el servidor", viewModel.error.value)
    }

    @Test
    fun `limpiar error borra el mensaje`() {
        val viewModel = crearViewModel()
        coEvery { repo.eliminar(any()) } throws RuntimeException("falla")
        viewModel.eliminarEquipo(1)

        viewModel.limpiarError()

        assertNull(viewModel.error.value)
    }
}