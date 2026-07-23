/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t.final_simuladorderam;

public class EstadisticasMemoria {
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
