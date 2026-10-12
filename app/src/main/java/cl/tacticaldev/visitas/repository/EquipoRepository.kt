package cl.tacticaldev.visitas.repository

import cl.tacticaldev.visitas.model.Equipo
import cl.tacticaldev.visitas.model.EquipoDao

// El Repositorio es el "intermediario" entre el ViewModel y la base de datos.
// Así el ViewModel no sabe nada de Room: solo le pide datos al repositorio.
// Recibe el DAO por el constructor (así lo creamos manualmente, sin Hilt).
class EquipoRepository(private val equipoDao: EquipoDao) {

    // Estas funciones solo llaman al DAO (por eso es tan simple).
    suspend fun obtenerTodos(): List<Equipo> {
        return equipoDao.obtenerTodos()
    }

    suspend fun buscarPorTexto(texto: String): List<Equipo> {
        return equipoDao.buscarPorTexto(texto)
    }

    suspend fun filtrarPorEstado(estado: String): List<Equipo> {
        return equipoDao.filtrarPorEstado(estado)
    }

    // Inserta datos de ejemplo SOLO si la tabla está vacía.
    // Si no hiciéramos esta revisión, cada vez que se abre la app
    // se duplicarían los equipos.
    suspend fun poblarDatosFicticios() {
        if (equipoDao.contarEquipos() == 0) {
            val equiposDeEjemplo = listOf(
                // Los 3 primeros son los que se ven en la imagen de diseño
                Equipo(nombre = "Vitrina Freezer A-102", ubicacion = "Minimarket Maipú", estado = "Operativo"),
                Equipo(nombre = "Conservadora B-07", ubicacion = "Lider Cerrillos", estado = "Con falla"),
                Equipo(nombre = "Vitrina Freezer C-15", ubicacion = "Santa Isabel", estado = "Sin conexión"),
                // Estos 2 los agregué para poder probar mejor los filtros
                Equipo(nombre = "Vitrina Freezer D-21", ubicacion = "Minimarket Pudahuel", estado = "Operativo"),
                Equipo(nombre = "Conservadora E-03", ubicacion = "Almacén Don Pepe", estado = "Con falla")
            )
            equipoDao.insertarVarios(equiposDeEjemplo)
        }
    }
}