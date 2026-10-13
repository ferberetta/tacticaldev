package cl.tacticaldev.visitas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.tacticaldev.visitas.repository.EquipoRepository

// ¿Por qué existe esta clase?
// Android crea los ViewModel por su cuenta, pero solo sabe crear los que
// NO reciben parámetros. Como el nuestro recibe un repositorio,
// necesitamos esta "fábrica" que le diga cómo construirlo (sin Hilt).
class EquipoViewModelFactory(
    private val repository: EquipoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Creamos el ViewModel pasándole el repositorio.
        return EquipoViewModel(repository) as T
    }
}