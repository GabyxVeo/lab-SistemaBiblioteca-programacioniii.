package modelo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class RegistradorOperaciones implements AutoCloseable {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final String operacion;
    private final List<String> destino;
    private boolean cerrado = false;
    private boolean exitosa = false;
    private int entradas = 0;

    public RegistradorOperaciones(String operacion, List<String> destino) {
        this.operacion = operacion;
        this.destino = destino;
        escribir("Recurso abierto (inicio de la operación)");
    }

    public void registrar(String mensaje) {
        if (cerrado) {
            throw new IllegalStateException("El registrador de '" + operacion + "' ya fue cerrado.");
        }
        entradas++;
        escribir(mensaje);
    }


    public void marcarExitosa() {
        exitosa = true;
    }

    public boolean estaCerrado() {
        return cerrado;
    }

    @Override
    public void close() {
        if (cerrado) {
            return;
        }
        cerrado = true;
        escribir("Recurso cerrado -> " + (exitosa ? "operación EXITOSA" : "operación CON ERROR")
                + " (" + entradas + " entradas)");
    }

    private void escribir(String mensaje) {
        destino.add("[" + LocalTime.now().format(HORA) + "] [" + operacion + "] " + mensaje);
    }
}
