package util;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Classe para aplicar temas profissionais ao sistema.
 * Define cores, fontes e estilos padronizados.
 */
public class TemasApp {
    
    // Cores Primárias - Azul Profissional
    public static final Color COR_PRIMARIA = new Color(25, 103, 210);      // Azul
    public static final Color COR_PRIMARIA_ESCURA = new Color(13, 71, 161);  // Azul Escuro
    public static final Color COR_PRIMARIA_CLARA = new Color(66, 165, 245);  // Azul Claro
    
    // Cores Secundárias - Verde Sucesso
    public static final Color COR_SUCESSO = new Color(76, 175, 80);        // Verde
    public static final Color COR_ERRO = new Color(244, 67, 54);           // Vermelho
    public static final Color COR_AVISO = new Color(255, 193, 7);          // Amarelo
    public static final Color COR_INFO = new Color(33, 150, 243);          // Azul Info
    
    // Cores Neutras
    public static final Color COR_FUNDO = new Color(245, 245, 245);        // Cinza Claro
    public static final Color COR_FUNDO_ESCURO = new Color(33, 33, 33);    // Cinza Escuro
    public static final Color COR_TEXTO = new Color(33, 33, 33);           // Texto Escuro
    public static final Color COR_TEXTO_CLARO = new Color(255, 255, 255);  // Texto Branco
    public static final Color COR_BORDA = new Color(189, 189, 189);        // Borda Cinza
    
    // Fontes
    public static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONTE_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONTE_CAMPO = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 10);

    // Dimensões
    public static final int ALTURA_BOTAO = 40;
    public static final int ALTURA_CAMPO = 35;
    public static final int ALTURA_LINHA = 50;
    public static final int ESPACAMENTO = 10;
    public static final int ESPACAMENTO_GRANDE = 20;
    public static final int RAIO_BORDA = 5;

    /**
     * Aplicar tema moderno à aplicação
     */
    public static void aplicarTemaModerno() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Cores principais
            UIManager.put("control", COR_FUNDO);
            UIManager.put("controlText", COR_TEXTO);
            UIManager.put("controlHighlight", COR_FUNDO);
            UIManager.put("controlShadow", COR_BORDA);
            
            // Botões
            UIManager.put("Button.background", COR_PRIMARIA);
            UIManager.put("Button.foreground", COR_TEXTO_CLARO);
            UIManager.put("Button.font", FONTE_BOTAO);
            UIManager.put("Button.border", BorderFactory.createEmptyBorder(5, 15, 5, 15));
            
            // Campos de texto
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.foreground", COR_TEXTO);
            UIManager.put("TextField.font", FONTE_CAMPO);
            UIManager.put("TextField.border", BorderFactory.createLineBorder(COR_BORDA, 1));
            
            // Tabelas
            UIManager.put("Table.background", Color.WHITE);
            UIManager.put("Table.foreground", COR_TEXTO);
            UIManager.put("Table.gridColor", COR_BORDA);
            UIManager.put("Table.font", FONTE_CAMPO);
            UIManager.put("TableHeader.background", COR_PRIMARIA);
            UIManager.put("TableHeader.foreground", COR_TEXTO_CLARO);
            
            // Diálogos
            UIManager.put("Dialog.background", COR_FUNDO);
            UIManager.put("Dialog.foreground", COR_TEXTO);
            UIManager.put("OptionPane.background", COR_FUNDO);
            UIManager.put("OptionPane.messageForeground", COR_TEXTO);
            
        } catch (Exception e) {
            System.err.println("Erro ao aplicar tema: " + e.getMessage());
        }
    }

    /**
     * Criar um botão estilizado
     */
    public static JButton criarBotaoEstilizado(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(COR_PRIMARIA);
        botao.setForeground(COR_TEXTO_CLARO);
        botao.setFont(FONTE_BOTAO);
        botao.setPreferredSize(new Dimension(120, ALTURA_BOTAO));
        botao.setFocusPainted(false);
        botao.setFocusable(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_PRIMARIA_ESCURA, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efeito hover
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                botao.setBackground(COR_PRIMARIA_ESCURA);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                botao.setBackground(COR_PRIMARIA);
            }
        });
        
        return botao;
    }

    /**
     * Criar campo de texto estilizado
     */
    public static JTextField criarCampoTextoEstilizado(int colunas) {
        JTextField campo = new JTextField(colunas);
        campo.setBackground(Color.WHITE);
        campo.setForeground(COR_TEXTO);
        campo.setFont(FONTE_CAMPO);
        campo.setPreferredSize(new Dimension(200, ALTURA_CAMPO));
        campo.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        return campo;
    }

    /**
     * Criar label estilizado
     */
    public static JLabel criarLabelEstilizado(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_LABEL);
        label.setForeground(COR_TEXTO);
        return label;
    }

    /**
     * Criar painel com fundo estilizado
     */
    public static JPanel criarPainelEstilizado() {
        JPanel painel = new JPanel();
        painel.setBackground(COR_FUNDO);
        return painel;
    }

    /**
     * Criar painel com cor primária
     */
    public static JPanel criarPainelCabecalho() {
        JPanel painel = new JPanel();
        painel.setBackground(COR_PRIMARIA);
        painel.setPreferredSize(new Dimension(800, 80));
        return painel;
    }

    /**
     * Criar linha separadora
     */
    public static JSeparator criarSeparador() {
        JSeparator sep = new JSeparator();
        sep.setBackground(COR_BORDA);
        sep.setForeground(COR_BORDA);
        return sep;
    }

    /**
     * Criar tabela estilizada
     */
    public static JTable criarTabelaEstilizada(Object[][] dados, String[] colunas) {
        JTable tabela = new JTable(dados, colunas);
        tabela.setBackground(Color.WHITE);
        tabela.setForeground(COR_TEXTO);
        tabela.setGridColor(COR_BORDA);
        tabela.setRowHeight(30);
        tabela.getTableHeader().setBackground(COR_PRIMARIA);
        tabela.getTableHeader().setForeground(COR_TEXTO_CLARO);
        tabela.getTableHeader().setFont(FONTE_BOTAO);
        tabela.setFont(FONTE_CAMPO);
        tabela.setSelectionBackground(COR_PRIMARIA_CLARA);
        tabela.setSelectionForeground(COR_TEXTO);
        return tabela;
    }

    /**
     * Mostrar mensagem de sucesso
     */
    public static void mostrarSucesso(JFrame parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "Sucesso", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Mostrar mensagem de erro
     */
    public static void mostrarErro(JFrame parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "Erro", 
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Mostrar mensagem de aviso
     */
    public static void mostrarAviso(JFrame parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "Aviso", 
            JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Confirmar ação
     */
    public static boolean confirmar(JFrame parent, String mensagem) {
        int resultado = JOptionPane.showConfirmDialog(parent, mensagem, "Confirmação",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return resultado == JOptionPane.YES_OPTION;
    }
}
