package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Classe que representa o Controle de Multas por atraso na devolução de livros.
 * Gerencia cálculos de multas, pagamentos e histórico de penalidades.
 */
public class ControleMulta {
    private int idMulta;
    private int idEmprestimo;
    private int idEstudante;
    private String nomeEstudante;
    private double valorMultaDiaria;
    private int numeroDiasAtraso;
    private double valorTotalMulta;
    private Date dataCalculo;
    private String statusPagamento;
    private Date dataPagamento;
    private double valorPago;
    private String metodoPagamento;
    private String numeroRecibo;
    private String observacoes;
    private Timestamp criadoEm;
    private Timestamp modificadoEm;

    // Construtores
    public ControleMulta() {
        this.statusPagamento = "Pendente";
        this.valorPago = 0;
    }

    public ControleMulta(int idEmprestimo, int idEstudante, double valorMultaDiaria, 
                        int numeroDiasAtraso) {
        this();
        this.idEmprestimo = idEmprestimo;
        this.idEstudante = idEstudante;
        this.valorMultaDiaria = valorMultaDiaria;
        this.numeroDiasAtraso = numeroDiasAtraso;
        this.valorTotalMulta = valorMultaDiaria * numeroDiasAtraso;
    }

    // Getters e Setters
    public int getIdMulta() { return idMulta; }
    public void setIdMulta(int idMulta) { this.idMulta = idMulta; }

    public int getIdControleMulta() { return idMulta; }
    public void setIdControleMulta(int idControleMulta) { this.idMulta = idControleMulta; }

    public int getIdEmprestimo() { return idEmprestimo; }
    public void setIdEmprestimo(int idEmprestimo) { this.idEmprestimo = idEmprestimo; }

    public int getIdEstudante() { return idEstudante; }
    public void setIdEstudante(int idEstudante) { this.idEstudante = idEstudante; }

    public String getNomeEstudante() { return nomeEstudante; }
    public void setNomeEstudante(String nomeEstudante) { this.nomeEstudante = nomeEstudante; }

    public double getValorMultaDiaria() { return valorMultaDiaria; }
    public void setValorMultaDiaria(double valorMultaDiaria) { this.valorMultaDiaria = valorMultaDiaria; }

    public int getNumeroDiasAtraso() { return numeroDiasAtraso; }
    public void setNumeroDiasAtraso(int numeroDiasAtraso) { this.numeroDiasAtraso = numeroDiasAtraso; }

    public double getValorTotalMulta() { return valorTotalMulta; }
    public void setValorTotalMulta(double valorTotalMulta) { this.valorTotalMulta = valorTotalMulta; }

    public Date getDataCalculo() { return dataCalculo; }
    public void setDataCalculo(Date dataCalculo) { this.dataCalculo = dataCalculo; }

    public String getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(String statusPagamento) { this.statusPagamento = statusPagamento; }

    public Date getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(Date dataPagamento) { this.dataPagamento = dataPagamento; }

    public double getValorPago() { return valorPago; }
    public void setValorPago(double valorPago) { this.valorPago = valorPago; }

    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public String getNumeroRecibo() { return numeroRecibo; }
    public void setNumeroRecibo(String numeroRecibo) { this.numeroRecibo = numeroRecibo; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Timestamp getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Timestamp criadoEm) { this.criadoEm = criadoEm; }

    public Timestamp getModificadoEm() { return modificadoEm; }
    public void setModificadoEm(Timestamp modificadoEm) { this.modificadoEm = modificadoEm; }
}
