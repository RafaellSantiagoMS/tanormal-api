package br.com.fiap.entities;

import java.util.ArrayList;

// Resposta do endpoint de aviso: quanto a viagem deve atrasar e a que horas sair
public class Aviso {

    private int idTrajeto;
    private String trajeto;
    private String mensagem;
    private int atrasoEstimadoMinutos;
    private int chanceLentidaoPercentual;
    private String horarioSaidaHabitual;
    private String horarioSaidaSugerido;
    private String chegadaPrevista;
    private boolean rotaAlternativaRecomendada;
    private boolean chuvaConsiderada;
    private ArrayList<SituacaoLinha> linhas = new ArrayList<SituacaoLinha>();

    public Aviso() {
        super();
    }

    public int getIdTrajeto() {
        return idTrajeto;
    }

    public void setIdTrajeto(int idTrajeto) {
        this.idTrajeto = idTrajeto;
    }

    public String getTrajeto() {
        return trajeto;
    }

    public void setTrajeto(String trajeto) {
        this.trajeto = trajeto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getAtrasoEstimadoMinutos() {
        return atrasoEstimadoMinutos;
    }

    public void setAtrasoEstimadoMinutos(int atrasoEstimadoMinutos) {
        this.atrasoEstimadoMinutos = atrasoEstimadoMinutos;
    }

    public int getChanceLentidaoPercentual() {
        return chanceLentidaoPercentual;
    }

    public void setChanceLentidaoPercentual(int chanceLentidaoPercentual) {
        this.chanceLentidaoPercentual = chanceLentidaoPercentual;
    }

    public String getHorarioSaidaHabitual() {
        return horarioSaidaHabitual;
    }

    public void setHorarioSaidaHabitual(String horarioSaidaHabitual) {
        this.horarioSaidaHabitual = horarioSaidaHabitual;
    }

    public String getHorarioSaidaSugerido() {
        return horarioSaidaSugerido;
    }

    public void setHorarioSaidaSugerido(String horarioSaidaSugerido) {
        this.horarioSaidaSugerido = horarioSaidaSugerido;
    }

    public String getChegadaPrevista() {
        return chegadaPrevista;
    }

    public void setChegadaPrevista(String chegadaPrevista) {
        this.chegadaPrevista = chegadaPrevista;
    }

    public boolean isRotaAlternativaRecomendada() {
        return rotaAlternativaRecomendada;
    }

    public void setRotaAlternativaRecomendada(boolean rotaAlternativaRecomendada) {
        this.rotaAlternativaRecomendada = rotaAlternativaRecomendada;
    }

    public boolean isChuvaConsiderada() {
        return chuvaConsiderada;
    }

    public void setChuvaConsiderada(boolean chuvaConsiderada) {
        this.chuvaConsiderada = chuvaConsiderada;
    }

    public ArrayList<SituacaoLinha> getLinhas() {
        return linhas;
    }

    public void setLinhas(ArrayList<SituacaoLinha> linhas) {
        this.linhas = linhas;
    }
}
