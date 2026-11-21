package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Vista (MVC): ventana principal en Swing. No contiene lógica de negocio ni escucha eventos:
 * el controlador se conecta a los botones y la vista solo muestra datos y diálogos.
 * Al extender JFrame hereda setSize, setVisible, setLayout, add, etc.
 */
public final class VentanaBiblioteca extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Font FUENTE = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FUENTE_NEGRITA = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FUENTE_AREA = new Font("Monospaced", Font.PLAIN, 11);

    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[] {"Libro", "Revista"});
    private final JTextField txtUsuario = new JTextField(); // el usuario se escribe (carnet o nombre)
    private final JTextField txtTitulo = new JTextField();
    private final JTextField txtDatoExtra = new JTextField();
    private final JTextField txtDiasRetraso = new JTextField();
    private final JLabel lblDatoExtra = new JLabel("Autor:");

    private final JButton btnRegistrar = new JButton("Registrar Recurso");
    private final JButton btnPrestar = new JButton("Prestar Recurso");
    private final JButton btnDevolver = new JButton("Devolver Recurso");
    private final JButton btnMulta = new JButton("Calcular Multa");
    private final JButton btnVerCatalogo = new JButton("Ver Catálogo");
    private final JButton btnVerUsuarios = new JButton("Ver Usuarios");
    private final JButton btnAgregarUsuario = new JButton("Agregar Usuario");
    private final JButton btnReporte = new JButton("Generar Reporte en Hilo");
    private final JButton btnSimular = new JButton("Simular Préstamo Concurrente (Synchronized)");

    // El catálogo y los usuarios se ven en ventanas aparte (se abren con sus botones)
    private final VentanaLista ventanaCatalogo = new VentanaLista("Catálogo de recursos");
    private final VentanaLista ventanaUsuarios = new VentanaLista("Usuarios y límite de préstamos");
    private final JTextArea areaBitacora = new JTextArea();
    private final JProgressBar barra = new JProgressBar(0, 100);
    private final JLabel lblEstado = new JLabel("Listo.");

    public VentanaBiblioteca() {
        super("Sistema de Gestión de Biblioteca - Programación III - Laboratorio 3");
        setSize(900, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // BorderLayout: norte, centro y sur

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelSur(), BorderLayout.SOUTH);

        // Evento propio de la vista: cambia la etiqueta del dato extra según el tipo elegido
        cmbTipo.addActionListener(e -> actualizarDatoExtra());
        actualizarDatoExtra();
    }

    // ------------------------------------------------------------------ construcción de la interfaz

    private JLabel crearEncabezado() {
        JLabel encabezado = new JLabel("Sistema de Gestión de Biblioteca  |  Laboratorio III", SwingConstants.CENTER);
        encabezado.setOpaque(true);
        encabezado.setBackground(Color.decode("#0B3D6E"));
        encabezado.setForeground(Color.WHITE);
        encabezado.setFont(new Font("SansSerif", Font.BOLD, 20));
        encabezado.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
        return encabezado;
    }

    private JPanel crearPanelCentral() {
        JPanel centro = new JPanel(new GridLayout(1, 2, 12, 0)); // GridLayout: 1 fila x 2 columnas
        centro.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        centro.add(crearPanelFormulario());
        centro.add(crearAreaConScroll(areaBitacora, "Bitácora (RegistradorOperaciones - AutoCloseable)"));
        return centro;
    }

    private JPanel crearPanelFormulario() {
        // Formulario ordenado: 5 filas x 2 columnas (etiqueta + campo)
        JPanel formulario = new JPanel(new GridLayout(5, 2, 8, 8));
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Datos del recurso y del usuario"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        formulario.add(crearEtiqueta("Tipo de recurso:"));
        formulario.add(cmbTipo);
        formulario.add(crearEtiqueta("Título:"));
        formulario.add(txtTitulo);
        lblDatoExtra.setFont(FUENTE);
        formulario.add(lblDatoExtra);
        formulario.add(txtDatoExtra);
        formulario.add(crearEtiqueta("Usuario (carnet o nombre):"));
        formulario.add(txtUsuario);
        formulario.add(crearEtiqueta("Días de retraso:"));
        formulario.add(txtDiasRetraso);
        cmbTipo.setFont(FUENTE);
        txtUsuario.setFont(FUENTE);
        txtTitulo.setFont(FUENTE);
        txtDatoExtra.setFont(FUENTE);
        txtDiasRetraso.setFont(FUENTE);

        // Seis botones en rejilla de 3 filas x 2 columnas; "Agregar Usuario" ocupa todo el ancho abajo
        JPanel botones = new JPanel(new GridLayout(3, 2, 8, 8));
        for (JButton boton : new JButton[] {btnRegistrar, btnPrestar, btnDevolver, btnMulta,
                btnVerCatalogo, btnVerUsuarios}) {
            botones.add(boton);
        }
        for (JButton boton : new JButton[] {btnRegistrar, btnPrestar, btnDevolver, btnMulta,
                btnVerCatalogo, btnVerUsuarios, btnAgregarUsuario}) {
            boton.setFont(FUENTE_NEGRITA);
            boton.setPreferredSize(new Dimension(150, 36));
        }
        JPanel pila = new JPanel(new BorderLayout(0, 8));
        pila.add(botones, BorderLayout.NORTH);
        pila.add(btnAgregarUsuario, BorderLayout.CENTER);
        JPanel contenedorBotones = new JPanel(new BorderLayout());
        contenedorBotones.add(pila, BorderLayout.NORTH);

        JPanel izquierda = new JPanel(new BorderLayout(0, 10));
        izquierda.add(formulario, BorderLayout.NORTH);
        izquierda.add(contenedorBotones, BorderLayout.CENTER);
        return izquierda;
    }

    private JScrollPane crearAreaConScroll(JTextArea area, String titulo) {
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(FUENTE_AREA);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createTitledBorder(titulo));
        return scroll;
    }

    private JPanel crearPanelSur() {
        // Acciones concurrentes con FlowLayout + barra de progreso
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnReporte.setFont(FUENTE_NEGRITA);
        btnSimular.setFont(FUENTE_NEGRITA);
        barra.setStringPainted(true);
        barra.setPreferredSize(new Dimension(150, 24));
        barra.setForeground(new Color(0x1F7A4A));
        acciones.add(btnReporte);
        acciones.add(btnSimular);
        acciones.add(barra);

        lblEstado.setFont(FUENTE);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(0, 14, 8, 14));

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(acciones, BorderLayout.CENTER);
        sur.add(lblEstado, BorderLayout.SOUTH);
        return sur;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE);
        return etiqueta;
    }

    private void actualizarDatoExtra() {
        String tipo = getTipoSeleccionado();
        if (tipo.equals("Libro")) {
            lblDatoExtra.setText("Autor:");
        } else {
            lblDatoExtra.setText("Edición (número):");
        }
    }

    // ------------------------------------------------------------------ lectura de datos (para el controlador)

    public String getTipoSeleccionado() {
        return String.valueOf(cmbTipo.getSelectedItem());
    }

    public String getTitulo() {
        return txtTitulo.getText();
    }

    public String getDatoExtra() {
        return txtDatoExtra.getText();
    }

    public String getDiasRetraso() {
        return txtDiasRetraso.getText();
    }

    /** Lo que el usuario escribió (carnet o nombre); el modelo verifica si existe. */
    public String getUsuario() {
        return txtUsuario.getText();
    }

    /**
     * Diálogo con un formulario para agregar un usuario (JOptionPane con un JPanel adentro).
     * Devuelve {tipo, nombre, carnet, carrera/facultad} o null si el usuario cancela.
     */
    public String[] pedirDatosUsuario() {
        JComboBox<String> tipo = new JComboBox<>(new String[] {"Estudiante", "Docente"});
        JTextField nombre = new JTextField(16);
        JTextField carnet = new JTextField();
        JTextField extra = new JTextField();
        JLabel lblExtra = crearEtiqueta("Carrera:");
        tipo.addActionListener(e -> lblExtra.setText(
                String.valueOf(tipo.getSelectedItem()).equals("Estudiante") ? "Carrera:" : "Facultad:"));

        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        panel.add(crearEtiqueta("Tipo de usuario:"));
        panel.add(tipo);
        panel.add(crearEtiqueta("Nombre:"));
        panel.add(nombre);
        panel.add(crearEtiqueta("Carnet / código:"));
        panel.add(carnet);
        panel.add(lblExtra);
        panel.add(extra);

        int opcion = JOptionPane.showConfirmDialog(this, panel, "Agregar usuario",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }
        return new String[] {String.valueOf(tipo.getSelectedItem()), nombre.getText(), carnet.getText(),
                extra.getText()};
    }

    public JButton getBtnRegistrar() {
        return btnRegistrar;
    }

    public JButton getBtnPrestar() {
        return btnPrestar;
    }

    public JButton getBtnDevolver() {
        return btnDevolver;
    }

    public JButton getBtnMulta() {
        return btnMulta;
    }

    public JButton getBtnVerCatalogo() {
        return btnVerCatalogo;
    }

    public JButton getBtnVerUsuarios() {
        return btnVerUsuarios;
    }

    public JButton getBtnAgregarUsuario() {
        return btnAgregarUsuario;
    }

    public JButton getBtnReporte() {
        return btnReporte;
    }

    public JButton getBtnSimular() {
        return btnSimular;
    }

    // ------------------------------------------------------------------ actualización de la pantalla

    public void setCatalogo(String texto) {
        ventanaCatalogo.setTexto(texto);
    }

    public void setUsuarios(String texto) {
        ventanaUsuarios.setTexto(texto);
    }

    public void mostrarCatalogo() {
        ventanaCatalogo.mostrar(this, 440, 90);
    }

    public void mostrarUsuarios() {
        ventanaUsuarios.mostrar(this, 440, 370);
    }

    public void setBitacora(String texto) {
        areaBitacora.setText(texto);
        areaBitacora.setCaretPosition(areaBitacora.getDocument().getLength());
    }

    public void setProgreso(int porcentaje) {
        barra.setValue(porcentaje);
    }

    public void setEstado(String texto) {
        lblEstado.setText(texto);
    }

    public void setBotonReporteHabilitado(boolean habilitado) {
        btnReporte.setEnabled(habilitado);
    }

    public void setBotonSimularHabilitado(boolean habilitado) {
        btnSimular.setEnabled(habilitado);
    }

    /** Limpia los campos de texto y devuelve el foco al primero (se usa en el finally del controlador). */
    public void limpiarCampos() {
        txtTitulo.setText("");
        txtDatoExtra.setText("");
        txtDiasRetraso.setText("");
        txtTitulo.requestFocus();
    }

    // ------------------------------------------------------------------ diálogos (el padre es esta ventana)
    // "this" como primer parámetro: el diálogo se centra sobre la ventana y la bloquea mientras está abierto.

    public void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarAdvertencia(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }

    public void mostrarInformacion(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
}
