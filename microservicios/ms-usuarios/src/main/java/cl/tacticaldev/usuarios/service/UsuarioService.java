package cl.tacticaldev.usuarios.service;

import cl.tacticaldev.usuarios.dto.LoginRequest;
import cl.tacticaldev.usuarios.dto.UsuarioRequest;
import cl.tacticaldev.usuarios.dto.UsuarioResponse;
import cl.tacticaldev.usuarios.model.Rol;
import cl.tacticaldev.usuarios.model.Usuario;
import cl.tacticaldev.usuarios.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse crearUsuario(UsuarioRequest request) {

        if (request.getRol() == Rol.ADMINISTRADOR) {
            throw new IllegalArgumentException(
                    "No esta permitido registrar administradores publicamente");
        }

        String rut = request.getRut().trim().toUpperCase();

        if (usuarioRepository.existsByRut(rut)) {
            throw new IllegalArgumentException("El RUT ya esta registrado");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre().trim());
        usuario.setApellido(request.getApellido().trim());
        usuario.setRut(rut);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setActivo(true);

        return convertirRespuesta(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuario(Long id) {
        return convertirRespuesta(buscarUsuario(id));
    }

    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request) {

        Usuario usuario = buscarUsuario(id);

        String rut = request.getRut().trim().toUpperCase();

        usuarioRepository.findByRut(rut)
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException(
                            "El RUT ya pertenece a otro usuario");
                });

        usuario.setNombre(request.getNombre().trim());
        usuario.setApellido(request.getApellido().trim());
        usuario.setRut(rut);

        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {
            usuario.setPassword(
                    passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRol() == Rol.ADMINISTRADOR) {
            throw new IllegalArgumentException(
                    "No esta permitido asignar el rol administrador mediante esta operacion");
        }

        if (request.getRol() != null) {
            usuario.setRol(request.getRol());
        }

        return convertirRespuesta(usuarioRepository.save(usuario));
    }

    public UsuarioResponse cambiarEstado(Long id, Boolean activo) {

        Usuario usuario = buscarUsuario(id);
        usuario.setActivo(activo);

        return convertirRespuesta(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse iniciarSesion(LoginRequest request) {

        String rut = request.getRut().trim().toUpperCase();

        Usuario usuario = usuarioRepository.findByRut(rut)
                .orElseThrow(() ->
                        new IllegalArgumentException("Credenciales incorrectas"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new IllegalArgumentException("La cuenta esta desactivada");
        }

        if (!passwordEncoder.matches(
                request.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        return convertirRespuesta(usuario);
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado"));
    }

    private UsuarioResponse convertirRespuesta(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getRut(),
                usuario.getRol(),
                usuario.getActivo(),
                usuario.getFechaCreacion()
        );
    }
}
