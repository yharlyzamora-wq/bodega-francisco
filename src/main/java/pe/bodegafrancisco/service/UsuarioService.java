package pe.bodegafrancisco.service;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.bodegafrancisco.dao.UsuarioDao;
import pe.bodegafrancisco.dto.RegistroForm;
import pe.bodegafrancisco.exception.ReglaNegocioException;
import pe.bodegafrancisco.model.Rol;

/** Reglas de registro de usuarios (RF06). */
@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    private static final Pattern CORREO = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern CELULAR = Pattern.compile("^[0-9]{9}$");

    private final UsuarioDao usuarioDao;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioDao usuarioDao, PasswordEncoder encoder) {
        this.usuarioDao = usuarioDao;
        this.encoder = encoder;
    }

    /** Registra un cliente. Siempre con rol CLIENTE: el rol nunca viene del formulario. */
    public void registrarCliente(RegistroForm f) {
        String correo = normalizarCorreo(f.getCorreo());
        String celular = StringUtils.trimToNull(f.getCelular());
        if (!CORREO.matcher(correo).matches()) throw new ReglaNegocioException("El correo no tiene un formato válido");
        if (celular != null && !CELULAR.matcher(celular).matches()) throw new ReglaNegocioException("El celular debe tener 9 dígitos");
        if (!StringUtils.equals(f.getPassword(), f.getConfirmacion())) throw new ReglaNegocioException("Las contraseñas no coinciden");
        validarPassword(f.getPassword());
        if (usuarioDao.existePorCorreo(correo)) throw new ReglaNegocioException("Ya existe una cuenta con ese correo");

        usuarioDao.crear(StringUtils.trim(f.getNombre()), StringUtils.trim(f.getApellido()), correo, celular,
                encoder.encode(f.getPassword()), Rol.CLIENTE);
        log.info("Cliente registrado: {}", correo);
    }

    /** Crea una cuenta con rol dado (uso interno: administrador inicial). */
    public void crearCuentaInterna(String nombre, String correo, String password, Rol rol) {
        String c = normalizarCorreo(correo);
        validarPassword(password);
        if (!usuarioDao.existePorCorreo(c)) {
            usuarioDao.crear(nombre, "-", c, null, encoder.encode(password), rol);
            log.info("Cuenta {} creada: {}", rol, c);
        }
    }

    public static String normalizarCorreo(String correo) {
        return StringUtils.lowerCase(StringUtils.trimToEmpty(correo));
    }

    static void validarPassword(String p) {
        if (p == null || p.length() < 8 || !p.chars().anyMatch(Character::isLetter) || !p.chars().anyMatch(Character::isDigit)) {
            throw new ReglaNegocioException("La contraseña debe tener al menos 8 caracteres, con letras y números");
        }
    }
}
