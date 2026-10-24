package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import controller.FuncionarioController;
import model.Funcionario;


public class TelaLogin extends JFrame {
    private static final String USUARIO_PLACEHOLDER = "Usuário";
    private static final String SENHA_PLACEHOLDER = "Senha";

    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JComboBox<String> cbTipoUsuario;
    private JButton btnEntrar;
    private JLabel lblToggleSenha;

    private static class TelaLoginResizeAdapter extends java.awt.event.ComponentAdapter {
        private final JPanel cardPanel;

        public TelaLoginResizeAdapter(JPanel cardPanel) {
            this.cardPanel = cardPanel;
        }

        @Override
        public void componentResized(java.awt.event.ComponentEvent e) {
            int targetWidth = Math.max(360, ((java.awt.Window) e.getComponent()).getWidth() / 2);
            int targetHeight = 620;
            cardPanel.setPreferredSize(new Dimension(targetWidth, targetHeight));
            cardPanel.revalidate();
        }
    }

    private class UsernameFocusAdapter extends java.awt.event.FocusAdapter {
        private final String placeholder;

        public UsernameFocusAdapter(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (txtUsuario.getText().equals(placeholder)) {
                txtUsuario.setText("");
                txtUsuario.setForeground(new Color(30, 40, 50));
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (txtUsuario.getText().isEmpty()) {
                txtUsuario.setText(placeholder);
                txtUsuario.setForeground(new Color(150, 150, 150));
            }
        }
    }

    private class PasswordFocusAdapter extends java.awt.event.FocusAdapter {
        private final String placeholder;

