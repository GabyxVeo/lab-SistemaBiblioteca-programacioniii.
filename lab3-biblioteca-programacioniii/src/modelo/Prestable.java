package modelo;

import excepciones.ReglaNegocioException;


public interface Prestable {


    int MULTA_DIARIA = 1;

    String getTitulo();

    boolean estaDisponible();

    boolean prestar();

    void devolver();

    int getDiasPrestamo();


    default int multa(int diasRetraso) {
        validarDiasRetraso(diasRetraso);
        return diasRetraso * MULTA_DIARIA;
    }


    static void validarDiasRetraso(int diasRetraso) {
        if (diasRetraso < 0) {
            throw new ReglaNegocioException("Los días de retraso no pueden ser negativos: " + diasRetraso);
        }
    }
}
