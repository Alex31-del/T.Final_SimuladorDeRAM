package t.final_simuladorderam;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;


public class PanelProcesos extends JPanel implements ActionListener{
    
   private JLabel lNombre, lMemoria,  lAlgoritmo;
   private JTextField tNombre, tMemoria;
   private JButton bCrear, bEliminar, bColor;
   private JComboBox<String> cAlgoritmo;
   private JTable tProceso;
   private DefaultTableModel modelo;
   private MemoriaRAM memoria;
   private PanelRam panelRam;
   private Color colorProceso = Color.CYAN;

    public PanelProcesos(MemoriaRAM memoria, PanelRam panelRam) {
        
        this.memoria = memoria;
        this.panelRam = panelRam;
        setLayout(new BorderLayout());
        
        JPanel datos = new JPanel(new GridLayout(5,3));
        lNombre = new JLabel("Nombre:");
        tNombre  = new JTextField();
        
        lMemoria = new JLabel("Memoria (MB):");
        tMemoria = new JTextField();
        
        lAlgoritmo = new JLabel("Algoritmo:");
        
        cAlgoritmo =new JComboBox<>();
        cAlgoritmo.addItem("First Fit");
        cAlgoritmo.addItem("Best Fit");
        cAlgoritmo.addItem("Worst Fit");
        
        bCrear = new JButton("Crear");
        bCrear.addActionListener(this);
        bEliminar = new JButton("Eliminar");
        bEliminar.addActionListener(this);
        bColor = new JButton("Elegir color");
        bColor.addActionListener(this);

        
        datos.add(lNombre);
        datos.add(tNombre);
        
        datos.add(lMemoria);
        datos.add(tMemoria);
        
        datos.add(lAlgoritmo);
        datos.add(cAlgoritmo);
        
        datos.add(bCrear);
        datos.add(bEliminar);
        datos.add(bColor);
       
        add(datos, BorderLayout.NORTH);
        modelo  = new DefaultTableModel();
        modelo.addColumn("Proceso");
        modelo.addColumn("Memoria");
        modelo.addColumn("Estado");
        
        tProceso = new JTable(modelo);
        
        add(new JScrollPane(tProceso), BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       if(e.getSource()==bCrear){
           crearProceso();
           
       }
       if(e.getSource()==bEliminar){
           eliminarProceso();
       }
       
        if(e.getSource()==bColor){

          VentanaColor vc = new VentanaColor(
            (JFrame) SwingUtilities.getWindowAncestor(this));

         vc.setVisible(true);

            if(vc.getColorSeleccionado()!=null){

             colorProceso = vc.getColorSeleccionado();

            }
        }
    }
   
        private void crearProceso() {

            try {

               String nombre = tNombre.getText().trim();
              int memoriaProceso = Integer.parseInt(tMemoria.getText());

                 if(nombre.isEmpty()){
                         JOptionPane.showMessageDialog(this, "Ingrese el nombre del proceso");
                    return;
                 }

                Proceso p = new Proceso(nombre, memoriaProceso, colorProceso);

                String algoritmo = cAlgoritmo.getSelectedItem().toString();

                switch (algoritmo) {

                    case "First Fit":
                        memoria.firstFit(p);
                    break;

                     case "Best Fit":
                        memoria.bestFit(p);
                    break;

                    case "Worst Fit":
                        memoria.worstFit(p);
                    break;
                }

                actualizarTabla();
                limpiarCampos();

            } catch (NumberFormatException e) {

                 JOptionPane.showMessageDialog(this,"La memoria debe ser un número.");
            }  
            
            actualizarTabla();
            limpiarCampos();
            System.out.println("Llamando a repaint...");
            panelRam.repaint();
        }
        
        private void eliminarProceso(){
            int fila = tProceso.getSelectedRow();
            
            if(fila==-1){
                JOptionPane.showMessageDialog(this, "Seleccione un proceso.");
                return;
                
            }
            
            String nombre = modelo.getValueAt(fila, 0).toString();
            memoria.liberarProceso(nombre);
            actualizarTabla();
            panelRam.repaint();
        }

        private void actualizarTabla(){
             modelo.setRowCount(0);
            for(BloqueMemoria b: memoria.getBloques()){
                 if(!b.isOcupado()) continue;          
        
                    Proceso p= b.getProceso();
                    modelo.addRow(new Object[]{
                    p.getNombre(),
                    p.getMemoria()+" MB",
                     "Activo"
                    });
            }       
        }

        private void limpiarCampos(){
            tNombre.setText("");
            tMemoria.setText("");
            
            tNombre.requestFocus();
            
            
        }
   
}