package model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Classe que representa uma Reserva de livro no sistema.
 * Permite que estudantes reservem livros com antecedência.
 */
public class Reserva {
    private int idReserva;
    private int idEstudante;
    private int idCabecalho;
    private int idFuncionario;
    private Date dataReserva;
    private Time horaReserva;
    private Date dataRetiradadaPrevista;
    private Time horaRetiradadaPrevista;
    private Date dataRetiradadaReal;
    private Time horaRetiradadaReal;
    private String statusReserva;
    private int diasReserva;
    private String observacoes;
    private Timestamp criadoEm;
    private Timestamp modificadoEm;

    // Construtores
    public Reserva() {
        this.statusReserva = "Reservado";
        this.diasReserva = 7;
    }

    public Reserva(int idEstudante, int idCabecalho, int idFuncionario, 
                   Date dataReserva, Date dataRetiradadaPrevista) {
        this();
        this.idEstudante = idEstudante;
        this.idCabecalho = idCabecalho;
        this.idFuncionario = idFuncionario;
        this.dataReserva = dataReserva;
        this.dataRetiradadaPrevista = dataRetiradadaPrevista;
    }

    // Getters e Setters
    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public int getIdEstudante() { return idEstudante; }
    public void setIdEstudante(int idEstudante) { this.idEstudante = idEstudante; }

    public int getIdCabecalho() { return idCabecalho; }
    public void setIdCabecalho(int idCabecalho) { this.idCabecalho = idCabecalho; }

    public int getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(int idFuncionario) { this.idFuncionario = idFuncionario; }

    public Date getDataReserva() { return dataReserva; }
    public void setDataReserva(Date dataReserva) { this.dataReserva = dataReserva; }

    public Time getHoraReserva() { return horaReserva; }
    public void setHoraReserva(Time horaReserva) { this.horaReserva = horaReserva; }

    public Date getDataRetiradadaPrevista() { return dataRetiradadaPrevista; }
    public void setDataRetiradadaPrevista(Date dataRetiradadaPrevista) { this.dataRetiradadaPrevista = dataRetiradadaPrevista; }

    public Time getHoraRetiradadaPrevista() { return horaRetiradadaPrevista; }
    public void setHoraRetiradadaPrevista(Time horaRetiradadaPrevista) { this.horaRetiradadaPrevista = horaRetiradadaPrevista; }

    public Date getDataRetiradadaReal() { return dataRetiradadaReal; }
    public void setDataRetiradadaReal(Date dataRetiradadaReal) { this.dataRetiradadaReal = dataRetiradadaReal; }

    public Time getHoraRetiradadaReal() { return horaRetiradadaReal; }
    public void setHoraRetiradadaReal(Time horaRetiradadaReal) { this.horaRetiradadaReal = horaRetiradadaReal; }

    public String getStatusReserva() { return statusReserva; }
    public void setStatusReserva(String statusReserva) { this.statusReserva = statusReserva; }

    public int getDiasReserva() { return diasReserva; }
    public void setDiasReserva(int diasReserva) { this.diasReserva = diasReserva; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Timestamp getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Timestamp criadoEm) { this.criadoEm = criadoEm; }

    public Timestamp getModificadoEm() { return modificadoEm; }
    public void setModificadoEm(Timestamp modificadoEm) { this.modificadoEm = modificadoEm; }
}
