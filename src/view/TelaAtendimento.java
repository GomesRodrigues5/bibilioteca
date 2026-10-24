package view;

import controller.EstudanteController;
import dao.AtendimentoDAO;
import dao.ComputadorDAO;
import dao.CursoDAO;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import model.Atendimento;
import model.Computador;
import model.Curso;
import model.Estudante;

public class TelaAtendimento extends JPanel {
    private JTextField txtNumeroEstudante;
    private JButton btnVerificar;
    private JLabel lblNome;
    private JLabel lblCurso;
    private JComboBox<String> comboComputadores;
    private JButton btnAtender;
    private JTable tableAtendimentos;
    private DefaultTableModel atendimentosTableModel;
    private Timer atualizacaoTimer;
    private boolean exibirAlertas = true;

    private EstudanteController estudanteController = new EstudanteController();
    private AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
    private ComputadorDAO computadorDAO = new ComputadorDAO();
    private CursoDAO cursoDAO = new CursoDAO();
    
    private Estudante estudanteAtual = null;

    public TelaAtendimento() {
        this(true);
    }

    public TelaAtendimento(boolean exibirAlertas) {
        this.exibirAlertas = exibirAlertas;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 252));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Painel superior com logo e busca
        add(criarPainelSuperior(), BorderLayout.NORTH);

        // Painel central com informações do estudante e atendimento
        add(criarPainelCentral(), BorderLayout.CENTER);

        // Painel inferior com tabela de atendimentos
        add(criarPainelTabela(), BorderLayout.SOUTH);

        // Iniciar timer para atualizar tabela
        iniciarTimerAtualizacao();
    }

    private JPanel criarPainelSuperior() {
        JPanel painelTop = new JPanel(new BorderLayout(20, 0));
        painelTop.setOpaque(false);

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon verificarLogo = loadIcon("src/view/img/verificar.png", 80, 80);
        if (verificarLogo != null) {
            logoLabel.setIcon(verificarLogo);
        } else {
            logoLabel.setText("VERIFICAR");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            logoLabel.setForeground(new Color(30, 40, 55));
        }

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        logoPanel.setPreferredSize(new Dimension(120, 120));

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

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        actionPanel.setOpaque(false);
        actionPanel.add(fieldWrapper);
        actionPanel.add(btnVerificar);

        painelTop.add(logoPanel, BorderLayout.WEST);
        painelTop.add(actionPanel, BorderLayout.CENTER);

        return painelTop;
    }

    private JPanel criarPainelCentral() {
        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setOpaque(false);
        painelCentral.setBorder(new EmptyBorder(20, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel labelNome = new JLabel("Nome:");
        labelNome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        painelCentral.add(labelNome, gbc);

        gbc.gridx = 1;
        lblNome = new JLabel();
        lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        painelCentral.add(lblNome, gbc);

        // Curso
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel labelCurso = new JLabel("Curso:");
        labelCurso.setFont(new Font("Segoe UI", Font.BOLD, 14));
        painelCentral.add(labelCurso, gbc);

        gbc.gridx = 1;
        lblCurso = new JLabel();
        lblCurso.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        painelCentral.add(lblCurso, gbc);

        // Computador
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel labelComputador = new JLabel("Computador:");
        labelComputador.setFont(new Font("Segoe UI", Font.BOLD, 14));
        painelCentral.add(labelComputador, gbc);

        gbc.gridx = 1;
        comboComputadores = new JComboBox<>();
        comboComputadores.setEnabled(false);
        comboComputadores.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        painelCentral.add(comboComputadores, gbc);

        // Botão Atender
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        btnAtender = new RoundedButton("ATENDER", 24);
        btnAtender.setPreferredSize(new Dimension(150, 42));
        btnAtender.setBackground(new Color(76, 175, 80));
        btnAtender.setForeground(Color.WHITE);
        btnAtender.setFocusPainted(false);
        btnAtender.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAtender.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtender.setBorder(new LineBorder(new Color(76, 175, 80), 2, true));
        btnAtender.setMargin(new Insets(0, 0, 0, 0));
        btnAtender.setEnabled(false);
        btnAtender.addActionListener(e -> atenderEstudante());
        painelCentral.add(btnAtender, gbc);

        return painelCentral;
    }

    private JPanel criarPainelTabela() {
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setOpaque(false);
        painelTabela.setBorder(BorderFactory.createTitledBorder("Atendimentos em Uso"));

        String[] colunas = {"ID", "Estudante", "Computador", "Entrada", "Tempo (min)", "Tempo Decorrido"};
        atendimentosTableModel = new AtendimentosTableModel(colunas, 0);

        tableAtendimentos = new JTable(atendimentosTableModel);
        tableAtendimentos.setRowHeight(25);
        tableAtendimentos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableAtendimentos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableAtendimentos.getTableHeader().setBackground(new Color(12, 32, 55));
        tableAtendimentos.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tableAtendimentos);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        painelTabela.add(scrollPane, BorderLayout.CENTER);

        return painelTabela;
    }

    private void verificarEstudante() {
        String numero = txtNumeroEstudante.getText().trim();
        if (numero.isEmpty() || numero.equals("Número do Estudante")) {
            JOptionPane.showMessageDialog(this, "Digite o número do estudante!");
            return;
        }

        estudanteAtual = estudanteController.buscarPorNumero(numero);
        if (estudanteAtual != null) {
            lblNome.setText(estudanteAtual.getNome());
            
            try {
                Curso curso = cursoDAO.buscarPorId(estudanteAtual.getIdCurso());
                lblCurso.setText(curso != null ? curso.getCurso() : "N/A");
            } catch (Exception ex) {
                lblCurso.setText("N/A");
            }

            // Carregar computadores disponíveis
            carregarComputadoresDisponiveis();
            comboComputadores.setEnabled(true);
            btnAtender.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Estudante não encontrado!");
            limparCampos();
        }
    }

    private void carregarComputadoresDisponiveis() {
        comboComputadores.removeAllItems();
        try {
            List<Computador> computadores = computadorDAO.listarDisponibles();
            for (Computador comp : computadores) {
                comboComputadores.addItem(comp.getNumero() + " (" + comp.getMarca() + ")");
            }
            if (computadores.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum computador disponível!");
                btnAtender.setEnabled(false);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar computadores: " + ex.getMessage());
        }
    }

    private void atenderEstudante() {
        if (estudanteAtual == null || comboComputadores.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um computador!");
            return;
        }

        // Verificar se estudante pode ser atendido
        try {
            List<Atendimento> atendimentosRecentes = atendimentoDAO.listarPorEstudante(estudanteAtual.getIdEstudante());
            if (!atendimentosRecentes.isEmpty()) {
                Atendimento ultimo = atendimentosRecentes.get(atendimentosRecentes.size() - 1);
                if (ultimo.getAcao() == null || !ultimo.getAcao().equals("Finalizado")) {
                    JOptionPane.showMessageDialog(this, "Estudante já está sendo atendido!");
                    return;
                } else {
                    // Verificar se passaram 15 minutos desde a finalização
                    long tempoFinalizado = ultimo.getDataDeEntrada().getTime() + (ultimo.getTempoLimiteMinutos() * 60 * 1000);
                    long agora = System.currentTimeMillis();
                    long diferencaMinutos = (agora - tempoFinalizado) / (1000 * 60);
                    if (diferencaMinutos < 15) {
                        JOptionPane.showMessageDialog(this, "Estudante deve aguardar 15 minutos após o último atendimento!");
                        return;
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar atendimentos: " + ex.getMessage());
            return;
        }

        try {
            // Obter computador selecionado
            List<Computador> computadores = computadorDAO.listarDisponibles();
            Computador computadorSelecionado = computadores.get(comboComputadores.getSelectedIndex());

            // Criar novo atendimento
            Atendimento atendimento = new Atendimento();
            atendimento.setIdEstudante(estudanteAtual.getIdEstudante());
            atendimento.setIdComputador(computadorSelecionado.getIdComputador());
            atendimento.setDataDeEntrada(new Date(System.currentTimeMillis()));
            atendimento.setHoraDeEntrada(new Time(System.currentTimeMillis()));
            
            // ATUALIZA OS MENUTOS DE UTILIZAÇÃO DOS COMPUTADORES
            LocalTime saida = LocalTime.now().plusMinutes(2); // 2 minutos padrão
            atendimento.setHoraDeSaida(Time.valueOf(saida));  
            atendimento.setTempoLimiteMinutos(2);
            atendimento.setAcao("Em Uso"); // 2 minutos padrão

            // Salvar no banco
            atendimentoDAO.salvar(atendimento);

            // Atualizar status do computador
            computadorSelecionado.setEmUso(true);
            computadorDAO.atualizar(computadorSelecionado);

            JOptionPane.showMessageDialog(this, "Atendimento registrado com sucesso!");
            limparCampos();
            atualizarTabelaAtendimentos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar atendimento: " + ex.getMessage());
        }
    }

    private void atualizarTabelaAtendimentos() {
        atendimentosTableModel.setRowCount(0);
        try {
            List<Atendimento> atendimentos = atendimentoDAO.listarEmUso();
            for (Atendimento at : atendimentos) {
                // Verifica se já finalizou
                if (at.getAcao() == null || at.getAcao().equals("Finalizado")) {
                    continue;
                }

                String nomeEstudante = estudanteController.buscarPorId(at.getIdEstudante()) != null ? 
                    estudanteController.buscarPorId(at.getIdEstudante()).getNome() : "N/A";
                
                Computador comp = computadorDAO.buscarPorId(at.getIdComputador());
                String nomeComputador = comp != null ? comp.getNumero() : "N/A";

                LocalDateTime entradaDateTime = LocalDateTime.of(at.getDataDeEntrada().toLocalDate(), at.getHoraDeEntrada().toLocalTime());
                LocalDateTime saidaDateTime = LocalDateTime.of(at.getDataDeEntrada().toLocalDate(), at.getHoraDeSaida().toLocalTime());
                if (saidaDateTime.isBefore(entradaDateTime)) {
                    saidaDateTime = saidaDateTime.plusDays(1);
                }
                LocalDateTime agora = LocalDateTime.now();

                long decorrido = Duration.between(entradaDateTime, agora).toMinutes();
                long restante = Duration.between(agora, saidaDateTime).toMinutes();

                if (!agora.isBefore(saidaDateTime) || restante <= 0) {
                    // Finaliza atendimento e libera o computador
                    at.setAcao("Finalizado");
                    try {
                        if (comp != null) {
                            // Para modelos específicos, executa ação adicional (bloquear estação)
                            if ("Geração Verd".equalsIgnoreCase(comp.getModelo())) {
                                try {
                                    Runtime.getRuntime().exec("rundll32.exe user32.dll,LockWorkStation");
                                } catch (Exception exExec) {
                                    exExec.printStackTrace();
                                }
                            }
                            comp.setEmUso(false);
                            computadorDAO.atualizar(comp);
                        }
                        atendimentoDAO.atualizar(at);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    continue;
                }

                // Adiciona à tabela
                Object[] row = {
                    at.getIdAtendimento(),
                    nomeEstudante,
                    nomeComputador,
                    at.getHoraDeEntrada(),
                    at.getTempoLimiteMinutos(),
                    decorrido + " min"
                };
                atendimentosTableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void iniciarTimerAtualizacao() {
        atualizacaoTimer = new Timer(1000, e -> atualizarTabelaAtendimentos());
        atualizacaoTimer.start();

        // Atualizar uma vez ao iniciar
        atualizarTabelaAtendimentos();
    }

    private void limparCampos() {
        estudanteAtual = null;
        lblNome.setText("");
        lblCurso.setText("");
        comboComputadores.removeAllItems();
        comboComputadores.setEnabled(false);
        btnAtender.setEnabled(false);
        txtNumeroEstudante.setText("Número do Estudante");
        txtNumeroEstudante.setForeground(new Color(150, 150, 150));
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
        field.addFocusListener(new PlaceholderFocusAdapter(field, placeholder));
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

    private static class PlaceholderFocusAdapter extends FocusAdapter {
        private final JTextField field;
        private final String placeholder;

        public PlaceholderFocusAdapter(JTextField field, String placeholder) {
            this.field = field;
            this.placeholder = placeholder;
        }

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
    }

    private static class AtendimentosTableModel extends DefaultTableModel {
        public AtendimentosTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
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