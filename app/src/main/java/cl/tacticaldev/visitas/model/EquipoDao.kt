package cl.tacticaldev.visitas.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// DAO = Data Access Object. Es una INTERFAZ donde escribimos las consultas.
// Room genera el código real automáticamente (por eso usamos KSP).
// Todas las funciones son "suspend": se ejecutan en segundo plano
// y así no se congela la pantalla.
@Dao
interface EquipoDao {

    // Trae TODOS los equipos de la tabla.
    @Query("SELECT * FROM equipos")
    suspend fun obtenerTodos(): List<Equipo>

    // Busca equipos cuyo nombre O ubicación contengan el texto escrito.
    // LIKE con '%' al inicio y al final significa "que contenga el texto".
    // :texto es el parámetro de la función.
    @Query("SELECT * FROM equipos WHERE nombre LIKE '%' || :texto || '%' OR ubicacion LIKE '%' || :texto || '%'")
    suspend fun buscarPorTexto(texto: String): List<Equipo>

    // Trae solo los equipos que tengan exactamente ese estado.
    @Query("SELECT * FROM equipos WHERE estado = :estado")
    suspend fun filtrarPorEstado(estado: String): List<Equipo>

    // Estas dos NO estaban en el enunciado, pero las necesito para
    // poblar la base con datos de ejemplo:

    // Inserta una lista de equipos de una sola vez.
    @Insert
    suspend fun insertarVarios(equipos: List<Equipo>)

    // Cuenta cuántos equipos hay (para saber si la tabla está vacía).
    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun contarEquipos(): Int
}