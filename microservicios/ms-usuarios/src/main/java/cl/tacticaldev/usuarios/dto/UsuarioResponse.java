package cl.tacticaldev.usuarios.dto;

import cl.tacticaldev.usuarios.model.Rol;
import java.time.LocalDateTime;

public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String correo;
    private Rol rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, String nombre, String apellido,
                           String rut, String correo, Rol rol,
                           Boolean activo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.correo = correo;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getRut() { return rut; }
    public String getCorreo() { return correo; }
    public Rol getRol() { return rol; }
    public Boolean getActivo() { return activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}