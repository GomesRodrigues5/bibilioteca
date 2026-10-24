package util;

import java.util.Stack;
import model.Funcionario;

/**
 * Gerenciador de autenticação usando Pilha (Stack) para validar credenciais
 * de funcionários sem acessar direto o banco de dados.
 */
public class AuthenticationManager {
    private static Stack<Funcionario> autenticationStack = new Stack<>();
    private static Funcionario funcionarioLogado;

    public static void setFuncionarioLogado(Funcionario funcionario) {
        funcionarioLogado = funcionario;
    }

    public static Funcionario getFuncionarioLogado() {
        return funcionarioLogado;
    }

    /**
     * Carrega todos os funcionários na pilha para validação
     */
    public static void loadFuncionariosToStack(java.util.List<Funcionario> funcionarios) {
        autenticationStack.clear();
        // Carrega em ordem reversa para que o primeiro seja mais acessível
        for (int i = funcionarios.size() - 1; i >= 0; i--) {
            autenticationStack.push(funcionarios.get(i));
        }
    }

    /**
     * Valida as credenciais de um funcionário usando a pilha
     * Retorna o funcionário se as credenciais forem válidas, null caso contrário
     */
    public static Funcionario validarCredenciais(String usuario, String senha) {
        if (autenticationStack.isEmpty()) {
            System.out.println("[STACK AUTH] Pilha vazia!");
            return null;
        }

        // Cria uma cópia temporária da pilha para não comprometer a original
        Stack<Funcionario> tempStack = new Stack<>();
        Funcionario funcionarioValidado = null;

        System.out.println("[STACK AUTH] Iniciando validação na pilha com " + autenticationStack.size() + " funcionários");

        // Procura pela combinação correta de usuário e senha
        int tentativas = 0;
        while (!autenticationStack.isEmpty()) {
            Funcionario func = autenticationStack.pop();
            tempStack.push(func);
            tentativas++;

            // Debug detalhado
            System.out.println("[STACK AUTH] Tentativa " + tentativas + ": Comparando");
            System.out.println("  - Nome do funcionário: '" + func.getNome() + "' (tipo: " + (func.getNome() != null ? func.getNome().getClass().getSimpleName() : "null") + ")");
            System.out.println("  - Usuário buscado: '" + usuario + "' (tipo: " + usuario.getClass().getSimpleName() + ")");
            System.out.println("  - Match de nome? " + (func.getNome() != null && func.getNome().equals(usuario)));
            
            if (func.getSenha() != null) {
                System.out.println("  - Senha do funcionário: <hash> (length: " + func.getSenha().length() + ")");
            } else {
                System.out.println("  - Senha do funcionário: NULL");
            }
            System.out.println("  - Senha buscada: <hidden> (length: " + (senha != null ? senha.length() : 0) + ")");
            System.out.println("  - Match de senha? [verificação por hash]");

            // Verifica se o nome do usuário e a senha correspondem
            if (func.getNome() != null && func.getNome().equals(usuario) &&
                func.getSenha() != null && util.PasswordHasher.verify(senha, func.getSenha())) {
                funcionarioValidado = func;
                System.out.println("[STACK AUTH] ✓ MATCH ENCONTRADO!");
                break;
            }
        }

        // Reconstrói a pilha original
        while (!tempStack.isEmpty()) {
            autenticationStack.push(tempStack.pop());
        }

        return funcionarioValidado;
    }

    /**
     * Verifica se um usuário existe na pilha por nome
     */
    public static Funcionario buscarPorUsuario(String usuario) {
        if (autenticationStack.isEmpty()) {
            return null;
        }

        Stack<Funcionario> tempStack = new Stack<>();
        Funcionario funcionarioEncontrado = null;

        while (!autenticationStack.isEmpty()) {
            Funcionario func = autenticationStack.pop();
            tempStack.push(func);

            if (func.getNome() != null && func.getNome().equals(usuario)) {
                funcionarioEncontrado = func;
            }
        }

        // Reconstrói a pilha original
        while (!tempStack.isEmpty()) {
            autenticationStack.push(tempStack.pop());
        }

        return funcionarioEncontrado;
    }

    /**
     * Limpa a pilha de autenticação
     */
    public static void clearStack() {
        autenticationStack.clear();
    }

    /**
     * Retorna o tamanho da pilha
     */
    public static int getStackSize() {
        return autenticationStack.size();
    }

    /**
     * Verifica se a pilha está vazia
     */
    public static boolean isStackEmpty() {
        return autenticationStack.isEmpty();
    }
}
