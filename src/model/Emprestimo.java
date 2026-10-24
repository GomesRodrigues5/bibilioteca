package model;

import java.sql.Date;
import java.sql.Time;

public class Emprestimo {
    private int idEmprestimo;
    private int idEstudante;
    private int idLivro;
    private Date dataDeEmprestimo;
    private Time horaDeEmprestimo;
    private Date dataEntrega;
    private Time horaDeEntrega;
    private double valor;
    private String acao;
    private java.util.Date dataEHoraDaEntrega;
    private int idFuncionario;

    // Construtores, getters e setters
    public Emprestimo() {}

    // Getters e setters
    public int getIdEmprestimo() { return idEmprestimo; }
    public void setIdEmprestimo(int idEmprestimo) { this.idEmprestimo = idEmprestimo; }

    public int getIdEstudante() { return idEstudante; }
    public void setIdEstudante(int idEstudante) { this.idEstudante = idEstudante; }

    public int getIdLivro() { return idLivro; }
    public void setIdLivro(int idLivro) { this.idLivro = idLivro; }

    public Date getDataDeEmprestimo() { return dataDeEmprestimo; }
    public void setDataDeEmprestimo(Date dataDeEmprestimo) { this.dataDeEmprestimo = dataDeEmprestimo; }

    public Time getHoraDeEmprestimo() { return horaDeEmprestimo; }
    public void setHoraDeEmprestimo(Time horaDeEmprestimo) { this.horaDeEmprestimo = horaDeEmprestimo; }

    public Date getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(Date dataEntrega) { this.dataEntrega = dataEntrega; }

    public Time getHoraDeEntrega() { return horaDeEntrega; }
    public void setHoraDeEntrega(Time horaDeEntrega) { this.horaDeEntrega = horaDeEntrega; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public java.util.Date getDataEHoraDaEntrega() { return dataEHoraDaEntrega; }
    public void setDataEHoraDaEntrega(java.util.Date dataEHoraDaEntrega) { this.dataEHoraDaEntrega = dataEHoraDaEntrega; }

    public int getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(int idFuncionario) { this.idFuncionario = idFuncionario; }
}