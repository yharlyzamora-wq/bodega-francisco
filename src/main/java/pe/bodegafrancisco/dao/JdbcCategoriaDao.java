package pe.bodegafrancisco.dao;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.bodegafrancisco.model.Categoria;

/** Implementación JDBC de categorías. */
@Repository
public class JdbcCategoriaDao implements CategoriaDao {

    private final JdbcTemplate jdbc;

    public JdbcCategoriaDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Categoria> listar() {
        return jdbc.query("SELECT id, nombre FROM categoria ORDER BY nombre",
                (rs, i) -> new Categoria(rs.getInt("id"), rs.getString("nombre")));
    }
}