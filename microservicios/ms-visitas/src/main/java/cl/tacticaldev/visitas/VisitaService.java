
package cl.tacticaldev.visitas;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitaService {

    private final VisitaRepository repository;

    public VisitaService(VisitaRepository repository) {
        this.repository = repository;
    }

    public List<Visita> obtenerTodas() {
        return repository.findAll();
    }

    public Visita crear(Visita visita) {
        visita.setId(null);
        return repository.save(visita);
    }

    public Optional<Visita> actualizar(Long id, Visita datos) {

        return repository.findById(id).map(visitaExistente -> {

            // Actualizar solamente los campos recibidos.
            if (datos.getEquipoId() != null) {
                visitaExistente.setEquipoId(datos.getEquipoId());
            }

            if (datos.getFecha() != null) {
                visitaExistente.setFecha(datos.getFecha());
            }

            if (datos.getObservaciones() != null) {
                visitaExistente.setObservaciones(datos.getObservaciones());
            }

            if (datos.getTecnico() != null) {
                visitaExistente.setTecnico(datos.getTecnico());
            }

            // Actualizar el estado del caso.
            visitaExistente.setSincronizada(datos.isSincronizada());

            // Preservar información adicional si no viene en la petición.
            if (datos.getRutaFoto() != null) {
                visitaExistente.setRutaFoto(datos.getRutaFoto());
            }

            if (datos.getLatitud() != null) {
                visitaExistente.setLatitud(datos.getLatitud());
            }

            if (datos.getLongitud() != null) {
                visitaExistente.setLongitud(datos.getLongitud());
            }

            return repository.save(visitaExistente);
        });
    }

    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }
}
