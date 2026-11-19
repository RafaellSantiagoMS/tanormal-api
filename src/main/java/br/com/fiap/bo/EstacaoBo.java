package br.com.fiap.bo;

import br.com.fiap.dao.EstacaoDAO;
import br.com.fiap.dao.LinhaDAO;
import br.com.fiap.entities.Estacao;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;

import java.sql.SQLException;
import java.util.ArrayList;

public class EstacaoBo {

    // Selecionar
    public ArrayList<Estacao> selecionarBo() throws SQLException, ClassNotFoundException {
        EstacaoDAO estacaoDao = new EstacaoDAO();
        try {
            return estacaoDao.selecionar();
        } finally {
            estacaoDao.fecharConexao();
        }
    }

    // Buscar por id
    public Estacao buscarPorIdBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        EstacaoDAO estacaoDao = new EstacaoDAO();
        try {
            Estacao estacao = estacaoDao.buscarPorId(id);
            if (estacao == null) {
                throw new NaoEncontradoException("Estação " + id + " não encontrada");
            }
            return estacao;
        } finally {
            estacaoDao.fecharConexao();
        }
    }

    // Inserir
    public Estacao inserirBo(Estacao estacao) throws SQLException, ClassNotFoundException, RegraNegocioException {
        validar(estacao);

        EstacaoDAO estacaoDao = new EstacaoDAO();
        try {
            int id = estacaoDao.inserir(estacao);
            return estacaoDao.buscarPorId(id);
        } finally {
            estacaoDao.fecharConexao();
        }
    }

    // Atualizar
    public Estacao atualizarBo(int id, Estacao estacao)
            throws SQLException, ClassNotFoundException, RegraNegocioException, NaoEncontradoException {
        buscarPorIdBo(id);
        validar(estacao);

        EstacaoDAO estacaoDao = new EstacaoDAO();
        try {
            estacao.setId(id);
            estacaoDao.atualizar(estacao);
            return estacaoDao.buscarPorId(id);
        } finally {
            estacaoDao.fecharConexao();
        }
    }

    // Deletar
    public void deletarBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        buscarPorIdBo(id);

        EstacaoDAO estacaoDao = new EstacaoDAO();
        try {
            estacaoDao.deletar(id);
        } finally {
            estacaoDao.fecharConexao();
        }
    }

    // Nome obrigatório e a linha precisa existir
    private void validar(Estacao estacao) throws SQLException, ClassNotFoundException, RegraNegocioException {
        if (estacao.getNome() == null || estacao.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("Informe o nome da estação");
        }

        LinhaDAO linhaDao = new LinhaDAO();
        try {
            if (linhaDao.buscarPorId(estacao.getIdLinha()) == null) {
                throw new RegraNegocioException("A linha " + estacao.getIdLinha() + " não existe");
            }
        } finally {
            linhaDao.fecharConexao();
        }
    }
}
