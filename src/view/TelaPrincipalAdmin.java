package view;
/*BIBILHOTECAS PRINCIPAIS*/

import controller.EstudanteController;
import controller.FuncionarioController;
import dao.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.SQLException;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import model.*;
import util.DigitOnlyFilter;
import util.LetterOnlyFilter;
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
    private List<Livro> livrosCache;
    private ComputadorDAO computadorDAO = new ComputadorDAO();
    private ArmarioDAO armarioDAO = new ArmarioDAO();
    private CursoDAO cursoDAO = new CursoDAO();
    private DepartamentoDAO departamentoDAO = new DepartamentoDAO();
    private EnderecoDAO enderecoDAO = new EnderecoDAO();
    private ContactoDAO contactoDAO = new ContactoDAO();
    private FuncaoDAO funcaoDAO = new FuncaoDAO();
    private EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private AtendimentoDAO atendimentoDAO;

    private DefaultTableModel estudantesTableModel;
    private JTable estudantesTable;
    private DefaultTableModel emprestimosTableModel;
    private JTable emprestimosTable;
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
    private DefaultTableModel enderecosTableModel;
    private JTable enderecosTable;
    private DefaultTableModel contactosTableModel;
    private JTable contactosTable;
    private DefaultTableModel funcoesTableModel;
    private JTable funcoesTable;

    private DefaultTableModel relatorioEmprestimosTableModel;
    private JTable relatorioEmprestimosTable;
    private JPanel relatorioEmprestimosResumoPanel;
    private JPanel relatorioAtendimentoResumoPanel;

    private class EstudanteSearchKeyAdapter extends KeyAdapter {
        private final JTextField txtBuscar;

        public EstudanteSearchKeyAdapter(JTextField txtBuscar) {
            this.txtBuscar = txtBuscar;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            String text = txtBuscar.getText();
            if (text == null || text.trim().isEmpty() || text.equals("Pesquisar por nome ou número")) {
                if (estudantesTable.getRowSorter() != null) {
                    ((TableRowSorter<?>) estudantesTable.getRowSorter()).setRowFilter(null);
                }
            } else {
                ((TableRowSorter<?>) estudantesTable.getRowSorter()).setRowFilter(
                    RowFilter.regexFilter("(?i)" + Pattern.quote(text.trim()), 1, 2)
                );
            }
        }
    }

    private class LivroSearchKeyAdapter extends KeyAdapter {
        private final JTextField txtBuscar;

        public LivroSearchKeyAdapter(JTextField txtBuscar) {
            this.txtBuscar = txtBuscar;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            String text = txtBuscar.getText();
            if (text == null || text.trim().isEmpty() || text.equals("Pesquisar por código, ISBN, ISSN, CDU ou título")) {
                carregarLivros();
            } else {
                filtrarLivros(text.trim());
            }
        }
    }

    private class TableSearchKeyAdapter extends KeyAdapter {
        private final JTextField txtBuscar;
        private final JTable table;
        private final int[] columns;
        private final String placeholder;

        public TableSearchKeyAdapter(JTextField txtBuscar, JTable table, String placeholder, int... columns) {
            this.txtBuscar = txtBuscar;
            this.table = table;
            this.columns = columns;
            this.placeholder = placeholder;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            String text = txtBuscar.getText();
            if (text == null || text.trim().isEmpty() || text.equals(placeholder)) {
                if (table.getRowSorter() != null) {
                    ((TableRowSorter<?>) table.getRowSorter()).setRowFilter(null);
                }
            } else {
                if (table.getRowSorter() != null) {
                    ((TableRowSorter<?>) table.getRowSorter()).setRowFilter(
                        RowFilter.regexFilter("(?i)" + Pattern.quote(text.trim()), columns)
                    );
                }
            }
        }
    }

    private static class EstudantesTableModel extends DefaultTableModel {
        public EstudantesTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 7; // Apenas coluna AÇÃO é editável
        }
    }

    private static class FuncionariosTableModel extends DefaultTableModel {
        public FuncionariosTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 4;
        }
    }

    private static class LivrosTableModel extends DefaultTableModel {
        public LivrosTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 11;
        }
    }

    // Componentes de Empréstimo
    private JLabel lblNomeEstudanteEmp;
    private JLabel lblCursoEstudanteEmp;
    private JLabel lblTituloLivroEmp;
    private JLabel lblCodigoLivroEmp;
    private JLabel lblArmarioLivroEmp;
    private JLabel lblPartileiraLivroEmp;
    private JLabel lblDataEntregaEmp;
    private JLabel lblHoraEntregaEmp;
    private JButton btnConfirmarEmprestimoEmp;
    private Estudante estudanteAtualEmp;
    private Livro livroAtualEmp;

    // Construtor com suporte a funcionário autenticado
    public TelaPrincipalAdmin(Funcionario funcionario) {
        this.funcionarioAutenticado = funcionario;
        initComponents();
    }

    public TelaPrincipalAdmin() {
        initComponents();
    }

    private void initComponents() {
        // Inicializa DAOs que podem lançar erros de compilação em tempo de carga da classe
        atendimentoDAO = new AtendimentoDAO();

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
        cardPanel.add(createGerenciarEnderecosPanel(), "enderecos");
        cardPanel.add(createGerenciarContactosPanel(), "contactos");
        cardPanel.add(createGerenciarFuncoesPanel(), "funcoes");
        cardPanel.add(createGerenciarEmprestimosPanel(), "emprestimos");
        cardPanel.add(new TelaAtendimento(false), "atendimento");
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
        menuPanel.add(createMenuButton("Inicio", "inicio", "Inicio"));
        if (funcionarioAutenticado != null && "Admin".equals(funcionarioAutenticado.getFuncao())) {
            menuPanel.add(createMenuButton("Estudantes", "user", "estudantes"));
            menuPanel.add(createMenuButton("Funcionários", "funcionario", "funcionarios"));
            menuPanel.add(createMenuButton("Livros", "livro", "livros"));
            menuPanel.add(createMenuButton("Computadores", "computador", "computadores"));
            menuPanel.add(createMenuButton("Armários", "armario", "armarios"));
            menuPanel.add(createMenuButton("Cursos", "curso", "cursos"));
            menuPanel.add(createMenuButton("Departamentos", "departamentos", "departamentos"));
            menuPanel.add(createMenuButton("Endereços", "enderecos", "enderecos"));
            menuPanel.add(createMenuButton("Contactos", "contacto", "contactos"));
            menuPanel.add(createMenuButton("Funções", "funcoes", "funcoes"));
        }
        menuPanel.add(createMenuButton("Empréstimos", "emprestar", "emprestimos"));
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
            button.addActionListener(e -> new TelaAtendimento());
        }
        return button;
    }

    private JPanel createGerenciarEstudantesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Painel superior com título e botão adicionar no canto direito
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel title = new JLabel("Gerenciar Estudantes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        topPanel.add(title, BorderLayout.WEST);

        JButton btnAdicionar = createPrimaryButton("Adicionar Estudante");
        btnAdicionar.addActionListener(e -> showAdicionarEstudanteModal());
        topPanel.add(btnAdicionar, BorderLayout.EAST);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 36));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.addKeyListener(new EstudanteSearchKeyAdapter(txtBuscar));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        searchPanel.add(createLabeledFieldPanel("Pesquisar por nome ou número", "src/view/icon/user.png", txtBuscar, 320, 40), BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        // Tabela de estudantes
        String[] columnNames = {"ID", "ESTUDANTE", "NÚMERO", "CURSO", "DEPARTAMENTO", "EMAIL", "CONTACTO", "ACÇÃO"};
        this.estudantesTableModel = new EstudantesTableModel(columnNames, 0);

        estudantesTable = new JTable(estudantesTableModel);
        estudantesTable.setRowHeight(35);
        estudantesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        estudantesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        estudantesTable.getTableHeader().setBackground(new Color(240, 245, 250));
        estudantesTable.getTableHeader().setForeground(new Color(30, 40, 55));
        estudantesTable.setSelectionBackground(new Color(220, 235, 255));
        estudantesTable.setGridColor(new Color(200, 210, 220));
        estudantesTable.setRowSorter(new TableRowSorter<>(estudantesTableModel));

        // Configurar coluna ACÇÃO com botões
        estudantesTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        estudantesTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox(), estudantesTable, this::deleteEstudanteRow, this::viewEstudanteRow, this::editEstudanteRow));

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
        JComboBox<EstudanteDAO.ContactoItem> comboContacto = new JComboBox<>();
        JComboBox<EstudanteDAO.EnderecoItem> comboEndereco = new JComboBox<>();

        ((AbstractDocument) txtNome.getDocument()).setDocumentFilter(new LetterOnlyFilter());
        ((AbstractDocument) txtNumero.getDocument()).setDocumentFilter(new DigitOnlyFilter());

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
        formPanel.add(createLabeledFieldPanel("Número do Estudante", "src/view/icon/contacto.png", txtNumero, 300, 50), gbc);

        JTextField txtEmail = new JTextField();

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
        formPanel.add(createLabeledFieldPanel("Email", "src/view/icon/contacto.png", txtEmail, 300, 50), gbc);

        gbc.gridy = 3;
        formPanel.add(createLabeledComboPanel("Curso", "src/view/icon/curso.png", comboCurso, 300, 50), gbc);

        List<EstudanteDAO.ContactoItem> contactos = ordenarContactosPorMaisRecentes(carregarContactosEstudante());
        if (contactos.isEmpty()) {
            comboContacto.addItem(new EstudanteDAO.ContactoItem(0, "Sem telefone", null));
            comboContacto.setEnabled(false);
        } else {
            for (EstudanteDAO.ContactoItem contactoItem : contactos) {
                comboContacto.addItem(contactoItem);
            }
        }
        gbc.gridy = 4;
        formPanel.add(createLabeledComboPanel("Contacto", "src/view/icon/contacto.png", comboContacto, 300, 50), gbc);

        List<EstudanteDAO.EnderecoItem> enderecos = carregarEnderecosEstudante();
        if (enderecos.isEmpty()) {
            comboEndereco.addItem(new EstudanteDAO.EnderecoItem(0, "Sem endereço", ""));
            comboEndereco.setEnabled(false);
        } else {
            for (EstudanteDAO.EnderecoItem enderecoItem : enderecos) {
                comboEndereco.addItem(enderecoItem);
            }
        }
        gbc.gridy = 5;
        formPanel.add(createLabeledComboPanel("Endereço", "src/view/icon/enderecos.png", comboEndereco, 300, 50), gbc);

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
            EstudanteDAO.ContactoItem contactoSelecionado = (EstudanteDAO.ContactoItem) comboContacto.getSelectedItem();
            EstudanteDAO.EnderecoItem enderecoSelecionado = (EstudanteDAO.EnderecoItem) comboEndereco.getSelectedItem();

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
            estudante.setEmail(getFieldValue(txtEmail, "Email"));
            estudante.setIdCurso(cursoSelecionado.getId());
            estudante.setCurso(cursoSelecionado.getNome());
            estudante.setIdContacto(contactoSelecionado != null ? contactoSelecionado.getId() : 0);
            estudante.setContacto(contactoSelecionado != null ? contactoSelecionado.toString() : "");
            estudante.setIdEndereco(enderecoSelecionado != null ? enderecoSelecionado.getId() : 0);

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

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        topPanel.add(logoLabel, BorderLayout.WEST);

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
                    estudante.getEmail(),
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

    private void viewEstudanteRow(int row) {
        if (row < 0 || estudantesTableModel == null) {
            return;
        }
        int idEstudante = Integer.parseInt(estudantesTableModel.getValueAt(row, 0).toString());
        try {
            Estudante estudante = estudanteController.buscarPorId(idEstudante);
            if (estudante == null) {
                JOptionPane.showMessageDialog(this, "Estudante não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String enderecoStr = "";
            if (estudante.getIdEndereco() > 0) {
                Endereco endereco = enderecoDAO.buscarPorId(estudante.getIdEndereco());
                if (endereco != null) {
                    enderecoStr = endereco.getMunicipio() + ", " + endereco.getBairro();
                }
            }
            String detalhes = "ID: " + estudante.getIdEstudante() + "\n" +
                    "Nome: " + estudante.getNome() + "\n" +
                    "Número: " + estudante.getNumeroEstudante() + "\n" +
                    "Curso: " + estudante.getCurso() + "\n" +
                    "Departamento: " + estudante.getDepartamento() + "\n" +
                    "Contacto: " + estudante.getContacto() + "\n" +
                    "Email: " + estudante.getEmail() + "\n" +
                    "Endereço: " + enderecoStr;
            JOptionPane.showMessageDialog(this, detalhes, "Detalhes do Estudante", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar estudante: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editEstudanteRow(int row) {
        if (row < 0 || estudantesTableModel == null) {
            return;
        }
        int idEstudante = Integer.parseInt(estudantesTableModel.getValueAt(row, 0).toString());
        Estudante estudante = estudanteController.buscarPorId(idEstudante);
        if (estudante == null) {
            JOptionPane.showMessageDialog(this, "Estudante não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        showEditarEstudanteModal(estudante);
    }

    private void showEditarEstudanteModal(Estudante estudante) {
        JDialog dialog = new JDialog(this, "Editar Estudante", true);
        dialog.setSize(520, 580);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText("EDITAR ESTUDANTE");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JTextField txtNome = new JTextField(estudante.getNome());
        JTextField txtNumero = new JTextField(estudante.getNumeroEstudante());
        JComboBox<EstudanteDAO.CursoItem> comboCurso = new JComboBox<>();
        JComboBox<EstudanteDAO.ContactoItem> comboContacto = new JComboBox<>();
        JComboBox<EstudanteDAO.EnderecoItem> comboEndereco = new JComboBox<>();

        ((AbstractDocument) txtNome.getDocument()).setDocumentFilter(new LetterOnlyFilter());
        ((AbstractDocument) txtNumero.getDocument()).setDocumentFilter(new DigitOnlyFilter());

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
        formPanel.add(createLabeledFieldPanel("Número do Estudante", "src/view/icon/contacto.png", txtNumero, 300, 50), gbc);

        JTextField txtEmail = new JTextField(estudante.getEmail());

        List<EstudanteDAO.CursoItem> cursos = carregarCursos();
        if (cursos.isEmpty()) {
            comboCurso.addItem(new EstudanteDAO.CursoItem(0, "Nenhum curso cadastrado"));
            comboCurso.setEnabled(false);
        } else {
            for (EstudanteDAO.CursoItem curso : cursos) {
                comboCurso.addItem(curso);
                if (curso.getId() == estudante.getIdCurso()) {
                    comboCurso.setSelectedItem(curso);
                }
            }
        }
        gbc.gridy = 2;
        formPanel.add(createLabeledFieldPanel("Email", "src/view/icon/contacto.png", txtEmail, 300, 50), gbc);

        gbc.gridy = 3;
        formPanel.add(createLabeledComboPanel("Curso", "src/view/icon/curso.png", comboCurso, 300, 50), gbc);

        List<EstudanteDAO.ContactoItem> contactos = ordenarContactosPorMaisRecentes(carregarContactosEstudante());
        if (contactos.isEmpty()) {
            comboContacto.addItem(new EstudanteDAO.ContactoItem(0, "Sem telefone", null));
            comboContacto.setEnabled(false);
        } else {
            for (EstudanteDAO.ContactoItem contacto : contactos) {
                comboContacto.addItem(contacto);
                if (contacto.getId() == estudante.getIdContacto()) {
                    comboContacto.setSelectedItem(contacto);
                }
            }
        }
        gbc.gridy = 4;
        formPanel.add(createLabeledComboPanel("Telefone", "src/view/icon/contacto.png", comboContacto, 300, 50), gbc);

        List<EstudanteDAO.EnderecoItem> enderecos = carregarEnderecosEstudante();
        if (enderecos.isEmpty()) {
            comboEndereco.addItem(new EstudanteDAO.EnderecoItem(0, "Sem endereço", ""));
            comboEndereco.setEnabled(false);
        } else {
            for (EstudanteDAO.EnderecoItem endereco : enderecos) {
                comboEndereco.addItem(endereco);
                if (endereco.getId() == estudante.getIdEndereco()) {
                    comboEndereco.setSelectedItem(endereco);
                }
            }
        }
        gbc.gridy = 5;
        formPanel.add(createLabeledComboPanel("Endereço", "src/view/icon/enderecos.png", comboEndereco, 300, 50), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton("SALVAR ALTERAÇÃO", 24);
        btnSalvar.setPreferredSize(new Dimension(180, 45));
        btnSalvar.setBackground(new Color(34, 197, 94));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.setBorder(new LineBorder(new Color(34, 197, 94), 2, true));
        btnSalvar.addActionListener(e -> {
            String nome = getFieldValue(txtNome, "Nome");
            String numero = getFieldValue(txtNumero, "Número");
            EstudanteDAO.CursoItem cursoSelecionado = (EstudanteDAO.CursoItem) comboCurso.getSelectedItem();
            EstudanteDAO.ContactoItem contactoSelecionado = (EstudanteDAO.ContactoItem) comboContacto.getSelectedItem();
            EstudanteDAO.EnderecoItem enderecoSelecionado = (EstudanteDAO.EnderecoItem) comboEndereco.getSelectedItem();

            if (nome.isEmpty() || numero.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha o nome e o número do estudante.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cursoSelecionado == null || cursoSelecionado.getId() == 0) {
                JOptionPane.showMessageDialog(dialog, "Selecione um curso válido.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            estudante.setNome(nome);
            estudante.setNumeroEstudante(numero);
            estudante.setEmail(getFieldValue(txtEmail, "Email"));
            estudante.setIdCurso(cursoSelecionado.getId());
            estudante.setCurso(cursoSelecionado.getNome());
            estudante.setIdContacto(contactoSelecionado != null ? contactoSelecionado.getId() : 0);
            estudante.setContacto(contactoSelecionado != null ? contactoSelecionado.toString() : "");
            estudante.setIdEndereco(enderecoSelecionado != null ? enderecoSelecionado.getId() : 0);

            try {
                estudanteController.atualizarEstudante(estudante);
                carregarEstudantes();
                JOptionPane.showMessageDialog(dialog, "Estudante atualizado com sucesso!");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar alterações: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = new RoundedButton("CANCELAR", 24);
        btnCancelar.setPreferredSize(new Dimension(140, 45));
        btnCancelar.setBackground(new Color(220, 53, 69));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setBorder(new LineBorder(new Color(220, 53, 69), 2, true));
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        topPanel.add(logoLabel, BorderLayout.WEST);

        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private List<EstudanteDAO.ContactoItem> carregarContactosEstudante() {
        try {
            return estudanteController.listarContactos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar contactos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return new java.util.ArrayList<>();
        }
    }

    private List<EstudanteDAO.ContactoItem> ordenarContactosPorMaisRecentes(List<EstudanteDAO.ContactoItem> contactos) {
        if (contactos == null || contactos.isEmpty()) {
            return contactos;
        }
        contactos.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        return contactos;
    }

    private List<EstudanteDAO.EnderecoItem> carregarEnderecosEstudante() {
        try {
            return estudanteController.listarEnderecos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar endereços: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return new java.util.ArrayList<>();
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
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Gerenciar Funcionários");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        JButton btnNovo = createPrimaryButton("Adicionar Funcionário");
        btnNovo.addActionListener(e -> showAdicionarFuncionarioModal(null));
        topPanel.add(btnNovo, BorderLayout.EAST);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 36));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        searchPanel.add(createLabeledFieldPanel("Pesquisar por nome, cargo ou contacto", "src/view/icon/funcionario.png", txtBuscar, 320, 40), BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        funcionariosTableModel = new FuncionariosTableModel(new Object[]{"ID", "Nome", "Cargo", "Contacto", "Ações"}, 0);
        funcionariosTable = new JTable(funcionariosTableModel);
        funcionariosTable.setRowHeight(40);
        funcionariosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        funcionariosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        funcionariosTable.getTableHeader().setBackground(new Color(240, 245, 250));
        funcionariosTable.getTableHeader().setForeground(new Color(30, 40, 55));
        funcionariosTable.setSelectionBackground(new Color(220, 235, 255));
        funcionariosTable.setGridColor(new Color(200, 210, 220));
        funcionariosTable.setRowSorter(new TableRowSorter<>(funcionariosTableModel));
        funcionariosTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        funcionariosTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), funcionariosTable, this::deleteFuncionarioRow, this::viewFuncionarioRow, this::editFuncionarioRow));
        JScrollPane scrollPane = new JScrollPane(funcionariosTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        txtBuscar.addKeyListener(new TableSearchKeyAdapter(txtBuscar, funcionariosTable, "Pesquisar por nome, cargo ou contacto", 0, 1, 2));
        carregarFuncionarios();
        return panel;
    }

    private JPanel createGerenciarLivrosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Gerenciar Livros");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        JButton btnNovo = createPrimaryButton("Adicionar Livro");
        btnNovo.addActionListener(e -> showAdicionarLivroModal(null));
        topPanel.add(btnNovo, BorderLayout.EAST);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 36));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.addKeyListener(new LivroSearchKeyAdapter(txtBuscar));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(12, 0, 12, 0));
        searchPanel.add(createLabeledFieldPanel("Pesquisar por código, ISBN, ISSN, CDU ou título", "src/view/icon/livro.png", txtBuscar, 320, 40), BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        livrosTableModel = new LivrosTableModel(new Object[]{"Id", "Título", "Subtítulo", "Autor", "Edição", "ISBN", "ISSN", "NDI", "Ano de Publicação", "Código Interno", "Armário", "Partileira", "Disponível", "Ação"}, 0);
        livrosTable = new JTable(livrosTableModel);
        livrosTable.setRowHeight(40);
        livrosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        livrosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        livrosTable.getTableHeader().setBackground(new Color(240, 245, 250));
        livrosTable.getTableHeader().setForeground(new Color(30, 40, 55));
        livrosTable.setSelectionBackground(new Color(220, 235, 255));
        livrosTable.setGridColor(new Color(200, 210, 220));
        livrosTable.setRowSorter(new TableRowSorter<>(livrosTableModel));
        livrosTable.getColumnModel().getColumn(13).setCellRenderer(new ButtonRenderer());
        livrosTable.getColumnModel().getColumn(13).setCellEditor(new ButtonEditor(new JCheckBox(), livrosTable, this::deleteLivroRow, this::viewLivroRow, this::editLivroRow));
        JScrollPane livrosScrollPane = new JScrollPane(livrosTable);
        livrosScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        panel.add(livrosScrollPane, BorderLayout.CENTER);

        carregarLivros();
        return panel;
    }

    private JPanel createGerenciarComputadoresPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Gerenciar Computadores");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        JButton btnNovo = createPrimaryButton("Adicionar Computador");
        btnNovo.addActionListener(e -> showAdicionarComputadorModal(null));
        topPanel.add(btnNovo, BorderLayout.EAST);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 36));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        searchPanel.add(createLabeledFieldPanel("Pesquisar por marca, modelo ou status", "src/view/icon/computador.png", txtBuscar, 320, 40), BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        computadoresTableModel = new DefaultTableModel(new Object[]{"ID", "Marca", "Modelo", "Status", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        computadoresTable = new JTable(computadoresTableModel);
        computadoresTable.setRowHeight(40);
        computadoresTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        computadoresTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        computadoresTable.getTableHeader().setBackground(new Color(240, 245, 250));
        computadoresTable.getTableHeader().setForeground(new Color(30, 40, 55));
        computadoresTable.setSelectionBackground(new Color(220, 235, 255));
        computadoresTable.setGridColor(new Color(200, 210, 220));
        computadoresTable.setRowSorter(new TableRowSorter<>(computadoresTableModel));
        computadoresTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        computadoresTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), computadoresTable, this::deleteComputadorRow, this::viewComputadorRow, this::editComputadorRow));
        JScrollPane scrollPane = new JScrollPane(computadoresTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        txtBuscar.addKeyListener(new TableSearchKeyAdapter(txtBuscar, computadoresTable, "Pesquisar por marca, modelo ou status", 0, 1, 2, 3));
        carregarComputadores();
        return panel;
    }

    private JPanel createGerenciarArmariosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Gerenciar Armários");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);

        JButton btnNovo = createPrimaryButton("Adicionar Armário");
        btnNovo.addActionListener(e -> showAdicionarArmarioModal(null));
        topPanel.add(btnNovo, BorderLayout.EAST);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 36));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        searchPanel.add(createLabeledFieldPanel("Pesquisar por armário", "src/view/icon/armario.png", txtBuscar, 320, 40), BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        armariosTableModel = new DefaultTableModel(new Object[]{"ID", "Armário", "Partileira", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        armariosTable = new JTable(armariosTableModel);
        armariosTable.setRowHeight(40);
        armariosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        armariosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        armariosTable.getTableHeader().setBackground(new Color(240, 245, 250));
        armariosTable.getTableHeader().setForeground(new Color(30, 40, 55));
        armariosTable.setSelectionBackground(new Color(220, 235, 255));
        armariosTable.setGridColor(new Color(200, 210, 220));
        armariosTable.setRowSorter(new TableRowSorter<>(armariosTableModel));
        armariosTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        armariosTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), armariosTable, this::deleteArmarioRow, this::viewArmarioRow, this::editArmarioRow));

        JScrollPane scrollPane = new JScrollPane(armariosTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));

        panel.add(scrollPane, BorderLayout.CENTER);

        txtBuscar.addKeyListener(new TableSearchKeyAdapter(txtBuscar, armariosTable, "Pesquisar por armário", 0, 1));
        carregarArmarios();
        return panel;
    }

    private JPanel createGerenciarCursosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Gerenciar Cursos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        JButton btnNovo = createPrimaryButton("Adicionar Curso");
        btnNovo.addActionListener(e -> showAdicionarCursoModal(null));
        topPanel.add(btnNovo, BorderLayout.EAST);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 36));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        searchPanel.add(createLabeledFieldPanel("Pesquisar por curso ou departamento", "src/view/icon/curso.png", txtBuscar, 320, 40), BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        cursosTableModel = new DefaultTableModel(new Object[]{"ID", "Curso", "Departamento", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        cursosTable = new JTable(cursosTableModel);
        cursosTable.setRowHeight(40);
        cursosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cursosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        cursosTable.getTableHeader().setBackground(new Color(240, 245, 250));
        cursosTable.getTableHeader().setForeground(new Color(30, 40, 55));
        cursosTable.setSelectionBackground(new Color(220, 235, 255));
        cursosTable.setGridColor(new Color(200, 210, 220));
        cursosTable.setRowSorter(new TableRowSorter<>(cursosTableModel));
        cursosTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        cursosTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), cursosTable, this::deleteCursoRow, this::viewCursoRow, this::editCursoRow));
        JScrollPane scrollPane = new JScrollPane(cursosTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        txtBuscar.addKeyListener(new TableSearchKeyAdapter(txtBuscar, cursosTable, "Pesquisar por curso ou departamento", 0, 1, 2));
        carregarCursosTabela();
        return panel;
    }

    private JPanel createGerenciarDepartamentosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Gerenciar Departamentos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        JButton btnNovo = createPrimaryButton("Adicionar Departamento");
        btnNovo.addActionListener(e -> showAdicionarDepartamentoModal(null));
        topPanel.add(btnNovo, BorderLayout.EAST);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 36));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        searchPanel.add(createLabeledFieldPanel("Pesquisar por departamento", "src/view/icon/departamentos.png", txtBuscar, 320, 40), BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        departamentosTableModel = new DefaultTableModel(new Object[]{"ID", "Departamento", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        departamentosTable = new JTable(departamentosTableModel);
        departamentosTable.setRowHeight(40);
        departamentosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        departamentosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        departamentosTable.getTableHeader().setBackground(new Color(240, 245, 250));
        departamentosTable.getTableHeader().setForeground(new Color(30, 40, 55));
        departamentosTable.setSelectionBackground(new Color(220, 235, 255));
        departamentosTable.setGridColor(new Color(200, 210, 220));
        departamentosTable.setRowSorter(new TableRowSorter<>(departamentosTableModel));
        departamentosTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        departamentosTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), departamentosTable, this::deleteDepartamentoRow, this::viewDepartamentoRow, this::editDepartamentoRow));
        JScrollPane scrollPane = new JScrollPane(departamentosTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        txtBuscar.addKeyListener(new TableSearchKeyAdapter(txtBuscar, departamentosTable, "Pesquisar por departamento", 0, 1));
        carregarDepartamentos();
        return panel;
    }

    private JPanel createGerenciarEnderecosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Gerenciar Endereços");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        JButton btnNovo = createPrimaryButton("Adicionar Endereço");
        btnNovo.addActionListener(e -> showAdicionarEnderecoModal(null));

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 36));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        searchPanel.add(createLabeledFieldPanel("Pesquisar por município ou bairro", "src/view/icon/enderecos.png", txtBuscar, 320, 40), BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        topPanel.add(btnNovo, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        enderecosTableModel = new DefaultTableModel(new Object[]{"ID", "Município", "Bairro", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        enderecosTable = new JTable(enderecosTableModel);
        enderecosTable.setRowHeight(40);
        enderecosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        enderecosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        enderecosTable.getTableHeader().setBackground(new Color(240, 245, 250));
        enderecosTable.getTableHeader().setForeground(new Color(30, 40, 55));
        enderecosTable.setSelectionBackground(new Color(220, 235, 255));
        enderecosTable.setGridColor(new Color(200, 210, 220));
        enderecosTable.setRowSorter(new TableRowSorter<>(enderecosTableModel));
        enderecosTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        enderecosTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), enderecosTable, this::deleteEnderecoRow, this::viewEnderecoRow, this::editEnderecoRow));
        JScrollPane scrollPane = new JScrollPane(enderecosTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        txtBuscar.addKeyListener(new TableSearchKeyAdapter(txtBuscar, enderecosTable, "Pesquisar por município ou bairro", 0, 1, 2));
        carregarEnderecos();
        return panel;
    }

    private JPanel createGerenciarContactosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Gerenciar Contactos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        JButton btnNovo = createPrimaryButton("Adicionar Contacto");
        btnNovo.addActionListener(e -> showAdicionarContactoModal(null));

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 36));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        searchPanel.add(createLabeledFieldPanel("Pesquisar por telefone", "src/view/icon/contacto.png", txtBuscar, 320, 40), BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        topPanel.add(btnNovo, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        contactosTableModel = new DefaultTableModel(new Object[]{"ID", "Telefone 1", "Telefone 2", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        contactosTable = new JTable(contactosTableModel);
        contactosTable.setRowHeight(40);
        contactosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contactosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        contactosTable.getTableHeader().setBackground(new Color(240, 245, 250));
        contactosTable.getTableHeader().setForeground(new Color(30, 40, 55));
        contactosTable.setSelectionBackground(new Color(220, 235, 255));
        contactosTable.setGridColor(new Color(200, 210, 220));
        contactosTable.setRowSorter(new TableRowSorter<>(contactosTableModel));
        contactosTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        contactosTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), contactosTable, this::deleteContactoRow, this::viewContactoRow, this::editContactoRow));
        JScrollPane scrollPane = new JScrollPane(contactosTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        txtBuscar.addKeyListener(new TableSearchKeyAdapter(txtBuscar, contactosTable, "Pesquisar por telefone", 0, 1, 2));
        carregarContactos();
        return panel;
    }

    private JPanel createGerenciarFuncoesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Gerenciar Funções");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 40, 55));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(title, BorderLayout.WEST);
        JButton btnNovo = createPrimaryButton("Adicionar Função");
        btnNovo.addActionListener(e -> showAdicionarFuncaoModal(null));

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(280, 36));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        searchPanel.add(createLabeledFieldPanel("Pesquisar por função ou descrição", "src/view/icon/funcoes.png", txtBuscar, 320, 40), BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        topPanel.add(btnNovo, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        funcoesTableModel = new DefaultTableModel(new Object[]{"ID", "Função", "Descrição", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        funcoesTable = new JTable(funcoesTableModel);
        funcoesTable.setRowHeight(40);
        funcoesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        funcoesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        funcoesTable.getTableHeader().setBackground(new Color(240, 245, 250));
        funcoesTable.getTableHeader().setForeground(new Color(30, 40, 55));
        funcoesTable.setSelectionBackground(new Color(220, 235, 255));
        funcoesTable.setGridColor(new Color(200, 210, 220));
        funcoesTable.setRowSorter(new TableRowSorter<>(funcoesTableModel));
        funcoesTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        funcoesTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), funcoesTable, this::deleteFuncaoRow, this::viewFuncaoRow, this::editFuncaoRow));
        JScrollPane scrollPane = new JScrollPane(funcoesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        txtBuscar.addKeyListener(new TableSearchKeyAdapter(txtBuscar, funcoesTable, "Pesquisar por função ou descrição", 0, 1, 2));
        carregarFuncoes();
        return panel;
    }

    private void carregarEnderecos() {
        enderecosTableModel.setRowCount(0);
        try {
            List<Endereco> enderecos = enderecoDAO.listarTodos();
            for (Endereco endereco : enderecos) {
                enderecosTableModel.addRow(new Object[]{
                    endereco.getIdEndereco(),
                    endereco.getMunicipio(),
                    endereco.getBairro(),
                    "Ações"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar endereços: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarContactos() {
        contactosTableModel.setRowCount(0);
        try {
            List<Contacto> contactos = contactoDAO.listarTodos();
            for (Contacto contacto : contactos) {
                contactosTableModel.addRow(new Object[]{
                    contacto.getIdContacto(),
                    contacto.getTelefone1(),
                    contacto.getTelefone2(),
                    "Ações"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar contactos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarFuncoes() {
        funcoesTableModel.setRowCount(0);
        try {
            List<Funcao> funcoes = funcaoDAO.listarTodos();
            for (Funcao funcao : funcoes) {
                funcoesTableModel.addRow(new Object[]{
                    funcao.getIdFuncao(),
                    funcao.getFuncao(),
                    funcao.getDescricao(),
                    "Ações"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar funções: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAdicionarEnderecoModal(Endereco endereco) {
        JDialog dialog = createModalDialog(endereco == null ? "Adicionar Endereço" : "Editar Endereço");
        JLabel logoLabel = new JLabel();
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText(endereco == null ? "ADICIONAR" : "EDITAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JTextField txtMunicipio = new JTextField(endereco != null ? endereco.getMunicipio() : "");
        JTextField txtBairro = new JTextField(endereco != null ? endereco.getBairro() : "");
        ((AbstractDocument) txtMunicipio.getDocument()).setDocumentFilter(new LetterOnlyFilter());
        ((AbstractDocument) txtBairro.getDocument()).setDocumentFilter(new LetterOnlyFilter());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Município", "src/view/icon/localizacao.png", txtMunicipio, 300, 50), gbc);
        gbc.gridy = 1;
        formPanel.add(createLabeledFieldPanel("Bairro", "src/view/icon/localizacao.png", txtBairro, 300, 50), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton(endereco == null ? "SALVAR" : "SALVAR ALTERAÇÃO", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            String municipio = getFieldValue(txtMunicipio, "Município");
            String bairro = getFieldValue(txtBairro, "Bairro");
            if (municipio.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha o município.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Endereco atual = endereco != null ? endereco : new Endereco();
            atual.setMunicipio(municipio);
            atual.setBairro(bairro);
            try {
                if (endereco == null) {
                    enderecoDAO.salvar(atual);
                } else {
                    enderecoDAO.atualizar(atual);
                }
                carregarEnderecos();
                JOptionPane.showMessageDialog(dialog, "Endereço salvo com sucesso!");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar endereço: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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

    private void showAdicionarContactoModal(Contacto contacto) {
        JDialog dialog = createModalDialog(contacto == null ? "Adicionar Contacto" : "Editar Contacto");
        JLabel logoLabel = new JLabel();
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText(contacto == null ? "ADICIONAR" : "EDITAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JTextField txtTelefone1 = new JTextField(contacto != null ? contacto.getTelefone1() : "");
        JTextField txtTelefone2 = new JTextField(contacto != null ? contacto.getTelefone2() : "");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Telefone 1", "src/view/icon/phone.png", txtTelefone1, 300, 50), gbc);
        gbc.gridy = 1;
        formPanel.add(createLabeledFieldPanel("Telefone 2", "src/view/icon/phone.png", txtTelefone2, 300, 50), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton(contacto == null ? "SALVAR" : "SALVAR ALTERAÇÃO", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            String telefone1 = getFieldValue(txtTelefone1, "Telefone 1");
            String telefone2 = getFieldValue(txtTelefone2, "Telefone 2");
            if (telefone1.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha o telefone principal.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Contacto atual = contacto != null ? contacto : new Contacto();
            atual.setTelefone1(telefone1);
            atual.setTelefone2(telefone2);
            try {
                if (contacto == null) {
                    contactoDAO.salvar(atual);
                } else {
                    contactoDAO.atualizar(atual);
                }
                carregarContactos();
                JOptionPane.showMessageDialog(dialog, "Contacto salvo com sucesso!");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar contacto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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

    private void showAdicionarFuncaoModal(Funcao funcao) {
        JDialog dialog = createModalDialog(funcao == null ? "Adicionar Função" : "Editar Função");
        JLabel logoLabel = new JLabel();
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText(funcao == null ? "ADICIONAR" : "EDITAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JTextField txtFuncao = new JTextField(funcao != null ? funcao.getFuncao() : "");
        JTextField txtDescricao = new JTextField(funcao != null ? funcao.getDescricao() : "");
        ((AbstractDocument) txtFuncao.getDocument()).setDocumentFilter(new LetterOnlyFilter());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Função", "src/view/icon/funcao.png", txtFuncao, 300, 50), gbc);
        gbc.gridy = 1;
        formPanel.add(createLabeledFieldPanel("Descrição", "src/view/icon/descricao.png", txtDescricao, 300, 50), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton(funcao == null ? "SALVAR" : "SALVAR ALTERAÇÃO", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            String funcaoNome = getFieldValue(txtFuncao, "Função");
            String descricao = getFieldValue(txtDescricao, "Descrição");
            if (funcaoNome.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha o nome da função.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Funcao atual = funcao != null ? funcao : new Funcao();
            atual.setFuncao(funcaoNome);
            atual.setDescricao(descricao);
            try {
                if (funcao == null) {
                    funcaoDAO.salvar(atual);
                } else {
                    funcaoDAO.atualizar(atual);
                }
                carregarFuncoes();
                JOptionPane.showMessageDialog(dialog, "Função salva com sucesso!");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar função: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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

    private void deleteEnderecoRow(int row) {
        if (row < 0 || enderecosTableModel == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este endereço?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(enderecosTableModel.getValueAt(row, 0).toString());
        try {
            enderecoDAO.deletar(id);
            enderecosTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Endereço deletado com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar endereço: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewEnderecoRow(int row) {
        if (row < 0 || enderecosTableModel == null) {
            return;
        }
        String municipio = enderecosTableModel.getValueAt(row, 1).toString();
        String bairro = enderecosTableModel.getValueAt(row, 2).toString();
        JOptionPane.showMessageDialog(this,
                "Município: " + municipio + "\nBairro: " + bairro,
                "Detalhes do Endereço", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editEnderecoRow(int row) {
        if (row < 0 || enderecosTableModel == null) {
            return;
        }
        int id = Integer.parseInt(enderecosTableModel.getValueAt(row, 0).toString());
        try {
            Endereco endereco = enderecoDAO.buscarPorId(id);
            if (endereco == null) {
                JOptionPane.showMessageDialog(this, "Endereço não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showAdicionarEnderecoModal(endereco);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar endereço: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteContactoRow(int row) {
        if (row < 0 || contactosTableModel == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este contacto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(contactosTableModel.getValueAt(row, 0).toString());
        try {
            contactoDAO.deletar(id);
            contactosTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Contacto deletado com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar contacto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewContactoRow(int row) {
        if (row < 0 || contactosTableModel == null) {
            return;
        }
        String telefone1 = contactosTableModel.getValueAt(row, 1).toString();
        String telefone2 = contactosTableModel.getValueAt(row, 2).toString();
        JOptionPane.showMessageDialog(this,
                "Telefone 1: " + telefone1 + "\nTelefone 2: " + telefone2,
                "Detalhes do Contacto", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editContactoRow(int row) {
        if (row < 0 || contactosTableModel == null) {
            return;
        }
        int id = Integer.parseInt(contactosTableModel.getValueAt(row, 0).toString());
        try {
            Contacto contacto = contactoDAO.buscarPorId(id);
            if (contacto == null) {
                JOptionPane.showMessageDialog(this, "Contacto não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showAdicionarContactoModal(contacto);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar contacto: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFuncaoRow(int row) {
        if (row < 0 || funcoesTableModel == null) {
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar esta função?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }
        int id = Integer.parseInt(funcoesTableModel.getValueAt(row, 0).toString());
        try {
            funcaoDAO.deletar(id);
            funcoesTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Função deletada com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar função: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewFuncaoRow(int row) {
        if (row < 0 || funcoesTableModel == null) {
            return;
        }
        String nome = funcoesTableModel.getValueAt(row, 1).toString();
        String descricao = funcoesTableModel.getValueAt(row, 2).toString();
        JOptionPane.showMessageDialog(this,
                "Função: " + nome + "\nDescrição: " + descricao,
                "Detalhes da Função", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editFuncaoRow(int row) {
        if (row < 0 || funcoesTableModel == null) {
            return;
        }
        int id = Integer.parseInt(funcoesTableModel.getValueAt(row, 0).toString());
        try {
            Funcao funcao = funcaoDAO.buscarPorId(id);
            if (funcao == null) {
                JOptionPane.showMessageDialog(this, "Função não encontrada para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showAdicionarFuncaoModal(funcao);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar função: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createGerenciarEmprestimosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Painel superior com logo e busca
        panel.add(criarPainelSuperiorEmprestimo(), BorderLayout.NORTH);

        // Painel central com informações
        panel.add(criarPainelCentralEmprestimo(), BorderLayout.CENTER);

        // Painel inferior com tabela
        panel.add(criarPainelTabelaEmprestimo(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelSuperiorEmprestimo() {
        JPanel painelTop = new JPanel(new BorderLayout(20, 0));
        painelTop.setOpaque(false);

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText("EMPRESTAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        logoPanel.setPreferredSize(new Dimension(120, 120));

        JTextField txtNumeroEstudante = new JTextField();
        ((AbstractDocument) txtNumeroEstudante.getDocument()).setDocumentFilter(new DigitOnlyFilter());

        JTextField txtCodigoLivro = new JTextField();

        JPanel fieldWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        fieldWrapper.setOpaque(false);
        fieldWrapper.add(createLabeledFieldPanel("Número do Estudante", "src/view/icon/user.png", txtNumeroEstudante, 230, 50));
        fieldWrapper.add(createLabeledFieldPanel("Código do Livro", "src/view/icon/livro.png", txtCodigoLivro, 230, 50));

        JButton btnVerificar = new RoundedButton("VERIFICAR", 24);
        btnVerificar.setPreferredSize(new Dimension(140, 42));
        btnVerificar.setBackground(new Color(255, 140, 0));
        btnVerificar.setForeground(Color.WHITE);
        btnVerificar.setFocusPainted(false);
        btnVerificar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnVerificar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerificar.setBorder(new LineBorder(new Color(255, 140, 0), 2, true));
        btnVerificar.setMargin(new Insets(0, 0, 0, 0));
        btnVerificar.addActionListener(e -> verificarEmprestimo(txtNumeroEstudante, txtCodigoLivro));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        actionPanel.setOpaque(false);
        actionPanel.add(fieldWrapper);
        actionPanel.add(btnVerificar);

        painelTop.add(logoPanel, BorderLayout.WEST);
        painelTop.add(actionPanel, BorderLayout.CENTER);

        return painelTop;
    }

    private JPanel criarPainelCentralEmprestimo() {
        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setOpaque(false);
        painelCentral.setBorder(new EmptyBorder(20, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Informações do Estudante
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel labelEstudante = new JLabel("INFORMAÇÕES DO ESTUDANTE", SwingConstants.LEFT);
        labelEstudante.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelEstudante.setForeground(new Color(12, 32, 55));
        painelCentral.add(labelEstudante, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel labelNomeEst = new JLabel("Nome:");
        labelNomeEst.setFont(new Font("Segoe UI", Font.BOLD, 13));
        painelCentral.add(labelNomeEst, gbc);

        gbc.gridx = 1;
        lblNomeEstudanteEmp = new JLabel("-");
        lblNomeEstudanteEmp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelCentral.add(lblNomeEstudanteEmp, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel labelCursoEst = new JLabel("Curso:");
        labelCursoEst.setFont(new Font("Segoe UI", Font.BOLD, 13));
        painelCentral.add(labelCursoEst, gbc);

        gbc.gridx = 1;
        lblCursoEstudanteEmp = new JLabel("-");
        lblCursoEstudanteEmp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelCentral.add(lblCursoEstudanteEmp, gbc);

        // Informações do Livro
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel labelLivro = new JLabel("INFORMAÇÕES DO LIVRO", SwingConstants.LEFT);
        labelLivro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelLivro.setForeground(new Color(12, 32, 55));
        painelCentral.add(labelLivro, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel labelTituloLivro = new JLabel("Título:");
        labelTituloLivro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        painelCentral.add(labelTituloLivro, gbc);

        gbc.gridx = 1;
        lblTituloLivroEmp = new JLabel("-");
        lblTituloLivroEmp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelCentral.add(lblTituloLivroEmp, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel labelCodigoLivro = new JLabel("Código:");
        labelCodigoLivro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        painelCentral.add(labelCodigoLivro, gbc);

        gbc.gridx = 1;
        lblCodigoLivroEmp = new JLabel("-");
        lblCodigoLivroEmp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelCentral.add(lblCodigoLivroEmp, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel labelArmario = new JLabel("Armário:");
        labelArmario.setFont(new Font("Segoe UI", Font.BOLD, 13));
        painelCentral.add(labelArmario, gbc);

        gbc.gridx = 1;
        lblArmarioLivroEmp = new JLabel("-");
        lblArmarioLivroEmp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelCentral.add(lblArmarioLivroEmp, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel labelPartileira = new JLabel("Partileira:");
        labelPartileira.setFont(new Font("Segoe UI", Font.BOLD, 13));
        painelCentral.add(labelPartileira, gbc);

        gbc.gridx = 1;
        lblPartileiraLivroEmp = new JLabel("-");
        lblPartileiraLivroEmp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelCentral.add(lblPartileiraLivroEmp, gbc);

        // Data e Hora de Entrega
        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel labelEntrega = new JLabel("INFORMAÇÕES DE ENTREGA", SwingConstants.LEFT);
        labelEntrega.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelEntrega.setForeground(new Color(12, 32, 55));
        painelCentral.add(labelEntrega, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        JLabel labelDataEntrega = new JLabel("Data de Entrega (7 dias):");
        labelDataEntrega.setFont(new Font("Segoe UI", Font.BOLD, 13));
        painelCentral.add(labelDataEntrega, gbc);

        gbc.gridx = 1;
        lblDataEntregaEmp = new JLabel("-");
        lblDataEntregaEmp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelCentral.add(lblDataEntregaEmp, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        JLabel labelHoraEntrega = new JLabel("Hora de Entrega:");
        labelHoraEntrega.setFont(new Font("Segoe UI", Font.BOLD, 13));
        painelCentral.add(labelHoraEntrega, gbc);

        gbc.gridx = 1;
        lblHoraEntregaEmp = new JLabel("-");
        lblHoraEntregaEmp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelCentral.add(lblHoraEntregaEmp, gbc);

        // Botão Confirmar
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        btnConfirmarEmprestimoEmp = new RoundedButton("CONFIRMAR EMPRÉSTIMO", 24);
        btnConfirmarEmprestimoEmp.setPreferredSize(new Dimension(200, 42));
        btnConfirmarEmprestimoEmp.setBackground(new Color(76, 175, 80));
        btnConfirmarEmprestimoEmp.setForeground(Color.WHITE);
        btnConfirmarEmprestimoEmp.setFocusPainted(false);
        btnConfirmarEmprestimoEmp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConfirmarEmprestimoEmp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmarEmprestimoEmp.setBorder(new LineBorder(new Color(76, 175, 80), 2, true));
        btnConfirmarEmprestimoEmp.setMargin(new Insets(0, 0, 0, 0));
        btnConfirmarEmprestimoEmp.setEnabled(false);
        btnConfirmarEmprestimoEmp.addActionListener(e -> confirmarEmprestimo());
        painelCentral.add(btnConfirmarEmprestimoEmp, gbc);

        return painelCentral;
    }

    private JPanel criarPainelTabelaEmprestimo() {
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setOpaque(false);
        painelTabela.setBorder(BorderFactory.createTitledBorder("Empréstimos em Andamento"));

        // Campo de pesquisa por número de estudante
        JTextField txtPesquisarEmprestimo = new JTextField();
        ((AbstractDocument) txtPesquisarEmprestimo.getDocument()).setDocumentFilter(new DigitOnlyFilter());
        JPanel pesquisaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pesquisaPanel.setOpaque(false);
        pesquisaPanel.add(createLabeledFieldPanel("Pesquisar por nº do Estudante", "src/view/icon/user.png", txtPesquisarEmprestimo, 280, 40));
        painelTabela.add(pesquisaPanel, BorderLayout.NORTH);

        emprestimosTableModel = new DefaultTableModel(new Object[]{"ID", "Estudante", "Livro", "Armário", "Empréstimo", "Entrega", "Hora Entrega", "Valor (KZ)", "Status", "Ação"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Apenas coluna Ação é editável
            }
        };

        emprestimosTable = new JTable(emprestimosTableModel);
        emprestimosTable.setRowHeight(30);
        emprestimosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emprestimosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        emprestimosTable.getTableHeader().setBackground(new Color(12, 32, 55));
        emprestimosTable.getTableHeader().setForeground(Color.WHITE);
        emprestimosTable.setRowSorter(new TableRowSorter<>(emprestimosTableModel));
        emprestimosTable.getColumnModel().getColumn(9).setCellRenderer(new SmallButtonRenderer());
        emprestimosTable.getColumnModel().getColumn(9).setMaxWidth(120);

        // Listener para pesquisa
        txtPesquisarEmprestimo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtPesquisarEmprestimo.getText().trim();
                if (text.isEmpty()) {
                    if (emprestimosTable.getRowSorter() != null) {
                        ((TableRowSorter<?>) emprestimosTable.getRowSorter()).setRowFilter(null);
                    }
                } else {
                    ((TableRowSorter<?>) emprestimosTable.getRowSorter()).setRowFilter(
                        RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1)  // Coluna Estudante (índice 1)
                    );
                }
            }
        });

        // Listener para clique na coluna Ação (Entrega)
        emprestimosTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = emprestimosTable.rowAtPoint(e.getPoint());
                int col = emprestimosTable.columnAtPoint(e.getPoint());
                
                if (col == 9 && row >= 0) { // Coluna Ação (índice 9)
                    try {
                        int modelRow = emprestimosTable.convertRowIndexToModel(row);
                        if (modelRow >= 0 && modelRow < emprestimosTableModel.getRowCount()) {
                            Integer idEmprestimo = (Integer) emprestimosTableModel.getValueAt(modelRow, 0);
                            String status = (String) emprestimosTableModel.getValueAt(modelRow, 8);
                            
                            if (status.equals("Pendente")) {
                                confirmarDevolucao(idEmprestimo);
                            } else {
                                JOptionPane.showMessageDialog(TelaPrincipalAdmin.this, "Este empréstimo já foi devolvido!");
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(TelaPrincipalAdmin.this, "Erro ao processar clique: " + ex.getMessage());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(emprestimosTable);
        scrollPane.setPreferredSize(new Dimension(0, 180));
        painelTabela.add(scrollPane, BorderLayout.CENTER);

        carregarEmprestimos();
        return painelTabela;
    }

    private void verificarEmprestimo(JTextField txtNumeroEstudante, JTextField txtCodigoLivro) {
        String numeroEstudante = txtNumeroEstudante.getText().trim();
        String codigoLivro = txtCodigoLivro.getText().trim();

        if (numeroEstudante.isEmpty() || codigoLivro.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o número do estudante e o código do livro!");
            return;
        }

        try {
            estudanteAtualEmp = estudanteController.buscarPorNumero(numeroEstudante);
            if (estudanteAtualEmp == null) {
                JOptionPane.showMessageDialog(this, "Estudante não encontrado!");
                limparCamposEmprestimo();
                return;
            }

            livroAtualEmp = livroDAO.buscarPorCodigo(codigoLivro);
            if (livroAtualEmp == null) {
                JOptionPane.showMessageDialog(this, "Livro não encontrado!");
                limparCamposEmprestimo();
                return;
            }

            // Preenchendo as informações
            lblNomeEstudanteEmp.setText(estudanteAtualEmp.getNome());
            
            try {
                Curso curso = cursoDAO.buscarPorId(estudanteAtualEmp.getIdCurso());
                lblCursoEstudanteEmp.setText(curso != null ? curso.getCurso() : "N/A");
            } catch (SQLException ex) {
                lblCursoEstudanteEmp.setText("N/A");
            }

            lblTituloLivroEmp.setText(livroAtualEmp.getTitulo());
            lblCodigoLivroEmp.setText(livroAtualEmp.getCodigo());

            try {
                Armario armario = armarioDAO.buscarPorId(livroAtualEmp.getIdArmario());
                lblArmarioLivroEmp.setText(armario != null ? armario.getArmario() : "N/A");
                lblPartileiraLivroEmp.setText(armario != null ? armario.getPartileira() : "N/A");
            } catch (SQLException ex) {
                lblArmarioLivroEmp.setText("N/A");
                lblPartileiraLivroEmp.setText("N/A");
            }

            // Data e hora de entrega
            java.sql.Date dataEntrega = calcularDataEntrega();
            lblDataEntregaEmp.setText(dataEntrega.toString());

            java.time.LocalTime hora = java.time.LocalTime.now();
            lblHoraEntregaEmp.setText(hora.toString());

            // Habilitar botão confirmar
            btnConfirmarEmprestimoEmp.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmarEmprestimo() {
        if (estudanteAtualEmp == null || livroAtualEmp == null) {
            JOptionPane.showMessageDialog(this, "Erro: estudante ou livro não encontrado!");
            return;
        }

        try {
            // Validar se o livro já está emprestado
            if (emprestimoDAO.existeEmprestimoAtivoPorLivro(livroAtualEmp.getIdLivro())) {
                JOptionPane.showMessageDialog(this,
                    "Este livro já está emprestado e não pode ser emprestado novamente até ser devolvido.",
                    "Empréstimo Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar se o estudante já tem um empréstimo pendente
            List<Emprestimo> emprestimos = emprestimoDAO.listarTodos();
            for (Emprestimo emp : emprestimos) {
                if (emp.getIdEstudante() == estudanteAtualEmp.getIdEstudante() && 
                    (emp.getAcao() == null || emp.getAcao().equals("Nao"))) {
                    JOptionPane.showMessageDialog(this, 
                        "Este estudante já possui um empréstimo pendente!\nPor favor, devolva o livro anterior antes de fazer um novo empréstimo.",
                        "Empréstimo Duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setIdEstudante(estudanteAtualEmp.getIdEstudante());
            emprestimo.setIdLivro(livroAtualEmp.getIdLivro());
            emprestimo.setDataDeEmprestimo(new java.sql.Date(System.currentTimeMillis()));
            emprestimo.setHoraDeEmprestimo(new java.sql.Time(System.currentTimeMillis()));
            
            java.sql.Date dataEntrega = calcularDataEntrega();
            emprestimo.setDataEntrega(dataEntrega);
            emprestimo.setHoraDeEntrega(new java.sql.Time(System.currentTimeMillis()));
            
            emprestimo.setAcao("Nao");
            emprestimo.setValor(0.0);
            
            if (funcionarioAutenticado != null) {
                emprestimo.setIdFuncionario(funcionarioAutenticado.getIdFuncionario());
            }

            emprestimoDAO.salvar(emprestimo);
            JOptionPane.showMessageDialog(this, "Empréstimo confirmado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCamposEmprestimo();
            carregarEmprestimos();
            carregarRelatorioEmprestimos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao confirmar empréstimo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCamposEmprestimo() {
        lblNomeEstudanteEmp.setText("-");
        lblCursoEstudanteEmp.setText("-");
        lblTituloLivroEmp.setText("-");
        lblCodigoLivroEmp.setText("-");
        lblArmarioLivroEmp.setText("-");
        lblPartileiraLivroEmp.setText("-");
        lblDataEntregaEmp.setText("-");
        lblHoraEntregaEmp.setText("-");
        btnConfirmarEmprestimoEmp.setEnabled(false);
        estudanteAtualEmp = null;
        livroAtualEmp = null;
    }

    private java.sql.Date calcularDataEntrega() {
        java.time.LocalDate dataAtual = java.time.LocalDate.now();
        java.time.LocalDate dataEntrega = dataAtual.plusDays(7);
        return java.sql.Date.valueOf(dataEntrega);
    }

    private void confirmarDevolucao(int idEmprestimo) {
        try {
            Emprestimo emprestimo = emprestimoDAO.buscarPorId(idEmprestimo);
            if (emprestimo == null) {
                JOptionPane.showMessageDialog(this, "Empréstimo não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirmar com o usuário
            int result = JOptionPane.showConfirmDialog(this,
                "Deseja confirmar a devolução do livro?\n\nEstudante: " + 
                new EstudanteDAO().buscarPorId(emprestimo.getIdEstudante()).getNome() +
                "\nLivro: " + livroDAO.buscarPorId(emprestimo.getIdLivro()).getTitulo(),
                "Confirmar Devolução", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                // Calcular multa por atraso ANTES de marcar como devolvido
                double multa = emprestimoDAO.calcularMultaPorAtraso(emprestimo);
                double valorTotal = emprestimo.getValor() + multa;
                
                // Atualizar status para "Devolvido" e definir valor final com multa
                emprestimo.setAcao("Devolvido");
                emprestimo.setValor(valorTotal);  // Valor final = original + multa
                emprestimo.setDataEHoraDaEntrega(new java.util.Date());
                emprestimoDAO.atualizar(emprestimo);

                // Mostrar a multa cobrada ao usuário
                if (multa > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Devolução confirmada!\n\nValor cobrado: " + String.format("%.2f KZ", valorTotal) + 
                        "\nMulta por atraso: " + String.format("%.2f KZ", multa),
                        "Devolução com Multa", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Devolução confirmada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }
                carregarEmprestimos();
                carregarRelatorioEmprestimos();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao confirmar devolução: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarEmprestimos() {
        if (emprestimosTableModel == null) {
            return;
        }

        emprestimosTableModel.setRowCount(0);
        List<Emprestimo> emprestimos = emprestimoDAO.listarTodos();
        for (Emprestimo emprestimo : emprestimos) {
            String estudanteNome = "N/A";
            String livroTitulo = "N/A";
            if (emprestimo.getIdEstudante() > 0) {
                try {
                    Estudante estudante = new EstudanteDAO().buscarPorId(emprestimo.getIdEstudante());
                    if (estudante != null) {
                        estudanteNome = estudante.getNome() + " (" + estudante.getNumeroEstudante() + ")";
                    }
                } catch (SQLException ex) {
                    estudanteNome = "Erro ao carregar estudante";
                }
            }
            String armarioNome = "N/A";
            if (emprestimo.getIdLivro() > 0) {
                try {
                    Livro livro = livroDAO.buscarPorId(emprestimo.getIdLivro());
                    if (livro != null) {
                        livroTitulo = livro.getTitulo();
                        Armario armario = armarioDAO.buscarPorId(livro.getIdArmario());
                        if (armario != null) {
                            armarioNome = armario.getArmario();
                        }
                    }
                } catch (SQLException ex) {
                    livroTitulo = "Erro ao carregar livro";
                    armarioNome = "Erro ao carregar armário";
                }
            }
            String dataEntrega = emprestimo.getDataEntrega() != null ? emprestimo.getDataEntrega().toString() : "";
            String horaEntrega = emprestimo.getHoraDeEntrega() != null ? emprestimo.getHoraDeEntrega().toString() : "";
            String dataEmprestimo = emprestimo.getDataDeEmprestimo() != null ? emprestimo.getDataDeEmprestimo().toString() : "";
            String status = emprestimo.getAcao() != null && emprestimo.getAcao().equals("Devolvido") ? "✓ Devolvido" : "Pendente";
            
            // Calcular multa por atraso
            

            double multa = emprestimoDAO.calcularMultaPorAtraso(emprestimo);
            double valorTotal = emprestimo.getValor() + multa;

            // IMPLEMENTAR 200,00 KZ A MULTA
            String valorFormatado = String.format("%.2f KZ", valorTotal);
            
            emprestimosTableModel.addRow(new Object[]{
                emprestimo.getIdEmprestimo(),
                estudanteNome,
                livroTitulo,
                armarioNome,
                dataEmprestimo,
                dataEntrega,
                horaEntrega,
                valorFormatado,
                status,
                "Entrega"
            });
        }
    }

    private void showAdicionarEmprestimoModal(Emprestimo emprestimo) {
        JDialog dialog = createModalDialog(emprestimo == null ? "Adicionar Empréstimo" : "Editar Empréstimo");
        dialog.setSize(560, 680);
        dialog.setResizable(true);
        dialog.setLocationRelativeTo(this);
        JLabel logoLabel = new JLabel();
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText(emprestimo == null ? "ADICIONAR" : "EDITAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JComboBox<Estudante> comboEstudante = new JComboBox<>();
        comboEstudante.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Estudante) {
                    Estudante estudante = (Estudante) value;
                    String nome = estudante.getNome() != null ? estudante.getNome() : "Nenhum estudante";
                    String numero = estudante.getNumeroEstudante() != null ? estudante.getNumeroEstudante() : "";
                    setText(nome + (numero.isEmpty() ? "" : " (" + numero + ")"));
                }
                return this;
            }
        });

        JComboBox<Livro> comboLivro = new JComboBox<>();
        comboLivro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Livro) {
                    Livro livro = (Livro) value;
                    setText(livro.getTitulo() != null ? livro.getTitulo() : "Nenhum livro");
                }
                return this;
            }
        });

        try {
            List<Estudante> estudantes = new EstudanteDAO().listarTodos();
            if (estudantes.isEmpty()) {
                Estudante vazio = new Estudante();
                vazio.setNome("Nenhum estudante cadastrado");
                comboEstudante.addItem(vazio);
                comboEstudante.setEnabled(false);
            } else {
                for (Estudante estudante : estudantes) {
                    comboEstudante.addItem(estudante);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Erro ao carregar estudantes: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        try {
            List<Livro> livros = livroDAO.listarTodos();
            if (livros.isEmpty()) {
                Livro vazio = new Livro();
                vazio.setTitulo("Nenhum livro cadastrado");
                comboLivro.addItem(vazio);
                comboLivro.setEnabled(false);
            } else {
                for (Livro livro : livros) {
                    comboLivro.addItem(livro);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Erro ao carregar livros: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JTextField txtValor = new JTextField(emprestimo != null ? String.valueOf(emprestimo.getValor()) : "");
        JComboBox<String> cbAcao = new JComboBox<>(new String[]{"Nao", "Devolvido"});
        JTextField txtDataEntrega = new JTextField(emprestimo != null && emprestimo.getDataEntrega() != null ? emprestimo.getDataEntrega().toString() : "");
        JTextField txtHoraEntrega = new JTextField(emprestimo != null && emprestimo.getHoraDeEntrega() != null ? emprestimo.getHoraDeEntrega().toString() : "");

        if (emprestimo != null && emprestimo.getAcao() != null) {
            cbAcao.setSelectedItem(emprestimo.getAcao());
        }

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledComboPanel("Estudante", "src/view/icon/user.png", comboEstudante, 300, 50), gbc);
        gbc.gridy = 1;
        formPanel.add(createLabeledComboPanel("Livro", "src/view/icon/livro.png", comboLivro, 300, 50), gbc);
        gbc.gridy = 2;
        formPanel.add(createLabeledFieldPanel("Valor", "src/view/icon/valor.png", txtValor, 300, 50), gbc);
        gbc.gridy = 3;
        formPanel.add(createLabeledComboPanel("Status", "src/view/icon/status.png", cbAcao, 300, 50), gbc);
        gbc.gridy = 4;
        formPanel.add(createLabeledFieldPanel("Data de Entrega", "src/view/icon/calendario.png", txtDataEntrega, 300, 50), gbc);
        gbc.gridy = 5;
        formPanel.add(createLabeledFieldPanel("Hora de Entrega", "src/view/icon/relogio.png", txtHoraEntrega, 300, 50), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton(emprestimo == null ? "SALVAR" : "SALVAR ALTERAÇÃO", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            Estudante selecionado = (Estudante) comboEstudante.getSelectedItem();
            Livro livroSelecionado = (Livro) comboLivro.getSelectedItem();
            String valorText = getFieldValue(txtValor, "Valor");
            String status = (String) cbAcao.getSelectedItem();
            String dataEntregaText = getFieldValue(txtDataEntrega, "Data de Entrega");
            String horaEntregaText = getFieldValue(txtHoraEntrega, "Hora de Entrega");

            if (selecionado == null || selecionado.getIdEstudante() == 0) {
                JOptionPane.showMessageDialog(dialog, "Selecione um estudante válido.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (livroSelecionado == null || livroSelecionado.getIdLivro() == 0) {
                JOptionPane.showMessageDialog(dialog, "Selecione um livro válido.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double valor = 0;
            if (!valorText.isEmpty()) {
                try {
                    valor = Double.parseDouble(valorText.replace(',', '.'));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Valor inválido.", "Atenção", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Emprestimo atual = emprestimo != null ? emprestimo : new Emprestimo();
            atual.setIdEstudante(selecionado.getIdEstudante());
            atual.setIdLivro(livroSelecionado.getIdLivro());
            if (emprestimo == null) {
                atual.setDataDeEmprestimo(new java.sql.Date(System.currentTimeMillis()));
                atual.setHoraDeEmprestimo(new java.sql.Time(System.currentTimeMillis()));
            }

            java.sql.Date dataEntrega = null;
            if (!dataEntregaText.isEmpty()) {
                try {
                    dataEntrega = java.sql.Date.valueOf(dataEntregaText);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog, "Formato de data de entrega inválido. Use YYYY-MM-DD.", "Atenção", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                dataEntrega = new java.sql.Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000);
            }
            atual.setDataEntrega(dataEntrega);

            java.sql.Time horaEntrega;
            if (!horaEntregaText.isEmpty()) {
                try {
                    horaEntrega = java.sql.Time.valueOf(horaEntregaText);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog, "Formato de hora de entrega inválido. Use HH:MM:SS.", "Atenção", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                horaEntrega = new java.sql.Time(System.currentTimeMillis());
            }
            atual.setHoraDeEntrega(horaEntrega);

            atual.setValor(valor);
            atual.setAcao(status);
            if ("Devolvido".equalsIgnoreCase(status)) {
                atual.setDataEHoraDaEntrega(new java.util.Date());
            } else {
                atual.setDataEHoraDaEntrega(null);
            }
            if (funcionarioAutenticado != null) {
                atual.setIdFuncionario(funcionarioAutenticado.getIdFuncionario());
            }

            try {
                if (emprestimo == null) {
                    emprestimoDAO.salvar(atual);
                } else {
                    emprestimoDAO.atualizar(atual);
                }
                carregarEmprestimos();
                JOptionPane.showMessageDialog(dialog, "Empréstimo salvo com sucesso!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar empréstimo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCancelar = new RoundedButton("CANCELAR", 24);
        styleActionButton(btnCancelar, new Color(220, 53, 69));
        btnCancelar.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        JPanel topPanelDialog = createDialogTopPanel(logoLabel, dialog);
        dialog.add(topPanelDialog, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteEmprestimoRow(int row) {
        if (row < 0 || emprestimosTableModel == null) {
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este empréstimo?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        int id = Integer.parseInt(emprestimosTableModel.getValueAt(row, 0).toString());
        try {
            emprestimoDAO.deletar(id);
            emprestimosTableModel.removeRow(row);
            JOptionPane.showMessageDialog(this, "Empréstimo deletado com sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar empréstimo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewEmprestimoRow(int row) {
        if (row < 0 || emprestimosTableModel == null) {
            return;
        }

        String estudante = emprestimosTableModel.getValueAt(row, 1).toString();
        String livro = emprestimosTableModel.getValueAt(row, 2).toString();
        String dataEmprestimo = emprestimosTableModel.getValueAt(row, 3).toString();
        String dataEntrega = emprestimosTableModel.getValueAt(row, 4).toString();
        String status = emprestimosTableModel.getValueAt(row, 5).toString();

        JOptionPane.showMessageDialog(this,
                "Estudante: " + estudante + "\nLivro: " + livro + "\nEmpréstimo: " + dataEmprestimo + "\nEntrega: " + dataEntrega + "\nStatus: " + status,
                "Detalhes do Empréstimo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editEmprestimoRow(int row) {
        if (row < 0 || emprestimosTableModel == null) {
            return;
        }

        int id = Integer.parseInt(emprestimosTableModel.getValueAt(row, 0).toString());
        try {
            Emprestimo emprestimo = emprestimoDAO.buscarPorId(id);
            if (emprestimo == null) {
                JOptionPane.showMessageDialog(this, "Empréstimo não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showAdicionarEmprestimoModal(emprestimo);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar empréstimo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createGerenciarRelatoriosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título
        JLabel title = new JLabel("Relatórios");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(30, 40, 55));
        panel.add(title, BorderLayout.NORTH);

        // Painel com abas para diferentes relatórios
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(245, 248, 252));
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabbedPane.addTab("Resumo Geral", criarResumoGeralPanel());
        tabbedPane.addTab("Empréstimos", criarRelatorioBibliografia());
        tabbedPane.addTab("Atendimento", criarRelatorioAtendimento());
        tabbedPane.addTab("Ocupação", criarRelatorioOcupacao());

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarResumoGeralPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 15, 15));
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        try {
            // Card: Total de Estudantes
            panel.add(criarCardEstatistica("Estudantes", String.valueOf(estudanteController.listarEstudantes().size()), new Color(76, 175, 80)));

            // Card: Total de Livros
            List<?> livros = livroDAO.listarTodos();
            panel.add(criarCardEstatistica("Livros", String.valueOf(livros.size()), new Color(33, 150, 243)));

            // Card: Total de Computadores
            List<?> computadores = computadorDAO.listarTodos();
            panel.add(criarCardEstatistica("Computadores", String.valueOf(computadores.size()), new Color(255, 152, 0)));

            // Card: Computadores em Uso
            long emUso = computadores.stream().filter(c -> {
                try {
                    return ((Computador) c).isEmUso();
                } catch (Exception e) {
                    return false;
                }
            }).count();
            panel.add(criarCardEstatistica("Computadores em Uso", String.valueOf(emUso), new Color(244, 67, 54)));

            // Card: Total de Funcionários
            List<?> funcionarios = funcionarioDAO.listarTodos();
            panel.add(criarCardEstatistica("Funcionários", String.valueOf(funcionarios.size()), new Color(156, 39, 176)));

            // Card: Total de Cursos
            List<?> cursos = cursoDAO.listarTodos();
            panel.add(criarCardEstatistica("Cursos", String.valueOf(cursos.size()), new Color(0, 188, 212)));

            // Card: Total de Departamentos
            List<?> departamentos = departamentoDAO.listarTodos();
            panel.add(criarCardEstatistica("Departamentos", String.valueOf(departamentos.size()), new Color(103, 58, 183)));

            // Card: Armários
            List<?> armarios = armarioDAO.listarTodos();
            panel.add(criarCardEstatistica("Armários", String.valueOf(armarios.size()), new Color(63, 81, 181)));

            // Card: Livros Disponíveis
            long disponiveis = livros.stream().filter(l -> {
                try {
                    return ((Livro) l).isDisponivel();
                } catch (Exception e) {
                    return false;
                }
            }).count();
            panel.add(criarCardEstatistica("Livros Disponíveis", String.valueOf(disponiveis), new Color(76, 175, 80)));

        } catch (Exception ex) {
            JLabel erro = new JLabel("Erro ao carregar dados: " + ex.getMessage());
            erro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            erro.setForeground(Color.RED);
            panel.add(erro);
        }

        return panel;
    }

    private JPanel criarCardEstatistica(String titulo, String valor, Color cor) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(cor, 2));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelTitulo.setForeground(new Color(100, 100, 100));
        card.add(labelTitulo, BorderLayout.NORTH);

        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 32));
        labelValor.setForeground(cor);
        labelValor.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(labelValor, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarRelatorioBibliografia() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Painel superior com filtros e botão imprimir
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JTextField txtDataDe = new JTextField(10);
        txtDataDe.setText("YYYY-MM-DD");
        JTextField txtDataAte = new JTextField(10);
        txtDataAte.setText("YYYY-MM-DD");

        JButton btnFiltrar = createPrimaryButton("Filtrar");
        JButton btnImprimir = createPrimaryButton("Imprimir Relatório");

        JPanel resumoPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        resumoPanel.setOpaque(false);
        resumoPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        this.relatorioEmprestimosResumoPanel = resumoPanel;

        btnFiltrar.addActionListener(e -> aplicarFiltroEmprestimos(relatorioEmprestimosTableModel, resumoPanel, txtDataDe.getText().trim(), txtDataAte.getText().trim()));
        btnImprimir.addActionListener(e -> {
            aplicarFiltroEmprestimos(relatorioEmprestimosTableModel, resumoPanel, txtDataDe.getText().trim(), txtDataAte.getText().trim());
            imprimirComponente(panel, "Relatório de Empréstimos");
        });

        topPanel.add(new JLabel("Data De:"));
        topPanel.add(txtDataDe);
        topPanel.add(new JLabel("Data Até:"));
        topPanel.add(txtDataAte);
        topPanel.add(btnFiltrar);
        topPanel.add(btnImprimir);

        JLabel subtitle = new JLabel("Relatório de Empréstimos");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        subtitle.setForeground(new Color(30, 40, 55));
        subtitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(subtitle, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] colunas = {"ID", "Estudante", "Livro", "Data Empréstimo", "Data Entrega", "Status"};
        relatorioEmprestimosTableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        relatorioEmprestimosTable = new JTable(relatorioEmprestimosTableModel);
        relatorioEmprestimosTable.setRowHeight(25);
        relatorioEmprestimosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        relatorioEmprestimosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        relatorioEmprestimosTable.getTableHeader().setBackground(new Color(12, 32, 55));
        relatorioEmprestimosTable.getTableHeader().setForeground(Color.WHITE);

        carregarRelatorioEmprestimos();

        JScrollPane scrollPane = new JScrollPane(relatorioEmprestimosTable);
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(resumoPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void carregarRelatorioEmprestimos() {
        if (relatorioEmprestimosTableModel == null) {
            return;
        }

        relatorioEmprestimosTableModel.setRowCount(0);
        try {
            List<Emprestimo> emprestimos = emprestimoDAO.listarTodos();
            for (Emprestimo emprestimo : emprestimos) {
                Estudante est = estudanteController.buscarPorId(emprestimo.getIdEstudante());
                String nomeEstudante = est != null ? est.getNome() : "N/A";
                String tituloLivro = "N/A";
                try {
                    Livro livro = livroDAO.buscarPorId(emprestimo.getIdLivro());
                    if (livro != null) {
                        tituloLivro = livro.getTitulo();
                    }
                } catch (Exception ex) {
                    tituloLivro = "Erro ao carregar livro";
                }

                String status = "Pendente";
                if (emprestimo.getAcao() != null && emprestimo.getAcao().equalsIgnoreCase("Devolvido")) {
                    status = "Devolvido";
                }

                Object[] row = {
                    emprestimo.getIdEmprestimo(),
                    nomeEstudante,
                    tituloLivro,
                    emprestimo.getDataDeEmprestimo(),
                    emprestimo.getDataEntrega(),
                    status
                };
                relatorioEmprestimosTableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void aplicarFiltroEmprestimos(DefaultTableModel model, JPanel resumoPanel, String dataDe, String dataAte) {
        if (model == null) {
            return;
        }
        try {
            Date de = parseDataFiltro(dataDe);
            Date ate = parseDataFiltro(dataAte);
            if (de != null && ate != null && ate.before(de)) {
                JOptionPane.showMessageDialog(this, "A data de início deve ser anterior ou igual à data de fim.", "Filtro inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            model.setRowCount(0);
            List<Emprestimo> emprestimos = emprestimoDAO.listarTodos();
            List<Emprestimo> emprestimosFiltrados = new java.util.ArrayList<>();
            for (Emprestimo emprestimo : emprestimos) {
                Date dataEmprestimo = emprestimo.getDataDeEmprestimo();
                if (!dataEstaNoIntervalo(dataEmprestimo, de, ate)) {
                    continue;
                }
                emprestimosFiltrados.add(emprestimo);
                Estudante est = estudanteController.buscarPorId(emprestimo.getIdEstudante());
                String nomeEstudante = est != null ? est.getNome() : "N/A";
                String tituloLivro = "N/A";
                try {
                    Livro livro = livroDAO.buscarPorId(emprestimo.getIdLivro());
                    if (livro != null) {
                        tituloLivro = livro.getTitulo();
                    }
                } catch (Exception ex) {
                    tituloLivro = "Erro ao carregar livro";
                }
                String status = "Pendente";
                if (emprestimo.getAcao() != null && emprestimo.getAcao().equalsIgnoreCase("Devolvido")) {
                    status = "Devolvido";
                }
                Object[] row = {
                    emprestimo.getIdEmprestimo(),
                    nomeEstudante,
                    tituloLivro,
                    emprestimo.getDataDeEmprestimo(),
                    emprestimo.getDataEntrega(),
                    status
                };
                model.addRow(row);
            }
            atualizarResumoEmprestimos(resumoPanel, emprestimosFiltrados);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use YYYY-MM-DD.", "Filtro inválido", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void aplicarFiltroAtendimentos(DefaultTableModel model, JPanel resumoPanel, String dataDe, String dataAte) {
        if (model == null) {
            return;
        }
        try {
            Date de = parseDataFiltro(dataDe);
            Date ate = parseDataFiltro(dataAte);
            if (de != null && ate != null && ate.before(de)) {
                JOptionPane.showMessageDialog(this, "A data de início deve ser anterior ou igual à data de fim.", "Filtro inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            model.setRowCount(0);
            List<?> atendimentos = atendimentoDAO.listarTodos();
            List<Atendimento> atendimentosFiltrados = new java.util.ArrayList<>();
            for (Object atend : atendimentos) {
                Atendimento atendimento = (Atendimento) atend;
                Date dataEntrada = atendimento.getDataDeEntrada();
                if (!dataEstaNoIntervalo(dataEntrada, de, ate)) {
                    continue;
                }
                atendimentosFiltrados.add(atendimento);
                Estudante est = estudanteController.buscarPorId(atendimento.getIdEstudante());
                String nomeEstudante = est != null ? est.getNome() : "N/A";
                Computador comp = computadorDAO.buscarPorId(atendimento.getIdComputador());
                String nomeComputador = comp != null ? comp.getNumero() : "N/A";
                Object[] row = {
                    atendimento.getIdAtendimento(),
                    nomeEstudante,
                    nomeComputador,
                    atendimento.getDataDeEntrada(),
                    atendimento.getHoraDeEntrada(),
                    atendimento.getTempoLimiteMinutos(),
                    atendimento.getAcao()
                };
                model.addRow(row);
            }
            atualizarResumoAtendimentos(resumoPanel, atendimentosFiltrados);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use YYYY-MM-DD.", "Filtro inválido", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void atualizarResumoEmprestimos(JPanel resumoPanel, List<Emprestimo> emprestimos) {
        if (resumoPanel == null) {
            return;
        }
        resumoPanel.removeAll();
        int total = emprestimos.size();
        long devolvidos = emprestimos.stream().filter(e -> e.getAcao() != null && e.getAcao().equalsIgnoreCase("Devolvido")).count();
        long pendentes = total - devolvidos;
        long livrosDistintos = emprestimos.stream().map(Emprestimo::getIdLivro).distinct().count();
        long estudantesDistintos = emprestimos.stream().map(Emprestimo::getIdEstudante).distinct().count();

        resumoPanel.add(criarCardEstatistica("Total de Empréstimos", String.valueOf(total), new Color(33, 150, 243)));
        resumoPanel.add(criarCardEstatistica("Devolvidos", String.valueOf(devolvidos), new Color(76, 175, 80)));
        resumoPanel.add(criarCardEstatistica("Pendente", String.valueOf(pendentes), new Color(244, 67, 54)));
        resumoPanel.add(criarCardEstatistica("Livros Distintos", String.valueOf(livrosDistintos), new Color(255, 152, 0)));
        resumoPanel.add(criarCardEstatistica("Estudantes Distintos", String.valueOf(estudantesDistintos), new Color(156, 39, 176)));
        resumoPanel.revalidate();
        resumoPanel.repaint();
    }

    private void atualizarResumoAtendimentos(JPanel resumoPanel, List<Atendimento> atendimentos) {
        if (resumoPanel == null) {
            return;
        }
        resumoPanel.removeAll();
        int total = atendimentos.size();
        long emUso = atendimentos.stream().filter(a -> a.getAcao() != null && a.getAcao().equalsIgnoreCase("Em Uso")).count();
        long finalizados = atendimentos.stream().filter(a -> a.getAcao() != null && a.getAcao().equalsIgnoreCase("Finalizado")).count();
        double mediaTempo = atendimentos.stream().mapToInt(Atendimento::getTempoLimiteMinutos).average().orElse(0.0);

        resumoPanel.add(criarCardEstatistica("Total de Atendimentos", String.valueOf(total), new Color(33, 150, 243)));
        resumoPanel.add(criarCardEstatistica("Em Uso", String.valueOf(emUso), new Color(244, 67, 54)));
        resumoPanel.add(criarCardEstatistica("Finalizados", String.valueOf(finalizados), new Color(76, 175, 80)));
        resumoPanel.add(criarCardEstatistica("Tempo Médio (min)", String.format("%.1f", mediaTempo), new Color(255, 152, 0)));
        resumoPanel.revalidate();
        resumoPanel.repaint();
    }

    private Date parseDataFiltro(String texto) {
        if (texto == null || texto.isBlank() || texto.equals("YYYY-MM-DD")) {
            return null;
        }
        return Date.valueOf(texto.trim());
    }

    private boolean dataEstaNoIntervalo(Date data, Date de, Date ate) {
        if (data == null) {
            return false;
        }
        if (de != null && data.before(de)) {
            return false;
        }
        if (ate != null && data.after(ate)) {
            return false;
        }
        return true;
    }

    private void imprimirTabela(JTable table, String titulo) {
        try {
            boolean concluido = table.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat(titulo), null, true, null, true, null);
            if (!concluido) {
                JOptionPane.showMessageDialog(this, "Impressão cancelada.", "Impressão", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao imprimir: " + ex.getMessage(), "Erro de Impressão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void imprimirComponente(Component component, String titulo) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(titulo);
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            double scaleX = pageFormat.getImageableWidth() / component.getWidth();
            double scaleY = pageFormat.getImageableHeight() / component.getHeight();
            double scale = Math.min(scaleX, scaleY);
            g2d.scale(scale, scale);
            component.printAll(g2d);
            return Printable.PAGE_EXISTS;
        });
        try {
            if (job.printDialog()) {
                job.print();
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao imprimir: " + ex.getMessage(), "Erro de Impressão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel criarRelatorioAtendimento() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Painel superior com filtros e botão imprimir
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JTextField txtDataDe = new JTextField(10);
        txtDataDe.setText("YYYY-MM-DD");
        JTextField txtDataAte = new JTextField(10);
        txtDataAte.setText("YYYY-MM-DD");

        topPanel.add(new JLabel("Data De:"));
        topPanel.add(txtDataDe);
        topPanel.add(new JLabel("Data Até:"));
        topPanel.add(txtDataAte);

        JLabel subtitle = new JLabel("Relatório de Atendimentos");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        subtitle.setForeground(new Color(30, 40, 55));
        subtitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(subtitle, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);

        String[] colunas = {"ID", "Estudante", "Computador", "Data Entrada", "Hora Entrada", "Tempo (min)", "Status"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(12, 32, 55));
        table.getTableHeader().setForeground(Color.WHITE);

        JButton btnFiltrar = createPrimaryButton("Filtrar");
        JButton btnImprimir = createPrimaryButton("Imprimir Relatório");

        JPanel resumoPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        resumoPanel.setOpaque(false);
        resumoPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        this.relatorioAtendimentoResumoPanel = resumoPanel;

        btnFiltrar.addActionListener(e -> aplicarFiltroAtendimentos(model, resumoPanel, txtDataDe.getText().trim(), txtDataAte.getText().trim()));
        btnImprimir.addActionListener(e -> {
            aplicarFiltroAtendimentos(model, resumoPanel, txtDataDe.getText().trim(), txtDataAte.getText().trim());
            imprimirComponente(panel, "Relatório de Atendimentos");
        });

        topPanel.add(btnFiltrar);
        topPanel.add(btnImprimir);

        try {
            List<?> atendimentos = atendimentoDAO.listarTodos();
            for (Object atend : atendimentos) {
                Atendimento atendimento = (Atendimento) atend;
                Estudante est = estudanteController.buscarPorId(atendimento.getIdEstudante());
                String nomeEstudante = est != null ? est.getNome() : "N/A";

                Computador comp = computadorDAO.buscarPorId(atendimento.getIdComputador());
                String nomeComputador = comp != null ? comp.getNumero() : "N/A";

                Object[] row = {
                    atendimento.getIdAtendimento(),
                    nomeEstudante,
                    nomeComputador,
                    atendimento.getDataDeEntrada(),
                    atendimento.getHoraDeEntrada(),
                    atendimento.getTempoLimiteMinutos(),
                    atendimento.getAcao()
                };
                model.addRow(row);
            }
            if (resumoPanel != null) {
                atualizarResumoAtendimentos(resumoPanel, (List<Atendimento>) atendimentos);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(table);
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(resumoPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarRelatorioOcupacao() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel subtitle = new JLabel("Ocupação de Computadores");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        subtitle.setForeground(new Color(30, 40, 55));
        subtitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(subtitle, BorderLayout.NORTH);

        String[] colunas = {"ID", "Número", "Marca", "Modelo", "Status"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(12, 32, 55));
        table.getTableHeader().setForeground(Color.WHITE);

        try {
            List<?> computadores = computadorDAO.listarTodos();
            for (Object comp : computadores) {
                Computador computador = (Computador) comp;
                String status = computador.isEmUso() ? "Em Uso" : "Disponível";
                Color statusColor = computador.isEmUso() ? new Color(244, 67, 54) : new Color(76, 175, 80);

                Object[] row = {
                    computador.getIdComputador(),
                    computador.getNumero(),
                    computador.getMarca(),
                    computador.getModelo(),
                    status
                };
                model.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new RoundedButton(text, 20);
        button.setPreferredSize(new Dimension(120, 32));
        button.setBackground(new Color(34, 197, 94));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
        livrosCache = livroDAO.listarTodos();
        for (Livro livro : livrosCache) {
            livrosTableModel.addRow(new Object[]{
                livro.getIdLivro(),
                livro.getTitulo(),
                livro.getSubtitulo(),
                livro.getAutor(),
                livro.getEdicao(),
                livro.getIsbn(),
                livro.getIssn(),
                livro.getCdn(),
                livro.getDataLancamento() != null ? livro.getDataLancamento().toString() : "",
                livro.getCodigo(),
                livro.getArmarioNome(),
                livro.getArmarioPartileira(),
                livro.isDisponivel() ? "Disponível" : "Indisponível",
                "Ações"
            });
        }
    }

    private void filtrarLivros(String filtro) {
        if (livrosCache == null) {
            carregarLivros();
        }
        livrosTableModel.setRowCount(0);
        Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(filtro));
        for (Livro livro : livrosCache) {
            if (pattern.matcher(livro.getCodigo() == null ? "" : livro.getCodigo()).find()
                    || pattern.matcher(livro.getTitulo() == null ? "" : livro.getTitulo()).find()
                    || pattern.matcher(livro.getIsbn() == null ? "" : livro.getIsbn()).find()
                    || pattern.matcher(livro.getIssn() == null ? "" : livro.getIssn()).find()
                    || pattern.matcher(livro.getCdn() == null ? "" : livro.getCdn()).find()
                    || pattern.matcher(livro.getArmarioPartileira() == null ? "" : livro.getArmarioPartileira()).find()) {
                livrosTableModel.addRow(new Object[]{
                    livro.getIdLivro(),
                    livro.getTitulo(),
                    livro.getSubtitulo(),
                    livro.getAutor(),
                    livro.getEdicao(),
                    livro.getIsbn(),
                    livro.getIssn(),
                    livro.getCdn(),
                    livro.getDataLancamento() != null ? livro.getDataLancamento().toString() : "",
                    livro.getCodigo(),
                    livro.getArmarioNome(),
                    livro.getArmarioPartileira(),
                    livro.isDisponivel() ? "Disponível" : "Indisponível",
                    "Ações"
                });
            }
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
                    armario.getPartileira(),
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

    private void viewFuncionarioRow(int row) {
        if (row < 0 || funcionariosTableModel == null) {
            return;
        }
        int idFuncionario = Integer.parseInt(funcionariosTableModel.getValueAt(row, 0).toString());
        try {
            Funcionario funcionario = funcionarioController.buscarPorId(idFuncionario);
            if (funcionario == null) {
                JOptionPane.showMessageDialog(this, "Funcionário não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String detalhes = "ID: " + funcionario.getIdFuncionario() + "\n" +
                    "Nome: " + funcionario.getNome() + "\n" +
                    "Função: " + funcionario.getFuncao() + "\n" +
                    "Departamento: " + funcionario.getDepartamento() + "\n" +
                    "Contacto: " + funcionario.getContacto();
            JOptionPane.showMessageDialog(this, detalhes, "Detalhes do Funcionário", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar funcionário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editFuncionarioRow(int row) {
        if (row < 0 || funcionariosTableModel == null) {
            return;
        }
        int idFuncionario = Integer.parseInt(funcionariosTableModel.getValueAt(row, 0).toString());
        try {
            Funcionario funcionario = funcionarioController.buscarPorId(idFuncionario);
            if (funcionario == null) {
                JOptionPane.showMessageDialog(this, "Funcionário não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showAdicionarFuncionarioModal(funcionario);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar funcionário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewLivroRow(int row) {
        if (row < 0 || livrosTableModel == null) {
            return;
        }
        int idLivro = Integer.parseInt(livrosTableModel.getValueAt(row, 0).toString());
        Livro livro = livroDAO.buscarPorId(idLivro);
        if (livro == null) {
            JOptionPane.showMessageDialog(this, "Livro não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String detalhes = "ID: " + livro.getIdLivro() + "\n" +
                "Título: " + livro.getTitulo() + "\n" +
                "Subtítulo: " + livro.getSubtitulo() + "\n" +
                "Autor: " + livro.getAutor() + "\n" +
                "Edição: " + livro.getEdicao() + "\n" +
                "ISBN: " + livro.getIsbn() + "\n" +
                "ISSN: " + livro.getIssn() + "\n" +
                "CDN: " + livro.getCdn() + "\n" +
                "Data Lançamento: " + (livro.getDataLancamento() != null ? livro.getDataLancamento().toString() : "") + "\n" +
                "Material: " + livro.getMaterial() + "\n" +
                "Ilustração: " + livro.getIlustracao() + "\n" +
                "Comprimento: " + livro.getComprimento() + "\n" +
                "Largura: " + livro.getLargura() + "\n" +
                "Cor: " + livro.getCor() + "\n" +
                "Páginas: " + livro.getPagina() + "\n" +
                "Código: " + livro.getCodigo() + "\n" +
                "Armário: " + livro.getArmarioNome() + "\n" +
                "Partileira: " + livro.getArmarioPartileira() + "\n" +
                "Disponível: " + (livro.isDisponivel() ? "Sim" : "Não");
        JOptionPane.showMessageDialog(this, detalhes, "Detalhes do Livro", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editLivroRow(int row) {
        if (row < 0 || livrosTableModel == null) {
            return;
        }
        int idLivro = Integer.parseInt(livrosTableModel.getValueAt(row, 0).toString());
        Livro livro = livroDAO.buscarPorId(idLivro);
        if (livro == null) {
            JOptionPane.showMessageDialog(this, "Livro não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        showAdicionarLivroModal(livro);
    }

    private void viewComputadorRow(int row) {
        if (row < 0 || computadoresTableModel == null) {
            return;
        }
        String marca = computadoresTableModel.getValueAt(row, 1).toString();
        String modelo = computadoresTableModel.getValueAt(row, 2).toString();
        String status = computadoresTableModel.getValueAt(row, 3).toString();
        JOptionPane.showMessageDialog(this,
                "Marca: " + marca + "\nModelo: " + modelo + "\nStatus: " + status,
                "Detalhes do Computador", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editComputadorRow(int row) {
        if (row < 0 || computadoresTableModel == null) {
            return;
        }
        int idComputador = Integer.parseInt(computadoresTableModel.getValueAt(row, 0).toString());
        try {
            Computador computador = computadorDAO.buscarPorId(idComputador);
            if (computador == null) {
                JOptionPane.showMessageDialog(this, "Computador não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showAdicionarComputadorModal(computador);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar computador: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewArmarioRow(int row) {
        if (row < 0 || armariosTableModel == null) {
            return;
        }
        String armario = armariosTableModel.getValueAt(row, 1).toString();
        String partileira = armariosTableModel.getValueAt(row, 2) != null ? armariosTableModel.getValueAt(row, 2).toString() : "";
        JOptionPane.showMessageDialog(this,
                "Armário: " + armario + "\n" +
                "Partileira: " + partileira,
                "Detalhes do Armário", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editArmarioRow(int row) {
        if (row < 0 || armariosTableModel == null) {
            return;
        }
        int idArmario = Integer.parseInt(armariosTableModel.getValueAt(row, 0).toString());
        try {
            Armario armario = armarioDAO.buscarPorId(idArmario);
            if (armario == null) {
                JOptionPane.showMessageDialog(this, "Armário não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showAdicionarArmarioModal(armario);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar armário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewCursoRow(int row) {
        if (row < 0 || cursosTableModel == null) {
            return;
        }
        String curso = cursosTableModel.getValueAt(row, 1).toString();
        String departamento = cursosTableModel.getValueAt(row, 2).toString();
        JOptionPane.showMessageDialog(this,
                "Curso: " + curso + "\nDepartamento: " + departamento,
                "Detalhes do Curso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editCursoRow(int row) {
        if (row < 0 || cursosTableModel == null) {
            return;
        }
        int idCurso = Integer.parseInt(cursosTableModel.getValueAt(row, 0).toString());
        try {
            Curso curso = new Curso();
            curso.setIdCurso(idCurso);
            curso.setCurso(cursosTableModel.getValueAt(row, 1).toString());
            curso.setDepartamento(cursosTableModel.getValueAt(row, 2).toString());
            showAdicionarCursoModal(curso);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar curso para edição: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewDepartamentoRow(int row) {
        if (row < 0 || departamentosTableModel == null) {
            return;
        }
        String departamento = departamentosTableModel.getValueAt(row, 1).toString();
        JOptionPane.showMessageDialog(this,
                "Departamento: " + departamento,
                "Detalhes do Departamento", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editDepartamentoRow(int row) {
        if (row < 0 || departamentosTableModel == null) {
            return;
        }
        int idDepartamento = Integer.parseInt(departamentosTableModel.getValueAt(row, 0).toString());
        try {
            Departamento departamento = new Departamento();
            departamento.setIdDepartamento(idDepartamento);
            departamento.setDepartamento(departamentosTableModel.getValueAt(row, 1).toString());
            showAdicionarDepartamentoModal(departamento);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar departamento para edição: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
        JComboBox<String> comboCargo = new JComboBox<>();
        comboCargo.setEditable(true);
        ((javax.swing.text.AbstractDocument) ((javax.swing.text.JTextComponent) comboCargo.getEditor().getEditorComponent()).getDocument()).setDocumentFilter(new LetterOnlyFilter());
        JComboBox<String> comboContacto = new JComboBox<>();
        comboContacto.setEditable(true);
        // Nunca pré-preencher o campo de senha com o hash armazenado; manter vazio por segurança
        JPasswordField txtSenha = new JPasswordField("");

        ((AbstractDocument) txtNome.getDocument()).setDocumentFilter(new LetterOnlyFilter());

        try {
            List<Funcao> funcoes = funcaoDAO.listarTodos();
            if (funcoes.isEmpty()) {
                comboCargo.addItem("Nenhuma função cadastrada");
            } else {
                for (Funcao funcaoItem : funcoes) {
                    comboCargo.addItem(funcaoItem.getFuncao());
                }
            }
            if (funcionario != null && funcionario.getFuncao() != null) {
                comboCargo.setSelectedItem(funcionario.getFuncao());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Erro ao carregar cargos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        try {
            List<Contacto> contactos = contactoDAO.listarTodos();
            if (contactos.isEmpty()) {
                comboContacto.addItem("Sem contacto cadastrado");
            } else {
                contactos.sort((a, b) -> Integer.compare(b.getIdContacto(), a.getIdContacto()));
                for (Contacto contactoItem : contactos) {
                    String texto = contactoItem.getTelefone1() + (contactoItem.getTelefone2() != null && !contactoItem.getTelefone2().trim().isEmpty() ? " / " + contactoItem.getTelefone2() : "");
                    comboContacto.addItem(texto);
                }
            }
            if (funcionario != null && funcionario.getContacto() != null) {
                comboContacto.setSelectedItem(funcionario.getContacto());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Erro ao carregar contactos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

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
        formPanel.add(createLabeledComboPanel("Cargo", "src/view/icon/funcionario.png", comboCargo, 300, 50), gbc);
        gbc.gridy = 2;
        formPanel.add(createLabeledFieldPanel("Senha", "src/view/icon/pass.png", txtSenha, 300, 50), gbc);

        gbc.gridy = 3;
        formPanel.add(createLabeledComboPanel("Contacto", "src/view/icon/contacto.png", comboContacto, 300, 50), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        JButton btnSalvar = new RoundedButton("SALVAR", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            String nome = getFieldValue(txtNome, "Nome");
            Object selectedCargo = comboCargo.getSelectedItem();
            String cargo = selectedCargo != null ? selectedCargo.toString().trim() : "";
            if ("Nenhuma função cadastrada".equals(cargo)) {
                cargo = "";
            }
            String senha = new String(txtSenha.getPassword()).trim();
            Object selectedContacto = comboContacto.getSelectedItem();
            String contacto = selectedContacto != null ? selectedContacto.toString().trim() : "";
            if ("Sem contacto cadastrado".equals(contacto)) {
                contacto = "";
            }
            if (nome.isEmpty() || cargo.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha nome e cargo.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (funcionario == null && senha.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Informe uma senha para o novo funcionário.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Funcionario novo = funcionario != null ? funcionario : new Funcionario();
            novo.setNome(nome);
            novo.setFuncao(cargo);
            if (!senha.isEmpty()) {
                novo.setSenha(senha);
            }
            if (funcionario != null && novo.getSenha() == null) {
                novo.setSenha(funcionario.getSenha());
            }
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
        dialog.setSize(520, 800);
        dialog.setLocationRelativeTo(this);

        JTextField txtTitulo = new JTextField(livro != null ? livro.getTitulo() : "");
        JTextField txtSubtitulo = new JTextField(livro != null ? livro.getSubtitulo() : "");
        JTextField txtAutor = new JTextField(livro != null ? livro.getAutor() : "");
        JTextField txtEdicao = new JTextField(livro != null ? livro.getEdicao() : "");
        JTextField txtIsbn = new JTextField(livro != null ? livro.getIsbn() : "");

        ((AbstractDocument) txtAutor.getDocument()).setDocumentFilter(new LetterOnlyFilter());
        JTextField txtIssn = new JTextField(livro != null ? livro.getIssn() : "");
        JTextField txtCdn = new JTextField(livro != null ? livro.getCdn() : "");
        JTextField txtDataLancamento = new JTextField(livro != null && livro.getDataLancamento() != null ? livro.getDataLancamento().toString() : "");
        JTextField txtMaterial = new JTextField(livro != null ? livro.getMaterial() : "");
        JTextField txtIlustracao = new JTextField(livro != null ? livro.getIlustracao() : "");
        JTextField txtComprimento = new JTextField(livro != null ? String.valueOf(livro.getComprimento()) : "");
        JTextField txtLargura = new JTextField(livro != null ? String.valueOf(livro.getLargura()) : "");
        JTextField txtCor = new JTextField(livro != null ? livro.getCor() : "");
        JTextField txtPagina = new JTextField(livro != null ? String.valueOf(livro.getPagina()) : "");
        JTextField txtCodigo = new JTextField(livro != null ? livro.getCodigo() : "");
        JCheckBox cbDisponivel = new JCheckBox("Disponível", livro != null && livro.isDisponivel());

        final byte[][] capadoLivroBytes = new byte[1][];
        final byte[][] pdfBytes = new byte[1][];
        if (livro != null) {
            capadoLivroBytes[0] = livro.getCapadoLivro();
            pdfBytes[0] = livro.getPdf();
        }

        JLabel lblCapadoLivroNome = new JLabel(livro != null && livro.getCapadoLivro() != null ? gerarNomeCapado(livro.getIdLivro()) : "Nenhum capado selecionado");
        ImageIcon capaIcon = loadIcon("src/view/icon/livro.png", 24, 24);
        if (capadoLivroBytes[0] == null && capaIcon != null) {
            lblCapadoLivroNome.setIcon(capaIcon);
        }
        JLabel lblPdfNome = new JLabel(livro != null && livro.getPdf() != null ? gerarNomePdf(livro.getTitulo()) : "Nenhum arquivo selecionado");
        JButton btnSelecionarCapado = new RoundedButton("Selecionar Capado", 16);
        JButton btnSelecionarPdf = new RoundedButton("Selecionar PDF", 16);
        btnSelecionarCapado.setPreferredSize(new Dimension(160, 40));
        btnSelecionarPdf.setPreferredSize(new Dimension(160, 40));

        btnSelecionarCapado.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Selecione o arquivo de Capado Livro");
            int result = chooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    capadoLivroBytes[0] = Files.readAllBytes(file.toPath());
                    lblCapadoLivroNome.setText(file.getName());
                    lblCapadoLivroNome.setIcon(null);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialog, "Erro ao ler o arquivo de capado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSelecionarPdf.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Selecione o arquivo PDF do livro");
            chooser.setFileFilter(new FileNameExtensionFilter("Arquivos PDF", "pdf"));
            int result = chooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    pdfBytes[0] = Files.readAllBytes(file.toPath());
                    lblPdfNome.setText(file.getName());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialog, "Erro ao ler o arquivo PDF: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        ((AbstractDocument) txtComprimento.getDocument()).setDocumentFilter(new DigitOnlyFilter());
        ((AbstractDocument) txtLargura.getDocument()).setDocumentFilter(new DigitOnlyFilter());
        ((AbstractDocument) txtPagina.getDocument()).setDocumentFilter(new DigitOnlyFilter());
        cbDisponivel.setOpaque(false);

        JComboBox<Armario> comboArmario = new JComboBox<>();
        comboArmario.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Armario) {
                    Armario armario = (Armario) value;
                    setText(armario.toString().isEmpty() ? "Nenhum armário" : armario.toString());
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
        formPanel.add(createLabeledFieldPanel("Título do Livro", "src/view/icon/livro.png", txtTitulo, 300, 50), gbc);
        gbc.gridy = 1;
        formPanel.add(createLabeledFieldPanel("Subtítulo do Livro", "src/view/icon/livro.png", txtSubtitulo, 300, 50), gbc);
        gbc.gridy = 2;
        formPanel.add(createLabeledFieldPanel("Autor do Livro", "src/view/icon/user.png", txtAutor, 300, 50), gbc);
        gbc.gridy = 3;
        formPanel.add(createLabeledFieldPanel("Edição do Livro", "src/view/icon/livro.png", txtEdicao, 300, 50), gbc);
        gbc.gridy = 4;
        //formPanel.add(createLabeledFieldPanel("ISBN - Número Padrão Internacional do Livro ", "src/view/icon/livro.png", txtIsbn, 300, 50), gbc);
        //gbc.gridy = 5;
        //formPanel.add(createLabeledFieldPanel("ISSN", "src/view/icon/livro.png", txtIssn, 300, 50), gbc);
        //gbc.gridy = 6;
        //formPanel.add(createLabeledFieldPanel("CDN", "src/view/icon/livro.png", txtCdn, 300, 50), gbc);
        //gbc.gridy = 7;
        formPanel.add(createLabeledFieldPanel("Data Lançamento. Exemplo: YYYY-MM-DD", "src/view/icon/calendario.png", txtDataLancamento, 300, 50), gbc);
        gbc.gridy = 8;
        formPanel.add(createLabeledFieldPanel("Material", "src/view/icon/livro.png", txtMaterial, 300, 50), gbc);
        gbc.gridy = 9;
        formPanel.add(createLabeledFieldPanel("Ilustração e de seguida Comprimento e Largura do Livro", "src/view/icon/livro.png", txtIlustracao, 300, 50), gbc);
        gbc.gridy = 10;
        formPanel.add(createLabeledFieldPanel("Comprimento", "src/view/icon/livro.png", txtComprimento, 300, 50), gbc);
        gbc.gridy = 11;
        formPanel.add(createLabeledFieldPanel("Largura", "src/view/icon/livro.png", txtLargura, 300, 50), gbc);
        gbc.gridy = 12;
        formPanel.add(createLabeledFieldPanel("Cor de Seguida Digite o Número de Pagínas do Livro", "src/view/icon/livro.png", txtCor, 300, 50), gbc);
        gbc.gridy = 13;
        formPanel.add(createLabeledFieldPanel("Páginas", "src/view/icon/user.png", txtPagina, 300, 50), gbc);
        gbc.gridy = 14;
        formPanel.add(createLabeledFieldPanel("ISBN - Número Padrão Internacional do Livro ", "src/view/icon/user.png", txtCodigo, 300, 50), gbc);
        gbc.gridy = 15;
        formPanel.add(createLabeledComboPanel("Armário", "src/view/icon/armario.png", comboArmario, 300, 50), gbc);
        gbc.gridy = 16;
        JPanel capadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        capadoPanel.setOpaque(false);
        capadoPanel.add(btnSelecionarCapado);
        capadoPanel.add(lblCapadoLivroNome);
        formPanel.add(createLabeledComponentPanel("Capado Livro", "src/view/icon/file.png", capadoPanel), gbc);
        gbc.gridy = 17;
        JPanel pdfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pdfPanel.setOpaque(false);
        pdfPanel.add(btnSelecionarPdf);
        pdfPanel.add(lblPdfNome);
        formPanel.add(createLabeledComponentPanel("PDF do Livro", "src/view/icon/file.png", pdfPanel), gbc);
        gbc.gridy = 18;
        JPanel disponivelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        disponivelPanel.setOpaque(false);
        disponivelPanel.add(cbDisponivel);
        formPanel.add(createLabeledComponentPanel("Disponível", "src/view/icon/check.png", disponivelPanel), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(25, 20, 20, 20));

        JButton btnSalvar = new RoundedButton("SALVAR", 24);
        styleActionButton(btnSalvar, new Color(34, 197, 94));
        btnSalvar.addActionListener(e -> {
            String titulo = getFieldValue(txtTitulo, "Título");
            String subtitulo = getFieldValue(txtSubtitulo, "Subtítulo");
            String autor = getFieldValue(txtAutor, "Autor");
            String edicao = getFieldValue(txtEdicao, "Edição");
           // String isbn = getFieldValue(txtIsbn, "ISBN");
           // String issn = getFieldValue(txtIssn, "ISSN");
            //String cdn = getFieldValue(txtCdn, "CDN");
            String dataLancamentoText = getFieldValue(txtDataLancamento, "Data Lançamento");
            String material = getFieldValue(txtMaterial, "Material");
            String ilustracao = getFieldValue(txtIlustracao, "Ilustração");
            String comprimentoText = getFieldValue(txtComprimento, "Comprimento");
            String larguraText = getFieldValue(txtLargura, "Largura");
            String cor = getFieldValue(txtCor, "Cor");
            String paginaText = getFieldValue(txtPagina, "Páginas");
            String codigo = getFieldValue(txtCodigo, "Código do Livro");
            int paginas = paginaText.isEmpty() ? 0 : Integer.parseInt(paginaText);
            int comprimento = comprimentoText.isEmpty() ? 0 : Integer.parseInt(comprimentoText);
            int largura = larguraText.isEmpty() ? 0 : Integer.parseInt(larguraText);
            Date dataLancamento = null;
            if (!dataLancamentoText.isEmpty()) {
                try {
                    dataLancamento = Date.valueOf(dataLancamentoText);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Data de lançamento inválida. Use formato YYYY-MM-DD.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            Armario armarioSelecionado = (Armario) comboArmario.getSelectedItem();
            if (titulo.isEmpty() || codigo.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha título e código.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Livro novo = livro != null ? livro : new Livro();
            novo.setTitulo(titulo);
            novo.setSubtitulo(subtitulo);
            novo.setAutor(autor);
            novo.setEdicao(edicao);
            //novo.setIsbn(isbn);
            //novo.setIssn(issn);
           // novo.setCdn(cdn);
            novo.setDataLancamento(dataLancamento);
            novo.setMaterial(material);
            novo.setIlustracao(ilustracao);
            novo.setComprimento(comprimento);
            novo.setLargura(largura);
            novo.setCor(cor);
            novo.setPagina(paginas);
            novo.setCodigo(codigo);
            novo.setDisponivel(cbDisponivel.isSelected());
            if (armarioSelecionado != null && armarioSelecionado.getIdArmario() != 0) {
                novo.setIdArmario(armarioSelecionado.getIdArmario());
            }
            novo.setCapadoLivro(capadoLivroBytes[0]);
            novo.setPdf(pdfBytes[0]);
            if (livro == null) {
                livroDAO.salvar(novo);
                salvarArquivosUpload(novo.getIdLivro(), novo.getCapadoLivro(), novo.getPdf(), novo.getTitulo());
            } else {
                livroDAO.atualizar(novo);
                salvarArquivosUpload(novo.getIdLivro(), novo.getCapadoLivro(), novo.getPdf(), novo.getTitulo());
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

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(245, 248, 252));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(500, 700));

        dialog.add(scrollPane, BorderLayout.CENTER);
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
        JTextField txtPartileira = new JTextField(armario != null ? armario.getPartileira() : "");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Armário", "src/view/icon/armario.png", txtArmario, 300, 50), gbc);
        gbc.gridy = 1;
        formPanel.add(createLabeledFieldPanel("Partileira", "src/view/icon/armario.png", txtPartileira, 300, 50), gbc);

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
            novo.setPartileira(txtPartileira.getText().trim());
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
        ((AbstractDocument) txtCurso.getDocument()).setDocumentFilter(new LetterOnlyFilter());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        formPanel.add(createLabeledFieldPanel("Curso", "src/view/icon/curso.png", txtCurso, 300, 50), gbc);

        JComboBox<Departamento> comboDepartamento = new JComboBox<>();
        comboDepartamento.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Departamento) {
                    Departamento departamento = (Departamento) value;
                    setText(departamento.getDepartamento() != null ? departamento.getDepartamento() : "Nenhum departamento");
                }
                return this;
            }
        });

        List<Departamento> departamentos = carregarDepartamentosParaCombo();
        if (departamentos.isEmpty()) {
            comboDepartamento.addItem(new Departamento());
            comboDepartamento.setEnabled(false);
        } else {
            for (Departamento departamento : departamentos) {
                comboDepartamento.addItem(departamento);
            }
        }
        if (curso != null && curso.getDepartamento() != null) {
            for (int i = 0; i < comboDepartamento.getItemCount(); i++) {
                Departamento departamento = comboDepartamento.getItemAt(i);
                if (departamento != null && curso.getDepartamento().equals(departamento.getDepartamento())) {
                    comboDepartamento.setSelectedIndex(i);
                    break;
                }
            }
        }

        gbc.gridy = 1;
        formPanel.add(createLabeledComponentPanel("Departamento", "src/view/icon/departamentos.png", comboDepartamento), gbc);

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
            Departamento departamentoSelecionado = (Departamento) comboDepartamento.getSelectedItem();
            if (departamentoSelecionado != null && departamentoSelecionado.getIdDepartamento() != 0) {
                novo.setIdDepartamento(departamentoSelecionado.getIdDepartamento());
                novo.setDepartamento(departamentoSelecionado.getDepartamento());
            } else {
                novo.setIdDepartamento(0);
                novo.setDepartamento("");
            }
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

    private List<Departamento> carregarDepartamentosParaCombo() {
        try {
            return departamentoDAO.listarTodos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar departamentos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return new java.util.ArrayList<>();
        }
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
        ((AbstractDocument) txtDepartamento.getDocument()).setDocumentFilter(new LetterOnlyFilter());

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
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(logoLabel, BorderLayout.CENTER);
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

    private void salvarArquivosUpload(int idLivro, byte[] capadoBytes, byte[] pdfBytes, String titulo) {
        File uploadsDir = new File("uploads");
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }
        if (capadoBytes != null && capadoBytes.length > 0) {
            File capadoFile = new File(uploadsDir, gerarNomeCapado(idLivro));
            try {
                Files.write(capadoFile.toPath(), capadoBytes);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar arquivo capado localmente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (pdfBytes != null && pdfBytes.length > 0) {
            File pdfFile = new File(uploadsDir, gerarNomePdf(titulo));
            try {
                Files.write(pdfFile.toPath(), pdfBytes);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar arquivo PDF localmente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String gerarNomeCapado(int idLivro) {
        return "Capado_livro_" + idLivro + ".bin";
    }

    private String gerarNomePdf(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return "Livro_sem_titulo.pdf";
        }
        String nome = titulo.trim().replaceAll("\\s+", "_");
        nome = nome.replaceAll("[^A-Za-z0-9_\\-]", "");
        return nome + ".pdf";
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("Bem-vindo ao painel administrativo");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(30, 40, 55));
        panel.add(title, gbc);

        if (funcionarioAutenticado == null || !"Admin".equals(funcionarioAutenticado.getFuncao())) {
            JButton btnAtendimento = createPrimaryButton("Atendimento");
            btnAtendimento.addActionListener(e -> cardLayout.show(cardPanel, "atendimento"));
            gbc.gridy = 1;
            panel.add(btnAtendimento, gbc);
        }

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
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            field.setText(placeholder);
            field.setForeground(new Color(150, 150, 150));
        } else {
            field.setForeground(new Color(30, 40, 50));
        }
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

        component.setOpaque(false);
        if (!(component instanceof JCheckBox)) {
            component.setPreferredSize(new Dimension(300 - 60, 50 - 16));
            component.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        }
        component.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputPanel.add(component, BorderLayout.CENTER);

        wrapper.add(inputPanel, BorderLayout.CENTER);
        return wrapper;
    }


}
