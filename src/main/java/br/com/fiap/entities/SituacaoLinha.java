package br.com.fiap.entities;

// Como cada linha do trajeto entrou na conta do aviso
public class SituacaoLinha {

    private int idLinha;
    private String nomeLinha;
    private String statusOficial;
    private int minutosPeloStatus;
    private int minutosPelosRelatos;
    private int quantidadeRelatos;
    private int atrasoMinutos;
    private boolean divergencia;

    public SituacaoLinha() {
        super();
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

    public String getStatusOficial() {
        return statusOficial;
    }

    public void setStatusOficial(String statusOficial) {
        this.statusOficial = statusOficial;
    }

    public int getMinutosPeloStatus() {
        return minutosPeloStatus;
    }

    public void setMinutosPeloStatus(int minutosPeloStatus) {
        this.minutosPeloStatus = minutosPeloStatus;
    }

    public int getMinutosPelosRelatos() {
        return minutosPelosRelatos;
    }

    public void setMinutosPelosRelatos(int minutosPelosRelatos) {
        this.minutosPelosRelatos = minutosPelosRelatos;
    }

    public int getQuantidadeRelatos() {
        return quantidadeRelatos;
    }

    public void setQuantidadeRelatos(int quantidadeRelatos) {
        this.quantidadeRelatos = quantidadeRelatos;
    }

    public int getAtrasoMinutos() {
        return atrasoMinutos;
    }

    public void setAtrasoMinutos(int atrasoMinutos) {
        this.atrasoMinutos = atrasoMinutos;
    }

    public boolean isDivergencia() {
        return divergencia;
    }

    public void setDivergencia(boolean divergencia) {
        this.divergencia = divergencia;
    }
}
