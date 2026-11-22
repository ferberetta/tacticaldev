package cl.tacticaldev.visitas.model

// Modelo de visita técnica recibido y enviado al microservicio de visitas.
data class Visita(
    val id: Int = 0,
    val equipoId: Int,
    // La fecha va como texto, ej: "12-08-2026 10:24".
    val fecha: String,
    val observaciones: String,
    val tecnico: String,
    val sincronizada: Boolean = false,
    val rutaFoto: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)
