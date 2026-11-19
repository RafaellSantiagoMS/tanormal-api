package br.com.fiap.dao;

import br.com.fiap.conexoes.ConexaoFactory;
import br.com.fiap.entities.Relato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RelatoDAO {

    public Connection minhaConexao;

    // A data e os minutos desde o envio são calculados pelo próprio Oracle (SYSDATE)
    private static final String SELECT = "Select R.ID_RELATO, R.ID_USUARIO, R.ID_LINHA, R.ID_ESTACAO, E.NM_ESTACAO, " +
            "R.DS_TIPO, R.DS_COMENTARIO, TO_CHAR(R.DT_RELATO, 'DD/MM/YYYY HH24:MI'), R.NR_CONFIRMACOES, " +
            "ROUND((SYSDATE - R.DT_RELATO) * 24 * 60) " +
            "from TN_RELATO R inner join TN_ESTACAO E on R.ID_ESTACAO = E.ID_ESTACAO ";

    // metodo construtor com parâmetro vazio
    public RelatoDAO() throws SQLException, ClassNotFoundException {
        super();
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    // Insert (a data é a hora do envio e começa sem confirmações)
    public int inserir(Relato relato) throws SQLException {
        int id = proximoId();

        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Insert into TN_RELATO (ID_RELATO, ID_USUARIO, ID_LINHA, ID_ESTACAO, DS_TIPO, DS_COMENTARIO, " +
                        "DT_RELATO, NR_CONFIRMACOES) values (?, ?, ?, ?, ?, ?, SYSDATE, 0)");
        stmt.setInt(1, id);
        stmt.setInt(2, relato.getIdUsuario());
        stmt.setInt(3, relato.getIdLinha());
        stmt.setInt(4, relato.getIdEstacao());
        stmt.setString(5, relato.getTipo());
        stmt.setString(6, relato.getComentario());

        stmt.execute();
        stmt.close();

        return id;
    }

    // Delete
    public void deletar(int id) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Delete from TN_RELATO where ID_RELATO = ?");
        stmt.setInt(1, id);

        stmt.execute();
        stmt.close();
    }

    // UpDate (corrigir linha, estação, tipo ou comentário)
    public void atualizar(Relato relato) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Update TN_RELATO set ID_LINHA = ?, ID_ESTACAO = ?, DS_TIPO = ?, DS_COMENTARIO = ? where ID_RELATO = ?");
        stmt.setInt(1, relato.getIdLinha());
        stmt.setInt(2, relato.getIdEstacao());
        stmt.setString(3, relato.getTipo());
        stmt.setString(4, relato.getComentario());
        stmt.setInt(5, relato.getId());

        stmt.executeUpdate();
        stmt.close();
    }

    // Outro passageiro confirmou o relato
    public void confirmar(int id) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Update TN_RELATO set NR_CONFIRMACOES = NR_CONFIRMACOES + 1 where ID_RELATO = ?");
        stmt.setInt(1, id);

        stmt.executeUpdate();
        stmt.close();
    }

    // Select (mais recentes primeiro)
    public ArrayList<Relato> selecionar() throws SQLException {
        ArrayList<Relato> listaRelatos = new ArrayList<Relato>();
        PreparedStatement stmt = minhaConexao.prepareStatement
                (SELECT + "order by R.DT_RELATO desc");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            listaRelatos.add(montarRelato(rs));
        }

        rs.close();
        stmt.close();
        return listaRelatos;
    }

    // Relatos de uma linha (mais recentes primeiro)
    public ArrayList<Relato> selecionarPorLinha(int idLinha) throws SQLException {
        ArrayList<Relato> listaRelatos = new ArrayList<Relato>();
        PreparedStatement stmt = minhaConexao.prepareStatement
                (SELECT + "where R.ID_LINHA = ? order by R.DT_RELATO desc");
        stmt.setInt(1, idLinha);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            listaRelatos.add(montarRelato(rs));
        }

        rs.close();
        stmt.close();
        return listaRelatos;
    }

    // Só os relatos que ainda valem para o aviso (enviados nos últimos 30 minutos)
    public ArrayList<Relato> selecionarValidosPorLinha(int idLinha) throws SQLException {
        ArrayList<Relato> listaRelatos = new ArrayList<Relato>();
        PreparedStatement stmt = minhaConexao.prepareStatement
                (SELECT + "where R.ID_LINHA = ? and R.DT_RELATO > SYSDATE - (? / (24 * 60)) order by R.DT_RELATO desc");
        stmt.setInt(1, idLinha);
        stmt.setInt(2, Relato.VALIDADE_MINUTOS);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            listaRelatos.add(montarRelato(rs));
        }

        rs.close();
        stmt.close();
        return listaRelatos;
    }

    // Buscar por id
    public Relato buscarPorId(int id) throws SQLException {
        Relato relato = null;

        PreparedStatement stmt = minhaConexao.prepareStatement
                (SELECT + "where R.ID_RELATO = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            relato = montarRelato(rs);
        }

        rs.close();
        stmt.close();
        return relato;
    }

    // Fecha a conexão com o banco (chamado no finally dos BOs)
    public void fecharConexao() throws SQLException {
        minhaConexao.close();
    }

    private int proximoId() throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement("Select SEQ_TN_RELATO.NEXTVAL from DUAL");
        ResultSet rs = stmt.executeQuery();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
        stmt.close();
        return id;
    }

    private Relato montarRelato(ResultSet rs) throws SQLException {
        Relato relato = new Relato();
        relato.setId(rs.getInt(1));
        relato.setIdUsuario(rs.getInt(2));
        relato.setIdLinha(rs.getInt(3));
        relato.setIdEstacao(rs.getInt(4));
        relato.setNomeEstacao(rs.getString(5));
        relato.setTipo(rs.getString(6));
        relato.setComentario(rs.getString(7));
        relato.setDataHora(rs.getString(8));
        relato.setConfirmacoes(rs.getInt(9));
        relato.setMinutosDesdeEnvio(rs.getInt(10));
        return relato;
    }
}
