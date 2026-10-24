package pe.bodegafrancisco.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import pe.bodegafrancisco.dao.ProductoDao;
import pe.bodegafrancisco.model.Producto;

/** Criterio de aceptación: un producto inactivo no debe aparecer en el catálogo. */
class CatalogoServiceTest {

    private static Producto p(int id, String nombre, int stock, boolean activo) {
        return new Producto(id, "PE-" + id, nombre, "Bebidas", new BigDecimal("3.50"), stock, activo);
    }

    /** DAO falso en memoria: aísla el servicio de la base de datos. */
    private static ProductoDao daoCon(List<Producto> datos) {
        return () -> datos;
    }

    @Test
    void excluyeProductosInactivos() {
        var service = new CatalogoService(daoCon(List.of(p(1, "Gaseosa", 10, true), p(2, "Detergente", 5, false))));
        var catalogo = service.listarCatalogo();
        assertEquals(1, catalogo.size());
        assertEquals("Gaseosa", catalogo.get(0).nombre());
    }

    @Test
    void marcaStockBajoSoloCuandoQuedanPocasUnidades() {
        assertTrue(p(1, "Helado", 3, true).stockBajo());
        assertFalse(p(2, "Arroz", 18, true).stockBajo());
        assertFalse(p(3, "Agotado", 0, true).disponible());
    }
}
