package cl.tacticaldev.visitas.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla de visitas. En mi parte (Diego) solo la dejo definida,
// porque el historial de visitas lo implementa Fernanda.
@Entity(tableName = "visitas")
data class Visita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Guarda el id del equipo visitado (relación simple, sin ForeignKey
    // para no complicar el código).
    val equipoId: Int,

    // La fecha va como texto, ej: "12-08-2026 10:24". Es lo más fácil de manejar.
    val fecha: String,

    val observaciones: String,

    // --- Campos nuevos para el historial ---

    // Nombre del técnico que realizó la visita.
    val tecnico: String,

    // false = aún no se envía al servidor. Por defecto toda visita parte sin sincronizar.
    val sincronizada: Boolean = false,

    // Ruta de la foto de evidencia en el teléfono. null = visita sin foto.
    val rutaFoto: String? = null,

    // Ubicación GPS donde se hizo la visita. null = no se registró.
    val latitud: Double? = null,
    val longitud: Double? = null
)