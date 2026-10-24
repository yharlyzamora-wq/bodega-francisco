package pe.bodegafrancisco.model;

/** Usuario del sistema. El hash de la contraseña nunca se muestra ni se registra en logs. */
public record Usuario(int id, String nombre, String apellido, String correo, String celular,
                      String passwordHash, Rol rol, boolean activo) {
    @Override
    public String toString() {
        return "Usuario[id=" + id + ", correo=" + correo + ", rol=" + rol + "]";
    }
}
