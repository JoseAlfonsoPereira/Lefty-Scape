import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Set;
import javax.imageio.ImageIO;

public class Lefty {

    static final double ESCALA = (0.10 / 2) * Juego.K;   // tamaño 10% en Scratch, imagen al doble de resolución
    static final double VELOCIDAD = 0.6;                  // igual que el Niño (unidades de Scratch por fotograma)

    // Centro de rotación de cada disfraz (píxeles de la imagen original)
    static final int[] CENTRO_X = {181, 181};
    static final int[] CENTRO_Y = {247, 238};

    // Mitad del ancho y del alto de su caja de colisión (en unidades de Scratch)
    static final double MEDIO_ANCHO = 8.9, MEDIO_ALTO = 12.3;

    double x = -184, y = 120;
    boolean visible = false;                     // empieza escondido
    boolean mirandoDerecha = true;
    int frame = 0, contador = 0;

    Image[] disfraces = new Image[2];
    int[] ancho = new int[2], alto = new int[2];

    public Lefty() {
        String[] archivos = {"recursos/lefty1.png", "recursos/lefty2.png"};
        try {
            for (int i = 0; i < 2; i++) {
                var img = ImageIO.read(new File(archivos[i]));
                ancho[i] = (int) (img.getWidth() * ESCALA);
                alto[i] = (int) (img.getHeight() * ESCALA);
                disfraces[i] = img.getScaledInstance(ancho[i], alto[i], Image.SCALE_SMOOTH);
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar a Lefty: " + e.getMessage());
        }
    }

    // "mostrar" + "ir a (-184, 120)"
    public void aparecer() {
        x = -184;
        y = 120;
        visible = true;
    }

    // Caja de colisión en coordenadas de Scratch
    public Rectangle2D.Double caja() {
        return new Rectangle2D.Double(x - MEDIO_ANCHO, y - MEDIO_ALTO, MEDIO_ANCHO * 2, MEDIO_ALTO * 2);
    }

    public void actualizar(Set<Integer> teclas, Mapa mapa) {
        if (!visible) return;

        double dx = 0, dy = 0;
        if (teclas.contains(KeyEvent.VK_W)) dy += VELOCIDAD;
        if (teclas.contains(KeyEvent.VK_S)) dy -= VELOCIDAD;
        if (teclas.contains(KeyEvent.VK_A)) { dx -= VELOCIDAD; mirandoDerecha = false; }
        if (teclas.contains(KeyEvent.VK_D)) { dx += VELOCIDAD; mirandoDerecha = true; }

        // Se mueve en cada eje por separado: si choca con una pared, se anula ese movimiento.
        // (Es el equivalente de tu "si tocando pared → mover -10 pasos")
        x += dx;
        if (mapa.choca(caja())) x -= dx;
        y += dy;
        if (mapa.choca(caja())) y -= dy;

        if (dx != 0 || dy != 0) {
            contador++;
            if (contador % 3 == 0) frame = 1 - frame;
        }
    }

    private int izquierda() {
        double cx = CENTRO_X[frame] * ESCALA;
        int sx = Juego.pantallaX(x);
        return mirandoDerecha ? (int) (sx - cx) : (int) (sx + cx - ancho[frame]);
    }

    private int arriba() {
        return (int) (Juego.pantallaY(y) - CENTRO_Y[frame] * ESCALA);
    }

    public void dibujar(Graphics2D g) {
        if (!visible || disfraces[0] == null) return;
        if (mirandoDerecha) {
            g.drawImage(disfraces[frame], izquierda(), arriba(), null);
        } else {
            g.drawImage(disfraces[frame], izquierda() + ancho[frame], arriba(), -ancho[frame], alto[frame], null);
        }
    }
}