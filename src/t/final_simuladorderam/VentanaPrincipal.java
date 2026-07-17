/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t.final_simuladorderam;
import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private MemoriaRAM  memoria;
    private PanelProcesos pnlProcesos;
    private PanelRam pnlRam;

    public VentanaPrincipal() {
        
        memoria = new MemoriaRAM(1024);
        pnlRam = new PanelRam(memoria);
        pnlProcesos = new PanelProcesos(memoria, pnlRam);

        
        setTitle("🖥 Simulador de Memoria RAM");

        setLayout(new BorderLayout(10,10));
        getContentPane().setBackground(Color.WHITE);
       ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        add(pnlProcesos, BorderLayout.WEST);
        add(pnlRam, BorderLayout.CENTER);
        
        setMinimumSize(new Dimension(700, 500));
        setSize(800,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
