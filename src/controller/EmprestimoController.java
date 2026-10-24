package controller;

import dao.*;
import model.*;
import util.ValidadorFormulario;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.*;

/**
 * Controller para gerenciar Empréstimos de livros.
 * Coordena lógica de empréstimo, devolução, renovação e cálculo de multas.
 */
public class EmprestimoController {
    
    private EmprestimoDAO emprestimoDAO;
    private CabecalhoLivroDAO cabecalhoDAO;
    private EstudanteDAO estudanteDAO;
    private FuncionarioDAO funcionarioDAO;
    private ControleMultaDAO multaDAO;
    private LivroController livroController;
    
    // Configurações
    private static final int DIAS_EMPRESTIMO_PADRAO = 14;
    private static final double MULTA_DIARIA = 200.00;
    private static final int MAX_RENOVACOES = 3;

    public EmprestimoController() {
        this.emprestimoDAO = new EmprestimoDAO();
        this.cabecalhoDAO = new CabecalhoLivroDAO();
        this.estudanteDAO = new EstudanteDAO();
        this.funcionarioDAO = new FuncionarioDAO();
        this.multaDAO = new ControleMultaDAO();
        this.livroController = new LivroController();
    }

    /**
     * Validar dados de empréstimo
     */
    public List<String> validarEmprestimo(int idEstudante, int idCabecalho,
                                         int idFuncionario, int diasEmprestimo) {
        List<String> erros = new ArrayList<>();

        try {
            if (idEstudante <= 0) {
                erros.add("ID do estudante inválido");
            } else if (estudanteDAO.buscarPorId(idEstudante) == null) {
                erros.add("Estudante não encontrado");
            }

            if (idCabecalho <= 0) {
                erros.add("ID do cabeçalho do livro inválido");
            } else if (cabecalhoDAO.obterPorId(idCabecalho) == null) {
                erros.add("Cabeçalho do livro não encontrado");
            }

            if (idFuncionario <= 0) {
                erros.add("ID do funcionário inválido");
            } else if (funcionarioDAO.buscarPorId(idFuncionario) == null) {
                erros.add("Funcionário não encontrado");
            }

            if (diasEmprestimo <= 0 || diasEmprestimo > 60) {
                erros.add("Dias de empréstimo deve estar entre 1 e 60");
            }
        } catch (Exception e) {
            erros.add("Erro ao validar empréstimo: " + e.getMessage());
        }

        return erros;
    }

