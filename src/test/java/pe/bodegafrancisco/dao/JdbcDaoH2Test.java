package pe.bodegafrancisco.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import pe.bodegafrancisco.model.Rol;

/** Prueba de integración: el esquema y los DAO funcionan sobre H2. */
class JdbcDaoH2Test {

    private static DriverManagerDataSource ds;
    private static JdbcTemplate jdbc;

    @BeforeAll
    static void crearBase() {
        ds = new DriverManagerDataSource("jdbc:h2:mem:pruebas;DB_CLOSE_DELAY=-1", "sa", "");
        jdbc = new JdbcTemplate(ds);
        ejecutarEsquema();
    }

    private static void ejecutarEsquema() {
        var poblador = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        poblador.setSqlScriptEncoding("UTF-8");
        poblador.execute(ds);
    }

    @Test
    void esquemaEsIdempotenteYCargaProductosIniciales() {
        ejecutarEsquema(); // segundo arranque: no debe duplicar ni fallar
        var productos = new JdbcProductoDao(jdbc).listarTodos();
        assertEquals(6, productos.size());
        assertTrue(productos.get(0).nombre().startsWith("Arroz")); // ordenados por nombre
    }

    @Test
    void usuarioSeGuardaYSeEncuentraPorCorreo() {
        var dao = new JdbcUsuarioDao(jdbc);
        assertFalse(dao.existePorCorreo("ana@correo.com"));
        dao.crear("Ana", "Ruiz", "ana@correo.com", "987654321", "$2a$10$hashdeprueba", Rol.CLIENTE);
        assertTrue(dao.existePorCorreo("ana@correo.com"));
        var u = dao.buscarPorCorreo("ana@correo.com").orElseThrow();
        assertEquals(Rol.CLIENTE, u.rol());
        assertTrue(u.activo());
    }
}
