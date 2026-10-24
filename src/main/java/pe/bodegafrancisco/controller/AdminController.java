package pe.bodegafrancisco.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.bodegafrancisco.service.CatalogoService;

/** Panel administrativo. Acceso restringido a ADMIN y PERSONAL en SecurityConfig. */
@Controller
public class AdminController {

    private final CatalogoService catalogoService;

    public AdminController(CatalogoService catalogoService) { this.catalogoService = catalogoService; }

    @GetMapping("/admin")
    public String panel(Model model) {
        model.addAttribute("resumen", catalogoService.resumen());
        return "admin/panel";
    }
}
