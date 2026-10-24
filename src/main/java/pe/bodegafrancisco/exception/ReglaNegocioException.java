package pe.bodegafrancisco.exception;

/** Error de regla de negocio con mensaje seguro para mostrar al usuario. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) { super(mensaje); }
}
