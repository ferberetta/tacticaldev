package cl.tacticaldev.visitas.viewmodel

import androidx.lifecycle.ViewModel
import cl.tacticaldev.visitas.data.Visita
import cl.tacticaldev.visitas.data.visitasFalsas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class FiltroHistorial { TODAS, COMPLETADAS, PENDIENTES }

class HistorialViewModel : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroHistorial.TODAS)
    val filtro: StateFlow<FiltroHistorial> = _filtro.asStateFlow()

    private val _visitas = MutableStateFlow(visitasFalsas)
    val visitas: StateFlow<List<Visita>> = _visitas.asStateFlow()

    fun cambiarFiltro(nuevo: FiltroHistorial) {
        _filtro.value = nuevo
    }

    fun visitasFiltradas(): List<Visita> = when (_filtro.value) {
        FiltroHistorial.TODAS -> _visitas.value
        FiltroHistorial.COMPLETADAS -> _visitas.value.filter { it.sincronizada }
        FiltroHistorial.PENDIENTES -> _visitas.value.filter { !it.sincronizada }
    }
}