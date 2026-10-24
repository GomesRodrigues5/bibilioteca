package model;

import java.sql.Timestamp;

/**
 * Classe que representa o Cabeçalho de Catalogação de um Livro.
 * Contém informações de indexação descritiva, classificação CDU e estrutura do livro.
 */
public class CabecalhoLivro {
    private int idCabecalho;
    private int idLivro;
    private int idCdu;
    private String indexacaoDescritiva;
    private int quantidadeExemplares;
    private int numeroCapitulo;
    private String tituloCapitulo;
    private int paginaInicio;
    private int paginaFim;
    private String numeroChamada;
    private String palavrasChaveCapitulo;
    private Timestamp criadoEm;
    private Timestamp modificadoEm;

    // Construtores
    public CabecalhoLivro() {
        this.quantidadeExemplares = 1;
        this.numeroCapitulo = 1;
    }

    public CabecalhoLivro(int idLivro, int idCdu, String indexacaoDescritiva) {
        this();
        this.idLivro = idLivro;
        this.idCdu = idCdu;
        this.indexacaoDescritiva = indexacaoDescritiva;
    }

    // Getters e Setters
    public int getIdCabecalho() { return idCabecalho; }
    public void setIdCabecalho(int idCabecalho) { this.idCabecalho = idCabecalho; }

    public int getIdLivro() { return idLivro; }
    public void setIdLivro(int idLivro) { this.idLivro = idLivro; }

    public int getIdCdu() { return idCdu; }
    public void setIdCdu(int idCdu) { this.idCdu = idCdu; }

    public String getIndexacaoDescritiva() { return indexacaoDescritiva; }
    public void setIndexacaoDescritiva(String indexacaoDescritiva) { this.indexacaoDescritiva = indexacaoDescritiva; }

    public int getQuantidadeExemplares() { return quantidadeExemplares; }
    public void setQuantidadeExemplares(int quantidadeExemplares) { this.quantidadeExemplares = quantidadeExemplares; }

    public int getNumeroCapitulo() { return numeroCapitulo; }
    public void setNumeroCapitulo(int numeroCapitulo) { this.numeroCapitulo = numeroCapitulo; }

    public String getTituloCapitulo() { return tituloCapitulo; }
    public void setTituloCapitulo(String tituloCapitulo) { this.tituloCapitulo = tituloCapitulo; }

    public int getPaginaInicio() { return paginaInicio; }
    public void setPaginaInicio(int paginaInicio) { this.paginaInicio = paginaInicio; }

    public int getPaginaFim() { return paginaFim; }
    public void setPaginaFim(int paginaFim) { this.paginaFim = paginaFim; }

    public String getNumeroChamada() { return numeroChamada; }
    public void setNumeroChamada(String numeroChamada) { this.numeroChamada = numeroChamada; }

    public String getPalavrasChaveCapitulo() { return palavrasChaveCapitulo; }
    public void setPalavrasChaveCapitulo(String palavrasChaveCapitulo) { this.palavrasChaveCapitulo = palavrasChaveCapitulo; }

    public Timestamp getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Timestamp criadoEm) { this.criadoEm = criadoEm; }

    public Timestamp getModificadoEm() { return modificadoEm; }
    public void setModificadoEm(Timestamp modificadoEm) { this.modificadoEm = modificadoEm; }
}
