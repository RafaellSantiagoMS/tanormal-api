package br.com.fiap.dao;

import br.com.fiap.conexoes.ConexaoFactory;
import br.com.fiap.entities.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsuarioDAO {

    public Connection minhaConexao;

    // metodo construtor com parâmetro vazio
    public UsuarioDAO() throws SQLException, ClassNotFoundException {
        super();
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    // Insert (o id vem da sequence SEQ_TN_USUARIO)
    public int inserir(Usuario usuario) throws SQLException {
        int id = proximoId();

        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Insert into TN_USUARIO (ID_USUARIO, NM_USUARIO, DS_EMAIL, DT_CADASTRO) values (?, ?, ?, SYSDATE)");
        stmt.setInt(1, id);
        stmt.setString(2, usuario.getNome());
        stmt.setString(3, usuario.getEmail());

        stmt.execute();
        stmt.close();

        return id;
    }

    // Delete
    public void deletar(int id) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Delete from TN_USUARIO where ID_USUARIO = ?");
        stmt.setInt(1, id);

        stmt.execute();
        stmt.close();
    }

    // UpDate
    public void atualizar(Usuario usuario) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Update TN_USUARIO set NM_USUARIO = ?, DS_EMAIL = ? where ID_USUARIO = ?");
        stmt.setString(1, usuario.getNome());
        stmt.setString(2, usuario.getEmail());
        stmt.setInt(3, usuario.getId());

        stmt.executeUpdate();
        stmt.close();
    }

    // Select
    public ArrayList<Usuario> selecionar() throws SQLException {
        ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>();
        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Select ID_USUARIO, NM_USUARIO, DS_EMAIL, TO_CHAR(DT_CADASTRO, 'DD/MM/YYYY HH24:MI') " +
                        "from TN_USUARIO order by ID_USUARIO");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            listaUsuarios.add(montarUsuario(rs));
        }

        rs.close();
        stmt.close();
        return listaUsuarios;
    }

    // Buscar por id
    public Usuario buscarPorId(int id) throws SQLException {
        Usuario usuario = null;

        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Select ID_USUARIO, NM_USUARIO, DS_EMAIL, TO_CHAR(DT_CADASTRO, 'DD/MM/YYYY HH24:MI') " +
                        "from TN_USUARIO where ID_USUARIO = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            usuario = montarUsuario(rs);
        }

        rs.close();
        stmt.close();
        return usuario;
    }

    // Buscar por e-mail (para não deixar cadastrar o mesmo e-mail duas vezes)
    public Usuario buscarPorEmail(String email) throws SQLException {
        Usuario usuario = null;

        PreparedStatement stmt = minhaConexao.prepareStatement
                ("Select ID_USUARIO, NM_USUARIO, DS_EMAIL, TO_CHAR(DT_CADASTRO, 'DD/MM/YYYY HH24:MI') " +
                        "from TN_USUARIO where LOWER(DS_EMAIL) = LOWER(?)");
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            usuario = montarUsuario(rs);
        }

        rs.close();
        stmt.close();
        return usuario;
    }

    // Fecha a conexão com o banco (chamado no finally dos BOs)
    public void fecharConexao() throws SQLException {
        minhaConexao.close();
    }

    private int proximoId() throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement("Select SEQ_TN_USUARIO.NEXTVAL from DUAL");
        ResultSet rs = stmt.executeQuery();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
        stmt.close();
        return id;
    }

    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt(1));
        usuario.setNome(rs.getString(2));
        usuario.setEmail(rs.getString(3));
        usuario.setDataCadastro(rs.getString(4));
        return usuario;
    }
}
