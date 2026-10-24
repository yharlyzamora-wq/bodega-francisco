package pe.bodegafrancisco.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LoginAttemptServiceTest {

    @Test
    void bloqueaTrasCincoFallosYSeLiberaConExito() {
        var s = new LoginAttemptService();
        for (int i = 0; i < 4; i++) s.registrarFallo("a@b.com");
        assertFalse(s.estaBloqueado("a@b.com"));
        s.registrarFallo("a@b.com");
        assertTrue(s.estaBloqueado("a@b.com"));
        s.registrarExito("a@b.com");
        assertFalse(s.estaBloqueado("a@b.com"));
    }
}
