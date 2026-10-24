package model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Classe que representa um Empréstimo de livro no sistema.
 * Acompanha todo o ciclo de vida do empréstimo, incluindo multas e devoluções.
 */
public class Emprestimo {
    private int idEmprestimo;
    private int idEstudante;
    private int idCabecalho;
    private int idLivro;
    private int idFuncionario;
    private Date dataEmprestimo;
    private Date dataDeEmprestimo;
    private Time horaEmprestimo;
    private Time horaDeEmprestimo;
    private Date dataDevolutaoPrevista;
    private Time horaDevolutaoPrevista;
    private Date dataDevolutaoReal;
    private Date dataEntrega;
    private Time horaDevolutaoReal;
    private Time horaDeEntrega;
    private Timestamp dataHoraDevolutaoReal;
    private Timestamp dataEHoraDaEntrega;
    private String statusEmprestimo;
    private String acao;
    private int numeroRenovacoes;
    private boolean multaAplicada;
    private double valorMulta;
    private double valor;
    private boolean multaPaga;
    private Date dataPagamentoMulta;
    private String observacoes;
    private Timestamp criadoEm;
    private Timestamp modificadoEm;

    // Campos auxiliares para exibição (não persistidos diretamente no empréstimo)
    private String tituloLivro;
    private String codigoInternoLivro;
    private String nomeEstudante;

    // Construtores
    public Emprestimo() {
        this.statusEmprestimo = "Ativo";
        this.numeroRenovacoes = 0;
        this.multaAplicada = false;
        this.valorMulta = 0;
        this.multaPaga = false;
    }

    public Emprestimo(int idEstudante, int idCabecalho, int idFuncionario, 
                     Date dataEmprestimo, Date dataDevolutaoPrevista) {
        this();
        this.idEstudante = idEstudante;
        this.idCabecalho = idCabecalho;
        this.idFuncionario = idFuncionario;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolutaoPrevista = dataDevolutaoPrevista;
    }

    // Getters e Setters
    public int getIdEmprestimo() { return idEmprestimo; }
    public void setIdEmprestimo(int idEmprestimo) { this.idEmprestimo = idEmprestimo; }

    public int getIdEstudante() { return idEstudante; }
    public void setIdEstudante(int idEstudante) { this.idEstudante = idEstudante; }

    public int getIdCabecalho() { return idCabecalho; }
    public void setIdCabecalho(int idCabecalho) { this.idCabecalho = idCabecalho; }

    public int getIdLivro() { return idLivro; }
    public void setIdLivro(int idLivro) { this.idLivro = idLivro; }

    public int getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(int idFuncionario) { this.idFuncionario = idFuncionario; }

    public Date getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(Date dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }

    public Date getDataDeEmprestimo() { return dataDeEmprestimo != null ? dataDeEmprestimo : dataEmprestimo; }
    public void setDataDeEmprestimo(Date dataDeEmprestimo) { this.dataDeEmprestimo = dataDeEmprestimo; this.dataEmprestimo = dataDeEmprestimo; }

    public Time getHoraEmprestimo() { return horaEmprestimo; }
    public void setHoraEmprestimo(Time horaEmprestimo) { this.horaEmprestimo = horaEmprestimo; }

    public Time getHoraDeEmprestimo() { return horaDeEmprestimo != null ? horaDeEmprestimo : horaEmprestimo; }
    public void setHoraDeEmprestimo(Time horaDeEmprestimo) { this.horaDeEmprestimo = horaDeEmprestimo; this.horaEmprestimo = horaDeEmprestimo; }

    public Date getDataDevolutaoPrevista() { return dataDevolutaoPrevista; }
    public void setDataDevolutaoPrevista(Date dataDevolutaoPrevista) { this.dataDevolutaoPrevista = dataDevolutaoPrevista; }

    public Time getHoraDevolutaoPrevista() { return horaDevolutaoPrevista; }
    public void setHoraDevolutaoPrevista(Time horaDevolutaoPrevista) { this.horaDevolutaoPrevista = horaDevolutaoPrevista; }

