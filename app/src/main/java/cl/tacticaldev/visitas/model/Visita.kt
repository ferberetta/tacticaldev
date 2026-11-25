
package cl.tacticaldev.visitas.model

// Modelo de visita técnica recibido y enviado al microservicio de visitas.
data class Visita(
    val id: Int = 0,
    val equipoId: Int,

    // Fecha de registro.
    val fecha: String,

    // Información de la visita.
    val observaciones: String,
    val tecnico: String,

    // Usuario de la aplicación que registró la visita.
    val usuarioRegistro: String? = null,
    val usuarioRegistroId: String = "",

    // Estado de sincronización.
    val sincronizada: Boolean = false,

    // Información adicional.
    val rutaFoto: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)
