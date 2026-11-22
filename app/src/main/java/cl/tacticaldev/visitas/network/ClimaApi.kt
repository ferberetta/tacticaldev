package cl.tacticaldev.visitas.network

import cl.tacticaldev.visitas.model.clima.ClimaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaApi {
    @GET("v1/forecast")
    suspend fun obtenerClima(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current_weather") currentWeather: Boolean = true
    ): ClimaResponse
}
