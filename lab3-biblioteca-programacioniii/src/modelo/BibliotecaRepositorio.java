package modelo;

import excepciones.RecursoNoDisponibleException;
import excepciones.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class BibliotecaRepositorio {

    private final ArrayList<Usuario> usuarios = new ArrayList<>();
    private final ArrayList<Prestable> catalogo = new ArrayList<>();
    private final List<String> bitacora = Collections.synchronizedList(new ArrayList<>());
    private volatile int retardoTramiteMs = 0;

    public void cargarDatosIniciales() {

        usuarios.add(new Estudiante("Ana Lopez", "20231234", "Ing. Software"));
        usuarios.add(new Estudiante("Luis Perez", "20231235", "Ing. Software"));
        usuarios.add(new Docente("Marta Diaz", "D-001", "Ingenieria"));


        catalogo.add(new Libro("Java Como Programar", "Deitel"));
        catalogo.add(new Libro("Clean Code", "Robert Martin"));
        catalogo.add(new Revista("Revista Tecnologia", 45));
        catalogo.add(new Revista("Ciencia Hoy", 12));
    }


    public synchronized String registrar(Prestable recurso) {
        if (recurso == null) {
            throw new ReglaNegocioException("No se puede registrar un recurso nulo.");
        }
        for (Prestable existente : catalogo) {
            if (existente.getTitulo().equalsIgnoreCase(recurso.getTitulo())) {
                throw new ReglaNegocioException("Ya existe un recurso con el título '" + recurso.getTitulo() + "'.");
            }
        }
        catalogo.add(recurso);
        return recurso.getClass().getSimpleName() + " registrado: '" + recurso.getTitulo() + "'.";
    }


    public synchronized String registrarUsuario(String tipo, String nombre, String carnet, String extra) {

        String carreraOFacultad = extra == null ? "" : extra.trim();
        Usuario nuevo = tipo.equals("Docente")
                ? new Docente(nombre, carnet, carreraOFacultad)
                : new Estudiante(nombre, carnet, carreraOFacultad);
        if (carreraOFacultad.isEmpty()) {
            throw new ReglaNegocioException("Debe indicar la carrera o la facultad del usuario.");
        }
        for (Usuario existente : usuarios) {
            if (existente.getCarnet().equalsIgnoreCase(nuevo.getCarnet())) {
                throw new ReglaNegocioException("Ya existe un usuario con el carnet '" + nuevo.getCarnet() + "'.");
            }
        }
        usuarios.add(nuevo);
        return nuevo.getClass().getSimpleName() + " registrado: " + nuevo.presentarse() + ".";
    }


    public synchronized String procesarPrestamo(String carnet, String titulo)
            throws RecursoNoDisponibleException {
        Usuario usuario = buscarUsuario(carnet);
        Prestable recurso = buscarRecurso(titulo);

        if (!recurso.estaDisponible()) {

            throw new RecursoNoDisponibleException(recurso.getTitulo(), buscarPoseedor(recurso));
        }

        if (!usuario.puedePrestar()) {
            throw new ReglaNegocioException(usuario.getNombre() + " llegó a su límite de "
                    + usuario.getLimitePrestamos() + " préstamos.");
        }
        simularTramite();
        recurso.prestar();
        usuario.agregarPrestamo(recurso);
        return "Préstamo registrado: '" + recurso.getTitulo() + "' para " + usuario.presentarse()
                + " por " + recurso.getDiasPrestamo() + " días.";
    }

    public synchronized String devolver(String carnet, String titulo) {
        Usuario usuario = buscarUsuario(carnet);
        Prestable recurso = buscarRecurso(titulo);
        if (!usuario.tienePrestado(recurso)) {
            throw new ReglaNegocioException(usuario.getNombre() + " no tiene prestado '"
                    + recurso.getTitulo() + "'.");
        }
        recurso.devolver();
        usuario.quitarPrestamo(recurso);
        return "Devolución registrada: '" + recurso.getTitulo() + "' (" + usuario.getNombre() + ").";
    }

    public synchronized int calcularMulta(String titulo, int diasRetraso) {
        Prestable.validarDiasRetraso(diasRetraso);
        Prestable recurso = buscarRecurso(titulo);

        return recurso.multa(diasRetraso);
    }



    public synchronized List<String> resumenCatalogo() {
        List<String> lineas = new ArrayList<>();
        int numero = 1;
        for (Prestable recurso : catalogo) {

            lineas.add(numero + ". " + recurso + " | " + recurso.getDiasPrestamo() + " dias");
            numero++;
        }
        return lineas;
    }

    public synchronized List<String> resumenUsuarios() {
        List<String> lineas = new ArrayList<>();
        int numero = 1;
        for (Usuario usuario : usuarios) {

            lineas.add(numero + ". " + usuario.presentarse()
                    + " | Préstamos: " + usuario.getCantidadPrestados()
                    + "/" + usuario.getLimitePrestamos());
            numero++;
        }
        return lineas;
    }

    public synchronized String getCarnetUsuario(int indice) {
        if (indice < 0 || indice >= usuarios.size()) {
            throw new ReglaNegocioException("No existe el usuario número " + (indice + 1) + ".");
        }
        return usuarios.get(indice).getCarnet();
    }

    public synchronized String estadisticas() {
        int prestados = 0;
        for (Prestable recurso : catalogo) {
            if (!recurso.estaDisponible()) {
                prestados++;
            }
        }
        return "Recursos: " + catalogo.size() + " | Prestados: " + prestados
                + " | Disponibles: " + (catalogo.size() - prestados)
                + " | Usuarios: " + usuarios.size();
    }

    public synchronized Optional<String> primerTituloDisponible() {
        for (Prestable recurso : catalogo) {
            if (recurso.estaDisponible()) {
                return Optional.of(recurso.getTitulo());
            }
        }
        return Optional.empty();
    }
    private Usuario buscarUsuario(String dato) {
        if (dato == null || dato.isBlank()) {
            throw new ReglaNegocioException("Debe escribir el usuario (carnet o nombre).");
        }
        Usuario porNombre = null;
        for (Usuario usuario : usuarios) {
            if (usuario.getCarnet().equalsIgnoreCase(dato.trim())) {
                return usuario;
            }
            if (porNombre == null && usuario.getNombre().equalsIgnoreCase(dato.trim())) {
                porNombre = usuario;
            }
        }
        if (porNombre != null) {
            return porNombre;
        }
        throw new ReglaNegocioException("El usuario '" + dato.trim() + "' no existe. "
                + "Puede registrarlo con el botón \"Agregar Usuario\".");
    }

    private Prestable buscarRecurso(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new ReglaNegocioException("Debe indicar el título del recurso.");
        }
        for (Prestable recurso : catalogo) {
            if (recurso.getTitulo().equalsIgnoreCase(titulo.trim())) {
                return recurso;
            }
        }
        throw new ReglaNegocioException("No existe ningún recurso con el título '" + titulo.trim() + "'.");
    }

    private String buscarPoseedor(Prestable recurso) {
        for (Usuario usuario : usuarios) {
            if (usuario.tienePrestado(recurso)) {
                return usuario.getNombre();
            }
        }
        return "otro usuario";
    }


    public List<String> getBitacora() {
        return bitacora;
    }

    public List<String> copiaBitacora() {
        synchronized (bitacora) {
            return new ArrayList<>(bitacora);
        }
    }


    public void setRetardoTramiteMs(int milisegundos) {
        this.retardoTramiteMs = milisegundos;
    }

    private void simularTramite() {
        int ms = retardoTramiteMs;
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
