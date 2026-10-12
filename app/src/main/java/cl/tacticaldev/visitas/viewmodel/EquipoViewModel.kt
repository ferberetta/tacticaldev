package cl.tacticaldev.visitas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.tacticaldev.visitas.model.Equipo
import cl.tacticaldev.visitas.repository.EquipoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// El ViewModel guarda el ESTADO de la pantalla y tiene la LÓGICA.
// La pantalla (Composable) solo dibuja lo que el ViewModel le entrega.
// Sobrevive a rotaciones de pantalla, por eso no perdemos la búsqueda.
class EquipoViewModel(private val repository: EquipoRepository) : ViewModel() {

    // Patrón de StateFlow: una variable privada que se puede MODIFICAR (_equipos)
    // y otra pública que solo se puede LEER (equipos).
    // Así la pantalla no puede cambiar la lista por su cuenta.

    // Lista de equipos que se muestra en pantalla
    private val _equipos = MutableStateFlow<List<Equipo>>(emptyList())
    val equipos: StateFlow<List<Equipo>> = _equipos

    // Texto que el usuario escribió en la barra de búsqueda
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    // Filtro seleccionado: "Todos", "Operativo" o "Con falla"
    private val _filtroActual = MutableStateFlow("Todos")
    val filtroActual: StateFlow<String> = _filtroActual

    // init se ejecuta una vez, apenas se crea el ViewModel.
    init {
        // viewModelScope.launch = lanza una corrutina (trabajo en segundo plano)
        viewModelScope.launch {
            repository.poblarDatosFicticios() // 1) llena la BD si está vacía
            cargarEquipos()                   // 2) carga la lista inicial
        }
    }

    // Se llama cada vez que el usuario escribe una letra en el buscador.
    fun cambiarBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
        cargarEquipos()
    }

    // Se llama cuando el usuario toca un chip (Todos / Operativos / Con falla).
    fun cambiarFiltro(nuevoFiltro: String) {
        _filtroActual.value = nuevoFiltro
        cargarEquipos()
    }

    // Aquí está TODA la lógica de búsqueda + filtro, en un solo lugar.
    private fun cargarEquipos() {
        viewModelScope.launch {
            val texto = _textoBusqueda.value
            val filtro = _filtroActual.value

            val lista: List<Equipo>

            if (texto.isNotBlank()) {
                // Hay texto escrito: primero buscamos por texto en la BD...
                val resultadoBusqueda = repository.buscarPorTexto(texto)
                // ...y si además hay un filtro de estado, filtramos esa lista.
                if (filtro != "Todos") {
                    lista = resultadoBusqueda.filter { it.estado == filtro }
                } else {
                    lista = resultadoBusqueda
                }
            } else if (filtro != "Todos") {
                // No hay texto, pero sí filtro: pedimos solo ese estado.
                lista = repository.filtrarPorEstado(filtro)
            } else {
                // Sin texto y sin filtro: mostramos todo.
                lista = repository.obtenerTodos()
            }

            // Al cambiar este valor, Compose se entera y redibuja la lista sola.
            _equipos.value = lista
        }
    }
}