package br.com.fiap.dao;

import br.com.fiap.conexoes.ConexaoFactory;
import br.com.fiap.entities.Ocorrencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OcorrenciaDAO {

    public Connection minhaConexao;

    // Junta a ocorrência com a linha para já devolver o nome da linha
    private static final String SELECT = "Select O.ID_OCORRENCIA, O.ID_LINHA, 'Linha ' || L.NR_LINHA || '-' || L.NM_LINHA, " +
            "O.DS_STATUS, O.DS_FONTE, O.DS_OCORRENCIA, O.ST_CHUVA, " +
            "TO_CHAR(O.DT_INICIO, 'DD/MM/YYYY HH24:MI'), TO_CHAR(O.DT_FIM, 'DD/MM/YYYY HH24:MI') " +
            "from TN_OCORRENCIA O inner join TN_LINHA L on O.ID_LINHA = L.ID_LINHA ";

    // metodo construtor com parâmetro vazio
    public OcorrenciaDAO() throws SQLException, ClassNotFoundException {
        super();
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    // Insert (a ocorrência começa agora e fica aberta até ser encerrada)
    public int inserir(Ocorrencia ocorrencia) throws SQLException {
        int id = proximoId();

        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Insert into TN_OCORRENCIA (ID_OCORRENCIA, ID_LINHA, DS_STATUS, DS_FONTE, DS_OCORRENCIA, ST_CHUVA, DT_INICIO) " +
                        "values (?, ?, ?, ?, ?, ?, SYSDATE)");
        stmt.setInt(1, id);
        stmt.setInt(2, ocorrencia.getIdLinha());
        stmt.setString(3, ocorrencia.getStatus());
        stmt.setString(4, ocorrencia.getFonte());
        stmt.setString(5, ocorrencia.getDescricao());
        stmt.setInt(6, chuvaParaNumero(ocorrencia.isChuva()));

        stmt.execute();
        stmt.close();

        return id;
    }

    // Delete
    public void deletar(int id) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Delete from TN_OCORRENCIA where ID_OCORRENCIA = ?");
        stmt.setInt(1, id);

        stmt.execute();
        stmt.close();
    }

    // UpDate (status, descrição e chuva)
    public void atualizar(Ocorrencia ocorrencia) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Update TN_OCORRENCIA set DS_STATUS = ?, DS_OCORRENCIA = ?, ST_CHUVA = ? where ID_OCORRENCIA = ?");
        stmt.setString(1, ocorrencia.getStatus());
        stmt.setString(2, ocorrencia.getDescricao());
        stmt.setInt(3, chuvaParaNumero(ocorrencia.isChuva()));
        stmt.setInt(4, ocorrencia.getId());

        stmt.executeUpdate();
        stmt.close();
    }

    // Encerrar: grava a data de fim
    public void encerrar(int id) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Update TN_OCORRENCIA set DT_FIM = SYSDATE where ID_OCORRENCIA = ?");
        stmt.setInt(1, id);

        stmt.executeUpdate();
        stmt.close();
    }

    // Select (mais recentes primeiro)
    public ArrayList<Ocorrencia> selecionar() throws SQLException {
        return selecionarComFiltro("order by O.DT_INICIO desc", 0);
    }

    // Só as que ainda não foram encerradas
    public ArrayList<Ocorrencia> selecionarAbertas() throws SQLException {
        return selecionarComFiltro("where O.DT_FIM is null order by O.DT_INICIO desc", 0);
    }

    // Histórico de uma linha
    public ArrayList<Ocorrencia> selecionarPorLinha(int idLinha) throws SQLException {
        return selecionarComFiltro("where O.ID_LINHA = ? order by O.DT_INICIO desc", idLinha);
    }

    // Quantas ocorrências OFICIAIS continuam abertas na linha, sem contar a informada
    public int contarOficiaisAbertas(int idLinha, int idIgnorado) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Select count(*) from TN_OCORRENCIA where ID_LINHA = ? and DS_FONTE = 'OFICIAL' " +
                        "and DT_FIM is null and ID_OCORRENCIA <> ?");
        stmt.setInt(1, idLinha);
        stmt.setInt(2, idIgnorado);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        int quantidade = rs.getInt(1);
        rs.close();
        stmt.close();
        return quantidade;
    }

    // Buscar por id
    public Ocorrencia buscarPorId(int id) throws SQLException {
        Ocorrencia ocorrencia = null;

        PreparedStatement stmt = minhaConexao.prepareStatement
                (SELECT + "where O.ID_OCORRENCIA = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            ocorrencia = montarOcorrencia(rs);
        }

        rs.close();
        stmt.close();
        return ocorrencia;
    }

    // Fecha a conexão com o banco (chamado no finally dos BOs)
    public void fecharConexao() throws SQLException {
        minhaConexao.close();
    }

    // Monta a lista; se o filtro tiver "?", o parâmetro é o id da linha
    private ArrayList<Ocorrencia> selecionarComFiltro(String filtro, int idLinha) throws SQLException {
        ArrayList<Ocorrencia> listaOcorrencias = new ArrayList<Ocorrencia>();
        PreparedStatement stmt = minhaConexao.prepareStatement(SELECT + filtro);
        if (filtro.contains("?")) {
            stmt.setInt(1, idLinha);
        }
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            listaOcorrencias.add(montarOcorrencia(rs));
        }

        rs.close();
        stmt.close();
        return listaOcorrencias;
    }

    private int proximoId() throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement("Select SEQ_TN_OCORRENCIA.NEXTVAL from DUAL");
        ResultSet rs = stmt.executeQuery();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
        stmt.close();
        return id;
    }

    // No banco a chuva é guardada como 1 (choveu) ou 0 (não choveu)
    private int chuvaParaNumero(boolean chuva) {
        if (chuva) {
            return 1;
        }
        return 0;
    }

    private Ocorrencia montarOcorrencia(ResultSet rs) throws SQLException {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(rs.getInt(1));
        ocorrencia.setIdLinha(rs.getInt(2));
        ocorrencia.setNomeLinha(rs.getString(3));
        ocorrencia.setStatus(rs.getString(4));
        ocorrencia.setFonte(rs.getString(5));
        ocorrencia.setDescricao(rs.getString(6));
        ocorrencia.setChuva(rs.getInt(7) == 1);
        ocorrencia.setInicio(rs.getString(8));
        ocorrencia.setFim(rs.getString(9));
        return ocorrencia;
    }
}
