package cl.tacticaldev.usuarios.repository;

import cl.tacticaldev.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByRut(String rut);

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByRut(String rut);

    boolean existsByCorreo(String correo);
}