import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

public class HombreMorado {

    static final double ESCALA = (0.12 / 2) * Juego.K;   // tamaño 12% en Scratch, imagen al doble de resolución
    static final double VELOCIDAD = 0.36;    // más lento que el Niño (0.6), si no sería imposible

    // Centro de rotación de cada disfraz, en píxeles de la imagen original
    static final int[] CENTRO_X = {70, 70};
    static final int[] CENTRO_Y = {224, 208};

    double x = -169, y = 97;
    boolean visible = false;                 // empieza escondido
    boolean mirandoDerecha = true;
    int frame = 0, contador = 0;

    Image[] disfraces = new Image[2];
    int[] ancho = new int[2], alto = new int[2];

    public HombreMorado() {
        String[] archivos = {"recursos/hombre1.png", "recursos/hombre2.png"};
        try {
            for (int i = 0; i < 2; i++) {
                var img = ImageIO.read(new File(archivos[i]));
                ancho[i] = (int) (img.getWidth() * ESCALA);
                alto[i] = (int) (img.getHeight() * ESCALA);
                disfraces[i] = img.getScaledInstance(ancho[i], alto[i], Image.SCALE_SMOOTH);
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar el Hombre Morado: " + e.getMessage());
        }
    }

    // Equivale a: si Tartas > 4 → mostrar, apuntar a Niño, mover, cambiar disfraz
    public void actualizar(Nino nino, int tartas) {
        if (tartas > 4) {
            visible = true;

            double dx = nino.x - x;
            double dy = nino.y - y;
            double distancia = Math.hypot(dx, dy);

            if (distancia > 0.5) {           // avanzar hacia el Niño
                x += dx / distancia * VELOCIDAD;
                y += dy / distancia * VELOCIDAD;
            }
            if (Math.abs(dx) > 0.1) mirandoDerecha = dx > 0;   // estilo izquierda-derecha

            contador++;
            if (contador % 4 == 0) frame = 1 - frame;          // 0.07 s ≈ 4 fotogramas
        }
    }

    // Esquina izquierda en pantalla, teniendo en cuenta el centro de rotación y el espejo
    private int izquierda() {
        double cx = CENTRO_X[frame] * ESCALA;
        int sx = Juego.pantallaX(x);
        return mirandoDerecha ? (int) (sx - cx) : (int) (sx + cx - ancho[frame]);
    }

    private int arriba() {
        return (int) (Juego.pantallaY(y) - CENTRO_Y[frame] * ESCALA);
    }

    public Rectangle area() {
        return new Rectangle(izquierda(), arriba(), ancho[frame], alto[frame]);
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