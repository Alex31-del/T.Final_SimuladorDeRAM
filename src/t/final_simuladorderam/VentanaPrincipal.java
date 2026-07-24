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

    private JPanel construirPanelCentral() {
        JPanel panelVisual = new JPanel(new BorderLayout());
        panelVisual.setBorder(new TitledBorder("Mapa visual de memoria"));
        panelVisual.add(panelMemoria, BorderLayout.CENTER);
        panelVisual.add(etiquetaEstadisticas, BorderLayout.SOUTH);
 
        JPanel panelDerecho = new JPanel(new BorderLayout(4, 4));
        panelDerecho.setPreferredSize(new Dimension(320, 400));
 
        JPanel panelTablaProcesos = new JPanel(new BorderLayout());
        panelTablaProcesos.setBorder(new TitledBorder("Procesos en memoria"));
        panelTablaProcesos.add(new JScrollPane(tablaProcesos), BorderLayout.CENTER);
 
        JPanel botonesLista = new JPanel(new GridLayout(2, 2, 4, 4));
        JButton botonLiberar = new JButton("Liberar");
        botonLiberar.setBackground(new Color(229, 115, 115)); // rojo suave
        botonLiberar.setForeground(Color.WHITE);
        botonLiberar.setOpaque(true);
        botonLiberar.setBorderPainted(false);
        botonLiberar.setFocusPainted(false);
        botonLiberar.addActionListener(e -> liberarProceso());
        
        JButton botonCompactar = new JButton("Compactar memoria");
        botonCompactar.setBackground(new Color(66, 165, 245)); // azul
        botonCompactar.setForeground(Color.WHITE);
        botonCompactar.setOpaque(true);
        botonCompactar.setBorderPainted(false);
        botonCompactar.setFocusPainted(false);
        botonCompactar.addActionListener(e -> compactarMemoria());
        
       
        botonIniciar.setBackground(new Color(102, 187, 106)); // verde
        botonIniciar.setForeground(Color.WHITE);
        botonIniciar.setOpaque(true);
        botonIniciar.setBorderPainted(false);
        botonIniciar.setFocusPainted(false);
        botonIniciar.addActionListener(e -> iniciarSimulacionAutomatica());
        
       
        botonDetener.setBackground(new Color(120, 120, 120)); // gris oscuro
        botonDetener.setForeground(Color.WHITE);
        botonDetener.setOpaque(true);
        botonDetener.setBorderPainted(false);
        botonDetener.setFocusPainted(false);
        botonDetener.addActionListener(e -> detenerSimulacionAutomatica());
        botonDetener.setEnabled(false); 
        botonesLista.add(botonLiberar);
        botonesLista.add(botonCompactar);
        botonesLista.add(botonIniciar);
        botonesLista.add(botonDetener);
        panelTablaProcesos.add(botonesLista, BorderLayout.SOUTH);
 
        JPanel panelTablaLibres = new JPanel(new BorderLayout());
        panelTablaLibres.setBorder(new TitledBorder("Bloques libres"));
        panelTablaLibres.add(new JScrollPane(tablaLibres), BorderLayout.CENTER);
        panelTablaLibres.setPreferredSize(new Dimension(320, 150));
 
        JButton botonReporte = new JButton("Generar reporte");
        botonReporte.setBackground(new Color(171, 71, 188)); // morado
        botonReporte.setForeground(Color.WHITE);
        botonReporte.setOpaque(true);
        botonReporte.setBorderPainted(false);
        botonReporte.setFocusPainted(false);
        botonReporte.addActionListener(e -> mostrarReporte());
        
        JButton botonGuardar = new JButton("Guardar simulación");
        botonGuardar.setBackground(new Color(66, 133, 244)); // azul
        botonGuardar.setForeground(Color.WHITE);
        botonGuardar.setOpaque(true);
        botonGuardar.setBorderPainted(false);
        botonGuardar.setFocusPainted(false);
        botonGuardar.addActionListener(e -> guardarSimulacion());
        
        JButton botonCargar = new JButton("Cargar simulación");
        botonCargar.setBackground(new Color(255, 167, 38)); // naranja
        botonCargar.setForeground(Color.WHITE);
        botonCargar.setOpaque(true);
        botonCargar.setBorderPainted(false);
        botonCargar.setFocusPainted(false);
        botonCargar.addActionListener(e -> cargarSimulacion());
        
        JPanel panelArchivo = new JPanel(new GridLayout(3, 1, 4, 4));
        panelArchivo.setBorder(new TitledBorder("Archivo / Reporte"));
        panelArchivo.add(botonReporte);
        panelArchivo.add(botonGuardar);
        panelArchivo.add(botonCargar);
 
        JPanel panelInferiorDerecho = new JPanel(new BorderLayout(4, 4));
        panelInferiorDerecho.add(panelTablaLibres, BorderLayout.CENTER);
        panelInferiorDerecho.add(panelArchivo, BorderLayout.SOUTH);
 
        panelDerecho.add(panelTablaProcesos, BorderLayout.CENTER);
        panelDerecho.add(panelInferiorDerecho, BorderLayout.SOUTH);
 
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelVisual, panelDerecho);
        split.setResizeWeight(0.65);
 
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.add(split, BorderLayout.CENTER);
        return envoltorio;
    }
    
     private JPanel construirBarraEstado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        panel.add(etiquetaEstadoSim, BorderLayout.WEST);
        panel.add(etiquetaAlgoritmoActual, BorderLayout.EAST);
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

    private void iniciarSimulacionAutomatica() {
        if (hiloSimulacion != null && hiloSimulacion.isAlive()) {
            return; // ya hay una simulación corriendo
        }

        botonIniciar.setEnabled(false);
        botonDetener.setEnabled(true);
        etiquetaEstadoSim.setText("Simulación en curso...");

        tareaSimulacion = new TareaSimulacion(
                gestor,
                () -> (String) comboAlgoritmo.getSelectedItem(),  // proveedorAlgoritmo
                this::actualizarVista,                             // actualizarVista
                mensaje -> {                                       // actualizarEstado
                    if (tareaSimulacion.estaActivo()) {
                        etiquetaEstadoSim.setText(mensaje);
                    } else {
                        etiquetaEstadoSim.setText("Simulación detenida.");
                    }
                }
        );
        hiloSimulacion = new Thread(tareaSimulacion);
        hiloSimulacion.start(); // OJO: start(), nunca run() directamente
    }
    
     private void detenerSimulacionAutomatica() {
        if (tareaSimulacion != null) {
            tareaSimulacion.detener();
        }
        botonIniciar.setEnabled(true);
        botonDetener.setEnabled(false);
        etiquetaEstadoSim.setText("Simulación detenida.");
    }
     
     private void mostrarReporte() {
        JTextArea area = new JTextArea(gestor.generarReporte());
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(560, 420));
        JOptionPane.showMessageDialog(this, scroll, "Reporte de simulación", JOptionPane.PLAIN_MESSAGE);
    }
     
    private void pedirNuevaSimulacion() {
        String entrada = JOptionPane.showInputDialog(this,
                "Tamaño total de la nueva memoria (KB):", "1024");
        if (entrada == null) return;
        try {
            int tamano = Integer.parseInt(entrada.trim());
            if (tamano <= 0) {
                mostrarError("El tamaño total debe ser mayor a 0.");
                return;
            }
            detenerSimulacionAutomatica();
            iniciarNuevaSimulacion(tamano);
            actualizarVista();
        } catch (NumberFormatException ex) {
            mostrarError("Debes escribir un número entero válido.");
        }
    }
     private void iniciarNuevaSimulacion(int tamanoTotal) {
        this.gestor = new GestorMemoria(tamanoTotal);
        
    }
     private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }
 
     
    private void actualizarVista() {
        List<BloqueMemoria> bloques = gestor.getBloques();
        panelMemoria.actualizar(bloques, gestor.getTamanoTotal());
 
        modeloTablaProcesos.setRowCount(0);
        int id = 1;
        for (BloqueMemoria b : bloques) {
            if (b.isOcupado()) {
                Proceso p = b.getProceso();
                String tiempo = p.esInfinito() ? "∞" : String.valueOf(p.getTiempoRestante());
                modeloTablaProcesos.addRow(new Object[]{
                        "P" + id, p.getNombre(), p.getTamano(), "Ejecutando", tiempo
                });
                id++;
            }
        }
 
        modeloTablaLibres.setRowCount(0);
        for (BloqueMemoria b : gestor.getBloquesLibres()) {
            modeloTablaLibres.addRow(new Object[]{b.getInicio(), b.getTamano()});
        }
 
        EstadisticasMemoria e = gestor.calcularFragmentacion();
        etiquetaEstadisticas.setText(String.format(
                "  Uso: %d/%d KB (%d%%)   |   Libre: %d KB   |   Huecos libres: %d   |   Mayor hueco: %d KB   |   Fragmentación externa: %d%%",
                e.getUsado(), e.getTamanoTotal(), e.getPorcentajeUso(), e.getLibre(),
                e.getHuecos(), e.getMayorHueco(), e.getFragmentacionExterna()));
    }
}
