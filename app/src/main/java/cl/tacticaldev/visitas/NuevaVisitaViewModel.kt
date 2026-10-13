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
