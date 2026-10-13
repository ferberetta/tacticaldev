package cl.tacticaldev.visitas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.tacticaldev.visitas.model.EquipoDao
import cl.tacticaldev.visitas.model.Visita
import cl.tacticaldev.visitas.model.VisitaDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class FiltroHistorial { TODAS, COMPLETADAS, PENDIENTES }

// Visita + datos del equipo, listo para mostrar en pantalla
data class VisitaUi(
    val visita: Visita,
    val equipoNombre: String,
    val puntoVenta: String
)

class HistorialViewModel(
    visitaDao: VisitaDao,
    private val equipoDao: EquipoDao
) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroHistorial.TODAS)
    val filtro: StateFlow<FiltroHistorial> = _filtro.asStateFlow()

    // Todas las visitas desde Room, unidas con su equipo
    val todas: StateFlow<List<VisitaUi>> = visitaDao.obtenerTodas()
        .map { visitas ->
            val equipos = equipoDao.obtenerTodos().associateBy { it.id }
            visitas.sortedByDescending { it.id }.map { v ->
                val e = equipos[v.equipoId]
                VisitaUi(
                    visita = v,
                    equipoNombre = e?.nombre ?: "Equipo desconocido",
                    puntoVenta = e?.ubicacion ?: ""
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Lista ya filtrada según la pestaña
    val visitasFiltradas: StateFlow<List<VisitaUi>> = combine(todas, _filtro) { lista, f ->
        when (f) {
            FiltroHistorial.TODAS -> lista
            FiltroHistorial.COMPLETADAS -> lista.filter { it.visita.sincronizada }
            FiltroHistorial.PENDIENTES -> lista.filter { !it.visita.sincronizada }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarFiltro(nuevo: FiltroHistorial) {
        _filtro.value = nuevo
    }
}

class HistorialViewModelFactory(
    private val visitaDao: VisitaDao,
    private val equipoDao: EquipoDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistorialViewModel(visitaDao, equipoDao) as T
    }
}