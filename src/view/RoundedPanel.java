package view;

import javax.swing.*;
import java.awt.*;

/**
 * Painel com bordas arredondadas e fundo customizável
 */
public class RoundedPanel extends JPanel {
    private final int radius;

    public RoundedPanel(int radius, Color bgColor) {
        super();
        this.radius = radius;
        setOpaque(false);
        setBackground(bgColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(new Color(255, 255, 255, 200));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
