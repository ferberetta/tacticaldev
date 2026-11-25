package cl.tacticaldev.usuarios.controller;

import cl.tacticaldev.usuarios.dto.LoginRequest;
import cl.tacticaldev.usuarios.dto.UsuarioRequest;
import cl.tacticaldev.usuarios.dto.UsuarioResponse;
import cl.tacticaldev.usuarios.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(
            @Valid @RequestBody UsuarioRequest request) {

        UsuarioResponse respuesta = usuarioService.crearUsuario(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> iniciarSesion(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(usuarioService.iniciarSesion(request));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {

        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuario(
            @PathVariable Long id) {

        return ResponseEntity.ok(usuarioService.obtenerUsuario(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest request) {

        return ResponseEntity.ok(
                usuarioService.actualizarUsuario(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activo) {

        return ResponseEntity.ok(
                usuarioService.cambiarEstado(id, activo));
    }
}