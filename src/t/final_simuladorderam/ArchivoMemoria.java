/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t.final_simuladorderam;

import java.awt.*;
import java.io.*;

/**
 *
 * @author user
 */
public class ArchivoMemoria {
    public static void guardar(GestorMemoria gestor, String ruta) throws IOException {
        File archivo = new File(ruta);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write("TAMANO_TOTAL=" + gestor.getTamanoTotal());
            bw.newLine();
            for (BloqueMemoria b : gestor.getBloques()) {
                StringBuilder linea = new StringBuilder();
                linea.append("BLOQUE;")
                        .append(b.getInicio()).append(";")
                        .append(b.getTamano()).append(";")
                        .append(b.isOcupado());
                if (b.isOcupado()) {
                    Proceso p = b.getProceso();
                    linea.append(";").append(p.getNombre())
                            .append(";").append(p.getTamano())
                            .append(";").append(p.getColor().getRGB())
                            .append(";").append(p.getTiempoEjecucion())
                            .append(";").append(p.getTiempoRestante());
                }
                bw.write(linea.toString());
                bw.newLine();
            }
        }
    }
    public static GestorMemoria cargar(String ruta) throws IOException {
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            throw new IOException("El archivo indicado no existe: " + ruta);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String primeraLinea = br.readLine();
            if (primeraLinea == null || !primeraLinea.startsWith("TAMANO_TOTAL=")) {
                throw new IOException("Formato de archivo inválido: falta TAMANO_TOTAL.");
            }
            int tamanoTotal = Integer.parseInt(primeraLinea.split("=")[1].trim());
            GestorMemoria gestor = new GestorMemoria(tamanoTotal);
            gestor.getBloques().clear();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(";");
                if (partes.length < 4 || !partes[0].equals("BLOQUE")) {
                    throw new IOException("Línea con formato inválido: " + linea);
                }
                int inicio = Integer.parseInt(partes[1]);
                int tamano = Integer.parseInt(partes[2]);
                boolean ocupado = Boolean.parseBoolean(partes[3]);
                BloqueMemoria bloque;
                if (ocupado) {
                    String nombre = partes[4];
                    int tamanoProceso = Integer.parseInt(partes[5]);
                    Proceso p = new Proceso(nombre, tamanoProceso);
                    if (partes.length > 6) {
                        p.setColor(new Color(Integer.parseInt(partes[6])));
                    }
                    if (partes.length > 8) {
                        p.setTiempoEjecucion(Integer.parseInt(partes[7]));
                        p.setTiempoRestante(Integer.parseInt(partes[8]));
                    }
                    bloque = new BloqueMemoria(inicio, tamano, true, p);
                } else {
                    bloque = new BloqueMemoria(inicio, tamano, false, null);
                }
                gestor.getBloques().add(bloque);
            }
            gestor.getHistorial().add("Simulación cargada desde el archivo: " + archivo.getName());
            return gestor;
        } catch (NumberFormatException e) {
            throw new IOException("El archivo contiene datos numéricos inválidos.", e);
        }
    }
}


