/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t.final_simuladorderam;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.io.*;

public class VentanaPrincipal extends JFrame {
    private GestorMemoria gestor;
    private final PanelMemoria panelMemoria = new PanelMemoria();
 
    private final JTextField campoNombre = new JTextField(10);
    private final JSpinner campoTamano = new JSpinner(new SpinnerNumberModel(64, 1, 100000, 8));
    private final JSpinner campoTiempo = new JSpinner(new SpinnerNumberModel(30, 0, 100000, 5));
    private final JComboBox<String> comboAlgoritmo =new JComboBox<>(new String[]{GestorMemoria.FIRST_FIT, GestorMemoria.BEST_FIT, GestorMemoria.WORST_FIT});
    private final DefaultTableModel modeloTablaProcesos =
            new DefaultTableModel(new Object[]{"ID", "Nombre", "Memoria (KB)", "Estado", "Tiempo (seg)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    private final JTable tablaProcesos = new JTable(modeloTablaProcesos);
    private final DefaultTableModel modeloTablaLibres =
            new DefaultTableModel(new Object[]{"Inicio (KB)", "Tamaño (KB)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    
    private final JTable tablaLibres = new JTable(modeloTablaLibres);
   
 
    private final JLabel etiquetaEstadisticas = new JLabel();
    private final JLabel etiquetaEstadoSim = new JLabel("Simulación detenida.");
    private final JLabel etiquetaAlgoritmoActual = new JLabel("Algoritmo actual: First Fit");
    private Thread hiloSimulacion;
    private TareaSimulacion tareaSimulacion;
    private final JButton botonIniciar = new JButton("Iniciar simulación automática");
    private final JButton botonDetener = new JButton("Detener simulación");
    
    public VentanaPrincipal() {
        super("Simulador Visual de Memoria RAM — Programación 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 680);
        setLocationRelativeTo(null);
 
        iniciarNuevaSimulacion(1024);
 
        setLayout(new BorderLayout(8, 8));
        add(construirPanelSuperior(), BorderLayout.NORTH);
        add(construirPanelCentral(), BorderLayout.CENTER);
        add(construirBarraEstado(), BorderLayout.SOUTH);
 
        actualizarVista();
    }
    
    private JPanel construirPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setBorder(new TitledBorder("Crear proceso y asignar memoria"));
 
        panel.add(new JLabel("Nombre:"));
        panel.add(campoNombre);
        panel.add(new JLabel("Tamaño (KB):"));
        panel.add(campoTamano);
        panel.add(new JLabel("Tiempo (seg, 0 = infinito):"));
        panel.add(campoTiempo);
        panel.add(new JLabel("Algoritmo:"));
        comboAlgoritmo.addActionListener(e ->
                etiquetaAlgoritmoActual.setText("Algoritmo actual: " + comboAlgoritmo.getSelectedItem()));
        panel.add(comboAlgoritmo);
 
        JButton botonCrear = new JButton("Crear y asignar proceso");
        botonCrear.addActionListener(e -> crearProceso());
        botonCrear.setBackground(new Color(76, 175, 80));
        botonCrear.setForeground(Color.WHITE);
        botonCrear.setOpaque(true);
        botonCrear.setBorderPainted(false);
        botonCrear.setFocusPainted(false);
        panel.add(botonCrear);
 
        JButton botonNuevaSim = new JButton("Nueva simulación...");
        botonNuevaSim.addActionListener(e -> pedirNuevaSimulacion());
        panel.add(botonNuevaSim);
 
        return panel;
    }

    private void crearProceso() {
        String nombre = campoNombre.getText().trim();
        int tamano = (Integer) campoTamano.getValue();
        int tiempo = (Integer) campoTiempo.getValue();
        String algoritmo = (String) comboAlgoritmo.getSelectedItem();
 
        if (nombre.isEmpty()) {
            mostrarError("Debes escribir un nombre para el proceso.");
            return;
        }
        if (nombre.contains(" ")) {
            mostrarError("El nombre del proceso no debe contener espacios (usa guiones o guion bajo).");
            return;
        }
 
        try {
            Proceso proceso = new Proceso(nombre, tamano, tiempo == 0 ? -1 : tiempo);
            boolean asignado = gestor.asignarProceso(proceso, algoritmo);
            if (!asignado) {
                mostrarError("No hay un bloque libre suficientemente grande para \"" + nombre
                        + "\" (" + tamano + " KB) usando " + algoritmo
                        + ".\nPrueba compactar la memoria o liberar procesos.");
                return;
            }
            campoNombre.setText("");
            actualizarVista();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    
}
