/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t.final_simuladorderam;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class EstadoRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setText("  " + value.toString());       
        setIcon(crearPuntoVerde());              
        setHorizontalAlignment(SwingConstants.LEFT);

        return this;
    }

    // Dibuja un círculo verde de 10x10 como ícono
    private Icon crearPuntoVerde() {
        int tam = 10;
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(76, 175, 80));
                g2.fillOval(x, y + 3, tam, tam);
            }
            @Override
            public int getIconWidth() { return tam; }
            @Override
            public int getIconHeight() { return tam; }
        };
    }
}