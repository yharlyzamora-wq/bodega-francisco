package pe.bodegafrancisco.security;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;
import pe.bodegafrancisco.service.UsuarioService;

/** Limita intentos fallidos de login (fuerza bruta) usando una caché Guava que expira sola. */
@Service
public class LoginAttemptService {

    static final int MAX_INTENTOS = 5;
    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);
    private final Cache<String, Integer> intentos;

    public LoginAttemptService() { this(15); }

    LoginAttemptService(int minutosBloqueo) {
        this.intentos = CacheBuilder.newBuilder().expireAfterWrite(minutosBloqueo, TimeUnit.MINUTES).build();
    }

    public void registrarFallo(String correo) {
        Integer n = intentos.getIfPresent(correo);
        intentos.put(correo, (n == null ? 0 : n) + 1);
        log.warn("Intento de login fallido ({} de {})", intentos.getIfPresent(correo), MAX_INTENTOS);
    }

    public void registrarExito(String correo) { intentos.invalidate(correo); }

    public boolean estaBloqueado(String correo) {
        Integer n = intentos.getIfPresent(correo);
        return n != null && n >= MAX_INTENTOS;
    }

    @EventListener
    void alFallar(AuthenticationFailureBadCredentialsEvent e) {
        registrarFallo(UsuarioService.normalizarCorreo(e.getAuthentication().getName()));
    }

    @EventListener
    void alIngresar(AuthenticationSuccessEvent e) {
        registrarExito(UsuarioService.normalizarCorreo(e.getAuthentication().getName()));
    }
}
