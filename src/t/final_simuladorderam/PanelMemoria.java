import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

/**
 *
 * @author user
 */
public class PanelMemoria extends JPanel {

    private List<BloqueMemoria> bloques = new ArrayList<>();
    private int tamanoTotal = 1;
    private String bloqueSeleccionado; // nombre de proceso resaltado, o null

    public PanelMemoria() {
        setPreferredSize(new java.awt.Dimension(780, 160));
        setBackground(Color.WHITE);
    }

    public void actualizar(List<BloqueMemoria> bloques, int tamanoTotal) {
        this.bloques = bloques;
        this.tamanoTotal = Math.max(tamanoTotal, 1);
        repaint();
    }

    public void resaltarProceso(String nombreProceso) {
        this.bloqueSeleccionado = nombreProceso;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int margen = 15;
        int alturaBarra = 90;
        int anchoDisponible = getWidth() - margen * 2;
        int x = margen;
        int y = 25;

        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Mapa de memoria (0 KB — " + tamanoTotal + " KB):", margen, 15);

        for (BloqueMemoria b : bloques) {
            int ancho = (int) Math.round((b.getTamano() / (double) tamanoTotal) * anchoDisponible);
            ancho = Math.max(ancho, 3);

            boolean resaltado = b.isOcupado() && bloqueSeleccionado != null
                    && b.getProceso().getNombre().equalsIgnoreCase(bloqueSeleccionado);

            if (b.isOcupado()) {
                g2.setColor(b.getProceso().getColor());
            } else {
                g2.setColor(new Color(235, 235, 235));
            }
            g2.fillRect(x, y, ancho, alturaBarra);

            g2.setColor(resaltado ? Color.RED : Color.DARK_GRAY);
            g2.setStroke(new java.awt.BasicStroke(resaltado ? 3f : 1f));
            g2.drawRect(x, y, ancho, alturaBarra);
            g2.setStroke(new java.awt.BasicStroke(1f));

            String etiqueta = b.isOcupado() ? b.getProceso().getNombre() : "Libre";
            String tam = b.getTamano() + " KB";
            FontMetrics fm = g2.getFontMetrics();
            if (ancho > fm.stringWidth(etiqueta) + 6) {
                g2.setColor(Color.BLACK);
                g2.drawString(etiqueta, x + 4, y + alturaBarra / 2 - 2);
                g2.drawString(tam, x + 4, y + alturaBarra / 2 + 14);
            }
            x += ancho;
        }

        g2.setColor(Color.DARK_GRAY);
        g2.drawString("0", margen, y + alturaBarra + 15);
        g2.drawString(tamanoTotal + " KB", getWidth() - margen - 50, y + alturaBarra + 15);
    }
}

