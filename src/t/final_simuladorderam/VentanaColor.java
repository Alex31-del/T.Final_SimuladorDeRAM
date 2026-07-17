/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t.final_simuladorderam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VentanaColor extends JDialog implements ActionListener{

    private JColorChooser selector;
    private JButton btnAceptar;
    private Color colorSeleccionado;
    
    public VentanaColor(JFrame padre){
        
        super(padre, "Seleccionar Color", true);
        
        selector = new JColorChooser();
        
        btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(this);
        
        add(selector,BorderLayout.CENTER);
        add(btnAceptar,BorderLayout.SOUTH);
        
        
        setSize(800,400);
        setLocationRelativeTo(padre);
        setTitle("Seleccionar Color");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }
    
    public void actionPerformed(ActionEvent e){
         colorSeleccionado = selector.getColor();
         dispose();

    }
    
    public Color getColorSeleccionado(){
        return colorSeleccionado;
    }
}
