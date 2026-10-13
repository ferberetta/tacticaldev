package cl.tacticaldev.visitas


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update




fun rutValido(rut: String): Boolean {
    if (rut.length < 8) return false
    val cuerpo = rut.dropLast(1)
    val dv = rut.last()
    if (!cuerpo.all { it.isDigit() }) return false


    var suma = 0
    var multiplicador = 2
    for (c in cuerpo.reversed()) {
        suma += c.digitToInt() * multiplicador
        multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
    }
    val resto = 11 - (suma % 11)
    val esperado = when (resto) {
        11 -> '0'
        10 -> 'K'
        else -> resto.digitToChar()
    }
    return dv == esperado
}




fun formatearRut(rut: String): String {
    if (rut.length <= 1) return rut
    val cuerpo = rut.dropLast(1)
    val dv = rut.last()
    val conPuntos = cuerpo.reversed().chunked(3).joinToString(".").reversed()
    return "$conPuntos-$dv"
}


data class NuevaVisitaState(
    val equipo: String = "",
    val estado: String = "",
    val cargo: String = "",
    val rut: String = "",
    val observaciones: String = "",
    val intentoGuardar: Boolean = false
) {
    val errorEquipo: String?
        get() = if (intentoGuardar && equipo.isBlank()) "Selecciona un equipo" else null


    val errorEstado: String?
        get() = if (intentoGuardar && estado.isBlank()) "Selecciona el estado" else null


    val errorCargo: String?
        get() = if (intentoGuardar && cargo.isBlank()) "Selecciona el cargo" else null


    val errorRut: String?
        get() = when {
            !intentoGuardar -> null
            rut.isBlank() -> "Ingresa tu RUT"
            !rutValido(rut) -> "RUT no válido"
            else -> null
        }


    val errorObservaciones: String?
        get() = if (intentoGuardar && observaciones.trim().length < 5)
            "Escribe al menos 5 caracteres" else null


    val esValido: Boolean
        get() = equipo.isNotBlank() &&
                estado.isNotBlank() &&
                cargo.isNotBlank() &&
                rutValido(rut) &&
                observaciones.trim().length >= 5
}


class NuevaVisitaViewModel : ViewModel() {


    private val _state = MutableStateFlow(NuevaVisitaState())
    val state: StateFlow<NuevaVisitaState> = _state.asStateFlow()


    fun cambiarEquipo(valor: String) = _state.update { it.copy(equipo = valor) }
    fun cambiarEstado(valor: String) = _state.update { it.copy(estado = valor) }
    fun cambiarCargo(valor: String) = _state.update { it.copy(cargo = valor) }
    fun cambiarObservaciones(valor: String) = _state.update { it.copy(observaciones = valor) }




    fun cambiarRut(valor: String) {
        val limpio = valor.filter { it.isDigit() || it == 'k' || it == 'K' }
            .uppercase()
            .take(9)
        _state.update { it.copy(rut = limpio) }
    }




    fun guardar(): Boolean {
        _state.update { it.copy(intentoGuardar = true) }
        return _state.value.esValido
    }
}

