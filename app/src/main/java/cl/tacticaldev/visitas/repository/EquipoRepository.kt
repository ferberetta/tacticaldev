package cl.tacticaldev.visitas.repository

import cl.tacticaldev.visitas.model.Equipo
import cl.tacticaldev.visitas.network.EquiposApi

// Repositorio de equipos conectado al microservicio mediante Retrofit.
class EquipoRepository(private val api: EquiposApi) {

    suspend fun obtenerTodos(): List<Equipo> {
        return api.obtenerTodos()
    }

    suspend fun crear(equipo: Equipo): Equipo {
        return api.crear(equipo)
    }

    suspend fun actualizar(equipo: Equipo): Equipo {
        return api.actualizar(equipo.id, equipo)
    }

    suspend fun eliminar(id: Int) {
        api.eliminar(id)
    }
}
