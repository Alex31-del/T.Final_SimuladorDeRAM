package t.final_simuladorderam;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author user
 */
public class Proceso {
    private String nombre;
    private  int memoria;

    public Proceso(String nombre, int memoria) {
        this.nombre = nombre;
        this.memoria = memoria;
    }
    
    public String getNombre(){
        return nombre;
    }
    public int getMemoria(){
        return memoria;
    }
}
