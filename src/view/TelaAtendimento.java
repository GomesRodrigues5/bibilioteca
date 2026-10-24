package view;

import controller.EstudanteController;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import model.Estudante;

public class TelaAtendimento extends JDialog {
    private JTextField txtNumeroEstudante;
    private JButton btnVerificar;
    private JButton btnClose;
    private JLabel lblNome;
    private JLabel lblCurso;
    private JLabel lblComputadorAtribuido;
    private JTable tableAtendimentos;

    private EstudanteController estudanteController = new EstudanteController();

    public TelaAtendimento(JFrame parent) {
        super(parent, "Atendimento - Sistema de Biblioteca", true); // true = modal

        setSize(600, 400);
        setLocationRelativeTo(parent); // Centraliza na tela principal
        setUndecorated(true); // Remove bordas para não poder mover
        setShape(new RoundRectangle2D.Float(0, 0, 600, 400, 24, 24));
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(36, 207, 215), 2, true)); // Borda no tom do logotipo
        setLayout(new BorderLayout());

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 100, 100);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText("VERIFICAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        txtNumeroEstudante = new JTextField();
        ((AbstractDocument) txtNumeroEstudante.getDocument()).setDocumentFilter(new DigitOnlyFilter());
        JPanel fieldWrapper = createLabeledFieldPanel("Número do Estudante", "src/view/icon/user.png", txtNumeroEstudante, 260, 50);

        btnVerificar = new RoundedButton("VERIFICAR", 24);
        btnVerificar.setPreferredSize(new Dimension(150, 42));
        btnVerificar.setBackground(new Color(255, 140, 0));
        btnVerificar.setForeground(Color.WHITE);
        btnVerificar.setFocusPainted(false);
        btnVerificar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnVerificar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerificar.setBorder(new LineBorder(new Color(255, 140, 0), 2, true));
        btnVerificar.setMargin(new Insets(0, 0, 0, 0));
        btnVerificar.addActionListener(e -> verificarEstudante());

        // Botão Close no canto superior direito
        btnClose = new JButton();
        ImageIcon closeIcon = loadIcon("src/view/icon/close.png", 16, 16);
        if (closeIcon != null) {
            btnClose.setIcon(closeIcon);
        } else {
            btnClose.setText("✕");
            btnClose.setFont(new Font("Arial", Font.BOLD, 16));
            btnClose.setForeground(Color.RED);
        }
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setToolTipText("Fechar");
        btnClose.addActionListener(e -> dispose()); // Fecha o dialog

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topRightPanel.setOpaque(false);
        topRightPanel.add(btnClose);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        actionPanel.setOpaque(false);
        actionPanel.add(fieldWrapper);
        actionPanel.add(btnVerificar);

        JPanel topPanel = new JPanel(new BorderLayout(20, 0));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        logoPanel.setPreferredSize(new Dimension(140, 140));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        topPanel.add(logoPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.CENTER);
        topPanel.add(topRightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel panelInfo = new JPanel(new GridLayout(3, 2));
        panelInfo.add(new JLabel("Nome:"));
        lblNome = new JLabel();
        panelInfo.add(lblNome);
        panelInfo.add(new JLabel("Curso:"));
        lblCurso = new JLabel();
        panelInfo.add(lblCurso);
        panelInfo.add(new JLabel("Computador Atribuído:"));
        lblComputadorAtribuido = new JLabel();
        panelInfo.add(lblComputadorAtribuido);

        add(panelInfo, BorderLayout.CENTER);

        tableAtendimentos = new JTable(); // TODO: Configurar modelo da tabela
        add(new JScrollPane(tableAtendimentos), BorderLayout.SOUTH);

        setVisible(true);
    }

    private static class RoundedInputPanel extends JPanel {
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
            g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JPanel createLabeledFieldPanel(String placeholder, String iconPath, JTextField field, int width, int height) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel inputPanel = new RoundedInputPanel(24, new Color(36, 207, 215));
        inputPanel.setLayout(new BorderLayout(8, 0));
        inputPanel.setOpaque(false);
        inputPanel.setPreferredSize(new Dimension(width, height));
        inputPanel.setMinimumSize(new Dimension(width, height));

        JLabel iconLabel = new JLabel();
        ImageIcon icon = loadIcon(iconPath, 24, 24);
        if (icon != null) {
            iconLabel.setIcon(icon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        }
        inputPanel.add(iconLabel, BorderLayout.WEST);

        field.setBorder(BorderFactory.createEmptyBorder(height / 4 - 2, 8, height / 4 - 2, 8));
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setText(placeholder);
        field.setForeground(new Color(150, 150, 150));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(30, 40, 50));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(150, 150, 150));
                }
            }
        });
        inputPanel.add(field, BorderLayout.CENTER);

        wrapper.add(inputPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(new java.io.File(path));
            java.awt.Image scaled = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (java.io.IOException e) {
            return null;
        }
    }

    private static class DigitOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null || text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private void verificarEstudante() {
        String numero = txtNumeroEstudante.getText();
        Estudante estudante = estudanteController.buscarPorNumero(numero);
        if (estudante != null) {
            lblNome.setText(estudante.getNome());
            lblCurso.setText(String.valueOf(estudante.getIdCurso())); // TODO: Buscar nome do curso
            // TODO: Atribuir computador e iniciar timer
        } else {
            JOptionPane.showMessageDialog(this, "Estudante não encontrado!");
        }
    }
}