package modelo;

import excepciones.ReglaNegocioException;


public class Revista extends Recurso implements Prestable {
    private int edicion;

    public Revista(String titulo, int edicion) {
        super(titulo);
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


    @Override
    public int getDiasPrestamo() {
        return 3;
    }


    @Override
    public int multa(int diasRetraso) {
        Prestable.validarDiasRetraso(diasRetraso);
        return diasRetraso * 2;
    }

    public int getEdicion() {
        return edicion;
    }


    @Override
    public String toString() {
        return "[Revista] " + titulo + " (Edicion " + edicion + ") | " + getEstado();
    }
}
