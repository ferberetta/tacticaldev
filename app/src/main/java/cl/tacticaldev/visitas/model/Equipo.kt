package cl.tacticaldev.visitas.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "equipos")
data class Equipo(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,      // Ej: "Vitrina Freezer A-102"
    val ubicacion: String,   // Ej: "Minimarket Maipú" (punto de venta)
    val estado: String
)