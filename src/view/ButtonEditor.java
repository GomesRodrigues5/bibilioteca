package view;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Editor de botões em células de tabela
 */
public class ButtonEditor extends DefaultCellEditor {
    private JPanel panel;
    private JButton btnVisualizar;
    private JButton btnEditar;
    private JButton btnDeletar;
    private JTable table;
    private int currentRow;
    private Consumer<Integer> onDelete;
    private Consumer<Integer> onView;
    private Consumer<Integer> onEdit;

    public ButtonEditor(JCheckBox checkBox, JTable table, Consumer<Integer> onDelete) {
        this(checkBox, table, onDelete, null, null);
    }

    public ButtonEditor(JCheckBox checkBox, JTable table, Consumer<Integer> onDelete, Consumer<Integer> onView) {
        this(checkBox, table, onDelete, onView, null);
    }

    public ButtonEditor(JCheckBox checkBox, JTable table, Consumer<Integer> onDelete, Consumer<Integer> onView, Consumer<Integer> onEdit) {
        super(checkBox);
        this.table = table;
        this.onDelete = onDelete;
        this.onView = onView;
        this.onEdit = onEdit;
        this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        btnVisualizar = new RoundedButton("Visualizar", 20);
        btnVisualizar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnVisualizar.setBackground(new Color(0, 123, 255));
        btnVisualizar.setForeground(Color.WHITE);
        btnVisualizar.setBorderPainted(false);
        btnVisualizar.setFocusPainted(false);
        btnVisualizar.setPreferredSize(new Dimension(65, 26));
        btnVisualizar.setMargin(new Insets(2, 8, 2, 8));
        btnVisualizar.addActionListener(e -> {
            if (currentRow >= 0 && onView != null) {
                onView.accept(currentRow);
            } else if (currentRow >= 0) {
                String nome = table.getValueAt(currentRow, 1).toString();
                String numero = table.getValueAt(currentRow, 2).toString();
                String curso = table.getValueAt(currentRow, 3).toString();
                String departamento = table.getValueAt(currentRow, 4).toString();
                String contacto = table.getValueAt(currentRow, 5).toString();
                JOptionPane.showMessageDialog(table,
                        "Nome: " + nome + "\nNúmero: " + numero + "\nCurso: " + curso + "\nDepartamento: " + departamento + "\nContacto: " + contacto,
                        "Detalhes do Estudante", JOptionPane.INFORMATION_MESSAGE);
            }
            fireEditingStopped();
        });

        btnEditar = new RoundedButton("Editar", 20);
        btnEditar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnEditar.setBackground(new Color(255, 193, 7));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setBorderPainted(false);
        btnEditar.setFocusPainted(false);
        btnEditar.setPreferredSize(new Dimension(55, 26));
        btnEditar.setMargin(new Insets(2, 8, 2, 8));
        btnEditar.addActionListener(e -> {
            if (currentRow >= 0 && onEdit != null) {
                onEdit.accept(currentRow);
            }
            fireEditingStopped();
        });

        btnDeletar = new RoundedButton("Deletar", 20);
        btnDeletar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnDeletar.setBackground(new Color(220, 53, 69));
        btnDeletar.setForeground(Color.WHITE);
        btnDeletar.setBorderPainted(false);
        btnDeletar.setFocusPainted(false);
        btnDeletar.setPreferredSize(new Dimension(55, 26));
        btnDeletar.setMargin(new Insets(2, 8, 2, 8));
        btnDeletar.addActionListener(e -> {
            if (currentRow >= 0 && onDelete != null) {
                onDelete.accept(currentRow);
            }
            fireEditingStopped();
        });

        panel.add(btnVisualizar);
        panel.add(btnEditar);
        panel.add(btnDeletar);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.currentRow = row;
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "Ações";
    }
}
