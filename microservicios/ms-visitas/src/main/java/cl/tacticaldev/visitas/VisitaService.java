
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

    public Optional<Visita> actualizar(Long id, Visita visita) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }

        visita.setId(id);
        return Optional.of(repository.save(visita));
    }

    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }
}
