package modelo;

import excepciones.ReglaNegocioException;

// HERENCIA: Libro es un Recurso (extends) y además cumple el contrato Prestable (implements).
public class Libro extends Recurso implements Prestable {
    private String autor;

    public Libro(String titulo, String autor) {
        super(titulo); // super(): construye primero la parte heredada (Recurso)
        // Laboratorio III: regla de negocio
        if (autor == null || autor.isBlank()) {
            throw new ReglaNegocioException("El libro '" + this.titulo + "' debe tener autor.");
        }
        this.autor = autor.trim();
    }

    // INTERFAZ: implementación propia de Libro
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

    // INTERFAZ: un libro se presta 14 días (Revista lo hace distinto)
    @Override
    public int getDiasPrestamo() {
        return 14;
    }

    public String getAutor() {
        return autor;
    }

    // SOBRESCRITURA (overriding): reemplaza el toString() de Recurso
    @Override
    public String toString() {
        return "[Libro] " + titulo + " de " + autor + " | " + getEstado();
    }
}
