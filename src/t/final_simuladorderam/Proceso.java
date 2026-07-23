package t.final_simuladorderam;

import java.awt.Color;
import java.io.*;

public class Proceso implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private int tamano; // en KB
    private Color color;
 
    // NUEVO: tiempo de ejecución en segundos. -1 significa "infinito" (nunca se libera solo).
    private int tiempoEjecucion;
    private int tiempoRestante;
 
    public Proceso(String nombre, int tamano) {
        this(nombre, tamano, -1);
    }
 
    public Proceso(String nombre, int tamano, int tiempoEjecucion) {
        this.nombre = nombre;
        this.tamano = tamano;
        this.color = generarColor(nombre);
        this.tiempoEjecucion = tiempoEjecucion;
        this.tiempoRestante = tiempoEjecucion;
    }

    private Color generarColor(String semilla) {
        int hash = semilla.hashCode();
        float hue = ((hash % 360) + 360) % 360 / 360f;
        return Color.getHSBColor(hue, 0.55f, 0.92f);
    }
 
    public boolean tick() {
        if (tiempoRestante > 0) {
            tiempoRestante--;
            return tiempoRestante == 0;
        }
        return false;
    }
 
    public boolean esInfinito() {
        return tiempoEjecucion <= 0;
    }
 
    public String getNombre() {
        return nombre;
    }
 
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
 
    public int getTamano() {
        return tamano;
    }
 
    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

 
    public Color getColor() {
        return color;
    }
 
    public void setColor(Color color) {
        this.color = color;
    }
 
    public int getTiempoEjecucion() {
        return tiempoEjecucion;
    }
 
    public void setTiempoEjecucion(int tiempoEjecucion) {
        this.tiempoEjecucion = tiempoEjecucion;
    }
 
    public int getTiempoRestante() {
        return tiempoRestante;
    }
 
    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }
  
 
    @Override
    public String toString() {
        return nombre + " (" + tamano + " KB)";
    }
 
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Proceso)) return false;
        Proceso otro = (Proceso) obj;
        return nombre.equalsIgnoreCase(otro.nombre);
    }
 
    @Override
    public int hashCode() {
        return nombre.toLowerCase().hashCode();
    }
}
