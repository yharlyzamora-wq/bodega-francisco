package pe.bodegafrancisco.dao;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.bodegafrancisco.model.Rol;
import pe.bodegafrancisco.model.Usuario;

/** Implementación JDBC. Todas las consultas son parametrizadas. */
@Repository
public class JdbcUsuarioDao implements UsuarioDao {

    private final JdbcTemplate jdbc;

    public JdbcUsuarioDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return jdbc.query("""
                SELECT id, nombre, apellido, correo, celular, password_hash, rol, activo
                FROM usuario WHERE correo = ?
                """,
                (rs, i) -> new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("apellido"),
                        rs.getString("correo"), rs.getString("celular"), rs.getString("password_hash"),
                        Rol.valueOf(rs.getString("rol")), rs.getBoolean("activo")),
                correo).stream().findFirst();
    }

    @Override
    public boolean existePorCorreo(String correo) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM usuario WHERE correo = ?", Integer.class, correo);
        return n != null && n > 0;
    }

    @Override
    public void crear(String nombre, String apellido, String correo, String celular, String passwordHash, Rol rol) {
        jdbc.update("""
                INSERT INTO usuario (nombre, apellido, correo, celular, password_hash, rol)
                VALUES (?, ?, ?, ?, ?, ?)
                """, nombre, apellido, correo, celular, passwordHash, rol.name());
    }
}
