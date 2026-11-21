package modelo;

import excepciones.ReglaNegocioException;

// HERENCIA: Revista también es un Recurso e implementa la MISMA interfaz que Libro.
public class Revista extends Recurso implements Prestable {
    private int edicion;

    public Revista(String titulo, int edicion) {
        super(titulo); // super(): llama al constructor de Recurso
        // Laboratorio III: regla de negocio
        if (edicion <= 0) {
            throw new ReglaNegocioException("El número de edición debe ser mayor que cero.");
        }
        this.edicion = edicion;
    }

    @Override
    public boolean prestar() {
        if (disponible) {
            disponible = false;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void devolver() {
        disponible = true;
    }

    // INTERFAZ: una revista se presta solo 3 días
    @Override
    public int getDiasPrestamo() {
        return 3;
    }

    // SOBRESCRITURA de un método default de la interfaz: la revista cobra el doble por día
    @Override
    public int multa(int diasRetraso) {
        Prestable.validarDiasRetraso(diasRetraso);
        return diasRetraso * 2;
    }

    public int getEdicion() {
        return edicion;
    }

    // SOBRESCRITURA (overriding): su propia versión de toString()
    @Override
    public String toString() {
        return "[Revista] " + titulo + " (Edicion " + edicion + ") | " + getEstado();
    }
}
