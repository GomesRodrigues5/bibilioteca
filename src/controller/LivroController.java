package controller;

import dao.*;
import model.*;
import util.ValidadorFormulario;
import java.util.*;

/**
 * Controller para gerenciar operações com Livros.
 * Aplica validações de negócio e coordena operações entre Model e DAO.
 */
public class LivroController {
    
    private LivroDAO livroDAO;
    private CabecalhoLivroDAO cabecalhoDAO;
    private CDUAO cduDAO;
    private ArmarioDAO armarioDAO;

    public LivroController() {
        this.livroDAO = new LivroDAO();
        this.cabecalhoDAO = new CabecalhoLivroDAO();
        this.cduDAO = new CDUAO();
        this.armarioDAO = new ArmarioDAO();
    }

    /**
     * Valida todos os campos obrigatórios de um livro
     */
    public List<String> validarLivro(Livro livro) {
        List<String> erros = new ArrayList<>();

        // Campos obrigatórios
        if (!ValidadorFormulario.validarTextoObrigatorio(livro.getAutor())) {
            erros.add("Autor é obrigatório");
        }
        
        if (!ValidadorFormulario.validarTextoObrigatorio(livro.getTitulo())) {
            erros.add("Título é obrigatório");
        }
        
        if (!ValidadorFormulario.validarTextoObrigatorio(livro.getCodigoInterno())) {
            erros.add("Código interno é obrigatório");
        }

        // Tamanho máximo
        if (!ValidadorFormulario.validarTamanhoMaximo(livro.getAutor(), 200)) {
            erros.add("Autor deve ter no máximo 200 caracteres");
        }
        
        if (!ValidadorFormulario.validarTamanhoMaximo(livro.getTitulo(), 300)) {
            erros.add("Título deve ter no máximo 300 caracteres");
        }

        // ISBN (opcional mas validado se preenchido)
        if (livro.getIsbn() != null && !livro.getIsbn().isEmpty()) {
            if (!ValidadorFormulario.validarISBN(livro.getIsbn())) {
                erros.add("ISBN inválido");
            }
        }

        // ISSN (opcional mas validado)
        if (livro.getIssn() != null && !livro.getIssn().isEmpty()) {
            if (!ValidadorFormulario.validarISSN(livro.getIssn())) {
                erros.add("ISSN inválido");
            }
        }

        // Ano de publicação
        if (livro.getAnoPublicacao() > 0) {
            int anoAtual = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            if (livro.getAnoPublicacao() > anoAtual) {
                erros.add("Ano de publicação não pode ser no futuro");
            }
            if (livro.getAnoPublicacao() < 1000) {
                erros.add("Ano de publicação inválido");
            }
        }

        // Quantidade de exemplares
        if (livro.getQuantidadeExemplares() <= 0) {
            erros.add("Quantidade de exemplares deve ser maior que 0");
        }

        // Número de páginas
        if (livro.getNumeroPaginas() > 0 && livro.getNumeroPaginas() > 10000) {
            erros.add("Número de páginas parece inválido");
        }

        return erros;
    }

    /**
     * Salva um livro simples (sem cabeçalho de indexação).
     * Usado por forms rápidos que não precisam da indexação imediata.
     */
    public boolean salvarLivro(Livro livro) {
        List<String> erros = validarLivro(livro);
        if (!erros.isEmpty()) {
            System.err.println("Erros de validação: " + erros);
            return false;
        }
        if (livro.getCodigoInterno() != null && livroDAO.obterPorCodigo(livro.getCodigoInterno()) != null) {
            System.err.println("Código de livro já existe");
            return false;
        }
        return livroDAO.inserir(livro);
    }

