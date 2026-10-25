package pe.bodegafrancisco.model;

/** Tipos de movimiento de inventario. */
public enum TipoMovimiento {
    ENTRADA("Entrada (compra)"),
    SALIDA("Salida (merma o retiro)"),
    AJUSTE("Ajuste (stock real contado)");

    private final String etiqueta;

    TipoMovimiento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String etiqueta() {
        return etiqueta;
    }
}