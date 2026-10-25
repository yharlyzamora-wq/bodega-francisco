package pe.bodegafrancisco.dao;

import java.math.BigDecimal;
import java.util.Optional;
import pe.bodegafrancisco.model.Producto;

/** Operaciones de escritura sobre productos (gestión administrativa). */
public interface ProductoAdminDao {
    Optional<Producto> buscarPorId(int id);

    String siguienteCodigo();

    int crear(String codigo, String nombre, int categoriaId, BigDecimal precio, int stock);

    void actualizar(int id, String nombre, int categoriaId, BigDecimal precio);

    void cambiarEstado(int id, boolean activo);

    /**
     * Suma o resta unidades de forma atómica. @return false si el stock quedaría
     * negativo.
     */
    boolean aplicarDelta(int id, int delta);
}