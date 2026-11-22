package cl.tacticaldev.visitas;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitas")
public class VisitaController {

    private final VisitaRepository repository;

    public VisitaController(VisitaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Visita> obtenerTodas() {
        return repository.findAll();
    }

    @PostMapping
    public Visita crear(@RequestBody Visita visita) {
        visita.setId(null);
        return repository.save(visita);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Visita> actualizar(@PathVariable Long id, @RequestBody Visita visita) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        visita.setId(id);
        return ResponseEntity.ok(repository.save(visita));
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