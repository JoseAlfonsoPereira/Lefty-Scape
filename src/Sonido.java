import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Sonido {

    private static final Map<String, Clip> sonidos = new HashMap<>();

    public static void cargar(String nombre, String archivo) {
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(new File(archivo))) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            sonidos.put(nombre, clip);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el sonido " + archivo + ": " + e.getMessage());
        }
    }

    // "iniciar sonido": si ya estaba sonando, vuelve a empezar
    public static void reproducir(String nombre) {
        Clip clip = sonidos.get(nombre);
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public static void repetir(String nombre) {
        Clip clip = sonidos.get(nombre);
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void parar(String nombre) {
        Clip clip = sonidos.get(nombre);
        if (clip != null) clip.stop();
    }

    public static void pararTodos() {
        for (Clip clip : sonidos.values()) clip.stop();
    }
}