package view;

import javax.swing.*;
import java.awt.*;

/**
 * Painel de entrada com bordas arredondadas
 */
public class RoundedInputPanel extends JPanel {
    private final int radius;
    private final Color borderColor;

    public RoundedInputPanel(int radius, Color borderColor) {
        super();
        this.radius = radius;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
