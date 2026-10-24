package pe.bodegafrancisco.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.bodegafrancisco.dao.UsuarioDao;
import pe.bodegafrancisco.service.UsuarioService;

/** Carga el usuario desde la base de datos para Spring Security. */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioDao usuarioDao;
    private final LoginAttemptService intentos;

    public UsuarioDetailsService(UsuarioDao usuarioDao, LoginAttemptService intentos) {
        this.usuarioDao = usuarioDao;
        this.intentos = intentos;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) {
        String c = UsuarioService.normalizarCorreo(correo);
        var u = usuarioDao.buscarPorCorreo(c).orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
        return User.withUsername(u.correo())
                .password(u.passwordHash())
                .roles(u.rol().name())
                .disabled(!u.activo())
                .accountLocked(intentos.estaBloqueado(c))
                .build();
    }
}
