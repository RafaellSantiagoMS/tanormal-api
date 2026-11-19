package br.com.fiap.entities;

import java.util.ArrayList;

// Caminho que o passageiro faz todo dia (tabelas TN_TRAJETO e TN_TRAJETO_LINHA)
public class Trajeto {

    private int id;
    private int idUsuario;
    private String nome;
    private int idOrigem;
    private String nomeOrigem;
    private int idDestino;
    private String nomeDestino;
    private ArrayList<Integer> idsLinhas = new ArrayList<Integer>();
    private String horarioSaida;
    private int tempoNormalMinutos;

    public Trajeto() {
        super();
    }

    public Trajeto(int id, int idUsuario, String nome, int idOrigem, int idDestino, ArrayList<Integer> idsLinhas, String horarioSaida, int tempoNormalMinutos) {
        super();
        this.id = id;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.idOrigem = idOrigem;
        this.idDestino = idDestino;
        this.idsLinhas = idsLinhas;
        this.horarioSaida = horarioSaida;
        this.tempoNormalMinutos = tempoNormalMinutos;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(int idOrigem) {
        this.idOrigem = idOrigem;
    }

    public String getNomeOrigem() {
        return nomeOrigem;
    }

    public void setNomeOrigem(String nomeOrigem) {
        this.nomeOrigem = nomeOrigem;
    }

    public int getIdDestino() {
        return idDestino;
    }

    public void setIdDestino(int idDestino) {
        this.idDestino = idDestino;
    }

    public String getNomeDestino() {
        return nomeDestino;
    }

    public void setNomeDestino(String nomeDestino) {
        this.nomeDestino = nomeDestino;
    }

    public ArrayList<Integer> getIdsLinhas() {
        return idsLinhas;
    }

    public void setIdsLinhas(ArrayList<Integer> idsLinhas) {
        this.idsLinhas = idsLinhas;
    }

    public String getHorarioSaida() {
        return horarioSaida;
    }

    public void setHorarioSaida(String horarioSaida) {
        this.horarioSaida = horarioSaida;
    }

    public int getTempoNormalMinutos() {
        return tempoNormalMinutos;
    }

    public void setTempoNormalMinutos(int tempoNormalMinutos) {
        this.tempoNormalMinutos = tempoNormalMinutos;
    }

    // Confere se o horário está no formato HH:mm (ex.: "06:30")
    public boolean horarioValido() {
        if (horarioSaida == null) {
            return false;
        }
        String[] partes = horarioSaida.split(":");
        if (partes.length != 2 || partes[0].length() != 2 || partes[1].length() != 2) {
            return false;
        }
        try {
            int hora = Integer.parseInt(partes[0]);
            int minuto = Integer.parseInt(partes[1]);
            return hora >= 0 && hora <= 23 && minuto >= 0 && minuto <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Horário de saída sugerido: sair mais cedo o tanto de minutos que a viagem vai atrasar
    public String calcularSaidaSugerida(int atrasoMinutos) {
        return converterParaHorario(converterParaMinutos(horarioSaida) - atrasoMinutos);
    }

    // Horário de chegada saindo no horário de sempre: saída + tempo normal + atraso
    public String calcularChegadaPrevista(int atrasoMinutos) {
        return converterParaHorario(converterParaMinutos(horarioSaida) + tempoNormalMinutos + atrasoMinutos);
    }

    // "06:30" -> 390 minutos desde a meia-noite
    private int converterParaMinutos(String horario) {
        String[] partes = horario.split(":");
        return Integer.parseInt(partes[0]) * 60 + Integer.parseInt(partes[1]);
    }

    // 390 -> "06:30" (se passar da meia-noite, volta para o começo do dia)
    private String converterParaHorario(int minutos) {
        minutos = minutos % (24 * 60);
        if (minutos < 0) {
            minutos = minutos + 24 * 60;
        }
        int hora = minutos / 60;
        int minuto = minutos % 60;

        String textoHora = String.valueOf(hora);
        if (hora < 10) {
            textoHora = "0" + textoHora;
        }
        String textoMinuto = String.valueOf(minuto);
        if (minuto < 10) {
            textoMinuto = "0" + textoMinuto;
        }
        return textoHora + ":" + textoMinuto;
    }
}
