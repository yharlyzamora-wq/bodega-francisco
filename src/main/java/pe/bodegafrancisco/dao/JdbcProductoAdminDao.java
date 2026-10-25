package pe.bodegafrancisco.dao;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.bodegafrancisco.model.Producto;

/** Implementación JDBC de la gestión de productos. */
@Repository
public class JdbcProductoAdminDao implements ProductoAdminDao {

    private final JdbcTemplate jdbc;

    public JdbcProductoAdminDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Producto> buscarPorId(int id) {
        return jdbc.query(JdbcProductoDao.SELECT + " WHERE p.id = ?", JdbcProductoDao.MAPPER, id)
                .stream().findFirst();
    }

    @Override
    public String siguienteCodigo() {
        Integer n = jdbc.queryForObject("SELECT COALESCE(MAX(id), 0) + 1 FROM producto", Integer.class);
        return String.format("PE-%04d", n == null ? 1 : n);
    }

    @Override
    public int crear(String codigo, String nombre, int categoriaId, BigDecimal precio, int stock) {
        jdbc.update(
                "INSERT INTO producto (codigo, nombre, categoria_id, precio, stock, activo) VALUES (?, ?, ?, ?, ?, TRUE)",
                codigo, nombre, categoriaId, precio, stock);
        Integer id = jdbc.queryForObject("SELECT id FROM producto WHERE codigo = ?", Integer.class, codigo);
        return id == null ? 0 : id;
    }

    @Override
    public void actualizar(int id, String nombre, int categoriaId, BigDecimal precio) {
        jdbc.update("UPDATE producto SET nombre = ?, categoria_id = ?, precio = ? WHERE id = ?",
                nombre, categoriaId, precio, id);
    }

    @Override
    public void cambiarEstado(int id, boolean activo) {
        jdbc.update("UPDATE producto SET activo = ? WHERE id = ?", activo, id);
    }

    @Override
    public boolean aplicarDelta(int id, int delta) {
        return jdbc.update("UPDATE producto SET stock = stock + ? WHERE id = ? AND stock + ? >= 0",
                delta, id, delta) > 0;
    }
}