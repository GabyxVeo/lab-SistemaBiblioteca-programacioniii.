package main;

import controlador.ControladorBiblioteca;
import modelo.BibliotecaRepositorio;
import vista.VentanaBiblioteca;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Swing debe crearse en el EDT (hilo de eventos), por eso se usa invokeLater
        SwingUtilities.invokeLater(() -> {
            BibliotecaRepositorio modelo = new BibliotecaRepositorio();
            modelo.cargarDatosIniciales();
            VentanaBiblioteca vista = new VentanaBiblioteca();
            ControladorBiblioteca controlador = new ControladorBiblioteca(modelo, vista);
            controlador.iniciar();
        });
    }
}
