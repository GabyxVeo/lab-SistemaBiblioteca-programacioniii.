package hilos;

import modelo.BibliotecaRepositorio;
import modelo.RegistradorOperaciones;

import java.util.List;
import javax.swing.SwingUtilities;


public class TareaReporteCatalogo implements Runnable {

    private static final int PASOS = 10;
    private static final long PAUSA_MS = 250;

    private final BibliotecaRepositorio repo;
    private final OyenteTarea oyente;

    public TareaReporteCatalogo(BibliotecaRepositorio repo, OyenteTarea oyente) {
        this.repo = repo;
        this.oyente = oyente;
    }

    @Override
    public void run() {
        List<String> catalogo = repo.resumenCatalogo();
        int total = catalogo.size();
        int desde = 0;

        try (RegistradorOperaciones reg = new RegistradorOperaciones("REPORTE", repo.getBitacora())) {
            reg.registrar("Auditoría de " + total + " recursos en el hilo " + Thread.currentThread().getName());
            for (int paso = 1; paso <= PASOS; paso++) {
                Thread.sleep(PAUSA_MS);

                int hasta = total * paso / PASOS;
                for (int i = desde; i < hasta; i++) {
                    reg.registrar("   auditado -> " + catalogo.get(i));
                }
                desde = hasta;

                int porcentaje = paso * 100 / PASOS;
                reg.registrar("Paso " + paso + "/" + PASOS + " (" + porcentaje + "%)");
                String estado = "Generando reporte... " + porcentaje + "%";

                SwingUtilities.invokeLater(() -> oyente.alProgresar(porcentaje, estado));
            }
            reg.marcarExitosa();
            String resumen = "Reporte del catálogo finalizado.\n" + repo.estadisticas();
            SwingUtilities.invokeLater(() -> oyente.alTerminar(resumen));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            SwingUtilities.invokeLater(() -> oyente.alTerminar("El reporte fue cancelado."));
        }
    }
}
