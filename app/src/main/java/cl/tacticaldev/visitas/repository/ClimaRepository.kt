package cl.tacticaldev.visitas.repository

import cl.tacticaldev.visitas.network.ClimaApi

class ClimaRepository(private val api: ClimaApi) {
    suspend fun obtenerTemperatura(latitud: Double, longitud: Double): Double? {
        return api.obtenerClima(latitud, longitud).currentWeather?.temperature
    }
}
