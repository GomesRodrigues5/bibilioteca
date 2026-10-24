package model;

import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Classe que representa um Livro no sistema de gestão de biblioteca.
 * Segue as normas internacionais de catalogação bibliográfica (ISBD e CDU).
 */
public class Livro {
    private int idLivro;
    private String autor;
    private String titulo;
    private String subtitulo;
    private String editora;
    private String edicao;
    private int anoPublicacao;
    private String isbn;
    private String issn;
    private String eissn;
    private String ndi;
    private String material;
    private String idioma;
    private String ilustracao;
    private String encadernacao;
    private int altura_mm;
    private int largura_mm;
    private int profundidade_mm;
    private int numeroPaginas;
    private int pesoGramas;
    private String codigoInterno;
    private int quantidadeExemplares;
    private int quantidadeDisponivel;
    private byte[] capaLivro;
    private byte[] arquivoPdf;
    private String armarioNome;
    private String armarioPartileira;
    private byte[] capadoLivro;
    private byte[] pdf;
    private boolean disponivel;
    private int idArmario;
    private String sinopse;
    private String palavrasChave;
    private boolean ativo;
    private Timestamp criadoEm;
    private Timestamp modificadoEm;

    // Construtores
    public Livro() {
        this.material = "Papel";
        this.idioma = "Português";
        this.quantidadeExemplares = 1;
        this.quantidadeDisponivel = 1;
        this.ativo = true;
    }

    public Livro(String titulo, String codigoInterno) {
        this();
        this.titulo = titulo;
        this.codigoInterno = codigoInterno;
    }

    // Getters e Setters
    public int getIdLivro() { return idLivro; }
    public void setIdLivro(int idLivro) { this.idLivro = idLivro; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String subtitulo) { this.subtitulo = subtitulo; }

    public String getEditora() { return editora; }
    public void setEditora(String editora) { this.editora = editora; }

    public String getEdicao() { return edicao; }
    public void setEdicao(String edicao) { this.edicao = edicao; }

    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getIssn() { return issn; }
    public void setIssn(String issn) { this.issn = issn; }

    public String getEissn() { return eissn; }
    public void setEissn(String eissn) { this.eissn = eissn; }

    public String getNdi() { return ndi; }
    public void setNdi(String ndi) { this.ndi = ndi; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getIlustracao() { return ilustracao; }
    public void setIlustracao(String ilustracao) { this.ilustracao = ilustracao; }

    public String getEncadernacao() { return encadernacao; }
    public void setEncadernacao(String encadernacao) { this.encadernacao = encadernacao; }

    public int getAltura_mm() { return altura_mm; }
    public void setAltura_mm(int altura_mm) { this.altura_mm = altura_mm; }

    public int getLargura_mm() { return largura_mm; }
    public void setLargura_mm(int largura_mm) { this.largura_mm = largura_mm; }

    public int getProfundidade_mm() { return profundidade_mm; }
    public void setProfundidade_mm(int profundidade_mm) { this.profundidade_mm = profundidade_mm; }

    public int getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(int numeroPaginas) { this.numeroPaginas = numeroPaginas; }

    public int getPesoGramas() { return pesoGramas; }
    public void setPesoGramas(int pesoGramas) { this.pesoGramas = pesoGramas; }

    public String getCodigoInterno() { return codigoInterno; }
    public void setCodigoInterno(String codigoInterno) { this.codigoInterno = codigoInterno; }

    public int getQuantidadeExemplares() { return quantidadeExemplares; }
    public void setQuantidadeExemplares(int quantidadeExemplares) { this.quantidadeExemplares = quantidadeExemplares; }

    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(int quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }

    public byte[] getCapaLivro() { return capaLivro; }
    public void setCapaLivro(byte[] capaLivro) { this.capaLivro = capaLivro; }

    public byte[] getArquivoPdf() { return arquivoPdf; }
    public void setArquivoPdf(byte[] arquivoPdf) { this.arquivoPdf = arquivoPdf; }

    /**
     * Define a capa a partir do caminho do ficheiro (lê bytes).
     */
    public void setCapadoLivroPath(String caminho) {
        if (caminho == null || caminho.trim().isEmpty()) return;
        try {
            Path p = Paths.get(caminho);
            this.capaLivro = Files.readAllBytes(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Define o PDF a partir do caminho do ficheiro (lê bytes).
     */
    public void setPdfPath(String caminho) {
        if (caminho == null || caminho.trim().isEmpty()) return;
        try {
            Path p = Paths.get(caminho);
            this.arquivoPdf = Files.readAllBytes(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getIdArmario() { return idArmario; }
    public void setIdArmario(int idArmario) { this.idArmario = idArmario; }

    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }

    public String getPalavrasChave() { return palavrasChave; }
    public void setPalavrasChave(String palavrasChave) { this.palavrasChave = palavrasChave; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Timestamp getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Timestamp criadoEm) { this.criadoEm = criadoEm; }

    public Timestamp getModificadoEm() { return modificadoEm; }
    public void setModificadoEm(Timestamp modificadoEm) { this.modificadoEm = modificadoEm; }

    public String getArmarioNome() { return armarioNome; }
    public void setArmarioNome(String armarioNome) { this.armarioNome = armarioNome; }

    public String getArmarioPartileira() { return armarioPartileira; }
    public void setArmarioPartileira(String armarioPartileira) { this.armarioPartileira = armarioPartileira; }

    public byte[] getCapadoLivro() { return capadoLivro; }
    public void setCapadoLivro(byte[] capadoLivro) { this.capadoLivro = capadoLivro; }

    public byte[] getPdf() { return pdf; }
    public void setPdf(byte[] pdf) { this.pdf = pdf; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
        if (!disponivel) {
            this.quantidadeDisponivel = 0;
        } else if (this.quantidadeDisponivel == 0) {
            this.quantidadeDisponivel = Math.max(1, this.quantidadeExemplares);
        }
    }

    public String getCodigo() { return codigoInterno; }
    public void setCodigo(String codigo) { this.codigoInterno = codigo; }

    public String getCdn() { return ndi; }
    public void setCdn(String cdn) { this.ndi = cdn; }

    public Date getDataLancamento() {
        return anoPublicacao > 0 ? Date.valueOf(java.time.LocalDate.of(anoPublicacao, 1, 1)) : null;
    }
    public void setDataLancamento(Date dataLancamento) {
        this.anoPublicacao = dataLancamento == null ? 0 : dataLancamento.toLocalDate().getYear();
    }

    public int getComprimento() { return profundidade_mm; }
    public void setComprimento(int comprimento) { this.profundidade_mm = comprimento; }

    public int getLargura() { return largura_mm; }
    public void setLargura(int largura) { this.largura_mm = largura; }

    public String getCor() { return encadernacao; }
    public void setCor(String cor) { this.encadernacao = cor; }

    public int getPagina() { return numeroPaginas; }
    public void setPagina(int pagina) { this.numeroPaginas = pagina; }
}