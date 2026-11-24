package cl.tacticaldev.visitas.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val URL_EQUIPOS = "https://ms-equipos-jqhr.onrender.com/"
    private const val URL_VISITAS = "https://ms-visitas.onrender.com/"
    private const val URL_CLIMA = "https://api.open-meteo.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val equiposApi: EquiposApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_EQUIPOS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EquiposApi::class.java)
    }

    val visitasApi: VisitasApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_VISITAS)
            .client(okHttpClient)
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