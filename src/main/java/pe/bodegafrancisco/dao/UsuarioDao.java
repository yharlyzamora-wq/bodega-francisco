package pe.bodegafrancisco.dao;

import java.util.Optional;
import pe.bodegafrancisco.model.Rol;
import pe.bodegafrancisco.model.Usuario;

/** Contrato de persistencia de usuarios (DAO). */
public interface UsuarioDao {
    Optional<Usuario> buscarPorCorreo(String correo);
    boolean existePorCorreo(String correo);
    void crear(String nombre, String apellido, String correo, String celular, String passwordHash, Rol rol);
}
