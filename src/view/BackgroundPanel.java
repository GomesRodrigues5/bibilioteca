package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Painel com imagem de fundo e cor padrão se não houver imagem
 */
public class BackgroundPanel extends JPanel {
    private Image background;

    public BackgroundPanel() {
        setOpaque(true);
        try {
            background = ImageIO.read(new File("src/view/img/fundo.png"));
        } catch (IOException e) {
            background = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(15, 40, 65));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
