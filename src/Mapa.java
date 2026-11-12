import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Mapa {

    // Sprite5 a Sprite17 de Scratch: {izquierda, abajo, derecha, arriba}, en coordenadas de Scratch
    static final double[][] PAREDES = {
        {  -43.8, -122.7,   -1.9,  -82.2 },   // Sprite5
        {   32.8, -176.5,  239.2, -139.5 },   // Sprite6
        {  128.8, -163.0,  358.8,  -40.5 },   // Sprite7
        { -133.8,  -89.0,  -78.2,  -82.0 },   // Sprite8
        { -243.8, -177.5, -138.8,  -81.0 },   // Sprite9
        { -218.8,  -83.0, -175.8,  -60.0 },   // Sprite10
        { -240.8,  -57.0, -222.2,   57.0 },   // Sprite11
        { -239.8,   15.0, -177.2,   55.0 },   // Sprite12
        { -238.8,  139.5,  -79.2,  180.0 },   // Sprite13
        {   29.8,  142.0,  236.2,  179.5 },   // Sprite14
        {  162.8,   44.0,  242.2,   58.0 },   // Sprite15
        {  -44.7, -145.0,  -43.4, -131.0 },   // Sprite16
        {   -3.1, -148.6,   -1.8, -134.6 },   // Sprite17
    };

    final List<Rectangle2D.Double> paredes = new ArrayList<>();
    boolean activo = false;      // empiezan escondidas, como en Scratch

    public Mapa() {
        for (double[] p : PAREDES) {
            paredes.add(new Rectangle2D.Double(p[0], p[1], p[2] - p[0], p[3] - p[1]));
        }
    }

    // ¿Choca esta caja (en coordenadas de Scratch) con alguna pared?
    public boolean choca(Rectangle2D.Double caja) {
        if (!activo) return false;       // un sprite escondido no detecta colisiones
        for (Rectangle2D.Double pared : paredes) {
            if (pared.intersects(caja)) return true;
        }
        return false;
    }

    public void dibujar(Graphics2D g) {
        if (!activo) return;
        g.setColor(Color.BLACK);
        for (Rectangle2D.Double p : paredes) {
            int izq = Juego.pantallaX(p.x);
            int arriba = Juego.pantallaY(p.y + p.height);
            g.fillRect(izq, arriba, (int) Math.ceil(p.width * Juego.K), (int) Math.ceil(p.height * Juego.K));
        }
    }
}