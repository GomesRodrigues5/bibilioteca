package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.TemasApp;
import controller.LivroController;
import model.Livro;

public class TelaLivro extends JFrame {
    private JTextField txtTitulo, txtSubtitulo, txtAutor, txtEdicao, txtISBN, txtISSN, txtDataLancamento,
            txtMaterial, txtPaginas, txtCodigo, txtQuantidade;
    private JButton btnSalvar, btnCancelar, btnEscolherCapa, btnEscolherPDF;
    private JLabel lblCapa, lblPDF;
    private File capaFile, pdfFile;
    private LivroController controller;

    public TelaLivro() {
        super("Cadastrar Livro");
        // Aplicar tema e criar controller
        TemasApp.aplicarTemaModerno();
        controller = new LivroController();
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 480);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(8,8));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        txtTitulo = new JTextField();
        txtSubtitulo = new JTextField();
        txtAutor = new JTextField();
        txtEdicao = new JTextField();
        txtISBN = new JTextField();
        txtISSN = new JTextField();
        txtDataLancamento = new JTextField("yyyy-MM-dd");
        txtMaterial = new JTextField();
        txtPaginas = new JTextField();
        txtCodigo = new JTextField();
        txtQuantidade = new JTextField("1");

        int row = 0;
        addRow(form, c, row++, "Título:", txtTitulo);
        addRow(form, c, row++, "Subtítulo:", txtSubtitulo);
        addRow(form, c, row++, "Autor(es):", txtAutor);
        addRow(form, c, row++, "Edição:", txtEdicao);
        addRow(form, c, row++, "ISBN:", txtISBN);
        addRow(form, c, row++, "ISSN:", txtISSN);
        addRow(form, c, row++, "Data Lançamento:", txtDataLancamento);
        addRow(form, c, row++, "Material:", txtMaterial);
        addRow(form, c, row++, "Páginas:", txtPaginas);
        addRow(form, c, row++, "Código:", txtCodigo);
        addRow(form, c, row++, "Quantidade Exemplares:", txtQuantidade);

        // arquivos
        JPanel files = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnEscolherCapa = new JButton("Escolher Capa");
        lblCapa = new JLabel("Nenhuma capa selecionada");
        btnEscolherPDF = new JButton("Escolher PDF");
        lblPDF = new JLabel("Nenhum PDF selecionado");

        btnEscolherCapa.addActionListener(e -> escolherCapa());
        btnEscolherPDF.addActionListener(e -> escolherPDF());

        files.add(btnEscolherCapa);
        files.add(lblCapa);
        files.add(btnEscolherPDF);
        files.add(lblPDF);

        // botões
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        botoes.add(btnSalvar);
        botoes.add(btnCancelar);

        btnSalvar.addActionListener(e -> onSalvar());
        btnCancelar.addActionListener(e -> dispose());

        main.add(form, BorderLayout.CENTER);
        main.add(files, BorderLayout.NORTH);
        main.add(botoes, BorderLayout.SOUTH);

        getContentPane().add(main);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.gridwidth = 1; c.weightx = 0.0;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.gridy = row; c.gridwidth = 3; c.weightx = 1.0;
        panel.add(field, c);
    }

    private void escolherCapa() {
        JFileChooser fc = new JFileChooser();
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            capaFile = fc.getSelectedFile();
            lblCapa.setText(capaFile.getName());
        }
    }

    private void escolherPDF() {
        JFileChooser fc = new JFileChooser();
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            pdfFile = fc.getSelectedFile();
            lblPDF.setText(pdfFile.getName());
        }
    }

    private void onSalvar() {
        // validações básicas
        if (txtTitulo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Título é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!txtISBN.getText().trim().isEmpty()) {
            try {
                if (!util.ValidadorFormulario.validarISBN(txtISBN.getText().trim())) {
                    JOptionPane.showMessageDialog(this, "ISBN inválido.", "Validação", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                // ignore
            }
        }

        Livro l = new Livro();
        l.setTitulo(txtTitulo.getText().trim());
        l.setSubtitulo(txtSubtitulo.getText().trim());
        l.setAutor(txtAutor.getText().trim());
        l.setEdicao(txtEdicao.getText().trim());
        l.setIsbn(txtISBN.getText().trim());
        l.setIssn(txtISSN.getText().trim());

        String dataTxt = txtDataLancamento.getText().trim();
        if (!dataTxt.isEmpty()) {
            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(dataTxt);
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(d);
                l.setAnoPublicacao(cal.get(java.util.Calendar.YEAR));
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Data de lançamento inválida. Use yyyy-MM-dd", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        l.setMaterial(txtMaterial.getText().trim());
        try { l.setNumeroPaginas(Integer.parseInt(txtPaginas.getText().trim())); } catch (NumberFormatException ex) { l.setNumeroPaginas(0); }
        l.setCodigoInterno(txtCodigo.getText().trim());
        try { l.setQuantidadeExemplares(Integer.parseInt(txtQuantidade.getText().trim())); } catch (NumberFormatException ex) { l.setQuantidadeExemplares(1); }

        // arquivos (capas e PDF) — se necessário, o DAO deve fazer o upload para BLOB
        if (capaFile != null) l.setCapadoLivroPath(capaFile.getAbsolutePath());
        if (pdfFile != null) l.setPdfPath(pdfFile.getAbsolutePath());

        boolean ok = controller.salvarLivro(l);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Livro salvo com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao salvar o livro.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // para teste rápido
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaLivro t = new TelaLivro();
            t.setVisible(true);
        });
    }
}
