package t.final_simuladorderam;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.awt.Color;
/**
 *
 * @author user
 */
public class Proceso {
    private String nombre;
    private  int memoria;
    private Color color;

    public Proceso(String nombre, int memoria, Color color) {
        this.nombre = nombre;
        this.memoria = memoria;
        this.color=color;
        
    }
    
    public String getNombre(){
        return nombre;
    }
    public int getMemoria(){
        return memoria;
    }
    public Color getColor(){
        return color;
    }
}
