package cl.tacticaldev.equipos;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoRepository repository;

    public EquipoController(EquipoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Equipo> obtenerTodos() {
        return repository.findAll();
    }

    @PostMapping
    public Equipo crear(@RequestBody Equipo equipo) {
        equipo.setId(null);
        return repository.save(equipo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipo> actualizar(@PathVariable Long id, @RequestBody Equipo equipo) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        equipo.setId(id);
        return ResponseEntity.ok(repository.save(equipo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
