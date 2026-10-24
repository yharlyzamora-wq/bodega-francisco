package pe.bodegafrancisco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.bodegafrancisco.service.CatalogoService;

/** Controlador MVC del catálogo. No contiene lógica de negocio ni SQL. */
@Controller
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping({"/", "/catalogo"})
    public String catalogo(Model model) {
        model.addAttribute("productos", catalogoService.listarCatalogo());
        return "catalogo";
    }
}
