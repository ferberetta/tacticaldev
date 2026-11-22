package cl.tacticaldev.visitas

import cl.tacticaldev.visitas.model.Equipo
import cl.tacticaldev.visitas.network.EquiposApi
import cl.tacticaldev.visitas.repository.EquipoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class EquipoRepositoryTest {

    private val api = mockk<EquiposApi>()
    private val repo = EquipoRepository(api)

    @Test
    fun `obtenerTodos llama al api y devuelve lista`() = runTest {
        val lista = listOf(Equipo(1, "E1", "U1", "Operativo"))
        coEvery { api.obtenerTodos() } returns lista

        val resultado = repo.obtenerTodos()
        assertEquals(lista, resultado)
        coVerify(exactly = 1) { api.obtenerTodos() }
    }

    @Test
    fun `crear llama al api y devuelve equipo`() = runTest {
        val equipo = Equipo(0, "E1", "U1", "Operativo")
        val creado = equipo.copy(id = 1)
        coEvery { api.crear(equipo) } returns creado

        val resultado = repo.crear(equipo)
        assertEquals(creado, resultado)
    }

    @Test
    fun `actualizar llama al api y devuelve equipo`() = runTest {
        val equipo = Equipo(1, "E1", "U1", "Con falla")
        coEvery { api.actualizar(1, equipo) } returns equipo

        val resultado = repo.actualizar(equipo)
        assertEquals(equipo, resultado)
    }

    @Test
    fun `eliminar llama al api`() = runTest {
        coEvery { api.eliminar(1) } returns Unit
        repo.eliminar(1)
        coVerify { api.eliminar(1) }
    }
}
