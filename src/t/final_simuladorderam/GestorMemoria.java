/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t.final_simuladorderam;

import java.io.Serializable;
import java.util.*;
/**
 *
 * @author Alexander
 */
public class GestorMemoria implements Serializable{
    
    private static final long serialVersionUID = 1L;

    public static final String FIRST_FIT = "First Fit";
    public static final String BEST_FIT = "Best Fit";
    public static final String WORST_FIT = "Worst Fit";
    private static final double PORCENTAJE_MAX_PROCESO = 0.35;
    private static final double PORCENTAJE_MIN_PROCESO = 0.03;
    
    private int tamanoTotal;
    private List<BloqueMemoria> bloques;
    private final java.util.Random random = new java.util.Random();
    private List<String> historial;
    
     public GestorMemoria(int tamanoTotal) {
        this.tamanoTotal = tamanoTotal;
        this.bloques = new ArrayList<>();
        this.bloques.add(new BloqueMemoria(0, tamanoTotal, false, null));
        this.historial = new LinkedList<>();
        registrar("Simulación iniciada con " + tamanoTotal + " KB de memoria total.");
    }
     private void registrar(String evento) {
        historial.add(evento);
    }

    public int getTamanoTotal() {
        return tamanoTotal;
    }

    public List<BloqueMemoria> getBloques() {
        return bloques;
    }

    public List<String> getHistorial() {
        return historial;
    }
     
     
     
     
     
     
     
     
     
     
     public String simularCambioAleatorio(String algoritmo) {
        List<Integer> ocupados = new ArrayList<>();
        for (int i = 0; i < bloques.size(); i++) {
            if (bloques.get(i).isOcupado()) ocupados.add(i);
        }
        if (ocupados.isEmpty()) return null;

             int idx = ocupados.get(random.nextInt(ocupados.size()));
            BloqueMemoria actual = bloques.get(idx);
             Proceso p = actual.getProceso();
            int tamanoActual = actual.getTamano();

            int tamanoMaximo = Math.max(16, (int) Math.round(tamanoTotal * PORCENTAJE_MAX_PROCESO));
            int tamanoMinimo = Math.max(8, (int) Math.round(tamanoTotal * PORCENTAJE_MIN_PROCESO));

            boolean intentarCrecer = random.nextBoolean();

              // Si ya está en el límite superior, forzamos que intente achicarse (y viceversa).
             if (intentarCrecer && tamanoActual >= tamanoMaximo) intentarCrecer = false;
             if (!intentarCrecer && tamanoActual <= tamanoMinimo) intentarCrecer = true;

            if (!intentarCrecer) {
             return achicarProceso(idx, tamanoMinimo);
            }
            return crecerProceso(idx, algoritmo, tamanoMaximo);
    }
     private String achicarProceso(int idx, int tamanoMinimo) {
        BloqueMemoria actual = bloques.get(idx);
        Proceso p = actual.getProceso();
        int tamanoActual = actual.getTamano();

         int reduccion = Math.max(4, (int) Math.round(tamanoActual * (0.08 + random.nextDouble() * 0.22)));
         int nuevoTamano = tamanoActual - reduccion;

        if (nuevoTamano < tamanoMinimo) {
             nuevoTamano = tamanoMinimo;
             reduccion = tamanoActual - nuevoTamano;
         }
        if (reduccion <= 0) {
          // Ya está en el mínimo, no hay nada que reducir.
         return null;
        }

         actual.setTamano(nuevoTamano);
         p.setTamano(nuevoTamano);

         BloqueMemoria nuevoLibre = new BloqueMemoria(actual.getInicio() + nuevoTamano, reduccion, false, null);
         bloques.add(idx + 1, nuevoLibre);
         fusionarBloquesLibres();

         String msg = "El proceso " + p.getNombre() + " liberó memoria y bajó a " + nuevoTamano + " KB.";
         registrar(msg);
        return msg;
        }
     private String crecerProceso(int idx, String algoritmo, int tamanoMaximo) {
        BloqueMemoria actual = bloques.get(idx);
        Proceso p = actual.getProceso();
         int tamanoActual = actual.getTamano();

        int crecimiento = Math.max(8, (int) Math.round(tamanoActual * (0.08 + random.nextDouble() * 0.32)));
         int tamanoDeseado = Math.min(tamanoActual + crecimiento, tamanoMaximo);
         crecimiento = tamanoDeseado - tamanoActual;

        if (crecimiento <= 0) {
           // Ya está en el límite máximo permitido.
            return null;
        }

    // 1) ¿Puede crecer en el mismo lugar?
        if (idx + 1 < bloques.size() && !bloques.get(idx + 1).isOcupado() && bloques.get(idx + 1).getTamano() >= crecimiento) {
            
             BloqueMemoria libre = bloques.get(idx + 1);
            actual.setTamano(tamanoDeseado);
            p.setTamano(tamanoDeseado);
            if (libre.getTamano() == crecimiento) {
                 bloques.remove(idx + 1);
            } else {
                 libre.setInicio(libre.getInicio() + crecimiento);
                 libre.setTamano(libre.getTamano() - crecimiento);
            }
            String msg = "El proceso " + p.getNombre() + " ha aumentado correctamente su memoria a " + tamanoDeseado + " KB.";
            registrar(msg);
            return msg;
        }

    // 2) No pudo crecer ahí: buscar otro hueco con el algoritmo elegido.
            int indiceDestino = -1;
            int tamanoFinal = tamanoActual;
         for (int intento = 0; intento < 2 && indiceDestino == -1; intento++) {
            int objetivo = (intento == 0) ? tamanoDeseado : tamanoActual;
             indiceDestino = buscarBloque(objetivo, algoritmo);
            if (indiceDestino != -1) tamanoFinal = objetivo;
         }

        if (indiceDestino == -1) {
             String msg = "Memoria insuficiente para modificar al proceso " + p.getNombre() + ".";
            registrar(msg);
            return msg;
        }

        BloqueMemoria destino = bloques.get(indiceDestino);
         int nuevoInicio = destino.getInicio();
         int sobrante = destino.getTamano() - tamanoFinal;

         actual.liberar();
        p.setTamano(tamanoFinal);

         BloqueMemoria nuevoOcupado = new BloqueMemoria(nuevoInicio, tamanoFinal, true, p);
         bloques.set(indiceDestino, nuevoOcupado);
        if (sobrante > 0) {
              bloques.add(indiceDestino + 1, new BloqueMemoria(nuevoInicio + tamanoFinal, sobrante, false, null));
        }
        fusionarBloquesLibres();

        boolean crecioAlMover = tamanoFinal > tamanoActual;
        String msg = "El proceso " + p.getNombre() + " cambió correctamente de posición"+ (crecioAlMover ? " y aumentó a " + tamanoFinal + " KB." : ".");
           
        registrar(msg);
        return msg;
        }
     public String generarReporte() {
        EstadisticasMemoria e = calcularFragmentacion();
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE DE SIMULACIÓN DE MEMORIA RAM ===\n\n");
        sb.append("Memoria total: ").append(e.getTamanoTotal()).append(" KB\n");
        sb.append("Memoria usada: ").append(e.getUsado()).append(" KB (")
                .append(e.getPorcentajeUso()).append("%)\n");
        sb.append("Memoria libre: ").append(e.getLibre()).append(" KB\n");
        sb.append("Cantidad de huecos libres: ").append(e.getHuecos()).append("\n");
        sb.append("Mayor hueco libre contiguo: ").append(e.getMayorHueco()).append(" KB\n");
        sb.append("Fragmentación externa: ").append(e.getFragmentacionExterna()).append("%\n\n");

        sb.append("--- Mapa de memoria actual ---\n");
        for (BloqueMemoria b : bloques) {
            sb.append(b.toString()).append("\n");
        }

        sb.append("\n--- Historial de operaciones ---\n");
        for (String linea : historial) {
            sb.append("- ").append(linea).append("\n");
        }
        return sb.toString();
    }
    
