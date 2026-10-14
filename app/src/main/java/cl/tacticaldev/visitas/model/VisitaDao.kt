package cl.tacticaldev.visitas.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface VisitaDao {

    @Insert
    suspend fun insertarVarios(visitas: List<Visita>)
    @Query("SELECT COUNT(*) FROM visitas")
    suspend fun contarVisitas(): Int
    @Query("SELECT * FROM visitas")
    fun obtenerTodas(): Flow<List<Visita>>
}