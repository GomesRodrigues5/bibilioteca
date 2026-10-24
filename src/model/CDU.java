package model;

import java.sql.Timestamp;

/**
 * Classe que representa a Classificação Decimal Universal (CDU).
 * Segue o sistema internacional de classificação bibliográfica.
 */
public class CDU {
    private int idCdu;
    private String codigoCdu;
    private String descricao;
    private Integer classePaiId;
    private String tipoTabela;
    private boolean ativo;
    private Timestamp criadoEm;
    private Timestamp modificadoEm;

    // Construtores
    public CDU() {
        this.tipoTabela = "principal";
        this.ativo = true;
    }

    public CDU(String codigoCdu, String descricao) {
        this();
        this.codigoCdu = codigoCdu;
        this.descricao = descricao;
    }

    // Getters e Setters
    public int getIdCdu() { return idCdu; }
    public void setIdCdu(int idCdu) { this.idCdu = idCdu; }

    public String getCodigoCdu() { return codigoCdu; }
    public void setCodigoCdu(String codigoCdu) { this.codigoCdu = codigoCdu; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getClassePaiId() { return classePaiId; }
    public void setClassePaiId(Integer classePaiId) { this.classePaiId = classePaiId; }

    public String getTipoTabela() { return tipoTabela; }
    public void setTipoTabela(String tipoTabela) { this.tipoTabela = tipoTabela; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Timestamp getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Timestamp criadoEm) { this.criadoEm = criadoEm; }

    public Timestamp getModificadoEm() { return modificadoEm; }
    public void setModificadoEm(Timestamp modificadoEm) { this.modificadoEm = modificadoEm; }
}
