package hilos;

/**
 * Canal por el que las tareas en segundo plano avisan a la interfaz.
 * Las tareas SIEMPRE llaman a estos métodos dentro de SwingUtilities.invokeLater,
 * por lo que se ejecutan en el EDT y pueden tocar componentes Swing con seguridad.
 */
public interface OyenteTarea {

    void alProgresar(int porcentaje, String estado);

    void alTerminar(String resumen);
}
