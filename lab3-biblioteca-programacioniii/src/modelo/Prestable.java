package modelo;

import excepciones.ReglaNegocioException;

/** Contrato para los recursos que pueden salir de la biblioteca (interfaz propia del Laboratorio II). */
public interface Prestable {

    /** Multa base por día de retraso (en una interfaz es public static final implícito). */
    int MULTA_DIARIA = 1;

    String getTitulo();

    boolean estaDisponible();

    boolean prestar();

    void devolver();

    int getDiasPrestamo();

    /** Laboratorio III: multa por defecto. Revista la SOBRESCRIBE para cobrar el doble. */
    default int multa(int diasRetraso) {
        validarDiasRetraso(diasRetraso);
        return diasRetraso * MULTA_DIARIA;
    }

    /** Laboratorio III: regla de negocio compartida (throw de una excepción unchecked propia). */
    static void validarDiasRetraso(int diasRetraso) {
        if (diasRetraso < 0) {
            throw new ReglaNegocioException("Los días de retraso no pueden ser negativos: " + diasRetraso);
        }
    }
}
