package t.final_simuladorderam;
/**
 *
 * @author CRISTHIAN
 */
public class BloqueMemoria {
    
    private int inicio;
    private int tamano;
    private boolean ocupado;
    private Proceso proceso;

    public BloqueMemoria(int inicio, int tamano) {
        this.inicio = inicio;
        this.tamano = tamano;
        ocupado = false;
        proceso = null;
    }

    public int getInicio() {
        return inicio;
    }

    public int getTamano() {
        return tamano;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public Proceso getProceso() {
        return proceso;
    }
    
    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }
    
    public void asignarProceso(Proceso p){
        proceso = p;
        ocupado = true;
    }
    
    public void liberarProceso(){
        proceso = null;
        ocupado = false;
    }

    
            
}
