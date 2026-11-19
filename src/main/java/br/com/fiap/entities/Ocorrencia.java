package br.com.fiap.entities;

// Ocorrência numa linha, informada pela operadora (OFICIAL) ou pelos passageiros (tabela TN_OCORRENCIA)
public class Ocorrencia {

    private int id;
    private int idLinha;
    private String nomeLinha;
    private String status;
    private String fonte;
    private String descricao;
    private boolean chuva;
    private String inicio;
    private String fim;

    public Ocorrencia() {
        super();
    }

    public Ocorrencia(int id, int idLinha, String status, String fonte, String descricao, boolean chuva) {
        super();
        this.id = id;
        this.idLinha = idLinha;
        this.status = status;
        this.fonte = fonte;
        this.descricao = descricao;
        this.chuva = chuva;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isChuva() {
        return chuva;
    }

    public void setChuva(boolean chuva) {
        this.chuva = chuva;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getFim() {
        return fim;
    }

    public void setFim(String fim) {
        this.fim = fim;
    }

    // Enquanto não tem data de fim, a ocorrência está aberta
    public boolean isAberta() {
        return fim == null;
    }

    public boolean oficial() {
        return fonte.equals("OFICIAL");
    }
}
