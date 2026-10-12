package cl.tacticaldev.visitas.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// DAO de las visitas. Lo dejo mínimo: solo lo que necesito para
// guardar los datos ficticios (el historial en pantalla lo hace Fernanda).
@Dao
interface VisitaDao {

    // Inserta una lista de visitas de una sola vez.
    @Insert
    suspend fun insertarVarios(visitas: List<Visita>)

    // Cuenta cuántas visitas hay guardadas (sirve para verificar).
    @Query("SELECT COUNT(*) FROM visitas")
    suspend fun contarVisitas(): Int
}