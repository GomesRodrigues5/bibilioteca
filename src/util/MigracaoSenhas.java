package util;

import dao.FuncionarioDAO;
import model.Funcionario;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Utilitário para migrar senhas armazenadas em texto plano para hashes seguros.
 * - Detecta senhas que não começam com "PBKDF2$" ou "BCRYPT$"
 * - Gera hash usando `PasswordHasher.hash()` (que usa BCrypt se disponível)
 * - Atualiza o registro no banco via `FuncionarioDAO.atualizar`
 *
 * Uso: rodar a classe a partir do IDE. Recomenda-se backup do banco antes.
 */
public class MigracaoSenhas {

    public static void main(String[] args) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Deseja iniciar a migração de senhas para hashes seguros?\nRecomenda-se backup do banco antes.",
                "Confirmar migração de senhas", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            System.out.println("Migração cancelada pelo usuário.");
            return;
        }

        MigracaoSenhas migrador = new MigracaoSenhas();
        migrador.executar();
    }

    public void executar() {
        FuncionarioDAO dao = new FuncionarioDAO();
        try {
            List<Funcionario> todos = dao.listarTodos();
            int total = todos.size();
            int migradas = 0;
            int puladas = 0;

            for (Funcionario f : todos) {
                String senha = f.getSenha();
                if (senha == null || senha.isEmpty()) {
                    System.out.println("[SKIP] id=" + f.getIdFuncionario() + " sem senha definida.");
                    puladas++;
                    continue;
                }
                if (senha.startsWith("PBKDF2$") || senha.startsWith("BCRYPT$")) {
                    System.out.println("[OK] id=" + f.getIdFuncionario() + " já possui hash.");
                    puladas++;
                    continue;
                }

                // Gerar hash e atualizar
                String novoHash = PasswordHasher.hash(senha);
                f.setSenha(novoHash);
                try {
                    dao.atualizar(f);
                    System.out.println("[MIGRATED] id=" + f.getIdFuncionario() + " usuário='" + f.getNome() + "'");
                    migradas++;
                } catch (SQLException ex) {
                    System.err.println("Erro ao atualizar funcionario id=" + f.getIdFuncionario() + ": " + ex.getMessage());
                }
            }

            String resumo = String.format("Migração finalizada. Total=%d, Migradas=%d, Puladas=%d", total, migradas, puladas);
            System.out.println(resumo);
            JOptionPane.showMessageDialog(null, resumo, "Migração de Senhas", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            String msg = "Erro ao listar funcionários: " + ex.getMessage();
            System.err.println(msg);
            JOptionPane.showMessageDialog(null, msg, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