    /**
     * Realizar empréstimo de livro
     */
    public boolean realizarEmprestimo(int idEstudante, int idCabecalho, 
                                     int idFuncionario, int diasEmprestimo) {
        List<String> erros = validarEmprestimo(idEstudante, idCabecalho, idFuncionario, diasEmprestimo);
        if (!erros.isEmpty()) {
            System.err.println("Erros de validação: " + erros);
            return false;
        }

        try {
            // Calcular datas
            LocalDate hoje = LocalDate.now();
            LocalDate dataDevolucaoPrevista = hoje.plusDays(diasEmprestimo);

            // Criar empréstimo
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setIdEstudante(idEstudante);
            emprestimo.setIdCabecalho(idCabecalho);
            emprestimo.setIdFuncionario(idFuncionario);
            emprestimo.setDataEmprestimo(Date.valueOf(hoje));
            emprestimo.setDataDevolutaoPrevista(Date.valueOf(dataDevolucaoPrevista));
            emprestimo.setStatusEmprestimo("Ativo");
            emprestimo.setNumeroRenovacoes(0);

            // Inserir empréstimo
            if (!emprestimoDAO.inserir(emprestimo)) {
                System.err.println("Erro ao inserir empréstimo");
                return false;
            }

            // Diminuir quantidade disponível
            CabecalhoLivro cab = cabecalhoDAO.obterPorId(idCabecalho);
            if (cab != null) {
                livroController.diminuirQuantidadeDisponivel(cab.getIdLivro(), 1);
            }

            System.out.println("Empréstimo realizado com sucesso. ID: " + emprestimo.getIdEmprestimo());
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao realizar empréstimo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registrar devolução de livro
     */
    public boolean registrarDevolucao(int idEmprestimo) {
        if (idEmprestimo <= 0) {
            System.err.println("ID do empréstimo inválido");
            return false;
        }

        try {
            Emprestimo emprestimo = emprestimoDAO.obterPorId(idEmprestimo);
            if (emprestimo == null) {
                System.err.println("Empréstimo não encontrado");
                return false;
            }

            // Verificar atraso e calcular multa
            LocalDate hoje = LocalDate.now();
            LocalDate dataPrevista = emprestimo.getDataDevolutaoPrevista().toLocalDate();
            
            String statusFinal = "Devolvido";
            boolean contemMulta = false;

            if (hoje.isAfter(dataPrevista)) {
                long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(dataPrevista, hoje);
                double valorMulta = diasAtraso * MULTA_DIARIA;
                
                // Registrar multa
                ControleMulta multa = new ControleMulta();
                multa.setIdEmprestimo(idEmprestimo);
                multa.setIdEstudante(emprestimo.getIdEstudante());
                multa.setValorMultaDiaria(MULTA_DIARIA);
                multa.setNumeroDiasAtraso((int) diasAtraso);
                multa.setValorTotalMulta(valorMulta);
                
                multaDAO.inserir(multa);
                
                // Atualizar empréstimo com multa
                emprestimoDAO.atualizarStatusComMulta(idEmprestimo, statusFinal, 
                                                     valorMulta, false);
                contemMulta = true;
            } else {
                // Devolução no prazo
                emprestimoDAO.registrarDevolucao(idEmprestimo, statusFinal);
            }

            // Aumentar quantidade disponível
            CabecalhoLivro cab = cabecalhoDAO.obterPorId(emprestimo.getIdCabecalho());
            if (cab != null) {
                livroController.aumentarQuantidadeDisponivel(cab.getIdLivro(), 1);
            }

            if (contemMulta) {
                System.out.println("Devolução registrada com MULTA por atraso");
            } else {
                System.out.println("Devolução registrada sem multa");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao registrar devolução: " + e.getMessage());
            return false;
        }
    }

    /**
     * Renovar empréstimo
     */
    public boolean renovarEmprestimo(int idEmprestimo, int diasAdicionais) {
        if (idEmprestimo <= 0 || diasAdicionais <= 0) {
            System.err.println("Dados de renovação inválidos");
            return false;
        }

        try {
            Emprestimo emprestimo = emprestimoDAO.obterPorId(idEmprestimo);
            if (emprestimo == null) {
                System.err.println("Empréstimo não encontrado");
                return false;
            }

            // Verificar limite de renovações
            if (emprestimo.getNumeroRenovacoes() >= MAX_RENOVACOES) {
                System.err.println("Número máximo de renovações atingido (" + MAX_RENOVACOES + ")");
                return false;
            }

            // Verificar se tem multas pendentes
            List<ControleMulta> multas = multaDAO.obterPorEstudante(emprestimo.getIdEstudante());
            if (!multas.isEmpty() && multas.stream().anyMatch(m -> !"Pago".equals(m.getStatusPagamento()))) {
                System.err.println("Estudante com multas pendentes - renovação negada");
                return false;
            }

            // Calcular nova data de devolução
            LocalDate novaData = emprestimo.getDataDevolutaoPrevista().toLocalDate()
                                .plusDays(diasAdicionais);

            // Renovar
            if (emprestimoDAO.renovarEmprestimo(idEmprestimo, Date.valueOf(novaData))) {
                System.out.println("Empréstimo renovado com sucesso");
                return true;
            }
        } catch (Exception e) {
            System.err.println("Erro ao renovar empréstimo: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obter empréstimos ativos de um estudante
     */
    public List<Emprestimo> obterEmprestimosAtivosEstudante(int idEstudante) {
        List<Emprestimo> todos = emprestimoDAO.obterPorEstudante(idEstudante);
        return todos.stream()
            .filter(e -> "Ativo".equals(e.getStatusEmprestimo()) || "Atraso".equals(e.getStatusEmprestimo()))
            .toList();
    }

    /**
     * Obter empréstimos atrasados
     */
    public List<Emprestimo> obterEmprestimosAtrasados() {
        return emprestimoDAO.obterAtrasados();
    }

    /**
     * Obter histórico de empréstimos de um estudante
     */
    public List<Emprestimo> obterHistoricoEmprestimos(int idEstudante) {
        return emprestimoDAO.obterPorEstudante(idEstudante);
    }

    /**
     * Obter empréstimo por ID
     */
    public Emprestimo obterEmprestimo(int idEmprestimo) {
        if (idEmprestimo <= 0) return null;
        return emprestimoDAO.obterPorId(idEmprestimo);
    }

    /**
     * Retorna o título do livro associado a um cabeçalho de indexação.
     */
    public String obterTituloPorCabecalho(int idCabecalho) {
        if (idCabecalho <= 0) return "";
        try {
            CabecalhoLivro cab = cabecalhoDAO.obterPorId(idCabecalho);
            if (cab == null) return "";
            Livro livro = livroController.obterLivroPorId(cab.getIdLivro());
            return livro != null ? livro.getTitulo() : "";
        } catch (Exception e) {
            System.err.println("Erro ao obter título por cabeçalho: " + e.getMessage());
            return "";
        }
    }

    /**
     * Retorna o código interno do livro associado a um cabeçalho.
     */
    public String obterCodigoInternoPorCabecalho(int idCabecalho) {
        if (idCabecalho <= 0) return "";
        try {
            CabecalhoLivro cab = cabecalhoDAO.obterPorId(idCabecalho);
            if (cab == null) return "";
            Livro livro = livroController.obterLivroPorId(cab.getIdLivro());
            return livro != null ? livro.getCodigoInterno() : "";
        } catch (Exception e) {
            System.err.println("Erro ao obter código interno por cabeçalho: " + e.getMessage());
            return "";
        }
    }

    /**
     * Retorna o nome do estudante por id (usa EstudanteDAO). Retorna vazio se não encontrado.
     */
    public String obterNomeEstudantePorId(int idEstudante) {
        if (idEstudante <= 0) return "";
        try {
            model.Estudante est = estudanteDAO.buscarPorId(idEstudante);
            return est != null ? est.getNome() : "";
        } catch (Exception e) {
            System.err.println("Erro ao obter estudante: " + e.getMessage());
            return "";
        }
    }
}
