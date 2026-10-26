package pe.bodegafrancisco.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.bodegafrancisco.dto.ProductoForm;
import pe.bodegafrancisco.exception.ReglaNegocioException;
import pe.bodegafrancisco.model.Producto;
import pe.bodegafrancisco.model.TipoMovimiento;
import pe.bodegafrancisco.service.CatalogoService;
import pe.bodegafrancisco.service.ProductoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pe.bodegafrancisco.service.ExportacionService;

/**
 * Gestión de productos e inventario. Acceso restringido en SecurityConfig
 * (/admin/**).
 */
@Controller
@RequestMapping("/admin/productos")
public class ProductoAdminController {

    private static final String REDIRIGIR_LISTA = "redirect:/admin/productos";
    private static final String VISTA_FORM = "admin/producto-form";

    private final ProductoService service;
    private final CatalogoService catalogoService;
    private final ExportacionService exportacionService;

    public ProductoAdminController(ProductoService service, CatalogoService catalogoService,
            ExportacionService exportacionService) {
        this.service = service;
        this.catalogoService = catalogoService;
        this.exportacionService = exportacionService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q,
            @RequestParam(required = false) Integer cat, Model model) {
        model.addAttribute("productos", service.listar(q, cat));
        model.addAttribute("todos", service.listar(null, null));
        model.addAttribute("categorias", service.categorias());
        model.addAttribute("tipos", TipoMovimiento.values());
        model.addAttribute("resumen", catalogoService.resumen());
        model.addAttribute("q", q);
        model.addAttribute("cat", cat);
        return "admin/productos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new ProductoForm());
        return formulario(model, null);
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") ProductoForm form, BindingResult resultado,
            Principal principal, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                service.crear(form, principal.getName());
                flash.addFlashAttribute("ok", "Producto creado correctamente");
                return REDIRIGIR_LISTA;
            } catch (ReglaNegocioException e) {
                model.addAttribute("errorNegocio", e.getMessage());
            }
        }
        return formulario(model, null);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model model, RedirectAttributes flash) {
        Producto p = service.buscar(id).orElse(null);
        if (p == null) {
            flash.addFlashAttribute("error", "El producto no existe");
            return REDIRIGIR_LISTA;
        }
        model.addAttribute("form", ProductoForm.desde(p));
        return formulario(model, p);
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable int id, @Valid @ModelAttribute("form") ProductoForm form,
            BindingResult resultado, Model model, RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                service.actualizar(id, form);
                flash.addFlashAttribute("ok", "Producto actualizado correctamente");
                return REDIRIGIR_LISTA;
            } catch (ReglaNegocioException e) {
                model.addAttribute("errorNegocio", e.getMessage());
            }
        }
        return formulario(model, service.buscar(id).orElse(null));
    }

    @PostMapping("/{id}/estado")
    public String estado(@PathVariable int id, @RequestParam boolean activo, RedirectAttributes flash) {
        try {
            service.cambiarEstado(id, activo);
            flash.addFlashAttribute("ok", activo ? "Producto activado" : "Producto desactivado");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return REDIRIGIR_LISTA;
    }

    @PostMapping("/stock")
    public String stock(@RequestParam int productoId, @RequestParam TipoMovimiento tipo,
            @RequestParam(required = false) Integer cantidad,
            @RequestParam(required = false) String motivo,
            Principal principal, RedirectAttributes flash) {
        try {
            service.registrarMovimiento(productoId, tipo, cantidad, motivo, principal.getName());
            flash.addFlashAttribute("ok", "Movimiento registrado correctamente");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return REDIRIGIR_LISTA;
    }

    private String formulario(Model model, Producto producto) {
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", service.categorias());
        return VISTA_FORM;
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"stock-productos.xlsx\"")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(exportacionService.stockProductosXlsx());
    }
}