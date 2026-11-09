import java.awt.*;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;

public class Tarta {

    static final double ESCALA = (0.05 / 2) * 4;   // tamaño 5% en Scratch, imagen al doble de resolución

    double x = -22, y = 110;                 // posición inicial de tu Scratch
    boolean visible = true;

    Image imagen;
    int ancho, alto;
    Random azar = new Random();

    public Tarta() {
        try {
            var img = ImageIO.read(new File("recursos/tarta.png"));
            ancho = (int) (img.getWidth() * ESCALA);
            alto = (int) (img.getHeight() * ESCALA);
            imagen = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la Tarta: " + e.getMessage());
        }
    }

    // Equivale a "ir a posición aleatoria", pero ya dentro de la zona permitida
    public void saltarAOtroSitio() {
        x = Nino.LIM_IZQ + azar.nextInt(Nino.LIM_DER - Nino.LIM_IZQ + 1);
        y = Nino.LIM_ABAJO + azar.nextInt(Nino.LIM_ARRIBA - Nino.LIM_ABAJO + 1);
    }

    // Rectángulo que ocupa en pantalla (para detectar colisiones)
    public Rectangle area() {
        return new Rectangle(Juego.pantallaX(x) - ancho / 2,
                            Juego.pantallaY(y) - alto / 2, ancho, alto);
    }

    public void dibujar(Graphics2D g) {
        if (!visible || imagen == null) return;
        g.drawImage(imagen, Juego.pantallaX(x) - ancho / 2, Juego.pantallaY(y) - alto / 2, null);
    }
}