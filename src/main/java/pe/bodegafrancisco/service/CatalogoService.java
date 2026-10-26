package pe.bodegafrancisco.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.bodegafrancisco.dao.ProductoDao;
import pe.bodegafrancisco.model.Producto;
import pe.bodegafrancisco.model.ResumenInventario;

/**
 * Reglas del catálogo (RF01). Depende de la interfaz ProductoDao (inversión de
 * dependencias).
 */
@Service
public class CatalogoService {

   
    public CatalogoService(ProductoDao productoDao) {
        this.productoDao = productoDao;
    }

    /**
     * @return solo productos activos; un producto inactivo nunca se muestra al
     *         cliente.
     */
    public List<Producto> listarCatalogo() {
        List<Producto> activos = productoDao.listarTodos().stream()
                .filter(Producto::activo)
                .toList();
        log.debug("Catálogo cargado con {} productos activos", activos.size());
        return activos;
    }

    /** @return indicadores del inventario para el panel administrativo. */
    public ResumenInventario resumen() {
        List<Producto> activos = listarCatalogo();
        long bajo = activos.stream().filter(Producto::stockBajo).count();
        long agotados = activos.stream().filter(p -> p.stock() == 0).count();
        return new ResumenInventario(activos.size(), bajo, agotados);
    }

    /** @return productos activos con pocas unidades, para las alertas del panel. */
    public List<Producto> listarStockBajo() {
        return listarCatalogo().stream().filter(Producto::stockBajo).toList();
    }
}
