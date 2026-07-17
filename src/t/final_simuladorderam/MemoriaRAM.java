package t.final_simuladorderam;


import java.util.ArrayList;

public class MemoriaRAM {
    
    private int memoriaTotal;
    private ArrayList<BloqueMemoria> bloques;
    
    public MemoriaRAM(int memoriaTotal){
        
        this.memoriaTotal = memoriaTotal;
        
        bloques = new ArrayList<>();
        
        bloques.add(new BloqueMemoria(0, memoriaTotal));
    }   

    public int getMemoriaTotal() {
        return memoriaTotal;
    }

    public ArrayList<BloqueMemoria> getBloques() {
        return bloques;
    }
    
    
    
    public void dividirMemoria(BloqueMemoria bloque, Proceso p){
        
        if(bloque.getTamano() == p.getMemoria()){
            bloque.asignarProceso(p);
            return;
        }
        
        int restante = bloque.getTamano() - p.getMemoria();
        
        BloqueMemoria nuevo = new BloqueMemoria(bloque.getInicio() + p.getMemoria(), restante);
        
        bloque.setTamano(p.getMemoria());
        bloque.asignarProceso(p);
        
        int indice = bloques.indexOf(bloque);
        
        bloques.add(indice + 1, nuevo);
        
    }
    
    public boolean firstFit(Proceso p){
        
         for(int i=0;i<bloques.size();i++){

            BloqueMemoria bloque = bloques.get(i);

            if(!bloque.isOcupado() && bloque.getTamano()>=p.getMemoria()){

                dividirMemoria(bloque,p);

                return true;
            }

        }
         return false;
    }
    
    public boolean bestFit(Proceso p){
        
        BloqueMemoria mejor = null;
        
        for(int i = 0; i < bloques.size(); i++){

            BloqueMemoria bloque = bloques.get(i);

            if(!bloque.isOcupado() &&
                  bloque.getTamano() >= p.getMemoria()){

                if(mejor == null ||
                     bloque.getTamano() < mejor.getTamano()){

                    mejor = bloque;

                }

            }

        }

            if(mejor != null){

                dividirMemoria(mejor, p);
               return true;

            }
            return false;
    }
    
    public boolean worstFit(Proceso p){
        
        BloqueMemoria mayor = null;
        
        for(int i = 0; i < bloques.size(); i++){

             BloqueMemoria bloque = bloques.get(i);

            if(!bloque.isOcupado() &&
                bloque.getTamano() >= p.getMemoria()){
                
                if(mayor == null ||
                    bloque.getTamano() > mayor.getTamano()){

                    mayor = bloque;

                }

           }

       }

        if(mayor != null){

            dividirMemoria(mayor, p);
            return true;
        }
        return false;
    }
    
    
    public void liberarProceso(String nombre) {

        for (BloqueMemoria bloque : bloques) {

            if (bloque.isOcupado()) {

             if (bloque.getProceso().getNombre().equals(nombre)) {

                bloque.liberarProceso();

                fusionarBloques();

                return;
             }

          }

       }

    }
    
    public void fusionarBloques() {

         for (int i = 0; i < bloques.size() - 1; i++) {

             BloqueMemoria actual = bloques.get(i);
             BloqueMemoria siguiente = bloques.get(i + 1);

                if (!actual.isOcupado() && !siguiente.isOcupado()) {

                     actual.setTamano(actual.getTamano() + siguiente.getTamano());

                     bloques.remove(i + 1);

                     i--;
                }

        }

    }
}
