package pe.bodegafrancisco.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pe.bodegafrancisco.dao.UsuarioDao;
import pe.bodegafrancisco.dto.RegistroForm;
import pe.bodegafrancisco.exception.ReglaNegocioException;
import pe.bodegafrancisco.model.Rol;
import pe.bodegafrancisco.model.Usuario;

class UsuarioServiceTest {

    /** DAO en memoria. */
    static class DaoFalso implements UsuarioDao {
        final List<Usuario> datos = new ArrayList<>();
        public Optional<Usuario> buscarPorCorreo(String c) { return datos.stream().filter(u -> u.correo().equals(c)).findFirst(); }
        public boolean existePorCorreo(String c) { return buscarPorCorreo(c).isPresent(); }
        public void crear(String n, String a, String c, String cel, String hash, Rol rol) {
            datos.add(new Usuario(datos.size() + 1, n, a, c, cel, hash, rol, true));
        }
    }

    private DaoFalso dao;
    private UsuarioService service;

    @BeforeEach
    void preparar() {
        dao = new DaoFalso();
        service = new UsuarioService(dao, new BCryptPasswordEncoder());
    }

    private static RegistroForm form(String correo, String pass, String conf) {
        var f = new RegistroForm();
        f.setNombre("Ana"); f.setApellido("Ruiz"); f.setCorreo(correo); f.setPassword(pass); f.setConfirmacion(conf);
        return f;
    }

    @Test
    void registraClienteConPasswordHasheadaYRolCliente() {
        service.registrarCliente(form("  ANA@Correo.com ", "clave1234", "clave1234"));
        Usuario u = dao.datos.get(0);
        assertEquals("ana@correo.com", u.correo());
        assertEquals(Rol.CLIENTE, u.rol());
        assertNotEquals("clave1234", u.passwordHash());
        assertTrue(u.passwordHash().startsWith("$2"));
    }

    @Test
    void rechazaPasswordDebil() {
        assertThrows(ReglaNegocioException.class, () -> service.registrarCliente(form("a@b.com", "abc", "abc")));
        assertThrows(ReglaNegocioException.class, () -> service.registrarCliente(form("a@b.com", "sololetras", "sololetras")));
    }

    @Test
    void rechazaCorreoDuplicadoYConfirmacionDistinta() {
        service.registrarCliente(form("a@b.com", "clave1234", "clave1234"));
        assertThrows(ReglaNegocioException.class, () -> service.registrarCliente(form("A@B.com", "clave1234", "clave1234")));
        assertThrows(ReglaNegocioException.class, () -> service.registrarCliente(form("c@d.com", "clave1234", "otra1234")));
    }
}