     public static class EstadisticasMemoria {
        private final int tamanoTotal;
        private final int usado;
        private final int libre;
        private final int huecos;
        private final int mayorHueco;

        public EstadisticasMemoria(int tamanoTotal, int usado, int libre, int huecos, int mayorHueco) {
            this.tamanoTotal = tamanoTotal;
            this.usado = usado;
            this.libre = libre;
            this.huecos = huecos;
            this.mayorHueco = mayorHueco;
        }

        public int getTamanoTotal() { return tamanoTotal; }
        public int getUsado() { return usado; }
        public int getLibre() { return libre; }
        public int getHuecos() { return huecos; }
        public int getMayorHueco() { return mayorHueco; }

        public int getPorcentajeUso() {
            return tamanoTotal == 0 ? 0 : Math.round((usado * 100f) / tamanoTotal);
        }

        /** % de memoria libre que está "desperdiciada" por estar fragmentada en huecos pequeños. */
        public int getFragmentacionExterna() {
            if (libre == 0) return 0;
            return Math.round(((libre - mayorHueco) * 100f) / libre);
        }
    }
    public List<String> avanzarTiempo() {
        List<String> liberados = new ArrayList<>();
        for (BloqueMemoria b : bloques) {
            if (b.isOcupado()) {
                Proceso p = b.getProceso();
                if (p.tick()) {
                    liberados.add(p.getNombre());
                }
            }
        }
        for (String nombre : liberados) {
            liberarProceso(nombre);
        }
        return liberados;
    }
     public List<BloqueMemoria> getBloquesLibres() {
        List<BloqueMemoria> libres = new ArrayList<>();
        for (BloqueMemoria b : bloques) {
            if (!b.isOcupado()) {
                libres.add(b);
            }
        }
        return libres;
    }
}
