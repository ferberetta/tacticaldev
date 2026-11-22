package cl.tacticaldev.visitas.network

import cl.tacticaldev.visitas.model.Visita
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VisitasApi {

    @GET("api/visitas")
    suspend fun obtenerTodos(): List<Visita>

    @POST("api/visitas")
    suspend fun crear(@Body visita: Visita): Visita

    @PUT("api/visitas/{id}")
    suspend fun actualizar(@Path("id") id: Int, @Body visita: Visita): Visita

    @DELETE("api/visitas/{id}")
    suspend fun eliminar(@Path("id") id: Int)
}
