package model;

public class Livro {
    private int idLivro;
    private String titulo;
    private String cor;
    private int pagina;
    private String codigo;
    private int idArmario;
    private String armarioNome;
    private boolean disponivel;

    // Construtores, getters e setters
    public Livro() {}

    public Livro(String titulo, String codigo, boolean disponivel) {
        this.titulo = titulo;
        this.codigo = codigo;
        this.disponivel = disponivel;
    }

    // Getters e setters
    public int getIdLivro() { return idLivro; }
    public void setIdLivro(int idLivro) { this.idLivro = idLivro; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getIdArmario() { return idArmario; }
    public void setIdArmario(int idArmario) { this.idArmario = idArmario; }

    public String getArmarioNome() { return armarioNome; }
    public void setArmarioNome(String armarioNome) { this.armarioNome = armarioNome; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}