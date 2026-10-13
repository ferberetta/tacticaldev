package cl.tacticaldev.visitas.repository

import cl.tacticaldev.visitas.model.Equipo
import cl.tacticaldev.visitas.model.EquipoDao
import cl.tacticaldev.visitas.model.Visita
import cl.tacticaldev.visitas.model.VisitaDao

// El Repositorio es el "intermediario" entre el ViewModel y la base de datos.
// Ahora recibe DOS DAOs: el de equipos y el de visitas (así lo creamos manualmente, sin Hilt).
class EquipoRepository(
    private val equipoDao: EquipoDao,
    private val visitaDao: VisitaDao
) {

    suspend fun obtenerTodos(): List<Equipo> {
        return equipoDao.obtenerTodos()
    }

    suspend fun buscarPorTexto(texto: String): List<Equipo> {
        return equipoDao.buscarPorTexto(texto)
    }

    suspend fun filtrarPorEstado(estado: String): List<Equipo> {
        return equipoDao.filtrarPorEstado(estado)
    }

    // Inserta datos de ejemplo SOLO si la tabla de equipos está vacía.
    // Si no hiciéramos esta revisión, cada vez que se abre la app se duplicarían.
    suspend fun poblarDatosFicticios() {
        if (equipoDao.contarEquipos() == 0) {

            // 1) Insertamos 12 equipos (los 3 primeros son los del diseño)
            val equiposDeEjemplo = listOf(
                Equipo(nombre = "Vitrina Freezer A-102", ubicacion = "Minimarket Maipú", estado = "Operativo"),
                Equipo(nombre = "Conservadora B-07", ubicacion = "Lider Cerrillos", estado = "Con falla"),
                Equipo(nombre = "Vitrina Freezer C-15", ubicacion = "Santa Isabel", estado = "Sin conexión"),
                Equipo(nombre = "Vitrina Freezer D-21", ubicacion = "Minimarket Pudahuel", estado = "Operativo"),
                Equipo(nombre = "Conservadora E-03", ubicacion = "Almacén Don Pepe", estado = "Con falla"),
                Equipo(nombre = "Vitrina Freezer F-30", ubicacion = "Unimarc Estación Central", estado = "Operativo"),
                Equipo(nombre = "Conservadora G-11", ubicacion = "Minimarket La Esquina", estado = "Operativo"),
                Equipo(nombre = "Vitrina Freezer H-08", ubicacion = "Jumbo Maipú", estado = "Sin conexión"),
                Equipo(nombre = "Conservadora J-14", ubicacion = "Almacén El Sol", estado = "Operativo"),
                Equipo(nombre = "Vitrina Freezer K-19", ubicacion = "Tottus Cerrillos", estado = "Con falla"),
                Equipo(nombre = "Vitrina Freezer L-02", ubicacion = "Minimarket Los Aromos", estado = "Operativo"),
                Equipo(nombre = "Conservadora M-25", ubicacion = "Santa Isabel Pudahuel", estado = "Con falla")
            )
            equipoDao.insertarVarios(equiposDeEjemplo)

            // 2) Volvemos a leer los equipos para obtener los ids que Room les asignó.
            //    Necesitamos esos ids para enlazar cada visita con su equipo (equipoId).
            val equiposGuardados = equipoDao.obtenerTodos()

            // 3) Creamos 2 visitas por cada equipo con un for simple.
            val visitas = mutableListOf<Visita>()
            for (equipo in equiposGuardados) {
                visitas.add(
                    Visita(
                        equipoId = equipo.id,
                        fecha = "10-08-2026 15:37",
                        observaciones = "Revisión de rutina, temperatura estable",
                        tecnico = "Juan Perez"
                    )
                )
                visitas.add(
                    Visita(
                        equipoId = equipo.id,
                        fecha = "12-08-2026 10:24",
                        // La observación cambia según el estado actual del equipo
                        observaciones = "Estado reportado: ${equipo.estado}. Se registra evidencia de la revisión",
                        tecnico = "Fernanda Rabietas"
                    )
                )
            }
            visitaDao.insertarVarios(visitas)
        }
    }
}