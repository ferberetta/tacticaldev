package cl.tacticaldev.visitas.network

import cl.tacticaldev.visitas.model.Equipo
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EquiposApi {

    @GET("api/equipos")
    suspend fun obtenerTodos(): List<Equipo>

    @POST("api/equipos")
    suspend fun crear(@Body equipo: Equipo): Equipo

    @PUT("api/equipos/{id}")
    suspend fun actualizar(@Path("id") id: Int, @Body equipo: Equipo): Equipo

    @DELETE("api/equipos/{id}")
    suspend fun eliminar(@Path("id") id: Int)
}
