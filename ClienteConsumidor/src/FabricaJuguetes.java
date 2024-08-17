import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.Random;

public class FabricaJuguetes extends JFrame {

    private static final int CAPACIDAD_ALMACEN = 6;
    private int piezasDisponibles = 0;
    private boolean pausa = false;
    private JButton pausarButton;
<<<<<<< HEAD

    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore piezasSuficientes = new Semaphore(0);
    private final Semaphore espacioDisponible = new Semaphore(CAPACIDAD_ALMACEN);
    private final Semaphore clienteLlegado = new Semaphore(0);  // Semáforo para controlar la llegada de clientes

=======
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore piezasSuficientes = new Semaphore(0);
    private final Semaphore espacioDisponible = new Semaphore(CAPACIDAD_ALMACEN);
    private final Semaphore juguetesDisponibles = new Semaphore(0);
    private final Random random = new Random();
>>>>>>> 8100fffead07ef5804d927e8f4b947847cf6570c
    private final JLabel[] piezasLabels = new JLabel[CAPACIDAD_ALMACEN];
    private final JTextArea logArea = new JTextArea(10, 30);

    // Array de imágenes para las piezas
    private final ImageIcon[] piezaIcons;

    public FabricaJuguetes() {
        setTitle("Fábrica de Juguetes");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Cargar las imágenes desde la carpeta res
        piezaIcons = new ImageIcon[]{
                new ImageIcon(getClass().getResource("/alas.jpg")),
                new ImageIcon(getClass().getResource("/alienigena.jpg")),
                new ImageIcon(getClass().getResource("/Cabeza Juguete.jpg")),
                new ImageIcon(getClass().getResource("/sombreros.png"))
        };

        // Panel superior con título
        JLabel titulo = new JLabel("Fábrica de Juguetes", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(new Color(0x4B0082));
        add(titulo, BorderLayout.NORTH);

        // Panel para mostrar las piezas en el almacén
        JPanel almacenPanel = new JPanel(new GridLayout(1, CAPACIDAD_ALMACEN, 5, 5));
        almacenPanel.setBorder(BorderFactory.createTitledBorder("Almacén"));
        for (int i = 0; i < CAPACIDAD_ALMACEN; i++) {
            piezasLabels[i] = new JLabel("", SwingConstants.CENTER);
            piezasLabels[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
            almacenPanel.add(piezasLabels[i]);
        }
        add(almacenPanel, BorderLayout.CENTER);

        // Área de texto para los logs
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Registro de Actividades"));
        add(logScrollPane, BorderLayout.SOUTH);

        // Panel para los botones
        JPanel botonesPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Botón para iniciar la simulación
        JButton iniciarButton = new JButton("Iniciar Simulación");
        iniciarButton.setFont(new Font("Arial", Font.BOLD, 16));
        iniciarButton.setForeground(Color.WHITE);
        iniciarButton.setBackground(new Color(0x228B22));
        iniciarButton.addActionListener(e -> iniciarSimulacion());
<<<<<<< HEAD
        botonesPanel.add(iniciarButton);

        // Botón para pausar la simulación
        pausarButton = new JButton("Pausar");
        pausarButton.setFont(new Font("Arial", Font.BOLD, 16));
        pausarButton.setForeground(Color.WHITE);
        pausarButton.setBackground(new Color(0xB22222));
        pausarButton.addActionListener(e -> pausarSimulacion());
        pausarButton.setEnabled(false); // Desactivar el botón de pausa al principio
        botonesPanel.add(pausarButton);

        add(botonesPanel, BorderLayout.EAST);

        // Establecer color de fondo general
        getContentPane().setBackground(new Color(0xF0F8FF));
=======
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
>>>>>>> 8100fffead07ef5804d927e8f4b947847cf6570c
    }

    private void iniciarSimulacion() {
        // Iniciar el hilo del proveedor
        new Thread(new Proveedor()).start();
        new Thread(new Cliente()).start(); // Iniciar el hilo del cliente

        // Iniciar los hilos de los trabajadores
        for (int i = 1; i <= 5; i++) {
            new Thread(new Trabajador(), "Trabajador-" + i).start();
        }
<<<<<<< HEAD

        // Iniciar el hilo del cliente
        new Thread(new Cliente()).start();

        // Activar el botón de pausa
        pausarButton.setEnabled(true);
    }

=======
        // Activar el botón de pausa
        pausarButton.setEnabled(true);
    }
>>>>>>> 8100fffead07ef5804d927e8f4b947847cf6570c
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
                    clienteLlegado.acquire(); // Espera a que llegue un cliente
                    piezasSuficientes.acquire(3); // Espera hasta que haya al menos 3 piezas
                    mutex.acquire();
                    piezasDisponibles -= 3; // Toma 3 piezas para ensamblar un juguete
                    actualizarAlmacen();
                    logArea.append(Thread.currentThread().getName() + " ensambló un juguete. Piezas restantes: " + piezasDisponibles + "\n");
                    mutex.release();
                    espacioDisponible.release(3); // Libera espacio en el almacén
                    Thread.sleep(2000); // Simula el tiempo de ensamblaje
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
                    Thread.sleep(1500); // Simula el tiempo de suministro
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    class Cliente implements Runnable {
        @Override
        public void run() {
            try {
                Random random = new Random();
                while (true) {
                    Thread.sleep(random.nextInt(5000) + 2000); // Simula el tiempo aleatorio de llegada del cliente
                    logArea.append("Cliente ha llegado a la fábrica.\n");
                    clienteLlegado.release(); // Notifica la llegada de un cliente
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void actualizarAlmacen() {
        SwingUtilities.invokeLater(() -> {
            Random rand = new Random();
            int randomIndex = -1;
            for (int i = 0; i < CAPACIDAD_ALMACEN; i++) {
                if (i < piezasDisponibles) {
                    if (randomIndex == -1 || i % 2 == 0) {
                        randomIndex = rand.nextInt(piezaIcons.length);
                    }
                    piezasLabels[i].setIcon(piezaIcons[randomIndex]); // Selecciona la misma imagen para el par de espacios
                    piezasLabels[i].setText(""); // No se muestra texto, solo la imagen
                } else {
                    piezasLabels[i].setIcon(null); // Quitar la imagen si no hay piezas
                    piezasLabels[i].setText("");
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