package cl.tacticaldev.visitas.data

data class Visita(
    val id: Int,
    val equipo: String,
    val puntoVenta: String,
    val tecnico: String,
    val fechaHora: String,
    val observaciones: String,
    val sincronizada: Boolean,
    val latitud: Double,
    val longitud: Double
)