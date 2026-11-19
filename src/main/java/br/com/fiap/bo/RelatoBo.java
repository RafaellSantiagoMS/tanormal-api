package br.com.fiap.bo;

import br.com.fiap.dao.EstacaoDAO;
import br.com.fiap.dao.LinhaDAO;
import br.com.fiap.dao.RelatoDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.entities.Estacao;
import br.com.fiap.entities.Relato;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;

import java.sql.SQLException;
import java.util.ArrayList;

public class RelatoBo {

    public static final String[] TIPOS_VALIDOS = {"PARADO", "LENTO", "LOTADO"};

    // Selecionar
    public ArrayList<Relato> selecionarBo() throws SQLException, ClassNotFoundException {
        RelatoDAO relatoDao = new RelatoDAO();
        try {
            return relatoDao.selecionar();
        } finally {
            relatoDao.fecharConexao();
        }
    }

    // Relatos de uma linha
    public ArrayList<Relato> selecionarPorLinhaBo(int idLinha) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        new LinhaBo().buscarPorIdBo(idLinha);

        RelatoDAO relatoDao = new RelatoDAO();
        try {
            return relatoDao.selecionarPorLinha(idLinha);
        } finally {
            relatoDao.fecharConexao();
        }
    }

    // Buscar por id
    public Relato buscarPorIdBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        RelatoDAO relatoDao = new RelatoDAO();
        try {
            Relato relato = relatoDao.buscarPorId(id);
            if (relato == null) {
                throw new NaoEncontradoException("Relato " + id + " não encontrado");
            }
            return relato;
        } finally {
            relatoDao.fecharConexao();
        }
    }

    // Inserir
    public Relato inserirBo(Relato relato) throws SQLException, ClassNotFoundException, RegraNegocioException {
        UsuarioDAO usuarioDao = new UsuarioDAO();
        try {
            if (usuarioDao.buscarPorId(relato.getIdUsuario()) == null) {
                throw new RegraNegocioException("O usuário " + relato.getIdUsuario() + " não existe");
            }
        } finally {
            usuarioDao.fecharConexao();
        }
        validar(relato);

        RelatoDAO relatoDao = new RelatoDAO();
        try {
            int id = relatoDao.inserir(relato);
            return relatoDao.buscarPorId(id);
        } finally {
            relatoDao.fecharConexao();
        }
    }

    // Atualizar
    public Relato atualizarBo(int id, Relato relato)
            throws SQLException, ClassNotFoundException, RegraNegocioException, NaoEncontradoException {
        buscarPorIdBo(id);
        validar(relato);

        RelatoDAO relatoDao = new RelatoDAO();
        try {
            relato.setId(id);
            relatoDao.atualizar(relato);
            return relatoDao.buscarPorId(id);
        } finally {
            relatoDao.fecharConexao();
        }
    }

    // Outro passageiro confirma que está vendo a mesma coisa
    public Relato confirmarBo(int id) throws SQLException, ClassNotFoundException, RegraNegocioException, NaoEncontradoException {
        Relato relato = buscarPorIdBo(id);

        // Regra de negocios: relato vencido não pode mais ser confirmado
        if (!relato.estaValido()) {
            throw new RegraNegocioException("Esse relato foi enviado há mais de " + Relato.VALIDADE_MINUTOS +
                    " minutos e não pode mais ser confirmado");
        }

        RelatoDAO relatoDao = new RelatoDAO();
        try {
            relatoDao.confirmar(id);
            return relatoDao.buscarPorId(id);
        } finally {
            relatoDao.fecharConexao();
        }
    }

    // Deletar
    public void deletarBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        buscarPorIdBo(id);

        RelatoDAO relatoDao = new RelatoDAO();
        try {
            relatoDao.deletar(id);
        } finally {
            relatoDao.fecharConexao();
        }
    }

    // Tipo válido, linha existente e estação dessa linha
    private void validar(Relato relato) throws SQLException, ClassNotFoundException, RegraNegocioException {
        if (!LinhaBo.contem(TIPOS_VALIDOS, relato.getTipo())) {
            throw new RegraNegocioException("Tipo de relato inválido. Use PARADO, LENTO ou LOTADO");
        }
        if (relato.getComentario() != null && relato.getComentario().length() > 200) {
            throw new RegraNegocioException("O comentário pode ter no máximo 200 caracteres");
        }

        LinhaDAO linhaDao = new LinhaDAO();
        try {
            if (linhaDao.buscarPorId(relato.getIdLinha()) == null) {
                throw new RegraNegocioException("A linha " + relato.getIdLinha() + " não existe");
            }
        } finally {
            linhaDao.fecharConexao();
        }

        EstacaoDAO estacaoDao = new EstacaoDAO();
        try {
            Estacao estacao = estacaoDao.buscarPorId(relato.getIdEstacao());
            if (estacao == null) {
                throw new RegraNegocioException("A estação " + relato.getIdEstacao() + " não existe");
            }
            if (estacao.getIdLinha() != relato.getIdLinha()) {
                throw new RegraNegocioException("A estação " + estacao.getNome() + " não pertence a essa linha");
            }
        } finally {
            estacaoDao.fecharConexao();
        }
    }
}
