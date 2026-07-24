package t.final_simuladorderam;

import java.io.*;

public class BloqueMemoria implements Serializable {

    private static final long serialVersionUID = 1L;

    private int inicio;
    private int tamano;
    private boolean ocupado;
    private Proceso proceso; // null si el bloque está libre

    public BloqueMemoria(int inicio, int tamano, boolean ocupado, Proceso proceso) {
        this.inicio = inicio;
        this.tamano = tamano;
        this.ocupado = ocupado;
        this.proceso = proceso;
    }

    /** Indica si este bloque libre puede alojar un proceso del tamaño dado. */
    public boolean puedeAlojar(int tamanoSolicitado) {
        return !ocupado && this.tamano >= tamanoSolicitado;
    }

    public void liberar() {
        this.ocupado = false;
        this.proceso = null;
    }

    public int getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public Proceso getProceso() {
        return proceso;
    }

    public void setProceso(Proceso proceso) {
        this.proceso = proceso;
    }

    public int getFin() {
        return inicio + tamano - 1;
    }

    @Override
    public String toString() {
        if (ocupado) {
            return "[" + inicio + "-" + getFin() + "] OCUPADO por " + proceso.getNombre() + " (" + tamano + " KB)";
        }
        return "[" + inicio + "-" + getFin() + "] LIBRE (" + tamano + " KB)";
    }
}
