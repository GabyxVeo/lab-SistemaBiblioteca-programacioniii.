package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Ventana secundaria (JFrame) de solo lectura. Se usa dos veces: una para el catálogo
 * y otra para los usuarios. Como la vista, no tiene lógica: solo muestra el texto que le pasan.
 */
public final class VentanaLista extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JTextArea area = new JTextArea();

    public VentanaLista(String titulo) {
        super(titulo);
        setSize(480, 260);
        // HIDE_ON_CLOSE: al cerrarla solo se oculta; la ventana principal sigue abierta
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setMargin(new Insets(8, 8, 8, 8));
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    public void setTexto(String texto) {
        area.setText(texto);
        area.setCaretPosition(0);
    }

    /** Se abre junto a la ventana principal, desplazada (dx, dy) desde su esquina; así no tapa a la otra lista. */
    public void mostrar(Component padre, int dx, int dy) {
        if (!isVisible()) {
            setLocation(padre.getX() + dx, padre.getY() + dy);
        }
        setVisible(true);
        toFront();
    }
}
