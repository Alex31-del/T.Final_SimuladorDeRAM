/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t.final_simuladorderam;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
public class PanelRam extends JPanel{
    
    private MemoriaRAM memoria;
    
    private final int MARGEN_X = 40;
    private final int MARGEN_Y = 20;

    private final int ANCHO_RAM = 120;
    private final int ALTO_RAM = 500;
    
    public PanelRam(MemoriaRAM memoria){
        this.memoria=memoria;
        repaint();
        
        setPreferredSize(new Dimension(250,550));
        setBackground(Color.WHITE);
    }
    
    public MemoriaRAM getMemoria(){
        return memoria;
    }
    
    public void setMemoria(MemoriaRAM memoria){
        this.memoria=memoria;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        
        if(memoria == null)
        return;
        
        //Ram gráfica
        g.setColor(Color.BLACK);
        g.drawRect(MARGEN_X, MARGEN_Y, ANCHO_RAM, ALTO_RAM);
         int y = MARGEN_Y;

        for(BloqueMemoria bloque : memoria.getBloques()){

            int altoBloque =(bloque.getTamano()*ALTO_RAM)
                    / memoria.getMemoriaTotal();
                    

            if(bloque.isOcupado()){

                g.setColor(new Color(120,180,255));

            }else{

                g.setColor(Color.LIGHT_GRAY);

            }

            g.fillRect(
                    MARGEN_X,
                    y,
                    ANCHO_RAM,
                    altoBloque
            );

            g.setColor(Color.BLACK);
            g.drawRect(
                    MARGEN_X,
                    y,
                    ANCHO_RAM,
                    altoBloque
            );

            if(bloque.isOcupado()){

                g.drawString(
                        bloque.getProceso().getNombre()
                        +" ("+
                        bloque.getTamano()+" MB)",
                        MARGEN_X+5,
                        y+15
                );

            }else{

                g.drawString(
                        "Libre ("+
                        bloque.getTamano()+" MB)",
                        MARGEN_X+5,
                        y+15
                );

            }

            y += altoBloque;

        
    
        }

    }
}
