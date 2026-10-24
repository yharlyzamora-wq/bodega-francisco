package pe.bodegafrancisco.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pe.bodegafrancisco.model.Rol;
import pe.bodegafrancisco.service.UsuarioService;

/** Crea la cuenta administradora con datos del archivo local (nunca desde el código fuente). */
@Component
public class AdminInicial implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicial.class);
    private final UsuarioService usuarioService;
    private final String correo;
    private final String password;

    public AdminInicial(UsuarioService usuarioService,
                        @Value("${bodega.admin.correo:}") String correo,
                        @Value("${bodega.admin.password:}") String password) {
        this.usuarioService = usuarioService;
        this.correo = correo;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (StringUtils.isAnyBlank(correo, password)) {
            log.warn("No hay bodega.admin.correo/password en application-local.properties: no se creó administrador");
            return;
        }
        usuarioService.crearCuentaInterna("Administrador", correo, password, Rol.ADMIN);
    }
}
