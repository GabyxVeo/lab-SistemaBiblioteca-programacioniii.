package modelo;

import excepciones.ReglaNegocioException;

// Clase base de los recursos de la biblioteca (Libro y Revista heredan de aquí).
public class Recurso {
    protected String titulo;
    protected boolean disponible;

    public Recurso(String titulo) {
        // Laboratorio III: regla de negocio validada con throw
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

    // Este toString() es SOBRESCRITO en Libro y Revista.
    @Override
    public String toString() {
        return titulo + " | " + getEstado();
    }
}
