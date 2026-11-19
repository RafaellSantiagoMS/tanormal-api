package br.com.fiap.entities;

// Relato de um passageiro sobre a linha: PARADO, LENTO ou LOTADO (tabela TN_RELATO)
public class Relato {

    // Depois desse tempo o relato não vale mais para o aviso (mesmo corte do app e do Data Science)
    public static final int VALIDADE_MINUTOS = 30;

    private int id;
    private int idUsuario;
    private int idLinha;
    private int idEstacao;
    private String nomeEstacao;
    private String tipo;
    private String comentario;
    private String dataHora;
    private int confirmacoes;
    private int minutosDesdeEnvio;

    public Relato() {
        super();
    }

    public Relato(int id, int idUsuario, int idLinha, int idEstacao, String tipo, String comentario) {
        super();
        this.id = id;
        this.idUsuario = idUsuario;
        this.idLinha = idLinha;
        this.idEstacao = idEstacao;
        this.tipo = tipo;
        this.comentario = comentario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdLinha() {
        return idLinha;
    }

    public void setIdLinha(int idLinha) {
        this.idLinha = idLinha;
    }

    public int getIdEstacao() {
        return idEstacao;
    }

    public void setIdEstacao(int idEstacao) {
        this.idEstacao = idEstacao;
    }

    public String getNomeEstacao() {
        return nomeEstacao;
    }

    public void setNomeEstacao(String nomeEstacao) {
        this.nomeEstacao = nomeEstacao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public int getConfirmacoes() {
        return confirmacoes;
    }

    public void setConfirmacoes(int confirmacoes) {
        this.confirmacoes = confirmacoes;
    }

    public int getMinutosDesdeEnvio() {
        return minutosDesdeEnvio;
    }

    public void setMinutosDesdeEnvio(int minutosDesdeEnvio) {
        this.minutosDesdeEnvio = minutosDesdeEnvio;
    }

    public boolean estaValido() {
        return minutosDesdeEnvio < VALIDADE_MINUTOS;
    }

    // Quantos minutos de atraso esse tipo de relato indica
    public int pesoEmMinutos() {
        if (tipo.equals("PARADO")) {
            return 8;
        } else if (tipo.equals("LENTO")) {
            return 4;
        } else {
            return 2; // LOTADO
        }
    }

    // Relato sozinho vale 1. Cada confirmação de outro passageiro soma 0,5, até no máximo 3.
    public double fatorConfianca() {
        double fator = 1 + 0.5 * confirmacoes;
        if (fator > 3) {
            fator = 3;
        }
        return fator;
    }

    // Lotação não quer dizer que o trem está lento
    public boolean indicaLentidao() {
        return !tipo.equals("LOTADO");
    }
}
