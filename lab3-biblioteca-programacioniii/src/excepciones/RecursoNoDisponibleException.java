package excepciones;

/**
 * Excepción CHECKED del dominio (extends Exception).
 * Se lanza cuando se intenta prestar un recurso que ya está prestado.
 * El compilador obliga a quien la pueda recibir a atraparla (try-catch) o a declararla (throws).
 */
public class RecursoNoDisponibleException extends Exception {

    private static final long serialVersionUID = 1L;

    // Datos extra con su getter (requisito de la guía)
    private final String tituloRecurso;
    private final String usuarioActual;

    public RecursoNoDisponibleException(String tituloRecurso, String usuarioActual) {
        // super(mensaje): le pasa el texto a Exception, que lo guarda y lo devuelve con getMessage()
        super("El recurso '" + tituloRecurso + "' ya se encuentra prestado a: " + usuarioActual + ".");
        this.tituloRecurso = tituloRecurso;
        this.usuarioActual = usuarioActual;
    }

    public String getTituloRecurso() {
        return tituloRecurso;
    }

    public String getUsuarioActual() {
        return usuarioActual;
    }
}
