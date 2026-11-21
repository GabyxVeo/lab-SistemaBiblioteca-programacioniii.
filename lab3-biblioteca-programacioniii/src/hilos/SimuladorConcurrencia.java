package hilos;

import excepciones.RecursoNoDisponibleException;
import excepciones.ReglaNegocioException;
import modelo.BibliotecaRepositorio;
import modelo.RegistradorOperaciones;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingUtilities;


public class SimuladorConcurrencia implements Runnable {

    private final BibliotecaRepositorio repo;
    private final String titulo;
    private final String carnetMostrador;
    private final String carnetWeb;
    private final OyenteTarea oyente;

    private final CountDownLatch salida = new CountDownLatch(1);
    private final AtomicInteger exitos = new AtomicInteger();
    private final AtomicInteger rechazos = new AtomicInteger();

    public SimuladorConcurrencia(BibliotecaRepositorio repo, String titulo,
                                 String carnetMostrador, String carnetWeb, OyenteTarea oyente) {
        this.repo = repo;
        this.titulo = titulo;
        this.carnetMostrador = carnetMostrador;
        this.carnetWeb = carnetWeb;
        this.oyente = oyente;
    }

    @Override
    public void run() {
        Thread mostrador = new Thread(() -> intentarPrestar(carnetMostrador), "Hilo-Mostrador-1");
        Thread web = new Thread(() -> intentarPrestar(carnetWeb), "Hilo-Web-2");

        repo.setRetardoTramiteMs(200);
        try (RegistradorOperaciones reg = new RegistradorOperaciones("SIMULACION", repo.getBitacora())) {
            reg.registrar("Dos hilos competirán por '" + titulo + "'");
            avisar(10, "Preparando hilos...");
            mostrador.start();
            web.start();
            salida.countDown();

            Thread.sleep(100);
            reg.registrar("Estados a los 100 ms -> " + mostrador.getName() + ": " + mostrador.getState()
                    + " | " + web.getName() + ": " + web.getState());
            avisar(50, "Los hilos compiten por el candado...");

            mostrador.join();
            web.join();
            reg.registrar("Resultado -> éxitos: " + exitos.get() + ", rechazos: " + rechazos.get());
            reg.marcarExitosa();

            String resumen = "Simulación concurrente finalizada sobre '" + titulo + "'.\n"
                    + "Préstamos exitosos: " + exitos.get() + "\n"
                    + "Rechazados con RecursoNoDisponibleException: " + rechazos.get() + "\n"
                    + "El catálogo quedó consistente (no hubo doble préstamo).";
            SwingUtilities.invokeLater(() -> oyente.alTerminar(resumen));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            SwingUtilities.invokeLater(() -> oyente.alTerminar("La simulación fue interrumpida."));
        } finally {
            repo.setRetardoTramiteMs(0);
        }
    }

    private void intentarPrestar(String carnet) {
        String hilo = Thread.currentThread().getName();
        try (RegistradorOperaciones reg = new RegistradorOperaciones(hilo, repo.getBitacora())) {
            salida.await();
            reg.registrar("Intenta prestar '" + titulo + "' al carnet " + carnet);
            try {
                String mensaje = repo.procesarPrestamo(carnet, titulo);
                exitos.incrementAndGet();
                reg.registrar("ÉXITO -> " + mensaje);
                reg.marcarExitosa();
            } catch (RecursoNoDisponibleException e) {
                rechazos.incrementAndGet();
                reg.registrar("RECHAZADO -> " + e.getMessage());
                reg.marcarExitosa();
            } catch (ReglaNegocioException e) {
                reg.registrar("REGLA DE NEGOCIO -> " + e.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void avisar(int porcentaje, String estado) {
        SwingUtilities.invokeLater(() -> oyente.alProgresar(porcentaje, estado));
    }
}
