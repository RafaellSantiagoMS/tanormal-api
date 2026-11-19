package br.com.fiap.bo;

import br.com.fiap.dao.LinhaDAO;
import br.com.fiap.dao.RelatoDAO;
import br.com.fiap.entities.Aviso;
import br.com.fiap.entities.Linha;
import br.com.fiap.entities.Relato;
import br.com.fiap.entities.SituacaoLinha;
import br.com.fiap.entities.Trajeto;
import br.com.fiap.excecoes.NaoEncontradoException;

import java.sql.SQLException;
import java.util.ArrayList;

/*
 * Monta o aviso "Sua viagem deve atrasar uns X min" de um trajeto.
 *
 * Regra usada no protótipo (simples e fácil de explicar):
 * 1. O status oficial de cada linha vira minutos: Normal 0, Velocidade reduzida 10, Operação parcial 20, Paralisada 40.
 * 2. Os relatos dos últimos 30 minutos somam minutos: Parado 8, Lento 4, Lotado 2.
 *    Cada confirmação aumenta o peso do relato (até 3 vezes) e os relatos somam no máximo 25 min por linha.
 * 3. Em cada linha vale o maior dos dois, para não contar o mesmo problema duas vezes. Depois soma as linhas.
 * 4. Com chuva, o atraso aumenta 20%.
 * 5. A partir de 20 minutos de atraso o aviso sugere rota alternativa (mesmo corte do app e do Data Science).
 * 6. Se o site diz NORMAL mas os passageiros estão relatando lentidão, o aviso alerta essa divergência.
 */
public class AvisoBo {

    public static final int MAXIMO_MINUTOS_RELATOS = 25;
    public static final int LIMITE_ROTA_ALTERNATIVA = 20;
    public static final double AUMENTO_CHUVA = 1.2;
    // Quantos relatos de lentidão (já contando as confirmações) contestam um status NORMAL
    public static final double LIMITE_DIVERGENCIA = 2;

    public Aviso gerarAvisoBo(int idTrajeto, boolean chovendo)
            throws SQLException, ClassNotFoundException, NaoEncontradoException {
        Trajeto trajeto = new TrajetoBo().buscarPorIdBo(idTrajeto);

        Aviso aviso = new Aviso();
        int atrasoTotal = 0;
        boolean problemaOficial = false;
        double pesoLentidaoTotal = 0;

        LinhaDAO linhaDao = new LinhaDAO();
        RelatoDAO relatoDao = new RelatoDAO();
        try {
            for (Integer idLinha : trajeto.getIdsLinhas()) {
                Linha linha = linhaDao.buscarPorId(idLinha);
                ArrayList<Relato> relatos = relatoDao.selecionarValidosPorLinha(idLinha);

                int minutosStatus = linha.atrasoPeloStatus();
                int minutosRelatos = minutosPelosRelatos(relatos);
                double pesoLentidao = pesoDeLentidao(relatos);

                // Status e relatos falam do mesmo problema: vale o maior
                int atrasoLinha = minutosStatus;
                if (minutosRelatos > atrasoLinha) {
                    atrasoLinha = minutosRelatos;
                }

                SituacaoLinha situacao = new SituacaoLinha();
                situacao.setIdLinha(linha.getId());
                situacao.setNomeLinha(linha.getNomeCompleto());
                situacao.setStatusOficial(linha.getStatusOficial());
                situacao.setMinutosPeloStatus(minutosStatus);
                situacao.setMinutosPelosRelatos(minutosRelatos);
                situacao.setQuantidadeRelatos(relatos.size());
                situacao.setAtrasoMinutos(atrasoLinha);
                situacao.setDivergencia(linha.statusNormal() && pesoLentidao >= LIMITE_DIVERGENCIA);
                aviso.getLinhas().add(situacao);

                atrasoTotal = atrasoTotal + atrasoLinha;
                if (!linha.statusNormal()) {
                    problemaOficial = true;
                }
                pesoLentidaoTotal = pesoLentidaoTotal + pesoLentidao;
            }
        } finally {
            linhaDao.fecharConexao();
            relatoDao.fecharConexao();
        }

        if (chovendo) {
            atrasoTotal = (int) Math.round(atrasoTotal * AUMENTO_CHUVA);
        }

        aviso.setIdTrajeto(trajeto.getId());
        aviso.setTrajeto(trajeto.getNome());
        aviso.setAtrasoEstimadoMinutos(atrasoTotal);
        aviso.setChanceLentidaoPercentual(chanceDeLentidao(problemaOficial, pesoLentidaoTotal, chovendo));
        aviso.setHorarioSaidaHabitual(trajeto.getHorarioSaida());
        aviso.setHorarioSaidaSugerido(trajeto.calcularSaidaSugerida(atrasoTotal));
        aviso.setChegadaPrevista(trajeto.calcularChegadaPrevista(atrasoTotal));
        aviso.setRotaAlternativaRecomendada(atrasoTotal >= LIMITE_ROTA_ALTERNATIVA);
        aviso.setChuvaConsiderada(chovendo);
        aviso.setMensagem(montarMensagem(aviso));
        return aviso;
    }

    // Soma o peso de cada relato, multiplicado pelas confirmações, com teto de 25 min
    private int minutosPelosRelatos(ArrayList<Relato> relatos) {
        double soma = 0;
        for (Relato relato : relatos) {
            soma = soma + relato.pesoEmMinutos() * relato.fatorConfianca();
        }
        int minutos = (int) Math.round(soma);
        if (minutos > MAXIMO_MINUTOS_RELATOS) {
            minutos = MAXIMO_MINUTOS_RELATOS;
        }
        return minutos;
    }

    // Quantos relatos falam de trem lento ou parado (lotação não conta)
    private double pesoDeLentidao(ArrayList<Relato> relatos) {
        double soma = 0;
        for (Relato relato : relatos) {
            if (relato.indicaLentidao()) {
                soma = soma + relato.fatorConfianca();
            }
        }
        return soma;
    }

    // Chance de pegar lentidão: alta se a operadora já informou problema; senão sobe com os relatos
    private int chanceDeLentidao(boolean problemaOficial, double pesoLentidao, boolean chovendo) {
        int chance;
        if (problemaOficial) {
            chance = 90;
        } else {
            chance = (int) (10 + 15 * pesoLentidao);
            if (chance > 85) {
                chance = 85;
            }
        }
        if (chovendo) {
            chance = chance + 10;
        }
        if (chance > 95) {
            chance = 95;
        }
        return chance;
    }

    private String montarMensagem(Aviso aviso) {
        if (aviso.getAtrasoEstimadoMinutos() == 0) {
            return "Tá normal! Pode sair às " + aviso.getHorarioSaidaHabitual() + " como sempre.";
        }

        String mensagem = "Sua viagem deve atrasar uns " + aviso.getAtrasoEstimadoMinutos() + " min. " +
                "Saia às " + aviso.getHorarioSaidaSugerido();
        if (aviso.isRotaAlternativaRecomendada()) {
            mensagem = mensagem + " ou vá por uma rota alternativa.";
        } else {
            mensagem = mensagem + " para chegar no horário.";
        }

        for (SituacaoLinha situacao : aviso.getLinhas()) {
            if (situacao.isDivergencia()) {
                mensagem = mensagem + " Atenção: o site diz operação normal na " + situacao.getNomeLinha() +
                        ", mas passageiros estão relatando lentidão.";
            }
        }
        return mensagem;
    }
}
