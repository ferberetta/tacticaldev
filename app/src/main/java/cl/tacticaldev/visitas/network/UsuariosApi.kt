package cl.tacticaldev.visitas.network

import cl.tacticaldev.visitas.dto.LoginRequest
import cl.tacticaldev.visitas.dto.UsuarioRequest
import cl.tacticaldev.visitas.dto.UsuarioResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UsuariosApi {

    @POST("api/usuarios")
    suspend fun registrar(
        @Body request: UsuarioRequest
    ): UsuarioResponse

    @POST("api/usuarios/login")
    suspend fun iniciarSesion(
        @Body request: LoginRequest
    ): UsuarioResponse
}
