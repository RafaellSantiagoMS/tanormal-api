package br.com.fiap.dao;

import br.com.fiap.conexoes.ConexaoFactory;
import br.com.fiap.entities.Trajeto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TrajetoDAO {

    public Connection minhaConexao;

    // Junta o trajeto com as estações de origem e destino para já devolver os nomes
    private static final String SELECT = "Select T.ID_TRAJETO, T.ID_USUARIO, T.NM_TRAJETO, " +
            "T.ID_ESTACAO_ORIGEM, O.NM_ESTACAO, T.ID_ESTACAO_DESTINO, D.NM_ESTACAO, " +
            "T.HR_SAIDA, T.NR_TEMPO_NORMAL_MIN " +
            "from TN_TRAJETO T " +
            "inner join TN_ESTACAO O on T.ID_ESTACAO_ORIGEM = O.ID_ESTACAO " +
            "inner join TN_ESTACAO D on T.ID_ESTACAO_DESTINO = D.ID_ESTACAO ";

    // metodo construtor com parâmetro vazio
    public TrajetoDAO() throws SQLException, ClassNotFoundException {
        super();
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    // Insert do trajeto e das linhas dele, na ordem da viagem
    public int inserir(Trajeto trajeto) throws SQLException {
        int id = proximoId();

        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Insert into TN_TRAJETO (ID_TRAJETO, ID_USUARIO, NM_TRAJETO, ID_ESTACAO_ORIGEM, ID_ESTACAO_DESTINO, " +
                        "HR_SAIDA, NR_TEMPO_NORMAL_MIN) values (?, ?, ?, ?, ?, ?, ?)");
        stmt.setInt(1, id);
        stmt.setInt(2, trajeto.getIdUsuario());
        stmt.setString(3, trajeto.getNome());
        stmt.setInt(4, trajeto.getIdOrigem());
        stmt.setInt(5, trajeto.getIdDestino());
        stmt.setString(6, trajeto.getHorarioSaida());
        stmt.setInt(7, trajeto.getTempoNormalMinutos());

        stmt.execute();
        stmt.close();

        inserirLinhas(id, trajeto.getIdsLinhas());
        return id;
    }

    // Delete (primeiro as linhas do trajeto, depois o trajeto)
    public void deletar(int id) throws SQLException {
        deletarLinhas(id);

        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Delete from TN_TRAJETO where ID_TRAJETO = ?");
        stmt.setInt(1, id);

        stmt.execute();
        stmt.close();
    }

    // UpDate (as linhas são apagadas e gravadas de novo na ordem nova)
    public void atualizar(Trajeto trajeto) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Update TN_TRAJETO set NM_TRAJETO = ?, ID_ESTACAO_ORIGEM = ?, ID_ESTACAO_DESTINO = ?, " +
                        "HR_SAIDA = ?, NR_TEMPO_NORMAL_MIN = ? where ID_TRAJETO = ?");
        stmt.setString(1, trajeto.getNome());
        stmt.setInt(2, trajeto.getIdOrigem());
        stmt.setInt(3, trajeto.getIdDestino());
        stmt.setString(4, trajeto.getHorarioSaida());
        stmt.setInt(5, trajeto.getTempoNormalMinutos());
        stmt.setInt(6, trajeto.getId());

        stmt.executeUpdate();
        stmt.close();

        deletarLinhas(trajeto.getId());
        inserirLinhas(trajeto.getId(), trajeto.getIdsLinhas());
    }

    // Select
    public ArrayList<Trajeto> selecionar() throws SQLException {
        ArrayList<Trajeto> listaTrajetos = new ArrayList<Trajeto>();
        PreparedStatement stmt = minhaConexao.prepareStatement
                (SELECT + "order by T.ID_TRAJETO");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            listaTrajetos.add(montarTrajeto(rs));
        }

        rs.close();
        stmt.close();

        for (Trajeto trajeto : listaTrajetos) {
            trajeto.setIdsLinhas(selecionarLinhas(trajeto.getId()));
        }
        return listaTrajetos;
    }

    // Trajetos de um usuário
    public ArrayList<Trajeto> selecionarPorUsuario(int idUsuario) throws SQLException {
        ArrayList<Trajeto> listaTrajetos = new ArrayList<Trajeto>();
        PreparedStatement stmt = minhaConexao.prepareStatement
                (SELECT + "where T.ID_USUARIO = ? order by T.ID_TRAJETO");
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            listaTrajetos.add(montarTrajeto(rs));
        }

        rs.close();
        stmt.close();

        for (Trajeto trajeto : listaTrajetos) {
            trajeto.setIdsLinhas(selecionarLinhas(trajeto.getId()));
        }
        return listaTrajetos;
    }

    // Buscar por id
    public Trajeto buscarPorId(int id) throws SQLException {
        Trajeto trajeto = null;

        PreparedStatement stmt = minhaConexao.prepareStatement
                (SELECT + "where T.ID_TRAJETO = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            trajeto = montarTrajeto(rs);
        }

        rs.close();
        stmt.close();

        if (trajeto != null) {
            trajeto.setIdsLinhas(selecionarLinhas(id));
        }
        return trajeto;
    }

    // Fecha a conexão com o banco (chamado no finally dos BOs)
    public void fecharConexao() throws SQLException {
        minhaConexao.close();
    }

    // Ids das linhas do trajeto, na ordem da viagem
    private ArrayList<Integer> selecionarLinhas(int idTrajeto) throws SQLException {
        ArrayList<Integer> idsLinhas = new ArrayList<Integer>();
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Select ID_LINHA from TN_TRAJETO_LINHA where ID_TRAJETO = ? order by NR_ORDEM");
        stmt.setInt(1, idTrajeto);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            idsLinhas.add(rs.getInt(1));
        }

        rs.close();
        stmt.close();
        return idsLinhas;
    }

    private void inserirLinhas(int idTrajeto, ArrayList<Integer> idsLinhas) throws SQLException {
        int ordem = 1;
        for (Integer idLinha : idsLinhas) {
            PreparedStatement stmt = minhaConexao.prepareStatement
                    ("Insert into TN_TRAJETO_LINHA (ID_TRAJETO, NR_ORDEM, ID_LINHA) values (?, ?, ?)");
            stmt.setInt(1, idTrajeto);
            stmt.setInt(2, ordem);
            stmt.setInt(3, idLinha);
            stmt.execute();
            stmt.close();
            ordem++;
        }
    }

    private void deletarLinhas(int idTrajeto) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Delete from TN_TRAJETO_LINHA where ID_TRAJETO = ?");
        stmt.setInt(1, idTrajeto);
        stmt.execute();
        stmt.close();
    }

    private int proximoId() throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement("Select SEQ_TN_TRAJETO.NEXTVAL from DUAL");
        ResultSet rs = stmt.executeQuery();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
        stmt.close();
        return id;
    }

    private Trajeto montarTrajeto(ResultSet rs) throws SQLException {
        Trajeto trajeto = new Trajeto();
        trajeto.setId(rs.getInt(1));
        trajeto.setIdUsuario(rs.getInt(2));
        trajeto.setNome(rs.getString(3));
        trajeto.setIdOrigem(rs.getInt(4));
        trajeto.setNomeOrigem(rs.getString(5));
        trajeto.setIdDestino(rs.getInt(6));
        trajeto.setNomeDestino(rs.getString(7));
        trajeto.setHorarioSaida(rs.getString(8));
        trajeto.setTempoNormalMinutos(rs.getInt(9));
        return trajeto;
    }
}
