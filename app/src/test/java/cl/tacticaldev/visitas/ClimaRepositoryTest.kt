package cl.tacticaldev.visitas

import cl.tacticaldev.visitas.model.clima.ClimaResponse
import cl.tacticaldev.visitas.model.clima.CurrentWeather
import cl.tacticaldev.visitas.network.ClimaApi
import cl.tacticaldev.visitas.repository.ClimaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ClimaRepositoryTest {

    private val api = mockk<ClimaApi>()
    private val repo = ClimaRepository(api)

    @Test
    fun `obtenerTemperatura devuelve valor correcto`() = runTest {
        val response = ClimaResponse(CurrentWeather(25.5))
        coEvery { api.obtenerClima(10.0, 20.0) } returns response

        val resultado = repo.obtenerTemperatura(10.0, 20.0)
        assertEquals(25.5, resultado)
    }

    @Test
    fun `obtenerTemperatura devuelve null si no hay datos`() = runTest {
        val response = ClimaResponse(null)
        coEvery { api.obtenerClima(10.0, 20.0) } returns response

        val resultado = repo.obtenerTemperatura(10.0, 20.0)
        assertNull(resultado)
    }
}
