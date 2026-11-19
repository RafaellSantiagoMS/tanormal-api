package br.com.fiap.bo;

import br.com.fiap.dao.TrajetoDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.entities.Trajeto;
import br.com.fiap.entities.Usuario;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;

import java.sql.SQLException;
import java.util.ArrayList;

public class UsuarioBo {

    // Selecionar
    public ArrayList<Usuario> selecionarBo() throws SQLException, ClassNotFoundException {
        UsuarioDAO usuarioDao = new UsuarioDAO();
        try {
            return usuarioDao.selecionar();
        } finally {
            usuarioDao.fecharConexao();
        }
    }

    // Buscar por id
    public Usuario buscarPorIdBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        UsuarioDAO usuarioDao = new UsuarioDAO();
        try {
            Usuario usuario = usuarioDao.buscarPorId(id);
            if (usuario == null) {
                throw new NaoEncontradoException("Usuário " + id + " não encontrado");
            }
            return usuario;
        } finally {
            usuarioDao.fecharConexao();
        }
    }

    // Trajetos cadastrados pelo usuário
    public ArrayList<Trajeto> selecionarTrajetosBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        buscarPorIdBo(id);

        TrajetoDAO trajetoDao = new TrajetoDAO();
        try {
            return trajetoDao.selecionarPorUsuario(id);
        } finally {
            trajetoDao.fecharConexao();
        }
    }

    // Inserir
    public Usuario inserirBo(Usuario usuario) throws SQLException, ClassNotFoundException, RegraNegocioException {
        UsuarioDAO usuarioDao = new UsuarioDAO();
        try {
            // Regra de negocios
            validar(usuario);
            if (usuarioDao.buscarPorEmail(usuario.getEmail()) != null) {
                throw new RegraNegocioException("Já existe um usuário com esse e-mail");
            }

            int id = usuarioDao.inserir(usuario);
            return usuarioDao.buscarPorId(id);
        } finally {
            usuarioDao.fecharConexao();
        }
    }

    // Atualizar
    public Usuario atualizarBo(int id, Usuario usuario)
            throws SQLException, ClassNotFoundException, RegraNegocioException, NaoEncontradoException {
        UsuarioDAO usuarioDao = new UsuarioDAO();
        try {
            if (usuarioDao.buscarPorId(id) == null) {
                throw new NaoEncontradoException("Usuário " + id + " não encontrado");
            }

            // Regra de negocios
            validar(usuario);
            Usuario donoDoEmail = usuarioDao.buscarPorEmail(usuario.getEmail());
            if (donoDoEmail != null && donoDoEmail.getId() != id) {
                throw new RegraNegocioException("Já existe outro usuário com esse e-mail");
            }

            usuario.setId(id);
            usuarioDao.atualizar(usuario);
            return usuarioDao.buscarPorId(id);
        } finally {
            usuarioDao.fecharConexao();
        }
    }

    // Deletar
    public void deletarBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        UsuarioDAO usuarioDao = new UsuarioDAO();
        try {
            if (usuarioDao.buscarPorId(id) == null) {
                throw new NaoEncontradoException("Usuário " + id + " não encontrado");
            }
            usuarioDao.deletar(id);
        } finally {
            usuarioDao.fecharConexao();
        }
    }

    // Nome obrigatório e e-mail com @ e ponto
    private void validar(Usuario usuario) throws RegraNegocioException {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("Informe o nome do usuário");
        }
        if (usuario.getNome().length() > 100) {
            throw new RegraNegocioException("O nome pode ter no máximo 100 caracteres");
        }
        if (usuario.getEmail() == null || !usuario.getEmail().contains("@") || !usuario.getEmail().contains(".")) {
            throw new RegraNegocioException("Informe um e-mail válido");
        }
        if (usuario.getEmail().length() > 120) {
            throw new RegraNegocioException("O e-mail pode ter no máximo 120 caracteres");
        }
    }
}
