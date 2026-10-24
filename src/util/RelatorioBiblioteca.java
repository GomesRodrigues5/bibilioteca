package util;

import dao.*;
import model.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Classe para geração de relatórios do sistema de gestão de biblioteca.
 * Inclui relatórios financeiros, desempenho de alunos e excelência.
 */
public class RelatorioBiblioteca {
    
    private EmprestimoDAO emprestimoDAO;
    private ControleMultaDAO multaDAO;
    private EstudanteDAO estudanteDAO;
    private LivroDAO livroDAO;
    private CabecalhoLivroDAO cabecalhoDAO;

    public RelatorioBiblioteca() {
        this.emprestimoDAO = new EmprestimoDAO();
        this.multaDAO = new ControleMultaDAO();
        this.estudanteDAO = new EstudanteDAO();
        this.livroDAO = new LivroDAO();
        this.cabecalhoDAO = new CabecalhoLivroDAO();
    }

    /**
     * Relatório de Multas por Mês
     */
    public RelatorioMultasMensal relatorioMultasMensal(int mes, int ano) {
        RelatorioMultasMensal relatorio = new RelatorioMultasMensal();
        relatorio.setMes(mes);
        relatorio.setAno(ano);
        
        double totalMultas = multaDAO.obterTotalMultasPorMes(mes, ano);
        relatorio.setTotalMultas(totalMultas);
        
        // Listar todas as multas do mês
        List<ControleMulta> multasMes = obterMultasMes(mes, ano);
        relatorio.setMultas(multasMes);
        relatorio.setQuantidadeMultas(multasMes.size());
        
        double totalPago = multasMes.stream()
            .filter(m -> "Pago".equals(m.getStatusPagamento()))
            .mapToDouble(ControleMulta::getValorPago)
            .sum();
        relatorio.setTotalPago(totalPago);
        
        double totalPendente = totalMultas - totalPago;
        relatorio.setTotalPendente(totalPendente);
        
        return relatorio;
    }

    /**
     * Relatório de Livros Integrados por Data
     */
    public RelatorioLivrosIntegrados relatorioLivrosIntegrados(Date dataInicio, Date dataFim) {
        RelatorioLivrosIntegrados relatorio = new RelatorioLivrosIntegrados();
        relatorio.setDataInicio(dataInicio);
        relatorio.setDataFim(dataFim);
        
        // Buscar livros criados no período
        List<Livro> livrosIntegrados = obterLivrosPeriodo(dataInicio, dataFim);
        relatorio.setLivros(livrosIntegrados);
        relatorio.setQuantidadeLivros(livrosIntegrados.size());
        
        // Calcular quantidade total de exemplares
        int totalExemplares = livrosIntegrados.stream()
            .mapToInt(Livro::getQuantidadeExemplares)
            .sum();
        relatorio.setTotalExemplares(totalExemplares);
        
        return relatorio;
    }