        public PasswordFocusAdapter(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        public void focusGained(java.awt.event.FocusEvent e) {
            if (String.valueOf(txtSenha.getPassword()).equals(placeholder)) {
                txtSenha.setText("");
                txtSenha.setEchoChar('•');
                txtSenha.setForeground(new Color(30, 40, 50));
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent e) {
            if (txtSenha.getPassword().length == 0) {
                txtSenha.setText(placeholder);
                txtSenha.setEchoChar((char) 0);
                txtSenha.setForeground(new Color(150, 150, 150));
            }
        }
    }

    private class ToggleSenhaMouseAdapter extends MouseAdapter {
        private final String placeholder;

        public ToggleSenhaMouseAdapter(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (txtSenha.getEchoChar() == 0 && !String.valueOf(txtSenha.getPassword()).equals(placeholder)) {
                txtSenha.setEchoChar('•');
                lblToggleSenha.setText("Mostrar");
            } else if (!String.valueOf(txtSenha.getPassword()).equals(placeholder)) {
                txtSenha.setEchoChar((char) 0);
                lblToggleSenha.setText("Ocultar");
            }
        }
    }

    public TelaLogin() {
        
        setTitle("Login - Sistema de Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(backgroundPanel);

        JPanel cardPanel = new RoundedPanel(40, new Color(255, 255, 255, 240));
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        cardPanel.setPreferredSize(new Dimension(getWidth() / 2, 620));

        GridBagConstraints gbcCard = new GridBagConstraints();
        gbcCard.fill = GridBagConstraints.NONE;
        gbcCard.anchor = GridBagConstraints.CENTER;
        gbcCard.weightx = 1.0;
        gbcCard.weighty = 1.0;
        
        backgroundPanel.add(cardPanel, gbcCard);

        addComponentListener(new TelaLoginResizeAdapter(cardPanel));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0);

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon logoIcon = loadIcon("src/view/img/logotipobranco.png", 220, 220);
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        } else {
            logoLabel.setText("UÓR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
            logoLabel.setForeground(new Color(20, 124, 146));
        }
        cardPanel.add(logoLabel, gbc);

        gbc.gridy++;
        JLabel titleLabel = new JLabel("LOGIN DO SISTEMA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 35, 50));
        cardPanel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Acesse Sua Conta UÓR", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(100, 110, 130));
        cardPanel.add(subtitleLabel, gbc);

       
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 10, 0);
        cardPanel.add(createLabeledFieldPanel("Usuário", "src/view/icon/user.png", txtUsuario = new JTextField(), false), gbc);

        /*gbc.gridy++;
        gbc.insets = new Insets(10, 0, 20, 0);
        cardPanel.add(createComboPanel("Tipo de usuário", "src/view/img/logotipo.png", new String[]{"Tipo de usuário", "Funcionário", "Administrador"}), gbc);
      */
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        cardPanel.add(createLabeledFieldPanel("Senha", "src/view/icon/pass.png", txtSenha = new JPasswordField(), true), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(25, 0, 15, 0);
        btnEntrar = new RoundedButton("ENTRAR", 24);
        btnEntrar.setPreferredSize(new Dimension(100, 40));
        btnEntrar.setMinimumSize(new Dimension(100, 40));
        btnEntrar.setBackground(new Color(255, 140, 0));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setBorder(new LineBorder(new Color(255, 140, 0), 2, true));
        btnEntrar.addActionListener(e -> {
            System.err.println("\n[BOTÃO] ⚡ BOTÃO ENTRAR CLICADO!");
            autenticarFuncionario();
        });
        cardPanel.add(btnEntrar, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        JLabel forgotLabel = new JLabel("Esqueceu a senha?", SwingConstants.CENTER);
        forgotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotLabel.setForeground(new Color(80, 90, 110));
        cardPanel.add(forgotLabel, gbc);

        gbc.gridy++;
        JLabel createLabel = new JLabel("Criar Nova Conta", SwingConstants.CENTER);
        createLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        createLabel.setForeground(new Color(80, 90, 110));
        cardPanel.add(createLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(9, 0, 0, 0);
        JLabel footerLabel = new JLabel("UÓR - Sistema de Biblioteca © 2026", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(120, 130, 150));
       

        cardPanel.add(footerLabel, gbc);

        setVisible(true);
    }



    private JPanel createLabeledFieldPanel(String placeholder, String iconPath, JComponent field, boolean isPassword) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel inputPanel = new RoundedInputPanel(24, new Color(36, 207, 215));
        inputPanel.setLayout(new BorderLayout(5, 0)); // Reduzir gap horizontal
        inputPanel.setOpaque(false);
        inputPanel.setPreferredSize(new Dimension(30, 48));
        inputPanel.setMinimumSize(new Dimension(30, 48));

        JLabel iconLabel = new JLabel();
        ImageIcon icon = loadIcon(iconPath, 24, 24);
        if (icon != null) {
            iconLabel.setIcon(icon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // Adicionar padding à esquerda
        }
        inputPanel.add(iconLabel, BorderLayout.WEST);

        field.setBorder(BorderFactory.createEmptyBorder(12, 5, 12, 8));
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        if (isPassword) {
            txtSenha = (JPasswordField) field;
            txtSenha.setText(SENHA_PLACEHOLDER);
            txtSenha.setForeground(new Color(150, 150, 150));
            txtSenha.setEchoChar((char) 0);
            txtSenha.addFocusListener(new PasswordFocusAdapter(SENHA_PLACEHOLDER));
            inputPanel.add(txtSenha, BorderLayout.CENTER);

            lblToggleSenha = new JLabel("Mostrar");
            lblToggleSenha.setForeground(new Color(80, 90, 110));
            lblToggleSenha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblToggleSenha.setCursor(new Cursor(Cursor.HAND_CURSOR));
            lblToggleSenha.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5)); // Ajustar padding à direita
            lblToggleSenha.addMouseListener(new ToggleSenhaMouseAdapter(SENHA_PLACEHOLDER));
            JPanel togglePanel = new JPanel(new BorderLayout());
            togglePanel.setOpaque(false);
            togglePanel.setPreferredSize(new Dimension(50, 24)); // Definir tamanho fixo menor
            togglePanel.add(lblToggleSenha, BorderLayout.CENTER);
            inputPanel.add(togglePanel, BorderLayout.EAST);
        } else {
            txtUsuario = (JTextField) field;
            txtUsuario.setText(USUARIO_PLACEHOLDER);
            txtUsuario.setForeground(new Color(150, 150, 150));
            txtUsuario.addFocusListener(new UsernameFocusAdapter(USUARIO_PLACEHOLDER));
            inputPanel.add(txtUsuario, BorderLayout.CENTER);
        }

        wrapper.add(inputPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createComboPanel(String placeholder, String iconPath, String[] options) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel inputPanel = new RoundedInputPanel(24, new Color(36, 207, 215));
        inputPanel.setLayout(new BorderLayout(5, 0)); // Reduzir gap horizontal
        inputPanel.setOpaque(false);
        inputPanel.setPreferredSize(new Dimension(100, 70));
        inputPanel.setMinimumSize(new Dimension(200, 60));

        JLabel iconLabel = new JLabel();
        ImageIcon icon = loadIcon(iconPath, 24, 24);
        if (icon != null) {
            iconLabel.setIcon(icon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // Ajustar padding à esquerda
        }
        inputPanel.add(iconLabel, BorderLayout.WEST);

        String[] comboOptions = options;
        if (options.length == 0 || !options[0].equals(placeholder)) {
            comboOptions = new String[options.length + 1];
            comboOptions[0] = placeholder;
            System.arraycopy(options, 0, comboOptions, 1, options.length);
        }

        cbTipoUsuario = new JComboBox<>(comboOptions);
        cbTipoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbTipoUsuario.setBackground(Color.WHITE);
        cbTipoUsuario.setForeground(new Color(150, 150, 150));
        cbTipoUsuario.setBorder(BorderFactory.createEmptyBorder(12, 5, 12, 8));
        cbTipoUsuario.setSelectedIndex(0);
        cbTipoUsuario.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                if (cbTipoUsuario.getSelectedIndex() == 0) {
                    cbTipoUsuario.setForeground(new Color(150, 150, 150));
                } else {
                    cbTipoUsuario.setForeground(new Color(30, 40, 50));
                }
            }
        });
        inputPanel.add(cbTipoUsuario, BorderLayout.CENTER);

        wrapper.add(inputPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Autentica o funcionário usando as credenciais informadas
     */
    private void autenticarFuncionario() {
        System.out.println("\n[LOGIN] ⚡ BOTÃO ENTRAR CLICADO");
        
        String usuario = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();
        
        if (USUARIO_PLACEHOLDER.equals(usuario)) {
            usuario = "";
        }
        if (SENHA_PLACEHOLDER.equals(senha)) {
            senha = "";
        }

        System.out.println("[LOGIN] Usuário: '" + usuario + "'");
        System.out.println("[LOGIN] Senha: '" + senha + "'");
        
        // Validação 1: Usuário vazio
        if (usuario.isEmpty()) {
            System.out.println("[LOGIN] ❌ ERRO: Campo usuário está vazio!");
            JOptionPane.showMessageDialog(this, "Por favor, informe o nome do usuário.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtUsuario.requestFocus();
            return;
        }
        
        // Validação 2: Senha vazia
        if (senha.isEmpty()) {
            System.out.println("[LOGIN] ❌ ERRO: Campo senha está vazio!");
            JOptionPane.showMessageDialog(this, "Por favor, informe a senha.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtSenha.requestFocus();
            return;
        }
        
        System.out.println("[LOGIN] ✓ Campos preenchidos, validando credenciais...");
        
        // Autenticar
        try {
            FuncionarioController controller = new FuncionarioController();
            Funcionario funcionario = controller.autenticar(usuario, senha);
            
            if (funcionario!=null) {
                System.out.println("[LOGIN] ✓✓✓ AUTENTICAÇÃO SUCESSO! Bem-vindo: " + funcionario.getNome());
                new TelaPrincipalAdmin(funcionario);
                dispose();
            } else {
                System.out.println("[LOGIN] ❌ Usuário ou senha incorretos!");
                JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
                txtSenha.setText("");
                txtUsuario.requestFocus();
            }
        } catch (Exception ex) {
            System.err.println("[LOGIN ERROR] " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao autenticar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
        System.out.println();
    }


}