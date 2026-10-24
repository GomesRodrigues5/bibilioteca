package view;

import controller.EmprestimoController;
import model.Emprestimo;
import util.TemasApp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class TelaEmprestimo extends JFrame {
    private JTextField txtIdEstudante, txtIdCabecalho, txtIdFuncionario, txtDias;
    private JButton btnEmprestar, btnDevolver, btnRenovar, btnBuscar;
    private JTable tabela;
    private EmprestimoController controller;

    public TelaEmprestimo() {
        super("Gestão de Empréstimos");
        TemasApp.aplicarTemaModerno();
        controller = new EmprestimoController();
        initComponents();
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel topo = new JPanel(new GridLayout(2, 4, 8, 8));
        txtIdEstudante = new JTextField();
        txtIdCabecalho = new JTextField();
        txtIdFuncionario = new JTextField();
        txtDias = new JTextField("14");

        topo.add(new JLabel("ID Estudante:")); topo.add(txtIdEstudante);
        topo.add(new JLabel("ID Cabeçalho (Livro):")); topo.add(txtIdCabecalho);
        topo.add(new JLabel("ID Funcionário:")); topo.add(txtIdFuncionario);
        topo.add(new JLabel("Dias empréstimo:")); topo.add(txtDias);

        btnEmprestar = new JButton("Emprestar");
        btnDevolver = new JButton("Registrar Devolução");
        btnRenovar = new JButton("Renovar");
        btnBuscar = new JButton("Buscar Empréstimos do Estudante");

        btnEmprestar.addActionListener(this::onEmprestar);
        btnDevolver.addActionListener(this::onDevolver);
        btnRenovar.addActionListener(this::onRenovar);
        btnBuscar.addActionListener(this::onBuscar);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnBuscar); botoes.add(btnEmprestar); botoes.add(btnRenovar); botoes.add(btnDevolver);

        tabela = new JTable(new DefaultTableModel(new Object[]{"ID", "Nome Estudante", "Título do Livro", "Código Interno", "ID Cabeçalho", "Data Empréstimo", "Data Devolução Prevista", "Status", "Multa"}, 0));
        JScrollPane scroller = new JScrollPane(tabela);

        getContentPane().setLayout(new BorderLayout(8,8));
        getContentPane().add(topo, BorderLayout.NORTH);
        getContentPane().add(scroller, BorderLayout.CENTER);
        getContentPane().add(botoes, BorderLayout.SOUTH);
    }

    private void onEmprestar(ActionEvent ev) {
        int idEstudante = parseInt(txtIdEstudante.getText());
        int idCabecalho = parseInt(txtIdCabecalho.getText());
        int idFuncionario = parseInt(txtIdFuncionario.getText());
        int dias = parseInt(txtDias.getText());
        boolean ok = controller.realizarEmprestimo(idEstudante, idCabecalho, idFuncionario, dias);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Empréstimo realizado com sucesso.");
            onBuscar(null);
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao realizar empréstimo.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDevolver(ActionEvent ev) {
        String s = JOptionPane.showInputDialog(this, "Informe o ID do empréstimo a devolver:");
        int id = parseInt(s);
        if (id <= 0) return;
        boolean ok = controller.registrarDevolucao(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Devolução registrada.");
            onBuscar(null);
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao registrar devolução.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRenovar(ActionEvent ev) {
        String s = JOptionPane.showInputDialog(this, "Informe o ID do empréstimo a renovar:");
        int id = parseInt(s);
        if (id <= 0) return;
        String diasStr = JOptionPane.showInputDialog(this, "Quantos dias adicionais?", "7");
        int dias = parseInt(diasStr);
        boolean ok = controller.renovarEmprestimo(id, dias);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Renovação efetuada.");
            onBuscar(null);
        } else {
            JOptionPane.showMessageDialog(this, "Falha na renovação.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onBuscar(ActionEvent ev) {
        int idEstudante = parseInt(txtIdEstudante.getText());
        if (idEstudante <= 0) {
            JOptionPane.showMessageDialog(this, "Informe um ID de estudante válido para buscar.");
            return;
        }
        List<Emprestimo> lista = controller.obterEmprestimosAtivosEstudante(idEstudante);
        DefaultTableModel m = (DefaultTableModel) tabela.getModel();
        m.setRowCount(0);
        for (Emprestimo e : lista) {
            String nome = e.getNomeEstudante();
            String titulo = e.getTituloLivro();
            String codigo = e.getCodigoInternoLivro();
            m.addRow(new Object[]{e.getIdEmprestimo(), nome, titulo, codigo, e.getIdCabecalho(), e.getDataEmprestimo(), e.getDataDevolutaoPrevista(), e.getStatusEmprestimo(), e.getValorMulta()});
        }
    }

    private int parseInt(String s) {
        if (s == null) return 0;
        try { return Integer.parseInt(s.trim()); } catch (Exception ex) { return 0; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaEmprestimo().setVisible(true));
    }
}
