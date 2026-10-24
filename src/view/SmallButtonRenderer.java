package view;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Renderizador de botão simples em células de tabela
 */
public class SmallButtonRenderer extends JButton implements TableCellRenderer {
    public SmallButtonRenderer() {
        setText("Entrega");
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setBackground(new Color(255, 140, 0));
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        setFocusPainted(false);
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
        setPreferredSize(new Dimension(80, 26));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(new Color(255, 165, 79));
        } else {
            setBackground(new Color(255, 140, 0));
        }
        return this;
    }
}
