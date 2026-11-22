package cl.tacticaldev.visitas.model.clima

import com.google.gson.annotations.SerializedName

data class ClimaResponse(
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather?
)

data class CurrentWeather(
    val temperature: Double
)
