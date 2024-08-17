import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Semaphore;
import java.util.Random;

public class FabricaJuguetes extends JFrame {

    private static final int CAPACIDAD_ALMACEN = 10;
    private int piezasDisponibles = 0;
    private boolean pausa = false;
    private JButton pausarButton;
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore piezasSuficientes = new Semaphore(0);
    private final Semaphore espacioDisponible = new Semaphore(CAPACIDAD_ALMACEN);
    private final Semaphore juguetesDisponibles = new Semaphore(0);
    private final Random random = new Random();
    private final JLabel[] piezasLabels = new JLabel[CAPACIDAD_ALMACEN];
    private final JTextArea logArea = new JTextArea(10, 30);

    public FabricaJuguetes() {
        setTitle("Fábrica de Juguetes");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel para mostrar las piezas en el almacén
        JPanel almacenPanel = new JPanel(new GridLayout(1, CAPACIDAD_ALMACEN, 5, 5));
        for (int i = 0; i < CAPACIDAD_ALMACEN; i++) {
            piezasLabels[i] = new JLabel("", SwingConstants.CENTER);
            piezasLabels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            almacenPanel.add(piezasLabels[i]);
        }
        add(almacenPanel, BorderLayout.NORTH);

        // Área de texto para los logs
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        add(logScrollPane, BorderLayout.CENTER);

        // Botón para iniciar la simulación
        JButton iniciarButton = new JButton("Iniciar Simulación");
        iniciarButton.addActionListener(e -> iniciarSimulacion());
        add(iniciarButton, BorderLayout.SOUTH);

        // Botón para pausar la simulación
        pausarButton = new JButton("Pausar");
        pausarButton.addActionListener(e -> pausarSimulacion());
        pausarButton.setEnabled(false); // Desactivar el botón de pausa al principio
        add(pausarButton, BorderLayout.EAST);
    }
    class Cliente implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(random.nextInt(5000) + 1000); // Simula la llegada de un cliente de manera aleatoria
                    if (juguetesDisponibles.tryAcquire()) {
                        logArea.append("Cliente compró un juguete.\n");
                    } else {
                        logArea.append("Cliente no pudo comprar un juguete. No hay juguetes disponibles.\n");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void iniciarSimulacion() {
        // Iniciar el hilo del proveedor
        new Thread(new Proveedor()).start();
        new Thread(new Cliente()).start(); // Iniciar el hilo del cliente

        // Iniciar los hilos de los trabajadores
        for (int i = 1; i <= 5; i++) {
            new Thread(new Trabajador(), "Trabajador-" + i).start();
        }
        // Activar el botón de pausa
        pausarButton.setEnabled(true);
    }
    private void pausarSimulacion() {
        pausa = !pausa;
        if (pausa) {
            pausarButton.setText("Reanudar");
            logArea.append("Simulación pausada.\n");
        } else {
            pausarButton.setText("Pausar");
            logArea.append("Simulación reanudada.\n");
        }
    }
    class Trabajador implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    piezasSuficientes.acquire(3); // Espera hasta que haya al menos 3 piezas
                    mutex.acquire();
                    piezasDisponibles -= 3; // Toma 3 piezas para ensamblar un juguete
                    actualizarAlmacen();
                    logArea.append(Thread.currentThread().getName() + " ensambló un juguete. Piezas restantes: " + piezasDisponibles + "\n");
                    mutex.release();
                    espacioDisponible.release(3); // Libera espacio en el almacén
                    Thread.sleep(2000); // Simula el tiempo de ensamblaje, incrementado a 2000 ms
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    class Proveedor implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    while (pausa) {
                        Thread.sleep(100); // Espera hasta que se reanude la simulación
                    }
                    espacioDisponible.acquire(); // Espera hasta que haya espacio en el almacén
                    mutex.acquire();
                    piezasDisponibles++; // Añade una pieza al almacén
                    actualizarAlmacen();
                    logArea.append("Proveedor añadió una pieza. Piezas disponibles: " + piezasDisponibles + "\n");
                    mutex.release();
                    piezasSuficientes.release(); // Notifica que hay más piezas disponibles
                    Thread.sleep(1500); // Simula el tiempo de suministro, incrementado a 1500 ms
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void actualizarAlmacen() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < CAPACIDAD_ALMACEN; i++) {
                if (i < piezasDisponibles) {
                    piezasLabels[i].setText("Pieza");
                    piezasLabels[i].setOpaque(true);
                    piezasLabels[i].setBackground(Color.YELLOW);
                } else {
                    piezasLabels[i].setText("");
                    piezasLabels[i].setOpaque(false);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FabricaJuguetes frame = new FabricaJuguetes();
            frame.setVisible(true);
   });
}
}