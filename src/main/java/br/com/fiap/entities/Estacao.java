package br.com.fiap.entities;

// Estação de uma linha (tabela TN_ESTACAO)
public class Estacao {

    private int id;
    private String nome;
    private int idLinha;
    private String nomeLinha;

    public Estacao() {
        super();
    }

    public Estacao(int id, String nome, int idLinha) {
        super();
        this.id = id;
        this.nome = nome;
        this.idLinha = idLinha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdLinha() {
        return idLinha;
    }

    public void setIdLinha(int idLinha) {
        this.idLinha = idLinha;
    }

    public String getNomeLinha() {
        return nomeLinha;
    }

    public void setNomeLinha(String nomeLinha) {
        this.nomeLinha = nomeLinha;
    }
}
