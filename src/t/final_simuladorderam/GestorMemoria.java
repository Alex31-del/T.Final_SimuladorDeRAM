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
    public boolean liberarProceso(String nombre) {
        for (BloqueMemoria b : bloques) {
            if (b.isOcupado() && b.getProceso().getNombre().equalsIgnoreCase(nombre)) {
                registrar("Liberado el proceso " + b.getProceso().getNombre()
                        + " (" + b.getTamano() + " KB) en dirección " + b.getInicio() + ".");
                b.liberar();
                fusionarBloquesLibres();
                return true;
            }
        }
        return false;
    }
    private void fusionarBloquesLibres() {
        boolean fusionoAlgo = true;
        while (fusionoAlgo) {
            fusionoAlgo = false;
            for (int i = 0; i < bloques.size() - 1; i++) {
                BloqueMemoria actual = bloques.get(i);
                BloqueMemoria siguiente = bloques.get(i + 1);
                if (!actual.isOcupado() && !siguiente.isOcupado()) {
                    actual.setTamano(actual.getTamano() + siguiente.getTamano());
                    bloques.remove(i + 1);
                    fusionoAlgo = true;
                    break;
                }
            }
        }
    }
    public void compactar() {
        List<BloqueMemoria> nuevos = new ArrayList<>();
        int cursor = 0;
        for (BloqueMemoria b : bloques) {
            if (b.isOcupado()) {
                nuevos.add(new BloqueMemoria(cursor, b.getTamano(), true, b.getProceso()));
                cursor += b.getTamano();
            }
        }
        int libreFinal = tamanoTotal - cursor;
        if (libreFinal > 0) {
            nuevos.add(new BloqueMemoria(cursor, libreFinal, false, null));
        }
        this.bloques = nuevos;
        registrar("Memoria compactada. Espacio libre unificado al final: " + libreFinal + " KB.");
    }
    public EstadisticasMemoria calcularFragmentacion() {
        int usado = 0;
        int libre = 0;
        int huecos = 0;
        int mayorHueco = 0;
        for (BloqueMemoria b : bloques) {
            if (b.isOcupado()) {
                usado += b.getTamano();
            } else {
                libre += b.getTamano();
                huecos++;
                mayorHueco = Math.max(mayorHueco, b.getTamano());
            }
        }
        return new EstadisticasMemoria(tamanoTotal, usado, libre, huecos, mayorHueco);
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

    public boolean asignarProceso(Proceso p, String algoritmo) {
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proceso no puede estar vacío.");
        }
        if (p.getTamano() <= 0) {
            throw new IllegalArgumentException("El tamaño del proceso debe ser mayor a 0 KB.");
        }
        if (p.getTamano() > tamanoTotal) {
            throw new IllegalArgumentException("El proceso no puede ser más grande que la memoria total.");
        }
        for (BloqueMemoria b : bloques) {
            if (b.isOcupado() && b.getProceso().getNombre().equalsIgnoreCase(p.getNombre())) {
                throw new IllegalArgumentException("Ya existe un proceso llamado \"" + p.getNombre() + "\".");
            }
        }

        int indiceElegido = buscarBloque(p.getTamano(), algoritmo);
        if (indiceElegido == -1) {
            registrar("FALLÓ asignación de " + p.getNombre() + " (" + p.getTamano() + " KB): sin espacio suficiente ("
                    + algoritmo + ").");
            return false;
        }

        BloqueMemoria elegido = bloques.get(indiceElegido);
        int inicio = elegido.getInicio();
        int sobrante = elegido.getTamano() - p.getTamano();

        BloqueMemoria ocupado = new BloqueMemoria(inicio, p.getTamano(), true, p);
        bloques.set(indiceElegido, ocupado);

        if (sobrante > 0) {
            BloqueMemoria libreRestante = new BloqueMemoria(inicio + p.getTamano(), sobrante, false, null);
            bloques.add(indiceElegido + 1, libreRestante);
        }

        registrar("Asignado " + p.getNombre() + " (" + p.getTamano() + " KB) en dirección " + inicio
                + " usando " + algoritmo + ".");
        return true;
    }

    private int buscarBloque(int tamanoSolicitado, String algoritmo) {
        int indiceEncontrado = -1;

        switch (algoritmo) {
            case FIRST_FIT:
                for (int i = 0; i < bloques.size(); i++) {
                    if (bloques.get(i).puedeAlojar(tamanoSolicitado)) {
                        return i;
                    }
                }
                break;

            case BEST_FIT:
                int mejorTamano = Integer.MAX_VALUE;
                for (int i = 0; i < bloques.size(); i++) {
                    BloqueMemoria b = bloques.get(i);
                    if (b.puedeAlojar(tamanoSolicitado) && b.getTamano() < mejorTamano) {
                        mejorTamano = b.getTamano();
                        indiceEncontrado = i;
                    }
                }
                break;

            case WORST_FIT:
                int peorTamano = -1;
                for (int i = 0; i < bloques.size(); i++) {
                    BloqueMemoria b = bloques.get(i);
                    if (b.puedeAlojar(tamanoSolicitado) && b.getTamano() > peorTamano) {
                        peorTamano = b.getTamano();
                        indiceEncontrado = i;
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Algoritmo desconocido: " + algoritmo);
        }
        return indiceEncontrado;
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
