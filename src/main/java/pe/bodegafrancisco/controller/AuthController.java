package pe.bodegafrancisco.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pe.bodegafrancisco.dto.RegistroForm;
import pe.bodegafrancisco.exception.ReglaNegocioException;
import pe.bodegafrancisco.service.UsuarioService;

/** Login y registro. El procesamiento del login lo hace Spring Security. */
@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) { this.usuarioService = usuarioService; }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("admin", false);
        return "login";
    }

    @GetMapping("/admin/login")
    public String loginAdmin(Model model) {
        model.addAttribute("admin", true);
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("form", new RegistroForm());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("form") RegistroForm form, BindingResult result, Model model) {
        if (result.hasErrors()) return "registro";
        try {
            usuarioService.registrarCliente(form);
        } catch (ReglaNegocioException e) {
            model.addAttribute("errorNegocio", e.getMessage());
            return "registro";
        }
        return "redirect:/login?registrado";
    }
}
