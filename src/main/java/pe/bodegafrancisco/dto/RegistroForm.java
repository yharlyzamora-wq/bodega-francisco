package pe.bodegafrancisco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos del formulario de registro de clientes. */
public class RegistroForm {
    @NotBlank(message = "Ingresa tu nombre") @Size(max = 80, message = "Máximo 80 caracteres")
    private String nombre;
    @NotBlank(message = "Ingresa tu apellido") @Size(max = 80, message = "Máximo 80 caracteres")
    private String apellido;
    @NotBlank(message = "Ingresa tu correo") @Size(max = 120, message = "Máximo 120 caracteres")
    private String correo;
    @Size(max = 15, message = "Máximo 15 caracteres")
    private String celular;
    @NotBlank(message = "Crea una contraseña")
    private String password;
    @NotBlank(message = "Repite la contraseña")
    private String confirmacion;

    public String getNombre() { return nombre; }
    public void setNombre(String v) { this.nombre = v; }
    public String getApellido() { return apellido; }
    public void setApellido(String v) { this.apellido = v; }
    public String getCorreo() { return correo; }
    public void setCorreo(String v) { this.correo = v; }
    public String getCelular() { return celular; }
    public void setCelular(String v) { this.celular = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
    public String getConfirmacion() { return confirmacion; }
    public void setConfirmacion(String v) { this.confirmacion = v; }
}
