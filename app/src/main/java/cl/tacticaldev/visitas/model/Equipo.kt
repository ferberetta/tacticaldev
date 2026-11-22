package cl.tacticaldev.visitas.model

// Modelo de equipo refrigerado recibido desde el microservicio de equipos.
data class Equipo(
    val id: Int = 0,
    val nombre: String,      // Ej: "Vitrina Freezer A-102"
    val ubicacion: String,   // Ej: "Minimarket Maipú" (punto de venta)
    // Valores posibles: "Operativo", "Con falla", "Sin conexión"
    val estado: String
)
