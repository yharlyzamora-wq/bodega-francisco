package pe.bodegafrancisco.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/**
 * Seguridad (RF06): login por formulario, roles validados en el servidor,
 * BCrypt, CSRF activo y bloqueo por intentos fallidos.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var consolaH2 = PathPatternRequestMatcher.withDefaults().matcher("/h2-console/**");
        http
            .authorizeHttpRequests(a -> a
                .requestMatchers(consolaH2).hasRole("ADMIN")
                .requestMatchers("/css/**", "/", "/catalogo", "/login", "/admin/login", "/registro", "/error").permitAll()
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "PERSONAL")
                .anyRequest().authenticated())
            // La consola H2 usa sus propios formularios y marcos: se exceptúa solo esa ruta
            .csrf(c -> c.ignoringRequestMatchers(consolaH2))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .formLogin(f -> f
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("correo")
                .passwordParameter("password")
                .successHandler((req, res, auth) -> {
                    boolean interno = auth.getAuthorities().stream()
                            .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN") || g.getAuthority().equals("ROLE_PERSONAL"));
                    res.sendRedirect(req.getContextPath() + (interno ? "/admin" : "/"));
                })
                .failureHandler((req, res, ex) -> {
                    String base = "admin".equals(req.getParameter("origen")) ? "/admin/login" : "/login";
                    String motivo = ex instanceof LockedException ? "bloqueado" : "";
                    res.sendRedirect(req.getContextPath() + base + "?error" + (motivo.isEmpty() ? "" : "=" + motivo));
                }))
            .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/?salio"))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) ->
                    res.sendRedirect(req.getContextPath() + (req.getRequestURI().startsWith(req.getContextPath() + "/admin") ? "/admin/login" : "/login")))
                .accessDeniedHandler((req, res, ex) -> res.sendRedirect(req.getContextPath() + "/?sin_permiso")));
        return http.build();
    }
}