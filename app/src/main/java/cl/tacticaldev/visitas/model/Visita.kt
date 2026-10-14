package cl.tacticaldev.visitas.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "visitas")
data class Visita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val equipoId: Int,
    val fecha: String,
    val observaciones: String,
    val tecnico: String,
    val sincronizada: Boolean = false,
    val rutaFoto: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)