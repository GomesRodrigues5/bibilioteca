package view;
/*BIBILHOTECAS PRINCIPAIS*/

import controller.EstudanteController;
import controller.FuncionarioController;
import dao.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import model.*;
/*CLASSE TELA DO ADMINISTRADOR*/
public class TelaPrincipalAdmin extends JFrame {
    
    /*COMPONENTES GRAFICOS */
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel menuPanel;
    private JPanel menuWrapper;
    private boolean menuVisible = true;
    private Timer animationTimer;
    private int currentWidth;
    private static final int MENU_WIDTH = 260;
    private static final int ANIMATION_DURATION = 300;
    private static final int FRAME_HEIGHT = 720;

    // Funcionário autenticado
    private Funcionario funcionarioAutenticado;

    private EstudanteController estudanteController = new EstudanteController();
    private FuncionarioController funcionarioController = new FuncionarioController();
    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private LivroDAO livroDAO = new LivroDAO();
    private ComputadorDAO computadorDAO = new ComputadorDAO();
    private ArmarioDAO armarioDAO = new ArmarioDAO();
    private CursoDAO cursoDAO = new CursoDAO();
    private DepartamentoDAO departamentoDAO = new DepartamentoDAO();

    private DefaultTableModel estudantesTableModel;
    private JTable estudantesTable;
    private DefaultTableModel funcionariosTableModel;
    private JTable funcionariosTable;
    private DefaultTableModel livrosTableModel;
    private JTable livrosTable;
    private DefaultTableModel computadoresTableModel;
    private JTable computadoresTable;
    private DefaultTableModel armariosTableModel;
    private JTable armariosTable;
    private DefaultTableModel cursosTableModel;
    private JTable cursosTable;
    private DefaultTableModel departamentosTableModel;
    private JTable departamentosTable;

    // Construtor com suporte a funcionário autenticado
    public TelaPrincipalAdmin(Funcionario funcionario) {
        this();
        this.funcionarioAutenticado = funcionario;
    }

