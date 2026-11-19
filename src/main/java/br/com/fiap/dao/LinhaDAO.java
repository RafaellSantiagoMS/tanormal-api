package br.com.fiap.dao;

import br.com.fiap.conexoes.ConexaoFactory;
import br.com.fiap.entities.Linha;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LinhaDAO {

    public Connection minhaConexao;

    private static final String CAMPOS = "ID_LINHA, NR_LINHA, NM_LINHA, DS_OPERADORA, DS_STATUS_OFICIAL, " +
            "TO_CHAR(DT_ATUALIZACAO_STATUS, 'DD/MM/YYYY HH24:MI')";

    // metodo construtor com parâmetro vazio
    public LinhaDAO() throws SQLException, ClassNotFoundException {
        super();
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    // Insert (toda linha nova começa com status NORMAL)
    public int inserir(Linha linha) throws SQLException {
        int id = proximoId();

        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Insert into TN_LINHA (ID_LINHA, NR_LINHA, NM_LINHA, DS_OPERADORA, DS_STATUS_OFICIAL, DT_ATUALIZACAO_STATUS) " +
                        "values (?, ?, ?, ?, 'NORMAL', SYSDATE)");
        stmt.setInt(1, id);
        stmt.setInt(2, linha.getNumero());
        stmt.setString(3, linha.getNome());
        stmt.setString(4, linha.getOperadora());

        stmt.execute();
        stmt.close();

        return id;
    }

    // Delete
    public void deletar(int id) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Delete from TN_LINHA where ID_LINHA = ?");
        stmt.setInt(1, id);

        stmt.execute();
        stmt.close();
    }

    // UpDate dos dados da linha (o status tem um método próprio)
    public void atualizar(Linha linha) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Update TN_LINHA set NR_LINHA = ?, NM_LINHA = ?, DS_OPERADORA = ? where ID_LINHA = ?");
        stmt.setInt(1, linha.getNumero());
        stmt.setString(2, linha.getNome());
        stmt.setString(3, linha.getOperadora());
        stmt.setInt(4, linha.getId());

        stmt.executeUpdate();
        stmt.close();
    }

    // UpDate do status oficial (usado pelo leitor do site da operadora e pelas ocorrências oficiais)
    public void atualizarStatus(int id, String status) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Update TN_LINHA set DS_STATUS_OFICIAL = ?, DT_ATUALIZACAO_STATUS = SYSDATE where ID_LINHA = ?");
        stmt.setString(1, status);
        stmt.setInt(2, id);

        stmt.executeUpdate();
        stmt.close();
    }

    // Select
    public ArrayList<Linha> selecionar() throws SQLException {
        ArrayList<Linha> listaLinhas = new ArrayList<Linha>();
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Select " + CAMPOS + " from TN_LINHA order by ID_LINHA");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            listaLinhas.add(montarLinha(rs));
        }

        rs.close();
        stmt.close();
        return listaLinhas;
    }

    // Buscar por id
    public Linha buscarPorId(int id) throws SQLException {
        Linha linha = null;

        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Select " + CAMPOS + " from TN_LINHA where ID_LINHA = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            linha = montarLinha(rs);
        }

        rs.close();
        stmt.close();
        return linha;
    }

    // Buscar pelo número da linha (ex.: 12), que não pode repetir
    public Linha buscarPorNumero(int numero) throws SQLException {
        Linha linha = null;

        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Select " + CAMPOS + " from TN_LINHA where NR_LINHA = ?");
        stmt.setInt(1, numero);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            linha = montarLinha(rs);
        }

        rs.close();
        stmt.close();
        return linha;
    }

    // Fecha a conexão com o banco (chamado no finally dos BOs)
    public void fecharConexao() throws SQLException {
        minhaConexao.close();
    }

    private int proximoId() throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement("Select SEQ_TN_LINHA.NEXTVAL from DUAL");
        ResultSet rs = stmt.executeQuery();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
        stmt.close();
        return id;
    }

    private Linha montarLinha(ResultSet rs) throws SQLException {
        Linha linha = new Linha();
        linha.setId(rs.getInt(1));
        linha.setNumero(rs.getInt(2));
        linha.setNome(rs.getString(3));
        linha.setOperadora(rs.getString(4));
        linha.setStatusOficial(rs.getString(5));
        linha.setDataAtualizacaoStatus(rs.getString(6));
        return linha;
    }
}
