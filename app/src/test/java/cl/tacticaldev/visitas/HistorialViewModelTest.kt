package cl.tacticaldev.visitas

import cl.tacticaldev.visitas.model.Equipo
import cl.tacticaldev.visitas.model.Visita
import cl.tacticaldev.visitas.repository.ClimaRepository
import cl.tacticaldev.visitas.repository.EquipoRepository
import cl.tacticaldev.visitas.repository.VisitaRepository
import cl.tacticaldev.visitas.viewmodel.FiltroHistorial
import cl.tacticaldev.visitas.viewmodel.HistorialViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistorialViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val visitaRepo = mockk<VisitaRepository>()
    private val equipoRepo = mockk<EquipoRepository>()
    private val climaRepo = mockk<ClimaRepository>()

    private val freezer = Equipo(id = 1, nombre = "Freezer A", ubicacion = "Minimarket Maipú", estado = "Operativo")
    private val conservadora = Equipo(id = 2, nombre = "Conservadora B", ubicacion = "Local Centro", estado = "Operativo")

    private val v1 = Visita(id = 1, equipoId = 1, fecha = "01-01-2026 10:00", observaciones = "Revisión", tecnico = "Técnico 1-9", sincronizada = false)
    private val v2 = Visita(id = 2, equipoId = 2, fecha = "02-01-2026 11:00", observaciones = "Limpieza", tecnico = "Técnico 2-7", sincronizada = true)
    private val v3 = Visita(id = 3, equipoId = 1, fecha = "03-01-2026 12:00", observaciones = "Cambio filtro", tecnico = "Técnico 3-5", sincronizada = true)

    // El ViewModel carga datos al crearse, por eso se configuran los mocks antes de construirlo.
    private fun crearViewModel(
        equipos: List<Equipo> = listOf(freezer, conservadora),
        visitas: List<Visita> = listOf(v1, v2, v3)
    ): HistorialViewModel {
        coEvery { equipoRepo.obtenerTodos() } returns equipos
        coEvery { visitaRepo.obtenerTodas() } returns visitas
        return HistorialViewModel(visitaRepo, equipoRepo, climaRepo)
    }

    // visitasFiltradas solo emite cuando alguien la observa.
    private fun TestScope.mantenerSuscrito(vm: HistorialViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.visitasFiltradas.collect { }
        }
    }

    // ---------- Carga ----------

    @Test
    fun `al iniciar carga las visitas ordenadas de la mas nueva a la mas antigua`() {
        val vm = crearViewModel()

        assertEquals(listOf(3, 2, 1), vm.todas.value.map { it.visita.id })
    }

    @Test
    fun `cada visita muestra el nombre y punto de venta de su equipo`() {
        val vm = crearViewModel()

        val ultima = vm.todas.value.first { it.visita.id == 3 }
        assertEquals("Freezer A", ultima.equipoNombre)
        assertEquals("Minimarket Maipú", ultima.puntoVenta)
    }

    @Test
    fun `visita de un equipo inexistente muestra equipo desconocido`() {
        val huerfana = v1.copy(id = 9, equipoId = 99)
        val vm = crearViewModel(visitas = listOf(huerfana))

        assertEquals("Equipo desconocido", vm.todas.value[0].equipoNombre)
        assertEquals("", vm.todas.value[0].puntoVenta)
    }

    @Test
    fun `si no hay equipos en el servidor usa los equipos por defecto`() {
        val vm = crearViewModel(equipos = emptyList(), visitas = listOf(v1))

        assertEquals("Refrigeradores", vm.todas.value[0].equipoNombre)
    }

    @Test
    fun `si el servidor falla no se cae y deja la lista vacia`() {
        coEvery { equipoRepo.obtenerTodos() } throws RuntimeException("sin red")
        coEvery { visitaRepo.obtenerTodas() } throws RuntimeException("sin red")

        val vm = HistorialViewModel(visitaRepo, equipoRepo, climaRepo)

        assertEquals(emptyList<Any>(), vm.todas.value)
    }

    // ---------- Filtros ----------

    @Test
    fun `el filtro inicial es TODAS`() {
        val vm = crearViewModel()

        assertEquals(FiltroHistorial.TODAS, vm.filtro.value)
    }

    @Test
    fun `cambiar filtro actualiza el filtro actual`() {
        val vm = crearViewModel()

        vm.cambiarFiltro(FiltroHistorial.PENDIENTES)

        assertEquals(FiltroHistorial.PENDIENTES, vm.filtro.value)
    }

    @Test
    fun `filtro TODAS muestra todas las visitas`() = runTest {
        val vm = crearViewModel()
        mantenerSuscrito(vm)

        vm.cambiarFiltro(FiltroHistorial.TODAS)

        assertEquals(3, vm.visitasFiltradas.value.size)
    }

    @Test
    fun `filtro COMPLETADAS muestra solo las sincronizadas`() = runTest {
        val vm = crearViewModel()
        mantenerSuscrito(vm)

        vm.cambiarFiltro(FiltroHistorial.COMPLETADAS)

        assertEquals(listOf(3, 2), vm.visitasFiltradas.value.map { it.visita.id })
    }

    @Test
    fun `filtro PENDIENTES muestra solo las no sincronizadas`() = runTest {
        val vm = crearViewModel()
        mantenerSuscrito(vm)

        vm.cambiarFiltro(FiltroHistorial.PENDIENTES)

        assertEquals(listOf(1), vm.visitasFiltradas.value.map { it.visita.id })
    }

    // ---------- Crear ----------

    @Test
    fun `crear visita envia los datos correctos al repositorio`() {
        val vm = crearViewModel()
        val capturada = slot<Visita>()
        coEvery { visitaRepo.crear(capture(capturada)) } answers { capturada.captured }

        vm.crearVisita("Freezer A", "Técnico", "12345678-5", "Todo ok", "foto.jpg", -33.5, -70.7)

        coVerify(exactly = 1) { visitaRepo.crear(any()) }
        assertEquals(1, capturada.captured.equipoId)
        assertEquals("Técnico 12345678-5", capturada.captured.tecnico)
        assertEquals("Todo ok", capturada.captured.observaciones)
        assertEquals(false, capturada.captured.sincronizada)
        assertEquals("foto.jpg", capturada.captured.rutaFoto)
        assertEquals(-33.5, capturada.captured.latitud)
    }

    @Test
    fun `crear visita de un equipo que no existe no llama al repositorio`() {
        val vm = crearViewModel()

        vm.crearVisita("Equipo Fantasma", "Técnico", "1-9", "obs", null, null, null)

        coVerify(exactly = 0) { visitaRepo.crear(any()) }
    }

    @Test
    fun `crear visita sin conexion avisa que se guardo localmente`() {
        val vm = crearViewModel()
        coEvery { visitaRepo.crear(any()) } throws RuntimeException("sin red")

        vm.crearVisita("Freezer A", "Técnico", "1-9", "obs", null, null, null)

        assertEquals("Guardado localmente. Sin conexión al servidor.", vm.error.value)
    }

    // ---------- Completar ----------

    @Test
    fun `marcar completada actualiza la visita como sincronizada`() {
        val vm = crearViewModel()
        coEvery { visitaRepo.actualizar(any()) } answers { firstArg() }

        vm.marcarCompletada(v1)

        coVerify(exactly = 1) { visitaRepo.actualizar(v1.copy(sincronizada = true)) }
        assertNull(vm.error.value)
    }

    @Test
    fun `marcar completada con error de servidor muestra mensaje`() {
        val vm = crearViewModel()
        coEvery { visitaRepo.actualizar(any()) } throws RuntimeException("falla")

        vm.marcarCompletada(v1)

        assertEquals("No se pudo conectar con el servidor", vm.error.value)
    }

    // ---------- Eliminar ----------

    @Test
    fun `eliminar llama al repositorio con el id correcto`() {
        val vm = crearViewModel()
        coEvery { visitaRepo.eliminar(1) } returns Unit

        vm.eliminar(1)

        coVerify(exactly = 1) { visitaRepo.eliminar(1) }
        assertNull(vm.error.value)
    }

    @Test
    fun `eliminar con error de servidor muestra mensaje`() {
        val vm = crearViewModel()
        coEvery { visitaRepo.eliminar(any()) } throws RuntimeException("falla")

        vm.eliminar(1)

        assertEquals("No se pudo conectar con el servidor", vm.error.value)
    }

    @Test
    fun `limpiar error borra el mensaje`() {
        val vm = crearViewModel()
        coEvery { visitaRepo.eliminar(any()) } throws RuntimeException("falla")
        vm.eliminar(1)

        vm.limpiarError()

        assertNull(vm.error.value)
    }

    // ---------- Clima ----------

    @Test
    fun `cargar temperatura guarda el valor que entrega la API`() {
        val vm = crearViewModel()
        coEvery { climaRepo.obtenerTemperatura(-33.5, -70.7) } returns 18.5

        vm.cargarTemperatura(-33.5, -70.7)

        assertEquals(18.5, vm.temperatura.value)
    }

    @Test
    fun `cargar temperatura sin coordenadas deja la temperatura en null`() {
        val vm = crearViewModel()

        vm.cargarTemperatura(null, null)

        assertNull(vm.temperatura.value)
        coVerify(exactly = 0) { climaRepo.obtenerTemperatura(any(), any()) }
    }

    @Test
    fun `cargar temperatura con error de la API deja la temperatura en null`() {
        val vm = crearViewModel()
        coEvery { climaRepo.obtenerTemperatura(any(), any()) } throws RuntimeException("falla")

        vm.cargarTemperatura(-33.5, -70.7)

        assertNull(vm.temperatura.value)
    }
}