    public TelaPrincipalAdmin() {
        setTitle("Admin - Sistema de Biblioteca");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        menuPanel = createMenuPanel();
        menuWrapper = new JPanel(new BorderLayout());
        menuWrapper.setPreferredSize(new Dimension(MENU_WIDTH, FRAME_HEIGHT));
        menuWrapper.add(createToggleHeader(), BorderLayout.NORTH);
        menuWrapper.add(menuPanel, BorderLayout.CENTER);
        add(menuWrapper, BorderLayout.WEST);

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardPanel.add(createDashboardPanel(), "Inicio");
        cardPanel.add(createGerenciarEstudantesPanel(), "estudantes");
        cardPanel.add(createGerenciarFuncionariosPanel(), "funcionarios");
        cardPanel.add(createGerenciarLivrosPanel(), "livros");
        cardPanel.add(createGerenciarComputadoresPanel(), "computadores");
        cardPanel.add(createGerenciarArmariosPanel(), "armarios");
        cardPanel.add(createGerenciarCursosPanel(), "cursos");
        cardPanel.add(createGerenciarDepartamentosPanel(), "departamentos");
        cardPanel.add(createCardPanel("Gerenciar Empréstimos"), "emprestimos");
        cardPanel.add(createGerenciarRelatoriosPanel(), "relatorios");

        add(cardPanel, BorderLayout.CENTER);

        cardLayout.show(cardPanel, "Inicio");
        setVisible(true);
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(12, 32, 55));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon logoIcon = loadIcon("src/view/img/logotipobranco.png", 90, 90);
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        } 
        
        else 
        
        {
            logoLabel.setText("UÓR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            logoLabel.setForeground(Color.WHITE);
        }
        menuPanel.add(logoLabel);

        JLabel title = new JLabel("Sistema Biblioteca");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(10, 0, 30, 0));
        menuPanel.add(title);
        //ICONES DO MENU OPÇÃO DA TELAPRINCILA
        menuPanel.add(createMenuButton("Inicio", "inicio", "dashboard"));
        menuPanel.add(createMenuButton("Estudantes", "user", "estudantes"));
        menuPanel.add(createMenuButton("Funcionários", "funcionario", "funcionarios"));
        menuPanel.add(createMenuButton("Livros", "livro", "livros"));
        menuPanel.add(createMenuButton("Computadores", "computador", "computadores"));
        menuPanel.add(createMenuButton("Armários", "armario", "armarios"));
        menuPanel.add(createMenuButton("Cursos", "curso", "cursos"));
        menuPanel.add(createMenuButton("Departamentos", "departamentos", "departamentos"));
        menuPanel.add(createMenuButton("Empréstimos", "emprestar", "emprestimos"));
        menuPanel.add(createMenuButton("Atendimento", "atendimento", null));
        menuPanel.add(createMenuButton("Relatórios", "relatorio", "relatorios"));

        menuPanel.add(Box.createVerticalGlue());
        RoundedButton btnLogout = new RoundedButton("Sair", 24);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogout.setBackground(new Color(255, 140, 0));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(new LineBorder(new Color(255, 140, 0), 2, true));
        btnLogout.addActionListener(e -> {
            new TelaLogin();
            /*SAI DA TELA ADMIN  E CHAMA A TELALOGIN*/
            dispose();
        });
        menuPanel.add(btnLogout);

        return menuPanel;
    }

    private JPanel createToggleHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(12, 32, 55));
        headerPanel.setBorder(new EmptyBorder(8, 12, 8, 12));

        JButton toggleButton = new JButton();
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.setFocusPainted(false);
        toggleButton.setBackground(new Color(18, 46, 77));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        toggleButton.setBorder(new LineBorder(new Color(59, 133, 193), 1, true));
        toggleButton.setPreferredSize(new Dimension(40, 32));
        toggleButton.setMargin(new Insets(0, 0, 0, 0));

        // Carregar ícone close para quando o menu estiver visível
        ImageIcon closeIcon = loadIcon("src/view/icon/close.png", 16, 16);
        if (closeIcon != null) {
            toggleButton.setIcon(closeIcon);
        }

        toggleButton.addActionListener(e -> {
            animateMenuToggle(toggleButton);
        });
        headerPanel.add(toggleButton, BorderLayout.EAST);

        return headerPanel;
    }

    private void animateMenuToggle(JButton toggleButton) {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }

        final long startTime = System.currentTimeMillis();
        final int startWidth = menuWrapper.getWidth();
        final int targetWidth = menuVisible ? 50 : MENU_WIDTH;

        animationTimer = new Timer(16, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= ANIMATION_DURATION) {
                animationTimer.stop();
                menuVisible = !menuVisible;
                menuPanel.setVisible(menuVisible);
                menuWrapper.setPreferredSize(new Dimension(targetWidth, FRAME_HEIGHT));

                // Trocar ícones baseado no estado do menu
                if (menuVisible) {
                    ImageIcon closeIcon = loadIcon("src/view/icon/close.png", 16, 16);
                    toggleButton.setIcon(closeIcon);
                } else {
                    ImageIcon menuIcon = loadIcon("src/view/icon/menu.png", 16, 16);
                    toggleButton.setIcon(menuIcon);
                }

                revalidate();
                repaint();
            } else {
                float progress = (float) elapsed / ANIMATION_DURATION;
                int newWidth = (int) (startWidth + (targetWidth - startWidth) * progress);
                menuWrapper.setPreferredSize(new Dimension(newWidth, FRAME_HEIGHT));
                
                if (menuVisible) {
                    menuPanel.setVisible(true);
                    menuPanel.setOpaque(true);
                } else if (elapsed > ANIMATION_DURATION / 2) {
                    menuPanel.setVisible(false);
                }
                
                revalidate();
                repaint();
            }
        });
        animationTimer.start();
    }

    private JButton createMenuButton(String text, String iconKey, String cardName) {
        JButton button = new JButton(text);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(new Color(18, 46, 77));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(25, 102, 153), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        button.setIconTextGap(12);

        String iconPath = "src/view/icon/" + iconKey + ".png";
        ImageIcon icon = loadIcon(iconPath, 24, 24);
        if (icon == null) {
            icon = loadIcon("src/view/img/logotipo.png", 24, 24);
        }
        if (icon != null) {
            button.setIcon(icon);
        }
        if (cardName != null) {
            button.addActionListener(e -> cardLayout.show(cardPanel, cardName));
        } else {
            button.addActionListener(e -> new TelaAtendimento(TelaPrincipalAdmin.this));
        }
        return button;
    }

    private JPanel createGerenciarEstudantesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Painel superior com botão adicionar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);

        JButton btnAdicionar = new RoundedButton("Adicionar Estudante", 24);
        btnAdicionar.setPreferredSize(new Dimension(180, 40));
        btnAdicionar.setBackground(new Color(34, 197, 94));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.setFocusPainted(false);
        btnAdicionar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdicionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdicionar.setBorder(new LineBorder(new Color(34, 197, 94), 2, true));
        btnAdicionar.addActionListener(e -> showAdicionarEstudanteModal());

        topPanel.add(btnAdicionar);
        panel.add(topPanel, BorderLayout.NORTH);

        // Tabela de estudantes
        String[] columnNames = {"ID", "ESTUDANTE", "NÚMERO", "CURSO", "DEPARTAMENTO", "CONTACTO", "ACÇÃO"};
        this.estudantesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Apenas coluna ACÇÃO é editável
            }
        };

        estudantesTable = new JTable(estudantesTableModel);
        estudantesTable.setRowHeight(35);
        estudantesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        estudantesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        estudantesTable.getTableHeader().setBackground(new Color(240, 245, 250));
        estudantesTable.getTableHeader().setForeground(new Color(30, 40, 55));
        estudantesTable.setSelectionBackground(new Color(220, 235, 255));
        estudantesTable.setGridColor(new Color(200, 210, 220));

        // Configurar coluna ACÇÃO com botões
        estudantesTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        estudantesTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), estudantesTable, this::deleteEstudanteRow));

        carregarEstudantes();

        JScrollPane scrollPane = new JScrollPane(estudantesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showAdicionarEstudanteModal() {
        JDialog dialog = new JDialog(this, "Adicionar Estudante", true);
        dialog.setSize(500, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);
        dialog.setShape(new RoundRectangle2D.Float(0, 0, 500, 520, 24, 24));
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(36, 207, 215), 2, true));
        dialog.setLayout(new BorderLayout());

        // Logo
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText("ADICIONAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        // Campos do formulário
        JTextField txtNome = new JTextField();
        JTextField txtNumero = new JTextField();
        JComboBox<EstudanteDAO.CursoItem> comboCurso = new JComboBox<>();
        JTextField txtDepartamento = new JTextField();
        JTextField txtContacto = new JTextField();

        ((AbstractDocument) txtNumero.getDocument()).setDocumentFilter(new DigitOnlyFilter());
        ((AbstractDocument) txtContacto.getDocument()).setDocumentFilter(new DigitOnlyFilter());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Nome", "src/view/icon/user.png", txtNome, 300, 50), gbc);

        gbc.gridy = 1;
        formPanel.add(createLabeledFieldPanel("Número", "src/view/icon/user.png", txtNumero, 300, 50), gbc);

        List<EstudanteDAO.CursoItem> cursos = carregarCursos();
        if (cursos.isEmpty()) {
            comboCurso.addItem(new EstudanteDAO.CursoItem(0, "Nenhum curso cadastrado"));
            comboCurso.setEnabled(false);
        } else {
            for (EstudanteDAO.CursoItem cursoItem : cursos) {
                comboCurso.addItem(cursoItem);
            }
        }

        gbc.gridy = 2;
        formPanel.add(createLabeledComboPanel("Curso", "src/view/icon/user.png", comboCurso, 300, 50), gbc);

        gbc.gridy = 3;
        formPanel.add(createLabeledFieldPanel("Departamento", "src/view/icon/user.png", txtDepartamento, 300, 50), gbc);

        gbc.gridy = 4;
        formPanel.add(createLabeledFieldPanel("Contacto", "src/view/icon/user.png", txtContacto, 300, 50), gbc);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton("SALVAR", 24);
        btnSalvar.setPreferredSize(new Dimension(140, 45));
        btnSalvar.setMargin(new Insets(8, 16, 8, 16));
        btnSalvar.setBackground(new Color(34, 197, 94));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.setBorder(new LineBorder(new Color(34, 197, 94), 2, true));
        btnSalvar.addActionListener(e -> {
            String nome = getFieldValue(txtNome, "Nome");
            String numero = getFieldValue(txtNumero, "Número");
            EstudanteDAO.CursoItem cursoSelecionado = (EstudanteDAO.CursoItem) comboCurso.getSelectedItem();
            String departamento = getFieldValue(txtDepartamento, "Departamento");
            String contacto = getFieldValue(txtContacto, "Contacto");

            if (nome.isEmpty() || numero.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha o nome e o número do estudante.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cursoSelecionado == null || cursoSelecionado.getId() == 0) {
                JOptionPane.showMessageDialog(dialog, "Selecione um curso válido.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Estudante estudante = new Estudante();
            estudante.setNome(nome);
            estudante.setNumeroEstudante(numero);
            estudante.setIdCurso(cursoSelecionado.getId());
            estudante.setCurso(cursoSelecionado.getNome());
            estudante.setDepartamento(departamento);
            estudante.setContacto(contacto);
            estudante.setEmail(null);

            try {
                estudanteController.salvarEstudante(estudante);
                carregarEstudantes();
                JOptionPane.showMessageDialog(dialog, "Estudante salvo com sucesso!");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar estudante: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = new RoundedButton("CANCELAR", 24);
        btnCancelar.setPreferredSize(new Dimension(140, 45));
        btnCancelar.setMargin(new Insets(8, 16, 8, 16));
        btnCancelar.setBackground(new Color(220, 53, 69));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setBorder(new LineBorder(new Color(220, 53, 69), 2, true));
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        // Botão Close
        JButton btnClose = new JButton();
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
        btnClose.addActionListener(e -> dialog.dispose());

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topRightPanel.setOpaque(false);
        topRightPanel.add(btnClose);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(topRightPanel, BorderLayout.EAST);

        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void carregarEstudantes() {
        if (estudantesTableModel == null) {
            return;
        }

        estudantesTableModel.setRowCount(0);
        try {
            List<Estudante> estudantes = estudanteController.listarEstudantes();
            for (Estudante estudante : estudantes) {
                estudantesTableModel.addRow(new Object[]{
                    estudante.getIdEstudante(),
                    estudante.getNome(),
                    estudante.getNumeroEstudante(),
                    estudante.getCurso(),
                    estudante.getDepartamento(),
                    estudante.getContacto(),
                    "Ações"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar estudantes: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<EstudanteDAO.CursoItem> carregarCursos() {
        try {
            return estudanteController.listarCursos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar cursos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return new java.util.ArrayList<>();
        }
    }

    private void deleteEstudanteRow(int row) {
        if (row < 0 || estudantesTableModel == null) {
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente deletar este estudante?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        int idEstudante = Integer.parseInt(estudantesTableModel.getValueAt(row, 0).toString());
        try {
            estudanteController.deletarEstudante(idEstudante);
            estudantesTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Estudante deletado com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar estudante: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getFieldValue(JTextField field, String placeholder) {
        String value = field.getText();
        if (value == null || value.trim().isEmpty() || value.equals(placeholder)) {
            return "";
        }
        return value.trim();
    }

    private JPanel createGerenciarFuncionariosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        JLabel title = new JLabel("Gerenciar Funcionários");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 20, 10, 20));
        panel.add(title, BorderLayout.NORTH);

        funcionariosTableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Cargo", "Contacto", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        funcionariosTable = new JTable(funcionariosTableModel);
        funcionariosTable.setRowHeight(40);
        funcionariosTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        funcionariosTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), funcionariosTable, this::deleteFuncionarioRow));
        panel.add(new JScrollPane(funcionariosTable), BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JButton btnNovo = createPrimaryButton("Adicionar Funcionário");
        btnNovo.addActionListener(e -> showAdicionarFuncionarioModal(null));
        headerPanel.add(btnNovo, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.SOUTH);

        carregarFuncionarios();
        return panel;
    }

    private JPanel createGerenciarLivrosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        JLabel title = new JLabel("Gerenciar Livros");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 20, 10, 20));
        panel.add(title, BorderLayout.NORTH);

        livrosTableModel = new DefaultTableModel(new Object[]{"ID", "Título", "Cor", "Código", "Armário", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        livrosTable = new JTable(livrosTableModel);
        livrosTable.setRowHeight(40);
        livrosTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        livrosTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), livrosTable, this::deleteLivroRow));
        panel.add(new JScrollPane(livrosTable), BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JButton btnNovo = createPrimaryButton("Adicionar Livro");
        btnNovo.addActionListener(e -> showAdicionarLivroModal(null));
        headerPanel.add(btnNovo, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.SOUTH);

        carregarLivros();
        return panel;
    }

    private JPanel createGerenciarComputadoresPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        JLabel title = new JLabel("Gerenciar Computadores");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 20, 10, 20));
        panel.add(title, BorderLayout.NORTH);

        computadoresTableModel = new DefaultTableModel(new Object[]{"ID", "Marca", "Modelo", "Status", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        computadoresTable = new JTable(computadoresTableModel);
        computadoresTable.setRowHeight(40);
        computadoresTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        computadoresTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), computadoresTable, this::deleteComputadorRow));
        panel.add(new JScrollPane(computadoresTable), BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JButton btnNovo = createPrimaryButton("Adicionar Computador");
        btnNovo.addActionListener(e -> showAdicionarComputadorModal(null));
        headerPanel.add(btnNovo, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.SOUTH);

        carregarComputadores();
        return panel;
    }

    private JPanel createGerenciarArmariosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        JLabel title = new JLabel("Gerenciar Armários");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 20, 10, 20));
        panel.add(title, BorderLayout.NORTH);

        armariosTableModel = new DefaultTableModel(new Object[]{"ID", "Armário", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        armariosTable = new JTable(armariosTableModel);
        armariosTable.setRowHeight(40);
        armariosTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        armariosTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), armariosTable, this::deleteArmarioRow));
        panel.add(new JScrollPane(armariosTable), BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JButton btnNovo = createPrimaryButton("Adicionar Armário");
        btnNovo.addActionListener(e -> showAdicionarArmarioModal(null));
        headerPanel.add(btnNovo, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.SOUTH);

        carregarArmarios();
        return panel;
    }

    private JPanel createGerenciarCursosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        JLabel title = new JLabel("Gerenciar Cursos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 20, 10, 20));
        panel.add(title, BorderLayout.NORTH);

        cursosTableModel = new DefaultTableModel(new Object[]{"ID", "Curso", "Departamento", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        cursosTable = new JTable(cursosTableModel);
        cursosTable.setRowHeight(40);
        cursosTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        cursosTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), cursosTable, this::deleteCursoRow));
        panel.add(new JScrollPane(cursosTable), BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JButton btnNovo = createPrimaryButton("Adicionar Curso");
        btnNovo.addActionListener(e -> showAdicionarCursoModal(null));
        headerPanel.add(btnNovo, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.SOUTH);

        carregarCursosTabela();
        return panel;
    }

    private JPanel createGerenciarDepartamentosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        JLabel title = new JLabel("Gerenciar Departamentos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 20, 10, 20));
        panel.add(title, BorderLayout.NORTH);

        departamentosTableModel = new DefaultTableModel(new Object[]{"ID", "Departamento", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        departamentosTable = new JTable(departamentosTableModel);
        departamentosTable.setRowHeight(40);
        departamentosTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        departamentosTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), departamentosTable, this::deleteDepartamentoRow));
        panel.add(new JScrollPane(departamentosTable), BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JButton btnNovo = createPrimaryButton("Adicionar Departamento");
        btnNovo.addActionListener(e -> showAdicionarDepartamentoModal(null));
        headerPanel.add(btnNovo, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.SOUTH);

        carregarDepartamentos();
        return panel;
    }

    private JPanel createGerenciarRelatoriosPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 248, 252));
        JLabel title = new JLabel("Relatórios");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 20, 10, 20));

        JTextArea reportArea = new JTextArea("Relatórios gerais do sistema aparecerão aqui.\n\n- Total de estudantes\n- Total de livros\n- Total de computadores\n- Ocupação de armários\n- Cursos cadastrados\n- Departamentos cadastrados");
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        reportArea.setBackground(new Color(245, 248, 252));
        reportArea.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(title, new GridBagConstraints());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(10, 20, 20, 20);
        panel.add(new JScrollPane(reportArea), gbc);

        return panel;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new RoundedButton(text, 20);
        button.setPreferredSize(new Dimension(180, 45));
        button.setBackground(new Color(34, 197, 94));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new LineBorder(new Color(34, 197, 94), 2, true));
        return button;
    }

    private void carregarFuncionarios() {
        funcionariosTableModel.setRowCount(0);
        try {
            List<Funcionario> funcionarios = funcionarioDAO.listarTodos();
            for (Funcionario funcionario : funcionarios) {
                funcionariosTableModel.addRow(new Object[]{
                    funcionario.getIdFuncionario(),
                    funcionario.getNome(),
                    funcionario.getFuncao(),
                    funcionario.getContacto(),
                    "Ações"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar funcionários: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarLivros() {
        livrosTableModel.setRowCount(0);
        List<Livro> livros = livroDAO.listarTodos();
        for (Livro livro : livros) {
            livrosTableModel.addRow(new Object[]{
                livro.getIdLivro(),
                livro.getTitulo(),
                livro.getCodigo(),
                livro.getArmarioNome(),
                livro.isDisponivel() ? "Disponível" : "Indisponível",
                "Ações"
            });
        }
    }

    private void carregarComputadores() {
        computadoresTableModel.setRowCount(0);
        try {
            List<Computador> computadores = computadorDAO.listarTodos();
            for (Computador computador : computadores) {
                computadoresTableModel.addRow(new Object[]{
                    computador.getIdComputador(),
                    computador.getMarca(),
                    computador.getModelo(),
                    computador.isEmUso() ? "Em uso" : "Disponível",
                    "Ações"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar computadores: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarArmarios() {
        armariosTableModel.setRowCount(0);
        try {
            List<Armario> armarios = armarioDAO.listarTodos();
            for (Armario armario : armarios) {
                armariosTableModel.addRow(new Object[]{
                    armario.getIdArmario(),
                    armario.getArmario(),
                    "Ações"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar armários: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarCursosTabela() {
        cursosTableModel.setRowCount(0);
        try {
            List<Curso> cursos = cursoDAO.listarTodos();
            for (Curso curso : cursos) {
                cursosTableModel.addRow(new Object[]{
                    curso.getIdCurso(),
                    curso.getCurso(),
                    curso.getDepartamento(),
                    "Ações"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar cursos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarDepartamentos() {
        departamentosTableModel.setRowCount(0);
        try {
            List<Departamento> departamentos = departamentoDAO.listarTodos();
            for (Departamento departamento : departamentos) {
                departamentosTableModel.addRow(new Object[]{
                    departamento.getIdDepartamento(),
                    departamento.getDepartamento(),
                    "Ações"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar departamentos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Curso> carregarCursoItems() {
        try {
            return cursoDAO.listarTodos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar cursos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return new java.util.ArrayList<>();
        }
    }

    private void deleteFuncionarioRow(int row) {
        if (row < 0 || funcionariosTableModel == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este funcionário?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(funcionariosTableModel.getValueAt(row, 0).toString());
        try {
            funcionarioDAO.deletar(id);
            funcionariosTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Funcionário deletado com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar funcionário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteLivroRow(int row) {
        if (row < 0 || livrosTableModel == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este livro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(livrosTableModel.getValueAt(row, 0).toString());
        livroDAO.deletar(id);
        livrosTableModel.removeRow(row);
        JOptionPane.showMessageDialog(this, "Livro deletado com sucesso!");
    }

    private void deleteComputadorRow(int row) {
        if (row < 0 || computadoresTableModel == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este computador?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(computadoresTableModel.getValueAt(row, 0).toString());
        try {
            computadorDAO.deletar(id);
            computadoresTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Computador deletado com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar computador: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteArmarioRow(int row) {
        if (row < 0 || armariosTableModel == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este armário?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(armariosTableModel.getValueAt(row, 0).toString());
        try {
            armarioDAO.deletar(id);
            armariosTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Armário deletado com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar armário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCursoRow(int row) {
        if (row < 0 || cursosTableModel == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este curso?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(cursosTableModel.getValueAt(row, 0).toString());
        try {
            cursoDAO.deletar(id);
            cursosTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Curso deletado com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar curso: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDepartamentoRow(int row) {
        if (row < 0 || departamentosTableModel == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este departamento?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(departamentosTableModel.getValueAt(row, 0).toString());
        try {
            departamentoDAO.deletar(id);
            departamentosTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Departamento deletado com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar departamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAdicionarFuncionarioModal(Funcionario funcionario) {
        JDialog dialog = createModalDialog("Adicionar Funcionário");
        JLabel logoLabel = new JLabel();
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText("ADICIONAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JTextField txtNome = new JTextField(funcionario != null ? funcionario.getNome() : "");
        JTextField txtCargo = new JTextField(funcionario != null ? funcionario.getFuncao() : "");
        JTextField txtContacto = new JTextField(funcionario != null ? funcionario.getContacto() : "");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Nome", "src/view/icon/user.png", txtNome, 300, 50), gbc);
        gbc.gridy = 1;
        formPanel.add(createLabeledFieldPanel("Cargo", "src/view/icon/user.png", txtCargo, 300, 50), gbc);
        gbc.gridy = 2;
        formPanel.add(createLabeledFieldPanel("Contacto", "src/view/icon/user.png", txtContacto, 300, 50), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton("SALVAR", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            String nome = getFieldValue(txtNome, "Nome");
            String cargo = getFieldValue(txtCargo, "Cargo");
            String contacto = getFieldValue(txtContacto, "Contacto");
            if (nome.isEmpty() || cargo.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha nome e cargo.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Funcionario novo = funcionario != null ? funcionario : new Funcionario();
            novo.setNome(nome);
            novo.setFuncao(cargo);
            novo.setContacto(contacto);
            try {
                if (funcionario == null) {
                    funcionarioDAO.salvar(novo);
                } else {
                    funcionarioDAO.atualizar(novo);
                }
                carregarFuncionarios();
                JOptionPane.showMessageDialog(dialog, "Funcionário salvo com sucesso!");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar funcionário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = new RoundedButton("CANCELAR", 24);
        styleActionButton(btnCancelar, new Color(220, 53, 69));
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        JPanel topPanel = createDialogTopPanel(logoLabel, dialog);
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showAdicionarLivroModal(Livro livro) {
        JDialog dialog = createModalDialog("Adicionar Livro");
        JLabel logoLabel = new JLabel();
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText("ADICIONAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JTextField txtTitulo = new JTextField(livro != null ? livro.getTitulo() : "");
        JTextField txtCor = new JTextField(livro != null ? livro.getCor() : "");
        JTextField txtPagina = new JTextField(livro != null ? String.valueOf(livro.getPagina()) : "");
        JTextField txtCodigo = new JTextField(livro != null ? livro.getCodigo() : "");
        JCheckBox cbDisponivel = new JCheckBox("Disponível", livro != null && livro.isDisponivel());
        cbDisponivel.setOpaque(false);

        JComboBox<Armario> comboArmario = new JComboBox<>();
        comboArmario.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Armario) {
                    Armario armario = (Armario) value;
                    setText(armario.getArmario() != null ? armario.getArmario() : "Nenhum armário");
                }
                return this;
            }
        });
        List<Armario> armarios = carregarArmariosParaCombo();
        if (armarios.isEmpty()) {
            comboArmario.addItem(new Armario());
            comboArmario.setEnabled(false);
        } else {
            for (Armario armario : armarios) {
                comboArmario.addItem(armario);
            }
        }
        if (livro != null && livro.getIdArmario() != 0) {
            for (int i = 0; i < comboArmario.getItemCount(); i++) {
                Armario armario = comboArmario.getItemAt(i);
                if (armario != null && armario.getIdArmario() == livro.getIdArmario()) {
                    comboArmario.setSelectedIndex(i);
                    break;
                }
            }
        }

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Título", "src/view/icon/livro.png", txtTitulo, 300, 50), gbc);
        gbc.gridy = 1;
        formPanel.add(createLabeledFieldPanel("Cor", "src/view/icon/livro.png", txtCor, 300, 50), gbc);
        gbc.gridy = 2;
        formPanel.add(createLabeledFieldPanel("Páginas", "src/view/icon/user.png", txtPagina, 300, 50), gbc);
        gbc.gridy = 3;
        formPanel.add(createLabeledFieldPanel("Código", "src/view/icon/user.png", txtCodigo, 300, 50), gbc);
        gbc.gridy = 4;
        formPanel.add(createLabeledComboPanel("Armário", "src/view/icon/armario.png", comboArmario, 300, 50), gbc);
        gbc.gridy = 5;
        JPanel disponivelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        disponivelPanel.setOpaque(false);
        disponivelPanel.add(cbDisponivel);
        formPanel.add(createLabeledComponentPanel("Disponível", "src/view/icon/check.png", disponivelPanel), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton("SALVAR", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            String titulo = getFieldValue(txtTitulo, "Título");
            String cor = getFieldValue(txtCor, "Cor");
            String paginaText = getFieldValue(txtPagina, "Páginas");
            String codigo = getFieldValue(txtCodigo, "Código");
            int paginas = paginaText.isEmpty() ? 0 : Integer.parseInt(paginaText);
            Armario armarioSelecionado = (Armario) comboArmario.getSelectedItem();
            if (titulo.isEmpty() || codigo.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha título e código.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Livro novo = livro != null ? livro : new Livro();
            novo.setTitulo(titulo);
            novo.setCor(cor);
            novo.setPagina(paginas);
            novo.setCodigo(codigo);
            novo.setDisponivel(cbDisponivel.isSelected());
            if (armarioSelecionado != null && armarioSelecionado.getIdArmario() != 0) {
                novo.setIdArmario(armarioSelecionado.getIdArmario());
            }
            if (livro == null) {
                livroDAO.salvar(novo);
            } else {
                livroDAO.atualizar(novo);
            }
            carregarLivros();
            JOptionPane.showMessageDialog(dialog, "Livro salvo com sucesso!");
            dialog.dispose();
        });

        JButton btnCancelar = new RoundedButton("CANCELAR", 24);
        styleActionButton(btnCancelar, new Color(220, 53, 69));
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        JPanel topPanel = createDialogTopPanel(logoLabel, dialog);
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showAdicionarComputadorModal(Computador computador) {
        JDialog dialog = createModalDialog("Adicionar Computador");
        JLabel logoLabel = new JLabel();
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText("ADICIONAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JTextField txtMarca = new JTextField(computador != null ? computador.getMarca() : "");
        JTextField txtModelo = new JTextField(computador != null ? computador.getModelo() : "");
        JTextField txtNumero = new JTextField(computador != null ? computador.getNumero() : "");
        JCheckBox cbEmUso = new JCheckBox("Em uso", computador != null && computador.isEmUso());
        cbEmUso.setOpaque(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Marca", "src/view/icon/computador.png", txtMarca, 300, 50), gbc);
        gbc.gridy = 1;
        formPanel.add(createLabeledFieldPanel("Modelo", "src/view/icon/computador.png", txtModelo, 300, 50), gbc);
        gbc.gridy = 2;
        formPanel.add(createLabeledFieldPanel("Número", "src/view/icon/user.png", txtNumero, 300, 50), gbc);
        gbc.gridy = 3;
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        statusPanel.add(cbEmUso);
        formPanel.add(createLabeledComponentPanel("Em uso", "src/view/icon/check.png", statusPanel), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton("SALVAR", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            String marca = getFieldValue(txtMarca, "Marca");
            String modelo = getFieldValue(txtModelo, "Modelo");
            String numero = getFieldValue(txtNumero, "Número");
            if (marca.isEmpty() || modelo.isEmpty() || numero.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha marca, modelo e número.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Computador novo = computador != null ? computador : new Computador();
            novo.setMarca(marca);
            novo.setModelo(modelo);
            novo.setNumero(numero);
            novo.setEmUso(cbEmUso.isSelected());
            try {
                if (computador == null) {
                    computadorDAO.salvar(novo);
                } else {
                    computadorDAO.atualizar(novo);
                }
                carregarComputadores();
                JOptionPane.showMessageDialog(dialog, "Computador salvo com sucesso!");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar computador: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = new RoundedButton("CANCELAR", 24);
        styleActionButton(btnCancelar, new Color(220, 53, 69));
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        JPanel topPanel = createDialogTopPanel(logoLabel, dialog);
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showAdicionarArmarioModal(Armario armario) {
        JDialog dialog = createModalDialog("Adicionar Armário");
        JLabel logoLabel = new JLabel();
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText("ADICIONAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JTextField txtArmario = new JTextField(armario != null ? armario.getArmario() : "");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Armário", "src/view/icon/armario.png", txtArmario, 300, 50), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton("SALVAR", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            String armarioNome = getFieldValue(txtArmario, "Armário");
            if (armarioNome.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha o nome do armário.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Armario novo = armario != null ? armario : new Armario();
            novo.setArmario(armarioNome);
            try {
                if (armario == null) {
                    armarioDAO.salvar(novo);
                } else {
                    armarioDAO.atualizar(novo);
                }
                carregarArmarios();
                JOptionPane.showMessageDialog(dialog, "Armário salvo com sucesso!");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar armário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = new RoundedButton("CANCELAR", 24);
        styleActionButton(btnCancelar, new Color(220, 53, 69));
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        JPanel topPanel = createDialogTopPanel(logoLabel, dialog);
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showAdicionarCursoModal(Curso curso) {
        JDialog dialog = createModalDialog("Adicionar Curso");
        JLabel logoLabel = new JLabel();
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText("ADICIONAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JTextField txtCurso = new JTextField(curso != null ? curso.getCurso() : "");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Curso", "src/view/icon/curso.png", txtCurso, 300, 50), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton("SALVAR", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            String nome = getFieldValue(txtCurso, "Curso");
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha o nome do curso.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Curso novo = curso != null ? curso : new Curso();
            novo.setCurso(nome);
            try {
                if (curso == null) {
                    cursoDAO.salvar(novo);
                } else {
                    cursoDAO.atualizar(novo);
                }
                carregarCursosTabela();
                JOptionPane.showMessageDialog(dialog, "Curso salvo com sucesso!");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar curso: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = new RoundedButton("CANCELAR", 24);
        styleActionButton(btnCancelar, new Color(220, 53, 69));
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        JPanel topPanel = createDialogTopPanel(logoLabel, dialog);
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showAdicionarDepartamentoModal(Departamento departamento) {
        JDialog dialog = createModalDialog("Adicionar Departamento");
        JLabel logoLabel = new JLabel();
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText("ADICIONAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JTextField txtDepartamento = new JTextField(departamento != null ? departamento.getDepartamento() : "");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Departamento", "src/view/icon/departamento.png", txtDepartamento, 300, 50), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton("SALVAR", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            String nome = getFieldValue(txtDepartamento, "Departamento");
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha o nome do departamento.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Departamento novo = departamento != null ? departamento : new Departamento();
            novo.setDepartamento(nome);
            try {
                if (departamento == null) {
                    departamentoDAO.salvar(novo);
                } else {
                    departamentoDAO.atualizar(novo);
                }
                carregarDepartamentos();
                JOptionPane.showMessageDialog(dialog, "Departamento salvo com sucesso!");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar departamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = new RoundedButton("CANCELAR", 24);
        styleActionButton(btnCancelar, new Color(220, 53, 69));
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        JPanel topPanel = createDialogTopPanel(logoLabel, dialog);
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JDialog createModalDialog(String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(520, 540);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);
        return dialog;
    }

    private JPanel createDialogTopPanel(JLabel logoLabel, JDialog dialog) {
        JButton btnClose = new JButton();
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
        btnClose.addActionListener(e -> dialog.dispose());

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topRightPanel.setOpaque(false);
        topRightPanel.add(btnClose);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(topRightPanel, BorderLayout.EAST);
        return topPanel;
    }

    private List<Armario> carregarArmariosParaCombo() {
        try {
            return armarioDAO.listarTodos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar armários: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return new java.util.ArrayList<>();
        }
    }

    private void styleActionButton(JButton button, Color background) {
        button.setPreferredSize(new Dimension(140, 45));
        button.setMargin(new Insets(8, 16, 8, 16));
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new LineBorder(background, 2, true));
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 250));
        JLabel title = new JLabel("Bem-vindo ao painel administrativo");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(30, 40, 55));
        panel.add(title);
        return panel;
    }

    private JPanel createCardPanel(String titleText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(30, 30, 20, 30));
        panel.add(title, BorderLayout.NORTH);
        JLabel content = new JLabel("Conteúdo da tela " + titleText);
        content.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        content.setBorder(new EmptyBorder(10, 30, 10, 30));
        panel.add(content, BorderLayout.CENTER);
        return panel;
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

    private static class RoundedButton extends JButton {
        private final int radius;

        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground().darker());
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
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

        field.setPreferredSize(new Dimension(width - 60, height - 16));
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

    private JPanel createLabeledComboPanel(String placeholder, String iconPath, JComboBox<?> combo, int width, int height) {
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

        combo.setOpaque(false);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        combo.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        inputPanel.add(combo, BorderLayout.CENTER);

        wrapper.add(inputPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createLabeledComponentPanel(String placeholder, String iconPath, JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel inputPanel = new RoundedInputPanel(24, new Color(36, 207, 215));
        inputPanel.setLayout(new BorderLayout(8, 0));
        inputPanel.setOpaque(false);
        inputPanel.setPreferredSize(new Dimension(300, 50));
        inputPanel.setMinimumSize(new Dimension(300, 50));

        JLabel iconLabel = new JLabel();
        ImageIcon icon = loadIcon(iconPath, 24, 24);
        if (icon != null) {
            iconLabel.setIcon(icon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        }
        inputPanel.add(iconLabel, BorderLayout.WEST);

        component.setPreferredSize(new Dimension(300 - 60, 50 - 16));
        component.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        component.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputPanel.add(component, BorderLayout.CENTER);

        wrapper.add(inputPanel, BorderLayout.CENTER);
        return wrapper;
    }

    // Classes auxiliares para a tabela
    private static class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton btnVisualizar = new RoundedButton("Visualizar", 20);
        private JButton btnDeletar = new RoundedButton("Deletar", 20);

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 2));
            setOpaque(true);

            btnVisualizar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnVisualizar.setBackground(new Color(0, 123, 255));
            btnVisualizar.setForeground(Color.WHITE);
            btnVisualizar.setBorderPainted(false);
            btnVisualizar.setFocusPainted(false);
            btnVisualizar.setPreferredSize(new Dimension(75, 28));
            btnVisualizar.setMargin(new Insets(2, 8, 2, 8));

            btnDeletar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnDeletar.setBackground(new Color(220, 53, 69));
            btnDeletar.setForeground(Color.WHITE);
            btnDeletar.setBorderPainted(false);
            btnDeletar.setFocusPainted(false);
            btnDeletar.setPreferredSize(new Dimension(65, 28));
            btnDeletar.setMargin(new Insets(2, 8, 2, 8));

            add(btnVisualizar);
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

    private static class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnVisualizar;
        private JButton btnDeletar;
        private JTable table;
        private int currentRow;
        private Consumer<Integer> onDelete;
        private Consumer<Integer> onView;

        public ButtonEditor(JCheckBox checkBox, JTable table, Consumer<Integer> onDelete) {
            this(checkBox, table, onDelete, null);
        }

        public ButtonEditor(JCheckBox checkBox, JTable table, Consumer<Integer> onDelete, Consumer<Integer> onView) {
            super(checkBox);
            this.table = table;
            this.onDelete = onDelete;
            this.onView = onView;
            this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

            btnVisualizar = new RoundedButton("Visualizar", 20);
            btnVisualizar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnVisualizar.setBackground(new Color(0, 123, 255));
            btnVisualizar.setForeground(Color.WHITE);
            btnVisualizar.setBorderPainted(false);
            btnVisualizar.setFocusPainted(false);
            btnVisualizar.setPreferredSize(new Dimension(75, 28));
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

            btnDeletar = new RoundedButton("Deletar", 20);
            btnDeletar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnDeletar.setBackground(new Color(220, 53, 69));
            btnDeletar.setForeground(Color.WHITE);
            btnDeletar.setBorderPainted(false);
            btnDeletar.setFocusPainted(false);
            btnDeletar.setPreferredSize(new Dimension(65, 28));
            btnDeletar.setMargin(new Insets(2, 8, 2, 8));
            btnDeletar.addActionListener(e -> {
                if (currentRow >= 0 && onDelete != null) {
                    onDelete.accept(currentRow);
                }
                fireEditingStopped();
            });

            panel.add(btnVisualizar);
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
}
