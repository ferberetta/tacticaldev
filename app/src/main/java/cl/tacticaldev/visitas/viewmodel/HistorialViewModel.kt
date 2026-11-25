
package cl.tacticaldev.visitas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.tacticaldev.visitas.model.Equipo
import cl.tacticaldev.visitas.model.Visita
import cl.tacticaldev.visitas.repository.ClimaRepository
import cl.tacticaldev.visitas.repository.EquipoRepository
import cl.tacticaldev.visitas.repository.VisitaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class FiltroHistorial {
    TODAS,
    COMPLETADAS,
    PENDIENTES
}

// Visita + datos del equipo, listo para mostrar en pantalla
data class VisitaUi(
    val visita: Visita,
    val equipoNombre: String,
    val puntoVenta: String
)

class HistorialViewModel(
    private val visitaRepository: VisitaRepository,
    private val equipoRepository: EquipoRepository,
    private val climaRepository: ClimaRepository
) : ViewModel() {

    private var equiposCargados: List<Equipo> = emptyList()

    private val _filtro = MutableStateFlow(FiltroHistorial.TODAS)
    val filtro: StateFlow<FiltroHistorial> = _filtro.asStateFlow()

    private val _todas = MutableStateFlow<List<VisitaUi>>(emptyList())
    val todas: StateFlow<List<VisitaUi>> = _todas.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _temperatura = MutableStateFlow<Double?>(null)
    val temperatura: StateFlow<Double?> = _temperatura.asStateFlow()

    // Lista filtrada según la pestaña
    val visitasFiltradas: StateFlow<List<VisitaUi>> =
        combine(_todas, _filtro) { lista, f ->
            when (f) {
                FiltroHistorial.TODAS -> lista

                FiltroHistorial.COMPLETADAS ->
                    lista.filter { it.visita.sincronizada }

                FiltroHistorial.PENDIENTES ->
                    lista.filter { !it.visita.sincronizada }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private var visitasEnMemoria: MutableList<Visita> = mutableListOf()

    init {
        cargar()
    }

    fun cambiarFiltro(nuevo: FiltroHistorial) {
        _filtro.value = nuevo
    }

    fun cargar() {
        viewModelScope.launch {
            try {
                _error.value = null
                cargarDesdeServidor()
            } catch (e: Exception) {
                Log.e("HISTORIAL", "Error al cargar visitas", e)
                _error.value =
                    "No se pudo conectar con el servidor: ${e.message}"
            }
        }
    }

    // CREAR VISITA
    fun crearVisita(
        equipoNombre: String,
        cargo: String,
        rut: String,
        observaciones: String,
        rutaFoto: String?,
        latitud: Double?,
        longitud: Double?,
        usuarioRegistro: String,
        usuarioRegistroId: String
    ) {
        viewModelScope.launch {
            try {
                _error.value = null

                try {
                    val equiposActuales = equipoRepository.obtenerTodos()

                    if (equiposActuales.isNotEmpty()) {
                        equiposCargados = equiposActuales
                    }

                } catch (e: Exception) {
                    if (equiposCargados.isEmpty()) {
                        equiposCargados = listOf(
                            Equipo(
                                id = 1,
                                nombre = "Refrigeradores",
                                ubicacion = "Sin asignar",
                                estado = "Operativo"
                            ),
                            Equipo(
                                id = 2,
                                nombre = "Conservadoras",
                                ubicacion = "Sin asignar",
                                estado = "Operativo"
                            )
                        )
                    }
                }

                val equipo = equiposCargados.firstOrNull {
                    it.nombre.equals(equipoNombre, ignoreCase = true)
                }

                if (equipo == null) {
                    _error.value = "No se encontró el equipo seleccionado"
                    return@launch
                }

                val fecha = SimpleDateFormat(
                    "dd-MM-yyyy HH:mm",
                    Locale.getDefault()
                ).format(Date())

                val nuevaVisita = Visita(
                    id = 0,
                    equipoId = equipo.id,
                    fecha = fecha,
                    observaciones = observaciones,
                    tecnico = "$cargo $rut",
                    usuarioRegistro = usuarioRegistro,
                    usuarioRegistroId = usuarioRegistroId,
                    sincronizada = false,
                    rutaFoto = rutaFoto,
                    latitud = latitud,
                    longitud = longitud
                )

                try {
                    val guardada = visitaRepository.crear(nuevaVisita)
                    visitasEnMemoria.add(guardada)

                } catch (e: Exception) {
                    Log.e("HISTORIAL", "Error al guardar visita", e)

                    val idLocal = if (visitasEnMemoria.isEmpty()) {
                        1
                    } else {
                        visitasEnMemoria.maxOf { it.id } + 1
                    }

                    visitasEnMemoria.add(
                        nuevaVisita.copy(id = idLocal)
                    )

                    _error.value =
                        "Guardado localmente. Sin conexión al servidor."
                }

                cargarDesdeServidor()

            } catch (e: Exception) {
                Log.e("HISTORIAL", "Error al crear visita", e)
                _error.value = "Error al crear la visita: ${e.message}"
            }
        }
    }

    // MARCAR VISITA COMO COMPLETADA
    fun marcarCompletada(visita: Visita) {
        viewModelScope.launch {
            try {
                _error.value = null

                // Leer las propiedades como nullable para protegerse
                // de valores null recibidos mediante Gson.
                val idSeguro = (visita.id as Int?) ?: 0
                val equipoIdSeguro = (visita.equipoId as Int?) ?: 0
                val fechaSegura = (visita.fecha as String?) ?: ""
                val observacionesSeguras =
                    (visita.observaciones as String?) ?: ""
                val tecnicoSeguro = (visita.tecnico as String?) ?: ""
                val usuarioRegistroIdSeguro =
                    (visita.usuarioRegistroId as String?) ?: ""

                // Crear una visita nueva sin utilizar visita.copy().
                val visitaActualizada = Visita(
                    id = idSeguro,
                    equipoId = equipoIdSeguro,
                    fecha = fechaSegura,
                    observaciones = observacionesSeguras,
                    tecnico = tecnicoSeguro,
                    usuarioRegistro = visita.usuarioRegistro,
                    usuarioRegistroId = usuarioRegistroIdSeguro,
                    sincronizada = true,
                    rutaFoto = visita.rutaFoto,
                    latitud = visita.latitud,
                    longitud = visita.longitud
                )

                Log.d(
                    "ERROR_COMPLETAR_VISITA",
                    "Enviando actualización. ID: $idSeguro"
                )

                visitaRepository.actualizar(visitaActualizada)

                cargarDesdeServidor()

                Log.d(
                    "ERROR_COMPLETAR_VISITA",
                    "Visita completada correctamente. ID: $idSeguro"
                )

            } catch (e: HttpException) {
                val detalle = try {
                    e.response()?.errorBody()?.string()
                } catch (ex: Exception) {
                    null
                }

                Log.e(
                    "ERROR_COMPLETAR_VISITA",
                    "Error HTTP ${e.code()}",
                    e
                )

                _error.value =
                    "Error HTTP ${e.code()}: ${detalle ?: e.message()}"

            } catch (e: Exception) {
                Log.e(
                    "ERROR_COMPLETAR_VISITA",
                    "Error al completar visita",
                    e
                )

                _error.value =
                    "Error al completar visita: ${e.message ?: "Error desconocido"}"
            }
        }
    }

    // ELIMINAR VISITA
    fun eliminar(id: Int) {
        viewModelScope.launch {
            try {
                _error.value = null

                visitaRepository.eliminar(id)

                cargarDesdeServidor()

            } catch (e: Exception) {
                Log.e("HISTORIAL", "Error al eliminar visita", e)
                _error.value = "No se pudo eliminar la visita: ${e.message}"
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    // OBTENER TEMPERATURA
    fun cargarTemperatura(latitud: Double?, longitud: Double?) {
        viewModelScope.launch {

            if (latitud == null || longitud == null) {
                _temperatura.value = null
                return@launch
            }

            try {
                _temperatura.value =
                    climaRepository.obtenerTemperatura(latitud, longitud)

            } catch (e: Exception) {
                Log.e("HISTORIAL", "Error al obtener temperatura", e)
                _temperatura.value = null
            }
        }
    }

    // CARGAR VISITAS Y EQUIPOS DESDE EL SERVIDOR
    private suspend fun cargarDesdeServidor() {

        try {
            equiposCargados = equipoRepository.obtenerTodos()

            if (equiposCargados.isEmpty()) {
                equiposCargados = listOf(
                    Equipo(
                        id = 1,
                        nombre = "Refrigeradores",
                        ubicacion = "Sin asignar",
                        estado = "Operativo"
                    ),
                    Equipo(
                        id = 2,
                        nombre = "Conservadoras",
                        ubicacion = "Sin asignar",
                        estado = "Operativo"
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("HISTORIAL", "Error al cargar equipos", e)

            equiposCargados = listOf(
                Equipo(
                    id = 1,
                    nombre = "Refrigeradores",
                    ubicacion = "Sin asignar",
                    estado = "Operativo"
                ),
                Equipo(
                    id = 2,
                    nombre = "Conservadoras",
                    ubicacion = "Sin asignar",
                    estado = "Operativo"
                )
            )
        }

        val equiposPorId = equiposCargados.associateBy {
            it.id
        }

        var visitas = emptyList<Visita>()

        try {
            visitas = visitaRepository.obtenerTodas()

            if (visitas.isNotEmpty()) {
                visitasEnMemoria = visitas.toMutableList()
            }

        } catch (e: Exception) {
            Log.e("HISTORIAL", "Error al cargar visitas del servidor", e)
            visitas = visitasEnMemoria
        }

        _todas.value = visitas
            .sortedByDescending { it.id }
            .map { v ->

                val equipo = equiposPorId[v.equipoId]

                VisitaUi(
                    visita = v,
                    equipoNombre = equipo?.nombre ?: "Equipo desconocido",
                    puntoVenta = equipo?.ubicacion ?: ""
                )
            }
    }
}

class HistorialViewModelFactory(
    private val visitaRepository: VisitaRepository,
    private val equipoRepository: EquipoRepository,
    private val climaRepository: ClimaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistorialViewModel(
            visitaRepository,
            equipoRepository,
            climaRepository
        ) as T
    }
}
