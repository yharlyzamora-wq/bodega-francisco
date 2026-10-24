package pe.bodegafrancisco.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import pe.bodegafrancisco.model.Producto;

class CatalogoResumenTest {
    private static Producto p(int id, int stock, boolean activo) {
        return new Producto(id, "X" + id, "N" + id, "C", BigDecimal.ONE, stock, activo);
    }

    @Test
    void cuentaActivosStockBajoYAgotados() {
        var s = new CatalogoService(() -> List.of(p(1, 20, true), p(2, 3, true), p(3, 0, true), p(4, 9, false)));
        var r = s.resumen();
        assertEquals(3, r.activos());
        assertEquals(1, r.stockBajo());
        assertEquals(1, r.agotados());
    }
}
