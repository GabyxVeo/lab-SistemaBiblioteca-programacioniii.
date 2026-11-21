package modelo;

import excepciones.ReglaNegocioException;


public class Recurso {
    protected String titulo;
    protected boolean disponible;

    public Recurso(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            throw new ReglaNegocioException("El título del recurso no puede estar vacío.");
        }
        this.titulo = titulo.trim();
        this.disponible = true;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean estaDisponible() {
        return disponible;
    }

    public String getEstado() {
        if (disponible) {
            return "Disponible";
        } else {
            return "Prestado";
        }
    }


    @Override
    public String toString() {
        return titulo + " | " + getEstado();
    }
}
