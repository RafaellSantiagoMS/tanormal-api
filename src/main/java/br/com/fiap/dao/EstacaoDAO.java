package br.com.fiap.dao;

import br.com.fiap.conexoes.ConexaoFactory;
import br.com.fiap.entities.Estacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EstacaoDAO {

    public Connection minhaConexao;

    // Junta a estação com a linha para já devolver o nome da linha
    private static final String SELECT = "Select E.ID_ESTACAO, E.NM_ESTACAO, E.ID_LINHA, " +
            "'Linha ' || L.NR_LINHA || '-' || L.NM_LINHA " +
            "from TN_ESTACAO E inner join TN_LINHA L on E.ID_LINHA = L.ID_LINHA ";

    // metodo construtor com parâmetro vazio
    public EstacaoDAO() throws SQLException, ClassNotFoundException {
        super();
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    // Insert
    public int inserir(Estacao estacao) throws SQLException {
        int id = proximoId();

        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Insert into TN_ESTACAO (ID_ESTACAO, NM_ESTACAO, ID_LINHA) values (?, ?, ?)");
        stmt.setInt(1, id);
        stmt.setString(2, estacao.getNome());
        stmt.setInt(3, estacao.getIdLinha());

        stmt.execute();
        stmt.close();

        return id;
    }

    // Delete
    public void deletar(int id) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Delete from TN_ESTACAO where ID_ESTACAO = ?");
        stmt.setInt(1, id);

        stmt.execute();
        stmt.close();
    }

    // UpDate
    public void atualizar(Estacao estacao) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Update TN_ESTACAO set NM_ESTACAO = ?, ID_LINHA = ? where ID_ESTACAO = ?");
        stmt.setString(1, estacao.getNome());
        stmt.setInt(2, estacao.getIdLinha());
        stmt.setInt(3, estacao.getId());

        stmt.executeUpdate();
        stmt.close();
    }

    // Select
    public ArrayList<Estacao> selecionar() throws SQLException {
        ArrayList<Estacao> listaEstacoes = new ArrayList<Estacao>();
        PreparedStatement stmt = minhaConexao.prepareStatement
                (SELECT + "order by E.ID_ESTACAO");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            listaEstacoes.add(montarEstacao(rs));
        }

        rs.close();
        stmt.close();
        return listaEstacoes;
    }

    // Estações de uma linha, na ordem em que foram cadastradas
    public ArrayList<Estacao> selecionarPorLinha(int idLinha) throws SQLException {
        ArrayList<Estacao> listaEstacoes = new ArrayList<Estacao>();
        PreparedStatement stmt = minhaConexao.prepareStatement
                (SELECT + "where E.ID_LINHA = ? order by E.ID_ESTACAO");
        stmt.setInt(1, idLinha);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            listaEstacoes.add(montarEstacao(rs));
        }

        rs.close();
        stmt.close();
        return listaEstacoes;
    }

    // Buscar por id
    public Estacao buscarPorId(int id) throws SQLException {
        Estacao estacao = null;

        PreparedStatement stmt = minhaConexao.prepareStatement
                (SELECT + "where E.ID_ESTACAO = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            estacao = montarEstacao(rs);
        }

        rs.close();
        stmt.close();
        return estacao;
    }

    // Fecha a conexão com o banco (chamado no finally dos BOs)
    public void fecharConexao() throws SQLException {
        minhaConexao.close();
    }

    private int proximoId() throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement("Select SEQ_TN_ESTACAO.NEXTVAL from DUAL");
        ResultSet rs = stmt.executeQuery();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
        stmt.close();
        return id;
    }

    private Estacao montarEstacao(ResultSet rs) throws SQLException {
        Estacao estacao = new Estacao();
        estacao.setId(rs.getInt(1));
        estacao.setNome(rs.getString(2));
        estacao.setIdLinha(rs.getInt(3));
        estacao.setNomeLinha(rs.getString(4));
        return estacao;
    }
}