    /**
     * Cadastra um novo livro com indexação
     */
    public boolean cadastrarLivroComIndexacao(Livro livro, CabecalhoLivro cabecalho) {
        List<String> erros = validarLivro(livro);
        if (!erros.isEmpty()) {
            System.err.println("Erros de validação: " + erros);
            return false;
        }

        // Verificar se código já existe
        if (livroDAO.obterPorCodigo(livro.getCodigoInterno()) != null) {
            System.err.println("Código de livro já existe");
            return false;
        }

        try {
            // Inserir livro
            if (!livroDAO.inserir(livro)) {
                System.err.println("Erro ao inserir livro");
                return false;
            }

            // Inserir cabeçalho de indexação
            cabecalho.setIdLivro(livro.getIdLivro());
            if (!cabecalhoDAO.inserir(cabecalho)) {
                System.err.println("Erro ao inserir cabeçalho de indexação");
                livroDAO.deletarLogico(livro.getIdLivro());
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar livro com indexação: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza um livro e seu cabeçalho de indexação
     */
    public boolean atualizarLivroComIndexacao(Livro livro, CabecalhoLivro cabecalho) {
        List<String> erros = validarLivro(livro);
        if (!erros.isEmpty()) {
            System.err.println("Erros de validação: " + erros);
            return false;
        }

        try {
            if (!livroDAO.atualizar(livro)) {
                System.err.println("Erro ao atualizar livro");
                return false;
            }

            if (cabecalho != null && cabecalho.getIdCabecalho() > 0) {
                if (!cabecalhoDAO.atualizar(cabecalho)) {
                    System.err.println("Erro ao atualizar cabeçalho");
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar livro: " + e.getMessage());
            return false;
        }
    }

    public List<Livro> obterLivrosDisponiveis() {
        return livroDAO.obterLivrosDisponiveis();
    }

    public List<Livro> pesquisarPorTitulo(String titulo) {
        if (!ValidadorFormulario.validarTextoObrigatorio(titulo)) {
            return new ArrayList<>();
        }
        return livroDAO.pesquisarPorTitulo(titulo);
    }

    public List<Livro> pesquisarPorAutor(String autor) {
        if (!ValidadorFormulario.validarTextoObrigatorio(autor)) {
            return new ArrayList<>();
        }
        return livroDAO.pesquisarPorAutor(autor);
    }

    public Livro obterLivroPorId(int idLivro) {
        if (idLivro <= 0) return null;
        return livroDAO.obterPorId(idLivro);
    }

    public List<CabecalhoLivro> obterCabecalhosPorLivro(int idLivro) {
        if (idLivro <= 0) return new ArrayList<>();
        try {
            return cabecalhoDAO.obterPorIdLivro(idLivro);
        } catch (Exception e) {
            System.err.println("Erro ao obter cabeçalhos do livro: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public CDU obterCDU(int idCdu) {
        if (idCdu <= 0) return null;
        return cduDAO.obterPorId(idCdu);
    }

    public List<CDU> obterTodasCDUs() {
        return cduDAO.obterTodas();
    }

    public List<CDU> obterSubclassesCDU(int idClassePai) {
        return cduDAO.obterPorClassePai(idClassePai);
    }

    public boolean deletarLivro(int idLivro) {
        if (idLivro <= 0) return false;
        return livroDAO.deletarLogico(idLivro);
    }

    public boolean diminuirQuantidadeDisponivel(int idLivro, int quantidade) {
        Livro livro = livroDAO.obterPorId(idLivro);
        if (livro == null) return false;
        
        int novaQuantidade = livro.getQuantidadeDisponivel() - quantidade;
        if (novaQuantidade < 0) {
            System.err.println("Quantidade insuficiente");
            return false;
        }

        return livroDAO.atualizarQuantidadeDisponivel(idLivro, novaQuantidade);
    }

    public boolean aumentarQuantidadeDisponivel(int idLivro, int quantidade) {
        Livro livro = livroDAO.obterPorId(idLivro);
        if (livro == null) return false;
        
        int novaQuantidade = livro.getQuantidadeDisponivel() + quantidade;
        if (novaQuantidade > livro.getQuantidadeExemplares()) {
            System.err.println("Quantidade não pode ser maior que total de exemplares");
            return false;
        }

        return livroDAO.atualizarQuantidadeDisponivel(idLivro, novaQuantidade);
    }

    public List<Armario> obterArmarios() {
        try {
            return armarioDAO.listarTodos();
        } catch (Exception e) {
            System.err.println("Erro ao listar armários: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
