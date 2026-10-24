package view;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Renderizador de botões em células de tabela
 */
public class ButtonRenderer extends JPanel implements TableCellRenderer {
    private JButton btnVisualizar = new RoundedButton("Visualizar", 20);
    private JButton btnEditar = new RoundedButton("Editar", 20);
    private JButton btnDeletar = new RoundedButton("Deletar", 20);

    public ButtonRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 3, 2));
        setOpaque(true);

        btnVisualizar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnVisualizar.setBackground(new Color(0, 123, 255));
        btnVisualizar.setForeground(Color.WHITE);
        btnVisualizar.setBorderPainted(false);
        btnVisualizar.setFocusPainted(false);
        btnVisualizar.setPreferredSize(new Dimension(65, 24));
        btnVisualizar.setMargin(new Insets(2, 6, 2, 6));

        btnEditar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnEditar.setBackground(new Color(255, 193, 7));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setBorderPainted(false);
        btnEditar.setFocusPainted(false);
        btnEditar.setPreferredSize(new Dimension(55, 24));
        btnEditar.setMargin(new Insets(2, 6, 2, 6));

        btnDeletar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnDeletar.setBackground(new Color(220, 53, 69));
        btnDeletar.setForeground(Color.WHITE);
        btnDeletar.setBorderPainted(false);
        btnDeletar.setFocusPainted(false);
        btnDeletar.setPreferredSize(new Dimension(55, 24));
        btnDeletar.setMargin(new Insets(2, 6, 2, 6));

        add(btnVisualizar);
        add(btnEditar);
        add(btnDeletar);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(Color.WHITE);
        }
        return this;
    }
}
