package excepciones;

/**
 * Excepción UNCHECKED del dominio (extends RuntimeException).
 * Se lanza cuando se incumple una regla de negocio: título o autor vacío, edición inválida,
 * días de retraso negativos, usuario o recurso inexistente, límite de préstamos alcanzado, etc.
 * Al ser unchecked, el compilador no obliga a declararla con throws.
 */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje); // super(mensaje): el texto queda disponible con getMessage()
    }
}
