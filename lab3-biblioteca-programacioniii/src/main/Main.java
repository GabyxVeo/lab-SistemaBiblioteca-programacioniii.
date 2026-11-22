package main;

import controlador.ControladorBiblioteca;
import modelo.BibliotecaRepositorio;
import vista.VentanaBiblioteca;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            BibliotecaRepositorio modelo = new BibliotecaRepositorio();
            modelo.cargarDatosIniciales();
            VentanaBiblioteca vista = new VentanaBiblioteca();
            ControladorBiblioteca controlador = new ControladorBiblioteca(modelo, vista);
            controlador.iniciar();
        });
    }
}