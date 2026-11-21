package excepciones;


public class RecursoNoDisponibleException extends Exception {

    private static final long serialVersionUID = 1L;


    private final String tituloRecurso;
    private final String usuarioActual;

    public RecursoNoDisponibleException(String tituloRecurso, String usuarioActual) {

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
