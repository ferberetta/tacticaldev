package cl.tacticaldev.visitas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.tacticaldev.visitas.model.Equipo
import cl.tacticaldev.visitas.repository.EquipoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel de equipos: obtiene la lista desde el servidor y la filtra en memoria.
class EquipoViewModel(private val repository: EquipoRepository) : ViewModel() {

    private var listaCompleta: List<Equipo> = emptyList()

    private val _equipos = MutableStateFlow<List<Equipo>>(emptyList())
    val equipos: StateFlow<List<Equipo>> = _equipos

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    private val _filtroActual = MutableStateFlow("Todos")
    val filtroActual: StateFlow<String> = _filtroActual

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarEquipos()
    }

    fun cambiarBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
        aplicarFiltros()
    }

    fun cambiarFiltro(nuevoFiltro: String) {
        _filtroActual.value = nuevoFiltro
        aplicarFiltros()
    }

    fun cargarEquipos() {
        viewModelScope.launch {
            try {
                _error.value = null
                listaCompleta = repository.obtenerTodos()
                if (listaCompleta.isEmpty()) {
                    listaCompleta = listOf(
                        Equipo(id = 1, nombre = "Refrigeradores", ubicacion = "Sin asignar", estado = "Operativo"),
                        Equipo(id = 2, nombre = "Conservadoras", ubicacion = "Sin asignar", estado = "Operativo")
                    )
                }
                aplicarFiltros()
            } catch (e: Exception) {
                listaCompleta = listOf(
                    Equipo(id = 1, nombre = "Refrigeradores", ubicacion = "Sin asignar", estado = "Operativo"),
                    Equipo(id = 2, nombre = "Conservadoras", ubicacion = "Sin asignar", estado = "Operativo")
                )
                _error.value = "Sin conexión al servidor. Mostrando equipos por defecto."
                aplicarFiltros()
            }
        }
    }

    fun crearEquipo(nombre: String, ubicacion: String, estado: String) {
        viewModelScope.launch {
            try {
                _error.value = null
                repository.crear(Equipo(nombre = nombre, ubicacion = ubicacion, estado = estado))
                listaCompleta = repository.obtenerTodos()
                aplicarFiltros()
            } catch (e: Exception) {
                _error.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun actualizarEquipo(equipo: Equipo) {
        viewModelScope.launch {
            try {
                _error.value = null
                repository.actualizar(equipo)
                listaCompleta = repository.obtenerTodos()
                aplicarFiltros()
            } catch (e: Exception) {
                _error.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun eliminarEquipo(id: Int) {
        viewModelScope.launch {
            try {
                _error.value = null
                repository.eliminar(id)
                listaCompleta = repository.obtenerTodos()
                aplicarFiltros()
            } catch (e: Exception) {
                _error.value = "No se pudo conectar con el servidor"
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    private fun aplicarFiltros() {
        val texto = _textoBusqueda.value.trim()
        val filtro = _filtroActual.value

        _equipos.value = listaCompleta.filter { equipo ->
            val coincideTexto = texto.isEmpty() ||
                equipo.nombre.contains(texto, ignoreCase = true) ||
                equipo.ubicacion.contains(texto, ignoreCase = true)
            val coincideEstado = filtro == "Todos" || equipo.estado == filtro
            coincideTexto && coincideEstado
        }
    }
}
