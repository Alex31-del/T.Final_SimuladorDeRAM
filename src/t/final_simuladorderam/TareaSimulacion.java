/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package t.final_simuladorderam;

/**
 *
 * @author Alexander
 */
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class TareaSimulacion implements Runnable {

    private static final double PROBABILIDAD_CAMBIO = 0.8;
    private static final int SEGUNDOS_ENTRE_EVALUACIONES = 5;

    private final GestorMemoria gestor;
    private final Supplier<String> proveedorAlgoritmo;
    private final Runnable actualizarVista;
    private final Consumer<String> actualizarEstado;

    private final java.util.Random random = new java.util.Random();
    private int contadorSegundos = 0;
    private volatile boolean activo = true;

   
    public TareaSimulacion(GestorMemoria gestor,
                            Supplier<String> proveedorAlgoritmo,
                            Runnable actualizarVista,
                            Consumer<String> actualizarEstado) {
        this.gestor = gestor;
        this.proveedorAlgoritmo = proveedorAlgoritmo;
        this.actualizarVista = actualizarVista;
        this.actualizarEstado = actualizarEstado;
    }

    /** Se llama desde el botón "Detener simulación" para frenar el hilo de forma segura. */
    public void detener() {
        activo = false;
    }

    public boolean estaActivo() {
        return activo;
    }

    @Override
    public void run() {
        while (activo) {
            try {
                Thread.sleep(1000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            if (!activo) break;

            // 1) Avanza el tiempo de todos los procesos (puede liberar alguno).
            List<String> liberados = gestor.avanzarTiempo();

            // 2) Cada SEGUNDOS_ENTRE_EVALUACIONES, intenta un cambio aleatorio.
            String cambioMemoria = null;
            contadorSegundos++;
            if (contadorSegundos >= SEGUNDOS_ENTRE_EVALUACIONES) {
                contadorSegundos = 0;
                if (random.nextDouble() < PROBABILIDAD_CAMBIO) {
                    String algoritmo = proveedorAlgoritmo.get();
                    cambioMemoria = gestor.simularCambioAleatorio(algoritmo);
                }
            }

            // 3) Arma el mensaje de estado.
            StringBuilder estado = new StringBuilder("Simulación en curso...");
            if (!liberados.isEmpty()) {
                estado.append(" (se liberó: ").append(String.join(", ", liberados)).append(")");
            }
            if (cambioMemoria != null) {
                estado.append(" | ").append(cambioMemoria);
            }
            String mensajeFinal = estado.toString();

            // 4) Envía la actualización visual al hilo de Swing.
            SwingUtilities.invokeLater(() -> {
                actualizarVista.run();
                actualizarEstado.accept(mensajeFinal);
            });
        }
    }
}