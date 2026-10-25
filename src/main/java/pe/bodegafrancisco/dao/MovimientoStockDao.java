package pe.bodegafrancisco.dao;

import pe.bodegafrancisco.model.TipoMovimiento;

/** Historial de movimientos de inventario. */
public interface MovimientoStockDao {
    /**
     * Registra un movimiento. La cantidad lleva signo: positiva suma, negativa
     * resta.
     */
    void registrar(int productoId, Integer usuarioId, TipoMovimiento tipo, int cantidad, String motivo);
}