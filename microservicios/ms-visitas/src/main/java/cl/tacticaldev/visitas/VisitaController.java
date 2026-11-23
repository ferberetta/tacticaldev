package cl.tacticaldev.visitas;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitas")
public class VisitaController {

    private final VisitaService service;

    public VisitaController(VisitaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Visita> obtenerTodas() {
        return service.obtenerTodas();
    }

    @PostMapping
    public Visita crear(@RequestBody Visita visita) {
        return service.crear(visita);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Visita> actualizar(
            @PathVariable Long id,
            @RequestBody Visita visita) {
        return service.actualizar(id, visita)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}