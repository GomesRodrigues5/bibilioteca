package util;
import java.awt.Toolkit;

public class AlertaVoz {
    
    public static void falar(String texto) {
        try {
            // Usar beep do sistema como alerta sonoro
            Toolkit.getDefaultToolkit().beep();

            // Exibir mensagem no console
            System.out.println("🎵 Alerta de voz: " + texto);

        } catch (Exception e) {
            System.err.println("Erro no alerta de voz: " + e.getMessage());
        }
    }

    // Método alternativo usando Java Sound para tocar um arquivo de áudio
    public static void tocarSomAlerta() {
        try {
            // Toca dois beeps do sistema
            Toolkit.getDefaultToolkit().beep();
            Thread.sleep(200);
            Toolkit.getDefaultToolkit().beep();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}