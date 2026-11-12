import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Set;
import javax.imageio.ImageIO;

public class Nino {

    // En Scratch el Niño tiene tamaño 9% y sus imágenes son el doble de resolución
    static final double ESCALA = (0.09 / 2) * Juego.K;
    static final double VELOCIDAD = 0.6;   // unidades de Scratch por fotograma

    // Límites por donde puede andar (de momento, un rectángulo aproximado, en unidades de Scratch)
    static final int LIM_IZQ = -154, LIM_DER = 129, LIM_ABAJO = -72, LIM_ARRIBA = 119;

    // Posición en coordenadas de Scratch (centro de la pantalla = 0,0)
    double x = -22, y = -69;
    boolean mirandoDerecha = true;
    int frame = 0;          // 0 = disfraz "Niño", 1 = "Niño2"
    int contador = 0;
    boolean visible = true;

    Image[] disfraces = new Image[2];
    int ancho, alto;

    public Nino() {
        try {
            var img1 = ImageIO.read(new File("recursos/nino1.png"));
            var img2 = ImageIO.read(new File("recursos/nino2.png"));
            ancho = (int) (img1.getWidth() * ESCALA);
            alto = (int) (img1.getHeight() * ESCALA);
            disfraces[0] = img1.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            disfraces[1] = img2.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el Niño: " + e.getMessage());
        }
    }

    // Equivale a tu "por siempre" con los bloques "si tecla W/A/S/D"
    public void actualizar(Set<Integer> teclas) {
        boolean seMueve = false;

        if (teclas.contains(KeyEvent.VK_W)) { y += VELOCIDAD; seMueve = true; }
        if (teclas.contains(KeyEvent.VK_S)) { y -= VELOCIDAD; seMueve = true; }
        if (teclas.contains(KeyEvent.VK_A)) { x -= VELOCIDAD; mirandoDerecha = false; seMueve = true; }
        if (teclas.contains(KeyEvent.VK_D)) { x += VELOCIDAD; mirandoDerecha = true; seMueve = true; }

        // No salirse de la zona
        x = Math.max(LIM_IZQ, Math.min(LIM_DER, x));
        y = Math.max(LIM_ABAJO, Math.min(LIM_ARRIBA, y));

        // Animación: cambia de disfraz cada pocos fotogramas mientras anda
        if (seMueve) {
            contador++;
            if (contador % 3 == 0) frame = 1 - frame;
        }
    }

    // Rectángulo que ocupa en pantalla (para detectar colisiones)
    public Rectangle area() {
        return new Rectangle(Juego.pantallaX(x) - ancho / 2,
                             Juego.pantallaY(y) - alto / 2, ancho, alto);
    }

    public void dibujar(Graphics2D g) {
        if (!visible || disfraces[0] == null) return;
        int px = Juego.pantallaX(x) - ancho / 2;
        int py = Juego.pantallaY(y) - alto / 2;
        if (mirandoDerecha) {
            g.drawImage(disfraces[frame], px, py, null);
        } else {
            g.drawImage(disfraces[frame], px + ancho, py, -ancho, alto, null); // espejo
        }
    }
}