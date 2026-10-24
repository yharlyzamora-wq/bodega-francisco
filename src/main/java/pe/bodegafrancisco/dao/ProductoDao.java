package pe.bodegafrancisco.dao;

import java.util.List;
import pe.bodegafrancisco.model.Producto;

/** Contrato de persistencia de productos (patrón DAO). El servicio depende de esta interfaz. */
public interface ProductoDao {
    /** @return todos los productos registrados, activos o no. */
    List<Producto> listarTodos();
}
