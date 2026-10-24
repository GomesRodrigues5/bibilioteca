package model;

import java.sql.Date;
import java.sql.Time;

public class Atendimento {
    private int idAtendimento;
    private int idEstudante;
    private int idComputador;
    private Date dataDeEntrada;
    private Time horaDeEntrada;
    private Time horaDeSaida;
    private int tempoLimiteMinutos;
    private String acao;

    // Construtores, getters e setters
    public Atendimento() {}

    // Getters e setters
    public int getIdAtendimento() { return idAtendimento; }
    public void setIdAtendimento(int idAtendimento) { this.idAtendimento = idAtendimento; }

    public int getIdEstudante() { return idEstudante; }
    public void setIdEstudante(int idEstudante) { this.idEstudante = idEstudante; }

    public int getIdComputador() { return idComputador; }
    public void setIdComputador(int idComputador) { this.idComputador = idComputador; }

    public Date getDataDeEntrada() { return dataDeEntrada; }
    public void setDataDeEntrada(Date dataDeEntrada) { this.dataDeEntrada = dataDeEntrada; }

    public Time getHoraDeEntrada() { return horaDeEntrada; }
    public void setHoraDeEntrada(Time horaDeEntrada) { this.horaDeEntrada = horaDeEntrada; }

    public Time getHoraDeSaida() { return horaDeSaida; }
    public void setHoraDeSaida(Time horaDeSaida) { this.horaDeSaida = horaDeSaida; }

    public int getTempoLimiteMinutos() { return tempoLimiteMinutos; }
    public void setTempoLimiteMinutos(int tempoLimiteMinutos) { this.tempoLimiteMinutos = tempoLimiteMinutos; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }
}