    /**
     * Relatório de Desempenho de Alunos
     */
    public RelatorioDesempenhoAlunos relatorioDesempenhoAlunos() {
        RelatorioDesempenhoAlunos relatorio = new RelatorioDesempenhoAlunos();
        List<Estudante> estudantes;
        try {
            estudantes = estudanteDAO.listarTodos();
        } catch (java.sql.SQLException e) {
            System.err.println("Erro ao listar estudantes para relatório: " + e.getMessage());
            estudantes = Collections.emptyList();
        }
        List<DadosDesempenhoAluno> desempenhos = new ArrayList<>();
        
        for (Estudante aluno : estudantes) {
            DadosDesempenhoAluno desempenho = new DadosDesempenhoAluno();
            desempenho.setEstudante(aluno);
            
            // Empréstimos
            List<Emprestimo> emprestimos = emprestimoDAO.obterPorEstudante(aluno.getIdEstudante());
            desempenho.setTotalEmprestimos(emprestimos.size());
            
            // Devoluções no prazo
            int devolucoesNoPrazo = 0;
            int atrasados = 0;
            for (Emprestimo e : emprestimos) {
                if ("Devolvido".equals(e.getStatusEmprestimo())) {
                    if (e.getDataDevolutaoReal() != null && 
                        e.getDataDevolutaoReal().compareTo(e.getDataDevolutaoPrevista()) <= 0) {
                        devolucoesNoPrazo++;
                    } else {
                        atrasados++;
                    }
                }
            }
            desempenho.setDevolucoesNoPrazo(devolucoesNoPrazo);
            desempenho.setDevolucoesAtrasadas(atrasados);
            
            // Multas
            List<ControleMulta> multas = multaDAO.obterPorEstudante(aluno.getIdEstudante());
            desempenho.setTotalMultas(multas.size());
            double valorMultas = multas.stream()
                .mapToDouble(ControleMulta::getValorTotalMulta)
                .sum();
            desempenho.setValorTotalMultas(valorMultas);
            
            // Cálculo de responsabilidade (%)
            if (emprestimos.size() > 0) {
                double percentualResponsabilidade = (devolucoesNoPrazo * 100.0) / emprestimos.size();
                desempenho.setPercentualResponsabilidade(percentualResponsabilidade);
            }
            
            desempenhos.add(desempenho);
        }
        
        relatorio.setDesempenhos(desempenhos);
        return relatorio;
    }

    /**
     * Relatório de Alunos com Maior Empréstimo e Sem Multas
     */
    public List<DadosDesempenhoAluno> relatorioAlunosMaiorEmprestimoSemMultas() {
        List<DadosDesempenhoAluno> resultado = new ArrayList<>();
        List<Estudante> estudantes;
        try {
            estudantes = estudanteDAO.listarTodos();
        } catch (java.sql.SQLException e) {
            System.err.println("Erro ao listar estudantes para relatório: " + e.getMessage());
            return resultado;
        }
        
        for (Estudante aluno : estudantes) {
            List<Emprestimo> emprestimos = emprestimoDAO.obterPorEstudante(aluno.getIdEstudante());
            List<ControleMulta> multas = multaDAO.obterPorEstudante(aluno.getIdEstudante());
            
            // Se não tem multas e tem empréstimos
            if (multas.isEmpty() && !emprestimos.isEmpty()) {
                DadosDesempenhoAluno desempenho = new DadosDesempenhoAluno();
                desempenho.setEstudante(aluno);
                desempenho.setTotalEmprestimos(emprestimos.size());
                desempenho.setTotalMultas(0);
                desempenho.setValorTotalMultas(0);
                desempenho.setPercentualResponsabilidade(100.0);
                resultado.add(desempenho);
            }
        }
        
        // Ordenar por quantidade de empréstimos
        resultado.sort((a, b) -> b.getTotalEmprestimos() - a.getTotalEmprestimos());
        return resultado;
    }

    /**
     * Relatório de Excelência para publicação na página online
     */
    public RelatorioExcelencia relatorioExcelencia() {
        RelatorioExcelencia relatorio = new RelatorioExcelencia();
        
        // Alunos com melhor desempenho
        RelatorioDesempenhoAlunos desempenho = relatorioDesempenhoAlunos();
        List<DadosDesempenhoAluno> melhoresAlunos = desempenho.getDesempenhos();
        melhoresAlunos.sort((a, b) -> Double.compare(b.getPercentualResponsabilidade(), a.getPercentualResponsabilidade()));
        
        // Top 10 alunos
        List<DadosDesempenhoAluno> top10 = melhoresAlunos.stream()
            .limit(10)
            .toList();
        relatorio.setMelhoresAlunos(top10);
        
        // Estatísticas gerais
        int totalEmprestimosGeral = (int) melhoresAlunos.stream()
            .mapToInt(DadosDesempenhoAluno::getTotalEmprestimos)
            .sum();
        relatorio.setTotalEmprestimosGeral(totalEmprestimosGeral);
        
        double taxaMediaResponsabilidade = melhoresAlunos.stream()
            .mapToDouble(DadosDesempenhoAluno::getPercentualResponsabilidade)
            .average()
            .orElse(0.0);
        relatorio.setTaxaMediaResponsabilidade(taxaMediaResponsabilidade);
        
        // Livros mais emprestados
        List<Livro> livrosMaisEmprestados = obterLivrosMaisEmprestados(10);
        relatorio.setLivrosMaisEmprestados(livrosMaisEmprestados);
        
        // Data do relatório
        relatorio.setDataRelatorio(new java.util.Date());
        
        return relatorio;
    }

