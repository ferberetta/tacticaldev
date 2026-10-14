package cl.tacticaldev.visitas.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface EquipoDao {

    @Query("SELECT * FROM equipos")
    suspend fun obtenerTodos(): List<Equipo>
    @Query("SELECT * FROM equipos WHERE nombre LIKE '%' || :texto || '%' OR ubicacion LIKE '%' || :texto || '%'")
    suspend fun buscarPorTexto(texto: String): List<Equipo>

    @Query("SELECT * FROM equipos WHERE estado = :estado")
    suspend fun filtrarPorEstado(estado: String): List<Equipo>

    @Insert
    suspend fun insertarVarios(equipos: List<Equipo>)

    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun contarEquipos(): Int
}