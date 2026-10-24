package pe.bodegafrancisco.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.bodegafrancisco.model.Producto;

/** Implementación JDBC del DAO. Usa consultas parametrizadas (previene inyección SQL). */
@Repository
public class JdbcProductoDao implements ProductoDao {

    private static final String SQL = """
            SELECT p.id, p.codigo, p.nombre, c.nombre AS categoria, p.precio, p.stock, p.activo
            FROM producto p JOIN categoria c ON c.id = p.categoria_id
            ORDER BY p.nombre
            """;

    private final JdbcTemplate jdbc;

    public JdbcProductoDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Producto> listarTodos() {
        return jdbc.query(SQL, (rs, i) -> new Producto(
                rs.getInt("id"), rs.getString("codigo"), rs.getString("nombre"),
                rs.getString("categoria"), rs.getBigDecimal("precio"),
                rs.getInt("stock"), rs.getBoolean("activo")));
    }
}
