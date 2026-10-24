package model;

public class Computador {
    private int idComputador;
    private String marca;
    private String modelo;
    private String numero;
    private boolean emUso;

    // Construtores, getters e setters
    public Computador() {}

    public Computador(String marca, String modelo, String numero, boolean emUso) {
        this.marca = marca;
        this.modelo = modelo;
        this.numero = numero;
        this.emUso = emUso;
    }

    // Getters e setters
    public int getIdComputador() { return idComputador; }
    public void setIdComputador(int idComputador) { this.idComputador = idComputador; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public boolean isEmUso() { return emUso; }
    public void setEmUso(boolean emUso) { this.emUso = emUso; }
}