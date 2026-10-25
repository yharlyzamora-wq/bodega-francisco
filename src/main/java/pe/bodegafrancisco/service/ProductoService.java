package pe.bodegafrancisco.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.bodegafrancisco.dao.CategoriaDao;
import pe.bodegafrancisco.dao.MovimientoStockDao;
import pe.bodegafrancisco.dao.ProductoAdminDao;
import pe.bodegafrancisco.dao.ProductoDao;
import pe.bodegafrancisco.dao.UsuarioDao;
import pe.bodegafrancisco.dto.ProductoForm;
import pe.bodegafrancisco.exception.ReglaNegocioException;
import pe.bodegafrancisco.model.Categoria;
import pe.bodegafrancisco.model.Producto;
import pe.bodegafrancisco.model.TipoMovimiento;
import pe.bodegafrancisco.model.Usuario;

/** Reglas de gestión de productos e inventario (RF07). */
@Service
public class ProductoService {

    static final int STOCK_MAXIMO = 100_000;
    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("9999.99");
    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoDao lectura;
    private final ProductoAdminDao escritura;
    private final CategoriaDao categoriaDao;
    private final MovimientoStockDao movimientoDao;
    private final UsuarioDao usuarioDao;

    public ProductoService(ProductoDao lectura, ProductoAdminDao escritura, CategoriaDao categoriaDao,
            MovimientoStockDao movimientoDao, UsuarioDao usuarioDao) {
        this.lectura = lectura;
        this.escritura = escritura;
        this.categoriaDao = categoriaDao;
        this.movimientoDao = movimientoDao;
        this.usuarioDao = usuarioDao;
    }

    /**
     * Lista todos los productos (activos e inactivos) con filtro opcional por texto
     * y categoría.
     */
    public List<Producto> listar(String texto, Integer categoriaId) {
        String q = StringUtils.trimToEmpty(texto);
        return lectura.listarTodos().stream()
                .filter(p -> categoriaId == null || p.categoriaId() == categoriaId)
                .filter(p -> q.isEmpty() || StringUtils.containsIgnoreCase(p.nombre(), q)
                        || StringUtils.containsIgnoreCase(p.codigo(), q))
                .toList();
    }

    public Optional<Producto> buscar(int id) {
        return escritura.buscarPorId(id);
    }

    public List<Categoria> categorias() {
        return categoriaDao.listar();
    }

    @Transactional
    public void crear(ProductoForm f, String actor) {
        String nombre = validarNombre(f.getNombre());
        int categoriaId = validarCategoria(f.getCategoriaId());
        BigDecimal precio = validarPrecio(f.getPrecio());
        int stock = f.getStock() == null ? 0 : f.getStock();
        if (stock < 0 || stock > STOCK_MAXIMO) {
            throw new ReglaNegocioException("El stock inicial debe estar entre 0 y " + STOCK_MAXIMO);
        }
        String codigo = escritura.siguienteCodigo();
        int id = escritura.crear(codigo, nombre, categoriaId, precio, stock);
        if (stock > 0) {
            movimientoDao.registrar(id, idActor(actor), TipoMovimiento.ENTRADA, stock, "Stock inicial");
        }
        log.info("Producto {} creado por {}", codigo, actor);
    }

    @Transactional
    public void actualizar(int id, ProductoForm f) {
        existente(id);
        escritura.actualizar(id, validarNombre(f.getNombre()), validarCategoria(f.getCategoriaId()),
                validarPrecio(f.getPrecio()));
        log.info("Producto {} actualizado", id);
    }

    @Transactional
    public void cambiarEstado(int id, boolean activo) {
        existente(id);
        escritura.cambiarEstado(id, activo);
        log.info("Producto {} {}", id, activo ? "activado" : "desactivado");
    }

    /** Registra un movimiento de stock. El stock nunca puede quedar negativo. */
    @Transactional
    public void registrarMovimiento(int productoId, TipoMovimiento tipo, Integer cantidad, String motivo,
            String actor) {
        if (tipo == null)
            throw new ReglaNegocioException("Selecciona el tipo de movimiento");
        if (cantidad == null)
            throw new ReglaNegocioException("Ingresa la cantidad");
        if (cantidad > STOCK_MAXIMO)
            throw new ReglaNegocioException("La cantidad máxima es " + STOCK_MAXIMO);
        String nota = StringUtils.trimToNull(motivo);
        if (nota != null && nota.length() > 120)
            throw new ReglaNegocioException("El motivo admite máximo 120 caracteres");
        Producto p = existente(productoId);

        int delta;
        if (tipo == TipoMovimiento.AJUSTE) {
            if (cantidad < 0)
                throw new ReglaNegocioException("El stock real no puede ser negativo");
            delta = cantidad - p.stock();
            if (delta == 0)
                throw new ReglaNegocioException("El ajuste no cambia el stock actual");
        } else {
            if (cantidad <= 0)
                throw new ReglaNegocioException("La cantidad debe ser mayor que cero");
            delta = tipo == TipoMovimiento.ENTRADA ? cantidad : -cantidad;
        }
        if (!escritura.aplicarDelta(productoId, delta)) {
            throw new ReglaNegocioException("Stock insuficiente: solo hay " + p.stock() + " unidades");
        }
        movimientoDao.registrar(productoId, idActor(actor), tipo, delta, nota);
        log.info("Movimiento {} ({}) en producto {}", tipo, delta, productoId);
    }

    private Producto existente(int id) {
        return escritura.buscarPorId(id).orElseThrow(() -> new ReglaNegocioException("El producto no existe"));
    }

    private static String validarNombre(String nombre) {
        String n = StringUtils.normalizeSpace(nombre);
        if (StringUtils.length(n) < 2 || n.length() > 120) {
            throw new ReglaNegocioException("El nombre debe tener entre 2 y 120 caracteres");
        }
        return n;
    }

    private int validarCategoria(Integer id) {
        if (id == null || categoriaDao.listar().stream().noneMatch(c -> c.id() == id)) {
            throw new ReglaNegocioException("Selecciona una categoría válida");
        }
        return id;
    }

    private static BigDecimal validarPrecio(BigDecimal precio) {
        if (precio == null || precio.signum() <= 0 || precio.compareTo(PRECIO_MAXIMO) > 0) {
            throw new ReglaNegocioException("El precio debe estar entre 0.01 y 9999.99");
        }
        return precio.setScale(2, RoundingMode.HALF_UP);
    }

    private Integer idActor(String correo) {
        return usuarioDao.buscarPorCorreo(UsuarioService.normalizarCorreo(correo)).map(Usuario::id).orElse(null);
    }
}