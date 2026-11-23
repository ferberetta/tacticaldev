package cl.tacticaldev.equipos;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipoService {

    private final EquipoRepository repository;

    public EquipoService(EquipoRepository repository) {
        this.repository = repository;
    }

    public List<Equipo> obtenerTodos() {
        return repository.findAll();
    }

    public Equipo crear(Equipo equipo) {
        equipo.setId(null);
        return repository.save(equipo);
    }

    public Optional<Equipo> actualizar(Long id, Equipo equipo) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }

        equipo.setId(id);
        return Optional.of(repository.save(equipo));
    }

    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }
}