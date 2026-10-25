package pe.bodegafrancisco.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.bodegafrancisco.model.TipoMovimiento;

/** Implementación JDBC del historial de movimientos. */
@Repository
public class JdbcMovimientoStockDao implements MovimientoStockDao {

    private final JdbcTemplate jdbc;

    public JdbcMovimientoStockDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void registrar(int productoId, Integer usuarioId, TipoMovimiento tipo, int cantidad, String motivo) {
        jdbc.update(
                "INSERT INTO movimiento_stock (producto_id, usuario_id, tipo, cantidad, motivo) VALUES (?, ?, ?, ?, ?)",
                productoId, usuarioId, tipo.name(), cantidad, motivo);
    }
}