
package cl.tacticaldev.visitas.repository

import cl.tacticaldev.visitas.model.Visita
import cl.tacticaldev.visitas.network.VisitasApi

// Repositorio de visitas conectado al microservicio mediante Retrofit.
class VisitaRepository(private val api: VisitasApi) {

    suspend fun obtenerTodas(): List<Visita> {
        return api.obtenerTodos()
    }

    suspend fun crear(visita: Visita): Visita {
        return api.crear(visita)
    }

    suspend fun actualizar(visita: Visita): Visita {
        return api.actualizar(visita.id, visita)
    }

    suspend fun eliminar(id: Int) {
        api.eliminar(id)
    }
}
