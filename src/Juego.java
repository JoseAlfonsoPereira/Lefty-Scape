import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;

public class Juego extends JPanel implements ActionListener {

    static final int ANCHO = 1920;
    static final int ALTO = 1080;

    // Zoom: píxeles de pantalla por cada unidad del escenario de Scratch (480x360)
    static final double K = 3;
    static final int MAPA_ANCHO = (int) (480 * K);          // 1440
    static final int MAPA_ALTO = (int) (360 * K);           // 1080
    static final int MARGEN_X = (ANCHO - MAPA_ANCHO) / 2;   // 240: barras negras a los lados

    enum Estado { FASE_NINO, GLITCH, FASE_LEFTY, JUMPSCARE, VICTORIA }
    Estado estado = Estado.FASE_NINO;

    int tartas = 0;
    int vidas = 1;
    int vidasGuardia = 1;

    // Fondos
    Image fondo, fondoLefty;
    Image[] errores = new Image[11];

    Nino nino = new Nino();
    Tarta tarta = new Tarta();
    HombreMorado hombre = new HombreMorado();
    Lefty lefty = new Lefty();
    Mapa mapa = new Mapa();
    Set<Integer> teclas = new HashSet<>();

    // Control de la transición cuando pierdes
    long inicioDerrota;               // momento en que Vidas pasó a 0
    int cicloSonido = -1;
    boolean cancionIniciada = false;

    public Juego() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setFocusable(true);

        fondo = cargarImagen("recursos/fondo.png");
        fondoLefty = cargarImagen("recursos/fondo_lefty.png");
        for (int i = 0; i < errores.length; i++) {
            errores[i] = cargarImagen(String.format("recursos/error%02d.png", i + 1));
        }

        Sonido.cargar("musica", "recursos/musica.wav");
        Sonido.cargar("tv", "recursos/tv.wav");
        Sonido.cargar("cancion", "recursos/cancion.wav");
        Sonido.reproducir("musica");          // suena desde el principio

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e)  { teclas.add(e.getKeyCode()); }
            @Override public void keyReleased(KeyEvent e) { teclas.remove(e.getKeyCode()); }
        });

        new Timer(16, this).start();
        // perder();   // <- quita las // solo para probar la transición sin jugar
    }

    static Image cargarImagen(String ruta) {
        try {
            return ImageIO.read(new File(ruta));
        } catch (Exception e) {
            System.out.println("No se pudo cargar " + ruta + ": " + e.getMessage());
            return null;
        }
    }

    // Conversión de coordenadas de Scratch (centro = 0,0, Y hacia arriba) a píxeles de pantalla
    static int pantallaX(double x) { return (int) (ANCHO / 2 + x * K); }
    static int pantallaY(double y) { return (int) (ALTO / 2 - y * K); }

    @Override
    public void actionPerformed(ActionEvent e) {
        actualizar();
        repaint();
    }

    void actualizar() {
        if (estado == Estado.FASE_NINO) {
            actualizarFaseNino();
        } else if (estado == Estado.GLITCH) {
            actualizarGlitch();
        } else if (estado == Estado.FASE_LEFTY) {
            actualizarFaseLefty();
        }

        // Escenario: "si Vidas = 0 → esperar 4.4 → sonido FNAF song"
        if (vidas <= 0 && !cancionIniciada && msDesdeDerrota() >= 4400) {
            Sonido.repetir("cancion");
            cancionIniciada = true;
        }
    }

    void actualizarFaseNino() {
        nino.actualizar(teclas);

        if (tarta.visible && nino.area().intersects(tarta.area())) {
            tarta.saltarAOtroSitio();
            tartas++;
            if (tartas == 5) Sonido.parar("musica");   // "si Tartas = 5 → parar sonidos"
        }

        if (tartas >= 15) {
            tarta.visible = false;
            hombre.visible = false;
        } else {
            hombre.actualizar(nino, tartas);

            if (hombre.visible && nino.area().intersects(hombre.area())) {
                vidas--;
                if (vidas <= 0) perder();
            }
        }
    }

    // Todo lo que pasa en el momento en que Vidas llega a 0
    void perder() {
        vidas = 0;
        nino.visible = false;
        hombre.visible = false;
        tarta.visible = false;
        Sonido.parar("musica");
        estado = Estado.GLITCH;
        inicioDerrota = System.currentTimeMillis();
        cicloSonido = -1;
    }

    long msDesdeDerrota() {
        return System.currentTimeMillis() - inicioDerrota;
    }

    void actualizarGlitch() {
        long ms = msDesdeDerrota();

        // Dos repeticiones de 0.55 s: en cada una suena el efecto de TV, y al final se corta
        if (ms < 1100) {
            int ciclo = (int) (ms / 550);
            if (ciclo != cicloSonido) {
                Sonido.reproducir("tv");
                cicloSonido = ciclo;
            }
        } else if (cicloSonido != 2) {
            Sonido.parar("tv");
            cicloSonido = 2;
        }

        // Después: 3 s de pantalla negra y empieza la fase de Lefty
        if (ms >= 4100) {
            estado = Estado.FASE_LEFTY;
        }
    }

    void actualizarFaseLefty() {
        // "esperar 4.4 → mostrar": Lefty y las paredes aparecen a los 4.4 s
        if (!lefty.visible && msDesdeDerrota() >= 4400) {
            lefty.aparecer();
            mapa.activo = true;
        }
        lefty.actualizar(teclas, mapa);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLACK);             // "Fondo negro"
        g2.fillRect(0, 0, ANCHO, ALTO);

        if (estado == Estado.FASE_NINO) {
            dibujarImagen(g2, fondo);
            tarta.dibujar(g2);
            nino.dibujar(g2);
            hombre.dibujar(g2);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 32));
            g2.drawString("Tartas: " + tartas + "   Vidas: " + vidas, MARGEN_X + 20, 50);

        } else if (estado == Estado.GLITCH) {
            long ms = msDesdeDerrota();
            if (ms < 1100) {
                int i = (int) ((ms % 550) / 50);          // 0..10: una pantalla cada 0.05 s
                dibujarImagen(g2, errores[Math.min(i, 10)]);
            }                                              // si no, se queda en negro

        } else if (estado == Estado.FASE_LEFTY) {
            dibujarImagen(g2, fondoLefty);
            lefty.dibujar(g2);
            mapa.dibujar(g2);        // las paredes van encima, como en tus capas de Scratch
        }
    }

    // Dibuja una imagen de fondo ocupando todo el mapa (1440x1080, centrado)
    void dibujarImagen(Graphics2D g2, Image img) {
        if (img != null) g2.drawImage(img, MARGEN_X, 0, MAPA_ANCHO, MAPA_ALTO, null);
    }

    public static void main(String[] args) {
        JFrame ventana = new JFrame("Lefty");
        Juego juego = new Juego();
        ventana.add(juego);
        ventana.pack();
        ventana.setResizable(false);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
        juego.requestFocusInWindow();
    }
}