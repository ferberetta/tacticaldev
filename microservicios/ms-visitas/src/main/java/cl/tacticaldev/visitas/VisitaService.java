
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

        List<Visita> visitas = repository.findAll();

        for (Visita visita : visitas) {
            normalizar(visita);
        }

        return visitas;
    }

    public Visita crear(Visita visita) {
        visita.setId(null);
        normalizar(visita);
        return repository.save(visita);
    }

    public Optional<Visita> actualizar(Long id, Visita datos) {

        return repository.findById(id).map(existente -> {

            if (datos.getEquipoId() != null) {
                existente.setEquipoId(datos.getEquipoId());
            }

            if (datos.getFecha() != null) {
                existente.setFecha(datos.getFecha());
            }

            if (datos.getObservaciones() != null) {
                existente.setObservaciones(datos.getObservaciones());
            }

            if (datos.getTecnico() != null) {
                existente.setTecnico(datos.getTecnico());
            }

            existente.setSincronizada(datos.isSincronizada());

            if (datos.getRutaFoto() != null) {
                existente.setRutaFoto(datos.getRutaFoto());
            }

            if (datos.getLatitud() != null) {
                existente.setLatitud(datos.getLatitud());
            }

            if (datos.getLongitud() != null) {
                existente.setLongitud(datos.getLongitud());
            }

            normalizar(existente);

            return repository.save(existente);
        });
    }

    private void normalizar(Visita visita) {

        if (visita.getEquipoId() == null) {
            visita.setEquipoId(0L);
        }

        if (visita.getFecha() == null) {
            visita.setFecha("");
        }

        if (visita.getObservaciones() == null) {
            visita.setObservaciones("");
        }

        if (visita.getTecnico() == null) {
            visita.setTecnico("");
        }
    }

    public boolean eliminar(Long id) {

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }
}