    // Métodos auxiliares
    
    private List<ControleMulta> obterMultasMes(int mes, int ano) {
        List<ControleMulta> todasAsMultas = multaDAO.obterMultasPendentes();
        return todasAsMultas.stream()
            .filter(m -> {
                LocalDate data = m.getDataCalculo().toLocalDate();
                return data.getMonthValue() == mes && data.getYear() == ano;
            })
            .toList();
    }

    private List<Livro> obterLivrosPeriodo(Date dataInicio, Date dataFim) {
        List<Livro> todoLivros = livroDAO.listarTodos();
        return todoLivros.stream()
            .filter(l -> {
                LocalDate criacao = l.getCriadoEm().toLocalDateTime().toLocalDate();
                return !criacao.isBefore(dataInicio.toLocalDate()) && 
                       !criacao.isAfter(dataFim.toLocalDate());
            })
            .toList();
    }

    private List<Livro> obterLivrosMaisEmprestados(int limite) {
        List<Emprestimo> todosEmprestimos = emprestimoDAO.obterAtivos();
        Map<Integer, Integer> contadorLivros = new HashMap<>();
        
        for (Emprestimo e : todosEmprestimos) {
            contadorLivros.merge(e.getIdCabecalho(), 1, Integer::sum);
        }
        
        return contadorLivros.entrySet().stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(limite)
            .map(entry -> {
                try {
                    CabecalhoLivro cab = cabecalhoDAO.obterPorId(entry.getKey());
                    return cab != null ? livroDAO.obterPorId(cab.getIdLivro()) : null;
                } catch (java.sql.SQLException e) {
                    System.err.println("Erro ao carregar cabeçalho do relatório: " + e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();
    }

    // Classes internas para dados de relatório
    
    public static class RelatorioMultasMensal {
        private int mes;
        private int ano;
        private double totalMultas;
        private double totalPago;
        private double totalPendente;
        private int quantidadeMultas;
        private List<ControleMulta> multas;

        // Getters e Setters
        public int getMes() { return mes; }
        public void setMes(int mes) { this.mes = mes; }

        public int getAno() { return ano; }
        public void setAno(int ano) { this.ano = ano; }

        public double getTotalMultas() { return totalMultas; }
        public void setTotalMultas(double totalMultas) { this.totalMultas = totalMultas; }

        public double getTotalPago() { return totalPago; }
        public void setTotalPago(double totalPago) { this.totalPago = totalPago; }

        public double getTotalPendente() { return totalPendente; }
        public void setTotalPendente(double totalPendente) { this.totalPendente = totalPendente; }

        public int getQuantidadeMultas() { return quantidadeMultas; }
        public void setQuantidadeMultas(int quantidadeMultas) { this.quantidadeMultas = quantidadeMultas; }

        public List<ControleMulta> getMultas() { return multas; }
        public void setMultas(List<ControleMulta> multas) { this.multas = multas; }
    }

    public static class RelatorioLivrosIntegrados {
        private Date dataInicio;
        private Date dataFim;
        private List<Livro> livros;
        private int quantidadeLivros;
        private int totalExemplares;

        // Getters e Setters
        public Date getDataInicio() { return dataInicio; }
        public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

        public Date getDataFim() { return dataFim; }
        public void setDataFim(Date dataFim) { this.dataFim = dataFim; }

        public List<Livro> getLivros() { return livros; }
        public void setLivros(List<Livro> livros) { this.livros = livros; }

        public int getQuantidadeLivros() { return quantidadeLivros; }
        public void setQuantidadeLivros(int quantidadeLivros) { this.quantidadeLivros = quantidadeLivros; }

        public int getTotalExemplares() { return totalExemplares; }
        public void setTotalExemplares(int totalExemplares) { this.totalExemplares = totalExemplares; }
    }

    public static class RelatorioDesempenhoAlunos {
        private List<DadosDesempenhoAluno> desempenhos;

        public List<DadosDesempenhoAluno> getDesempenhos() { return desempenhos; }
        public void setDesempenhos(List<DadosDesempenhoAluno> desempenhos) { this.desempenhos = desempenhos; }
    }

    public static class DadosDesempenhoAluno {
        private Estudante estudante;
        private int totalEmprestimos;
        private int devolucoesNoPrazo;
        private int devolucoesAtrasadas;
        private int totalMultas;
        private double valorTotalMultas;
        private double percentualResponsabilidade;

        // Getters e Setters
        public Estudante getEstudante() { return estudante; }
        public void setEstudante(Estudante estudante) { this.estudante = estudante; }

        public int getTotalEmprestimos() { return totalEmprestimos; }
        public void setTotalEmprestimos(int totalEmprestimos) { this.totalEmprestimos = totalEmprestimos; }

        public int getDevolucoesNoPrazo() { return devolucoesNoPrazo; }
        public void setDevolucoesNoPrazo(int devolucoesNoPrazo) { this.devolucoesNoPrazo = devolucoesNoPrazo; }

        public int getDevolucoesAtrasadas() { return devolucoesAtrasadas; }
        public void setDevolucoesAtrasadas(int devolucoesAtrasadas) { this.devolucoesAtrasadas = devolucoesAtrasadas; }

        public int getTotalMultas() { return totalMultas; }
        public void setTotalMultas(int totalMultas) { this.totalMultas = totalMultas; }

        public double getValorTotalMultas() { return valorTotalMultas; }
        public void setValorTotalMultas(double valorTotalMultas) { this.valorTotalMultas = valorTotalMultas; }

        public double getPercentualResponsabilidade() { return percentualResponsabilidade; }
        public void setPercentualResponsabilidade(double percentualResponsabilidade) { this.percentualResponsabilidade = percentualResponsabilidade; }
    }

    public static class RelatorioExcelencia {
        private List<DadosDesempenhoAluno> melhoresAlunos;
        private int totalEmprestimosGeral;
        private double taxaMediaResponsabilidade;
        private List<Livro> livrosMaisEmprestados;
        private java.util.Date dataRelatorio;

        // Getters e Setters
        public List<DadosDesempenhoAluno> getMelhoresAlunos() { return melhoresAlunos; }
        public void setMelhoresAlunos(List<DadosDesempenhoAluno> melhoresAlunos) { this.melhoresAlunos = melhoresAlunos; }

        public int getTotalEmprestimosGeral() { return totalEmprestimosGeral; }
        public void setTotalEmprestimosGeral(int totalEmprestimosGeral) { this.totalEmprestimosGeral = totalEmprestimosGeral; }

        public double getTaxaMediaResponsabilidade() { return taxaMediaResponsabilidade; }
        public void setTaxaMediaResponsabilidade(double taxaMediaResponsabilidade) { this.taxaMediaResponsabilidade = taxaMediaResponsabilidade; }

        public List<Livro> getLivrosMaisEmprestados() { return livrosMaisEmprestados; }
        public void setLivrosMaisEmprestados(List<Livro> livrosMaisEmprestados) { this.livrosMaisEmprestados = livrosMaisEmprestados; }

        public java.util.Date getDataRelatorio() { return dataRelatorio; }
        public void setDataRelatorio(java.util.Date dataRelatorio) { this.dataRelatorio = dataRelatorio; }
    }
}
