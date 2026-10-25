package pe.bodegafrancisco.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import pe.bodegafrancisco.model.Producto;

/** Implementación JDBC de lectura de productos. Consultas parametrizadas. */
@Repository
public class JdbcProductoDao implements ProductoDao {

    static final String SELECT = """
            SELECT p.id, p.codigo, p.nombre, p.categoria_id, c.nombre AS categoria,
                   p.precio, p.stock, p.activo
            FROM producto p JOIN categoria c ON c.id = p.categoria_id
            """;

    static final RowMapper<Producto> MAPPER = (rs, i) -> new Producto(
            rs.getInt("id"), rs.getString("codigo"), rs.getString("nombre"),
            rs.getInt("categoria_id"), rs.getString("categoria"), rs.getBigDecimal("precio"),
            rs.getInt("stock"), rs.getBoolean("activo"));

    private final JdbcTemplate jdbc;

    public JdbcProductoDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Producto> listarTodos() {
        return jdbc.query(SELECT + " ORDER BY p.nombre", MAPPER);
    }
}