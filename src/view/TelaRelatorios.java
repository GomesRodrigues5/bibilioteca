package view;

import util.TemasApp;
import util.RelatorioBiblioteca;
import util.RelatorioBiblioteca.RelatorioMultasMensal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

public class TelaRelatorios extends JFrame {
    private RelatorioBiblioteca gerador = new RelatorioBiblioteca();

    public TelaRelatorios() {
        super("Relatórios - Biblioteca");
        TemasApp.aplicarTemaModerno();
        initComponents();
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();

        // --- Multas (painel com controles) ---
        JPanel painelMultas = new JPanel(new BorderLayout(8,8));
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Integer> cbMes = new JComboBox<>();
        for (int m = 1; m <= 12; m++) cbMes.addItem(m);
        JComboBox<Integer> cbAno = new JComboBox<>();
        int anoAtual = LocalDate.now().getYear();
        for (int a = anoAtual - 5; a <= anoAtual + 1; a++) cbAno.addItem(a);
        JButton btnGerar = TemasApp.criarBotaoEstilizado("Gerar Multas");
        JButton btnExport = TemasApp.criarBotaoEstilizado("Exportar CSV");

        controles.add(new JLabel("Mês:")); controles.add(cbMes);
        controles.add(new JLabel("Ano:")); controles.add(cbAno);
        controles.add(btnGerar); controles.add(btnExport);

        String[] colunas = new String[] {"ID", "Estudante", "Valor", "Status", "Data Cálculo"};
        DefaultTableModel model = new DefaultTableModel(new Object[0][0], colunas) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tabela = new JTable(model);
        tabela.setAutoCreateRowSorter(true);

        painelMultas.add(controles, BorderLayout.NORTH);
        painelMultas.add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnGerar.addActionListener(e -> {
            int mes = (int) cbMes.getSelectedItem();
            int ano = (int) cbAno.getSelectedItem();
            model.setRowCount(0);
            try {
                RelatorioMultasMensal rel = gerador.relatorioMultasMensal(mes, ano);
                for (var multa : rel.getMultas()) {
                    model.addRow(new Object[] {
                        multa.getIdControleMulta(),
                        multa.getNomeEstudante(),
                        multa.getValorTotalMulta(),
                        multa.getStatusPagamento(),
                        multa.getDataCalculo()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + ex.getMessage());
            }
        });

        btnExport.addActionListener(e -> {
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Nenhum dado para exportar.");
                return;
            }
            try {
                JFileChooser fc = new JFileChooser();
                if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    var file = fc.getSelectedFile();
                    try (FileWriter fw = new FileWriter(file)) {
                        // Cabeçalho
                        for (int c = 0; c < model.getColumnCount(); c++) {
                            fw.write(model.getColumnName(c));
                            if (c < model.getColumnCount()-1) fw.write(',');
                        }
                        fw.write('\n');
                        for (int r = 0; r < model.getRowCount(); r++) {
                            for (int c = 0; c < model.getColumnCount(); c++) {
                                fw.write(String.valueOf(model.getValueAt(r, c)).replace(',', ' '));
                                if (c < model.getColumnCount()-1) fw.write(',');
                            }
                            fw.write('\n');
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Exportado com sucesso.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar: " + ex.getMessage());
            }
        });

        // --- Desempenho ---
        JPanel painelDesempenho = new JPanel(new BorderLayout());
        painelDesempenho.add(new JLabel("Relatório de Desempenho - use Gerar para criar dados."), BorderLayout.CENTER);

        // --- Top 10 ---
        JPanel painelTop = new JPanel(new BorderLayout());
        painelTop.add(new JLabel("Top Borrowers / Top Livros - implementação pendente"), BorderLayout.CENTER);

        tabs.addTab("Multas", painelMultas);
        tabs.addTab("Desempenho", painelDesempenho);
        tabs.addTab("Top 10", painelTop);

        getContentPane().setLayout(new BorderLayout(8,8));
        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaRelatorios().setVisible(true));
    }
}
