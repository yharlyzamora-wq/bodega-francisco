package pe.bodegafrancisco.model;

import java.math.BigDecimal;

/** Producto del catálogo. Inmutable. */
public record Producto(int id, String codigo, String nombre, String categoria,
                       BigDecimal precio, int stock, boolean activo) {

    private static final int UMBRAL_STOCK_BAJO = 5;

    public boolean disponible() { return activo && stock > 0; }

    public boolean stockBajo() { return activo && stock > 0 && stock <= UMBRAL_STOCK_BAJO; }
}
