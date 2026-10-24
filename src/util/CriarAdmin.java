package util;

import dao.FuncionarioDAO;
import model.Funcionario;

import javax.swing.*;
import java.sql.SQLException;

public class CriarAdmin {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                criarAdminDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void criarAdminDialog() {
        String usuario = JOptionPane.showInputDialog(null, "Nome de usuário (login):", "Criar Administrador", JOptionPane.QUESTION_MESSAGE);
        if (usuario == null || usuario.trim().isEmpty()) return;

        String senha = JOptionPane.showInputDialog(null, "Senha:", "Criar Administrador", JOptionPane.QUESTION_MESSAGE);
        if (senha == null || senha.trim().isEmpty()) return;

        String senha2 = JOptionPane.showInputDialog(null, "Confirme a senha:", "Criar Administrador", JOptionPane.QUESTION_MESSAGE);
        if (senha2 == null || !senha.equals(senha2)) {
            JOptionPane.showMessageDialog(null, "Senhas não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nomeCompleto = JOptionPane.showInputDialog(null, "Nome completo:", usuario);
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            nomeCompleto = usuario;
        }

        Funcionario f = new Funcionario();
        f.setNome(usuario.trim());
        // deixe o DAO cuidar do hash; aqui passamos a senha em texto
        f.setSenha(senha);
        f.setFuncao("Administrador");
        f.setDepartamento(null);

        FuncionarioDAO dao = new FuncionarioDAO();
        try {
            dao.salvar(f);
            JOptionPane.showMessageDialog(null, "Administrador criado com sucesso. ID: " + f.getIdFuncionario());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao criar administrador: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
