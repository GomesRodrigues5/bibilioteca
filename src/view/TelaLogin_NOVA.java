package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import controller.FuncionarioController;
import model.Funcionario;
import util.TemasApp;
import util.AuthenticationManager;

/**
 * Tela de Login profissional para o Sistema de Gestão de Biblioteca.
 * Interface moderna com tema corporativo.
 */
public class TelaLogin_NOVA extends JFrame {
    
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnCancelar;
    private JLabel lblStatus;
    private FuncionarioController funcionarioController;
    
    private static final int LARGURA = 500;
    private static final int ALTURA = 600;

    public TelaLogin_NOVA() {
        inicializarComponentes();
        configurarJanela();
    }

    private void inicializarComponentes() {
        // Aplicar tema
        TemasApp.aplicarTemaModerno();
        
        // Painel principal
        JPanel painelPrincipal = TemasApp.criarPainelEstilizado();
        painelPrincipal.setLayout(new BorderLayout());

        // Painel de cabeçalho
        JPanel painelCabecalho = TemasApp.criarPainelCabecalho();
        painelCabecalho.setLayout(new FlexLayout(FlexLayout.CENTER));
        JLabel lblTitulo = new JLabel("Sistema de Gestão");
        lblTitulo.setFont(TemasApp.FONTE_TITULO);
        lblTitulo.setForeground(TemasApp.COR_TEXTO_CLARO);
        painelCabecalho.add(lblTitulo);
        
        JLabel lblSubtitulo = new JLabel("Biblioteca Universitária");
        lblSubtitulo.setFont(TemasApp.FONTE_SUBTITULO);
        lblSubtitulo.setForeground(TemasApp.COR_TEXTO_CLARO);
        painelCabecalho.add(lblSubtitulo);

        // Painel de login
        JPanel painelLogin = new JPanel();
        painelLogin.setBackground(Color.WHITE);
        painelLogin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TemasApp.COR_BORDA, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GroupLayout layout = new GroupLayout(painelLogin);
        painelLogin.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(false);

        // Labels
        JLabel lblUsuarioLabel = TemasApp.criarLabelEstilizado("Usuário:");
        JLabel lblSenhaLabel = TemasApp.criarLabelEstilizado("Senha:");

        // Campos de entrada
        txtUsuario = TemasApp.criarCampoTextoEstilizado(20);
        // Placeholder removido para compatibilidade com JTextField padrão
        
        txtSenha = new JPasswordField(20);
        txtSenha.setBackground(Color.WHITE);
        txtSenha.setForeground(TemasApp.COR_TEXTO);
        txtSenha.setFont(TemasApp.FONTE_CAMPO);
        txtSenha.setPreferredSize(new Dimension(200, TemasApp.ALTURA_CAMPO));
        txtSenha.setBorder(BorderFactory.createLineBorder(TemasApp.COR_BORDA, 1));

        // Botões
        btnEntrar = TemasApp.criarBotaoEstilizado("Entrar");
        btnCancelar = TemasApp.criarBotaoEstilizado("Cancelar");
        btnCancelar.setBackground(TemasApp.COR_BORDA);
        
        // Label de status
        lblStatus = new JLabel();
        lblStatus.setFont(TemasApp.FONTE_PEQUENA);
        lblStatus.setForeground(TemasApp.COR_ERRO);

        // Layout dos componentes
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(lblUsuarioLabel)
            .addComponent(txtUsuario)
            .addComponent(lblSenhaLabel)
            .addComponent(txtSenha)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnEntrar)
                .addComponent(btnCancelar))
            .addComponent(lblStatus)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(lblUsuarioLabel)
            .addComponent(txtUsuario)
            .addGap(15)
            .addComponent(lblSenhaLabel)
            .addComponent(txtSenha)
            .addGap(25)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(btnEntrar)
                .addComponent(btnCancelar))
            .addGap(10)
            .addComponent(lblStatus)
        );

        // Painel inferior (rodapé)
        JPanel painelRodape = new JPanel();
        painelRodape.setBackground(TemasApp.COR_FUNDO);
        JLabel lblRodape = new JLabel("© 2026 Biblioteca Universitária - Todos os direitos reservados");
        lblRodape.setFont(TemasApp.FONTE_PEQUENA);
        lblRodape.setForeground(TemasApp.COR_TEXTO);
        painelRodape.add(lblRodape);

        // Adicionar componentes ao painel principal
        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);
        painelPrincipal.add(painelLogin, BorderLayout.CENTER);
        painelPrincipal.add(painelRodape, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);

        // Eventos
        adicionarEventos();
    }

    private void adicionarEventos() {
        btnEntrar.addActionListener(e -> realizarLogin());
        btnCancelar.addActionListener(e -> sair());
        
        // Enter para fazer login
        txtSenha.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarLogin();
                }
            }
        });
    }

    private void realizarLogin() {
        String usuario = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        // Validação básica
        if (usuario.isEmpty() || senha.isEmpty()) {
            lblStatus.setText("Usuário e senha são obrigatórios");
            TemasApp.mostrarAviso(this, "Digite usuário e senha");
            return;
        }

        // Autenticar
        try {
            funcionarioController = new FuncionarioController();
            Funcionario funcionario = funcionarioController.autenticar(usuario, senha);

            if (funcionario != null) {
                lblStatus.setForeground(TemasApp.COR_SUCESSO);
                lblStatus.setText("Login realizado com sucesso!");
                
                // Salvar sessão
                AuthenticationManager.setFuncionarioLogado(funcionario);
                
                // Abrir tela principal
                new TelaPrincipalAdmin().setVisible(true);
                dispose();
            } else {
                lblStatus.setText("Usuário ou senha inválidos");
                txtSenha.setText("");
            }
        } catch (Exception ex) {
            lblStatus.setText("Erro ao fazer login: " + ex.getMessage());
            System.err.println("Erro no login: " + ex.getMessage());
        }
    }

    private void sair() {
        if (TemasApp.confirmar(this, "Deseja sair do sistema?")) {
            System.exit(0);
        }
    }

    private void configurarJanela() {
        setTitle("Sistema de Gestão de Biblioteca - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(LARGURA, ALTURA);
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImage(criarIcono());
    }

    private Image criarIcono() {
        try {
            BufferedImage imagem = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = imagem.createGraphics();
            g2d.setColor(TemasApp.COR_PRIMARIA);
            g2d.fillRect(0, 0, 32, 32);
            g2d.setColor(TemasApp.COR_TEXTO_CLARO);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("B", 8, 24);
            g2d.dispose();
            return imagem;
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin_NOVA().setVisible(true));
    }
}

/**
 * Classe auxiliar para layout flexível
 */
class FlexLayout implements LayoutManager {
    public static final int CENTER = 0;
    private int alignment;

    public FlexLayout(int alignment) {
        this.alignment = alignment;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {}

    @Override
    public void removeLayoutComponent(Component comp) {}

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return new Dimension(400, 200);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(200, 100);
    }

    @Override
    public void layoutContainer(Container parent) {
        Component[] comps = parent.getComponents();
        int y = 20;
        for (Component comp : comps) {
            int x = (parent.getWidth() - comp.getPreferredSize().width) / 2;
            comp.setBounds(x, y, comp.getPreferredSize().width, comp.getPreferredSize().height);
            y += comp.getPreferredSize().height + 10;
        }
    }
}
