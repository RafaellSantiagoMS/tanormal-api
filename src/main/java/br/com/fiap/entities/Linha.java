package br.com.fiap.entities;

// Linha de metrô ou trem e o status oficial que a operadora informa (tabela TN_LINHA)
public class Linha {

    private int id;
    private int numero;
    private String nome;
    private String operadora;
    private String statusOficial;
    private String dataAtualizacaoStatus;

    public Linha() {
        super();
    }

    public Linha(int id, int numero, String nome, String operadora, String statusOficial) {
        super();
        this.id = id;
        this.numero = numero;
        this.nome = nome;
        this.operadora = operadora;
        this.statusOficial = statusOficial;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getOperadora() {
        return operadora;
    }

    public void setOperadora(String operadora) {
        this.operadora = operadora;
    }

    public String getStatusOficial() {
        return statusOficial;
    }

    public void setStatusOficial(String statusOficial) {
        this.statusOficial = statusOficial;
    }

    public String getDataAtualizacaoStatus() {
        return dataAtualizacaoStatus;
    }

    public void setDataAtualizacaoStatus(String dataAtualizacaoStatus) {
        this.dataAtualizacaoStatus = dataAtualizacaoStatus;
    }

    // Nome do jeito que aparece no app, ex.: "Linha 12-Safira"
    public String getNomeCompleto() {
        return "Linha " + numero + "-" + nome;
    }

    // Quantos minutos o status oficial costuma acrescentar na viagem
    public int atrasoPeloStatus() {
        switch (statusOficial) {
            case "VELOCIDADE_REDUZIDA":
                return 10;
            case "OPERACAO_PARCIAL":
                return 20;
            case "PARALISADA":
                return 40;
            default:
                return 0;
        }
    }

    public boolean statusNormal() {
        return statusOficial.equals("NORMAL");
    }
}
