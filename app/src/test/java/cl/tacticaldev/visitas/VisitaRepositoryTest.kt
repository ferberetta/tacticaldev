package cl.tacticaldev.visitas

import cl.tacticaldev.visitas.model.Visita
import cl.tacticaldev.visitas.network.VisitasApi
import cl.tacticaldev.visitas.repository.VisitaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class VisitaRepositoryTest {

    private val api = mockk<VisitasApi>()
    private val repo = VisitaRepository(api)

    @Test
    fun `obtenerTodas llama al api`() = runTest {
        val lista = listOf(Visita(1, 1, "fecha", "obs", "tec"))
        coEvery { api.obtenerTodos() } returns lista
        assertEquals(lista, repo.obtenerTodas())
    }

    @Test
    fun `crear llama al api`() = runTest {
        val visita = Visita(0, 1, "fecha", "obs", "tec")
        val creada = visita.copy(id = 1)
        coEvery { api.crear(visita) } returns creada
        assertEquals(creada, repo.crear(visita))
    }

    @Test
    fun `actualizar llama al api`() = runTest {
        val visita = Visita(1, 1, "fecha", "obs", "tec")
        coEvery { api.actualizar(1, visita) } returns visita
        assertEquals(visita, repo.actualizar(visita))
    }

    @Test
    fun `eliminar llama al api`() = runTest {
        coEvery { api.eliminar(1) } returns Unit
        repo.eliminar(1)
        coVerify { api.eliminar(1) }
    }
}
