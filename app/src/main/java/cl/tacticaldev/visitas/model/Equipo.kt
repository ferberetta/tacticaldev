package cl.tacticaldev.visitas.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity le dice a Room: "esta clase es una TABLA de la base de datos".
// Cada atributo de la clase es una columna de la tabla.
@Entity(tableName = "equipos")
data class Equipo(
    // @PrimaryKey = identificador único de cada fila.
    // autoGenerate = true -> Room le pone el número solo (1, 2, 3...).
    // Por eso le damos valor por defecto 0: al insertar, Room lo reemplaza.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,      // Ej: "Vitrina Freezer A-102"
    val ubicacion: String,   // Ej: "Minimarket Maipú" (punto de venta)

    // Guardamos el estado como String simple (más fácil que un enum).
    // Valores posibles: "Operativo", "Con falla", "Sin conexión"
    val estado: String
)