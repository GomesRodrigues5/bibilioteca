package controller;

import dao.FuncionarioDAO;
import java.sql.SQLException;
import java.util.List;
import model.Funcionario;

public class FuncionarioController {
    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    /**
     * Autentica um funcionário verificando nome e senha no banco de dados
     * @param usuario Nome do funcionário
     * @param senha Senha do funcionário
     * @return Funcionário autenticado ou null se credenciais inválidas
     */
    public Funcionario autenticar(String usuario, String senha) throws SQLException {
        System.out.println("[AUTH] Validando: usuario='" + usuario + "' | senha='" + senha + "'");
        
        Funcionario funcionario = funcionarioDAO.validarCredenciais(usuario, senha);
        
        if (funcionario != null) {
            System.out.println("[AUTH] ✓ Autenticação OK: " + funcionario.getNome());
        } else {
            System.out.println("[AUTH] ✗ Credenciais inválidas!");
        }
        
        return funcionario;
    }

    /**
     * Salva um novo funcionário
     */
    public void salvarFuncionario(Funcionario funcionario) throws SQLException {
        funcionarioDAO.salvar(funcionario);
    }

    /**
     * Atualiza um funcionário existente
     */
    public void atualizarFuncionario(Funcionario funcionario) throws SQLException {
        funcionarioDAO.atualizar(funcionario);
    }

    /**
     * Deleta um funcionário
     */
    public void deletarFuncionario(int idFuncionario) throws SQLException {
        funcionarioDAO.deletar(idFuncionario);
    }

    /**
     * Lista todos os funcionários
     */
    public List<Funcionario> listarFuncionarios() throws SQLException {
        return funcionarioDAO.listarTodos();
    }

    /**
     * Busca um funcionário por nome
     */
    public Funcionario buscarPorNome(String nome) throws SQLException {
        return funcionarioDAO.buscarPorNome(nome);
    }
}
