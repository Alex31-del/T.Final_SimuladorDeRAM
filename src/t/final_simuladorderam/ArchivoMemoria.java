/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t.final_simuladorderam;

import java.awt.Color;
import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author user
 */
public class ArchivoMemoria {
    private JFileChooser selector;

    public ArchivoMemoria() {

        selector = new JFileChooser();

        FileNameExtensionFilter filtro =
                new FileNameExtensionFilter(
                        "Archivos de Memoria (*.txt)",
                        "txt");

        selector.setFileFilter(filtro);

    }

    /**
     * Permite seleccionar la ubicación donde guardar.
     */
    public File seleccionarArchivoGuardar() {

        int opcion = selector.showSaveDialog(null);

        if (opcion == JFileChooser.APPROVE_OPTION) {

            File archivo = selector.getSelectedFile();

            if (!archivo.getName().endsWith(".txt")) {

                archivo = new File(
                        archivo.getAbsolutePath() + ".txt");

            }

            return archivo;

        }

        return null;

    }

    /**
     * Permite seleccionar un archivo para abrir.
     */
    public File seleccionarArchivoAbrir() {

        int opcion = selector.showOpenDialog(null);

        if (opcion == JFileChooser.APPROVE_OPTION) {

            return selector.getSelectedFile();

        }

        return null;

    }

    /**
     * Guarda toda la memoria en un archivo.
     */
    public void guardarMemoria(MemoriaRAM memoria) {

        File archivo = seleccionarArchivoGuardar();

        if (archivo == null) {

            return;

        }

        BufferedWriter escritor = null;

        try {

            escritor = new BufferedWriter(
                    new FileWriter(archivo));

            escribirMemoria(memoria, escritor);

            JOptionPane.showMessageDialog(
                    null,
                    "Memoria guardada correctamente.");

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al guardar el archivo.\n"
                    + e.getMessage());

        } finally {

            try {

                if (escritor != null) {

                    escritor.close();

                }

            } catch (IOException e) {

                JOptionPane.showMessageDialog(
                        null,
                        "Error al cerrar el archivo.");

            }

        }

    }

    /**
     * Escribe toda la información de la memoria.
     */
    private void escribirMemoria(
            MemoriaRAM memoria,
            BufferedWriter escritor)
            throws IOException {

        escritor.write(
                String.valueOf(
                        memoria.getMemoriaTotal()));

        escritor.newLine();

        escritor.newLine();

        for (BloqueMemoria bloque
                : memoria.getBloques()) {

            escribirBloque(
                    bloque,
                    escritor);

        }

    }

    /**
     * Escribe un bloque.
     */
    private void escribirBloque(
            BloqueMemoria bloque,
            BufferedWriter escritor)
            throws IOException {

        if (bloque.isOcupado()) {

            escritor.write(

                    "OCUPADO,"
                    + bloque.getInicio()
                    + ","
                    + bloque.getTamano()
                    + ","
                    + bloque.getProceso().getNombre());

        } else {

            escritor.write(

                    "LIBRE,"
                    + bloque.getInicio()
                    + ","
                    + bloque.getTamano());

        }

        escritor.newLine();

    }
        /**
     * Carga una simulación desde un archivo.
     */
    public MemoriaRAM cargarMemoria() {

        File archivo = seleccionarArchivoAbrir();

        if (archivo == null) {
            return null;
        }

        BufferedReader lector = null;

        try {

            lector = new BufferedReader(
                    new FileReader(archivo));

            MemoriaRAM memoria = leerMemoria(lector);

            JOptionPane.showMessageDialog(
                    null,
                    "Memoria cargada correctamente.");

            return memoria;

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al abrir el archivo.\n"
                    + e.getMessage());

        } finally {

            try {

                if (lector != null) {
                    lector.close();
                }

            } catch (IOException e) {

                JOptionPane.showMessageDialog(
                        null,
                        "Error al cerrar el archivo.");

            }

        }

        return null;

    }

    /**
     * Lee toda la memoria almacenada.
     */
    private MemoriaRAM leerMemoria(BufferedReader lector)
            throws IOException {

        String linea;

        linea = lector.readLine();

        int memoriaTotal = Integer.parseInt(linea);

        MemoriaRAM memoria = new MemoriaRAM(memoriaTotal);

        memoria.getBloques().clear();

        while ((linea = lector.readLine()) != null) {

            if (linea.trim().isEmpty()) {
                continue;
            }

            BloqueMemoria bloque = leerBloque(linea);

            memoria.getBloques().add(bloque);

        }

        return memoria;

    }

    /**
     * Reconstruye un bloque desde una línea del archivo.
     */
    private BloqueMemoria leerBloque(String linea) {

        String datos[] = linea.split(",");

        String estado = datos[0];

        int inicio = Integer.parseInt(datos[1]);

        int tamano = Integer.parseInt(datos[2]);

        BloqueMemoria bloque =
                new BloqueMemoria(inicio, tamano);

        if (estado.equals("OCUPADO")) {

            String nombreProceso = datos[3];

            Proceso proceso =
                    new Proceso(nombreProceso, tamano, Color.CYAN);

            bloque.asignarProceso(proceso);

        }

        return bloque;

    }
        /**
     * Verifica si un archivo existe.
     */
    public boolean existeArchivo(File archivo) {

        return archivo != null && archivo.exists();

    }

    /**
     * Verifica si un archivo puede leerse.
     */
    public boolean puedeLeer(File archivo) {

        return archivo != null && archivo.canRead();

    }

    /**
     * Verifica si un archivo puede escribirse.
     */
    public boolean puedeEscribir(File archivo) {

        return archivo != null && archivo.canWrite();

    }

    /**
     * Devuelve el nombre del archivo.
     */
    public String nombreArchivo(File archivo) {

        if (archivo == null) {
            return "";
        }

        return archivo.getName();

    }

    /**
     * Devuelve la ruta completa del archivo.
     */
    public String rutaArchivo(File archivo) {

        if (archivo == null) {
            return "";
        }

        return archivo.getAbsolutePath();

    }

    /**
     * Muestra un mensaje informativo.
     */
    public void mostrarMensaje(String mensaje) {

        JOptionPane.showMessageDialog(
                null,
                mensaje,
                "Archivo de Memoria",
                JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * Muestra un mensaje de error.
     */
    public void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                null,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE);

    }

}


