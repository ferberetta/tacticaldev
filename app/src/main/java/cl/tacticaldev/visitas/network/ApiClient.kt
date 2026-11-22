package cl.tacticaldev.visitas.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Cliente Retrofit para conectar la app con los microservicios.
// 10.0.2.2 apunta al localhost del computador desde el emulador de Android.
object ApiClient {

    private const val URL_EQUIPOS = "http://10.0.2.2:8081/"
    private const val URL_VISITAS = "http://10.0.2.2:8082/"
    private const val URL_CLIMA = "https://api.open-meteo.com/"

    val equiposApi: EquiposApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_EQUIPOS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EquiposApi::class.java)
    }

    val visitasApi: VisitasApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_VISITAS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VisitasApi::class.java)
    }
    
    val climaApi: ClimaApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_CLIMA)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClimaApi::class.java)
    }
}
