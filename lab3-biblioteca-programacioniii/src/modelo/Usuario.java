package modelo;

import excepciones.ReglaNegocioException;
import java.util.ArrayList;

// CLASE ABSTRACTA (Laboratorio II): "un Usuario genérico" nunca existe solo en la biblioteca,
// siempre es Estudiante o Docente. Por eso no se puede hacer new Usuario(...).
public abstract class Usuario {
    protected String nombre;
    protected String carnet;
    protected ArrayList<Prestable> prestados = new ArrayList<>();

    // Una clase abstracta SÍ tiene constructor: las subclases lo llaman con super(...)
    public Usuario(String nombre, String carnet) {
        // Laboratorio III: reglas de negocio validadas con throw
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("El nombre del usuario no puede estar vacío.");
        }
        if (carnet == null || carnet.isBlank()) {
            throw new ReglaNegocioException("El carnet del usuario no puede estar vacío.");
        }
        this.nombre = nombre.trim();
        this.carnet = carnet.trim();
    }

    // MÉTODO ABSTRACTO: sin cuerpo. Cada subclase está OBLIGADA a implementarlo
    // (el límite de préstamos es lo que genuinamente cambia entre usuarios).
    public abstract int getLimitePrestamos();

    // MÉTODO CONCRETO: igual para todos, se escribe una sola vez aquí.
    public String presentarse() {
        return nombre + " (" + carnet + ")";
    }

    // SOBRECARGA (overloading): mismo nombre, distintos parámetros.
    // Esta versión no recibe nada y reutiliza la otra.
    public boolean puedePrestar() {
        return puedePrestar(1);
    }

    // SOBRECARGA (overloading): versión que recibe la cantidad de recursos que quiere.
    // LIGADURA DINÁMICA: getLimitePrestamos() ejecuta la versión de Estudiante o Docente
    // según el objeto real, aunque aquí solo se sabe que es un Usuario.
    public boolean puedePrestar(int cantidad) {
        return prestados.size() + cantidad <= getLimitePrestamos();
    }

    public void agregarPrestamo(Prestable recurso) {
        prestados.add(recurso);
    }

    public void quitarPrestamo(Prestable recurso) {
        prestados.remove(recurso);
    }

    public boolean tienePrestado(Prestable recurso) {
        return prestados.contains(recurso);
    }

    public int getCantidadPrestados() {
        return prestados.size();
    }

    public String getNombre() {
        return nombre;
    }

    public String getCarnet() {
        return carnet;
    }
}
