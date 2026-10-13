package cl.tacticaldev.visitas.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// @Database: aquí listamos TODAS las tablas (entities) de la base de datos.
// version = 1: si algún día cambio las tablas, hay que subir este número.
// exportSchema = false: para que no pida configurar carpetas de esquemas.
@Database(entities = [Equipo::class, Visita::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Room implementa esta función por nosotros y nos entrega el DAO.
    abstract fun equipoDao(): EquipoDao
    abstract fun visitaDao(): VisitaDao

    // companion object = lo que va "estático" en Kotlin.
    // Lo uso para que exista UNA SOLA base de datos en toda la app (patrón Singleton).
    companion object {

        // Aquí guardamos la única instancia. Parte en null (aún no se crea).
        // @Volatile hace que todos los hilos vean el valor actualizado.
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun obtenerBase(context: Context): AppDatabase {
            // Si ya existe, la devolvemos. Si no, la creamos.
            // synchronized evita que dos hilos la creen al mismo tiempo.
            return INSTANCIA ?: synchronized(this) {
                val nuevaInstancia = Room.databaseBuilder(
                    context.applicationContext,   // applicationContext evita fugas de memoria
                    AppDatabase::class.java,
                    "tacticaldev_db"              // nombre del archivo de la base de datos
                ).build()

                INSTANCIA = nuevaInstancia
                nuevaInstancia
            }
        }
    }
}