    public Date getDataDevolutaoReal() { return dataDevolutaoReal; }
    public void setDataDevolutaoReal(Date dataDevolutaoReal) { this.dataDevolutaoReal = dataDevolutaoReal; }

    public Date getDataEntrega() { return dataEntrega != null ? dataEntrega : dataDevolutaoPrevista; }
    public void setDataEntrega(Date dataEntrega) { this.dataEntrega = dataEntrega; this.dataDevolutaoPrevista = dataEntrega; }

    public Time getHoraDevolutaoReal() { return horaDevolutaoReal; }
    public void setHoraDevolutaoReal(Time horaDevolutaoReal) { this.horaDevolutaoReal = horaDevolutaoReal; }

    public Time getHoraDeEntrega() { return horaDeEntrega != null ? horaDeEntrega : horaDevolutaoReal; }
    public void setHoraDeEntrega(Time horaDeEntrega) { this.horaDeEntrega = horaDeEntrega; this.horaDevolutaoReal = horaDeEntrega; }

    public Timestamp getDataHoraDevolutaoReal() { return dataHoraDevolutaoReal; }
    public void setDataHoraDevolutaoReal(Timestamp dataHoraDevolutaoReal) { this.dataHoraDevolutaoReal = dataHoraDevolutaoReal; }

    public Timestamp getDataEHoraDaEntrega() { return dataEHoraDaEntrega; }
    public void setDataEHoraDaEntrega(Timestamp dataEHoraDaEntrega) { this.dataEHoraDaEntrega = dataEHoraDaEntrega; }
    public void setDataEHoraDaEntrega(java.util.Date dataEHoraDaEntrega) {
        this.dataEHoraDaEntrega = dataEHoraDaEntrega == null ? null : new Timestamp(dataEHoraDaEntrega.getTime());
    }

    public String getStatusEmprestimo() { return statusEmprestimo; }
    public void setStatusEmprestimo(String statusEmprestimo) { this.statusEmprestimo = statusEmprestimo; }

    public String getAcao() { return acao != null ? acao : statusEmprestimo; }
    public void setAcao(String acao) { this.acao = acao; this.statusEmprestimo = acao; }

    public int getNumeroRenovacoes() { return numeroRenovacoes; }
    public void setNumeroRenovacoes(int numeroRenovacoes) { this.numeroRenovacoes = numeroRenovacoes; }

    public boolean isMultaAplicada() { return multaAplicada; }
    public void setMultaAplicada(boolean multaAplicada) { this.multaAplicada = multaAplicada; }

    public double getValorMulta() { return valorMulta; }
    public void setValorMulta(double valorMulta) { this.valorMulta = valorMulta; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; this.valorMulta = valor; }

    public boolean isMultaPaga() { return multaPaga; }
    public void setMultaPaga(boolean multaPaga) { this.multaPaga = multaPaga; }

    public Date getDataPagamentoMulta() { return dataPagamentoMulta; }
    public void setDataPagamentoMulta(Date dataPagamentoMulta) { this.dataPagamentoMulta = dataPagamentoMulta; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Timestamp getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Timestamp criadoEm) { this.criadoEm = criadoEm; }

    public Timestamp getModificadoEm() { return modificadoEm; }
    public void setModificadoEm(Timestamp modificadoEm) { this.modificadoEm = modificadoEm; }

    public String getTituloLivro() { return tituloLivro; }
    public void setTituloLivro(String tituloLivro) { this.tituloLivro = tituloLivro; }

    public String getCodigoInternoLivro() { return codigoInternoLivro; }
    public void setCodigoInternoLivro(String codigoInternoLivro) { this.codigoInternoLivro = codigoInternoLivro; }

    public String getNomeEstudante() { return nomeEstudante; }
    public void setNomeEstudante(String nomeEstudante) { this.nomeEstudante = nomeEstudante; }
}