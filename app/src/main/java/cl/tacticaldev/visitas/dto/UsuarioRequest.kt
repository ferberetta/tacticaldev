package cl.tacticaldev.visitas.dto

data class UsuarioRequest(
    val nombre: String,
    val apellido: String,
    val rut: String,
    val password: String,
    val rol: String
)

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val rut: String,
    val rol: String,
    val activo: Boolean,
    val fechaCreacion: String?
)
