package br.com.fiap.bo;

import br.com.fiap.dao.EstacaoDAO;
import br.com.fiap.dao.LinhaDAO;
import br.com.fiap.dao.TrajetoDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.entities.Estacao;
import br.com.fiap.entities.Trajeto;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;

import java.sql.SQLException;
import java.util.ArrayList;

public class TrajetoBo {

    // Selecionar
    public ArrayList<Trajeto> selecionarBo() throws SQLException, ClassNotFoundException {
        TrajetoDAO trajetoDao = new TrajetoDAO();
        try {
            return trajetoDao.selecionar();
        } finally {
            trajetoDao.fecharConexao();
        }
    }

    // Buscar por id
    public Trajeto buscarPorIdBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        TrajetoDAO trajetoDao = new TrajetoDAO();
        try {
            Trajeto trajeto = trajetoDao.buscarPorId(id);
            if (trajeto == null) {
                throw new NaoEncontradoException("Trajeto " + id + " não encontrado");
            }
            return trajeto;
        } finally {
            trajetoDao.fecharConexao();
        }
    }

    // Inserir
    public Trajeto inserirBo(Trajeto trajeto) throws SQLException, ClassNotFoundException, RegraNegocioException {
        validarUsuario(trajeto.getIdUsuario());
        validar(trajeto);

        TrajetoDAO trajetoDao = new TrajetoDAO();
        try {
            int id = trajetoDao.inserir(trajeto);
            return trajetoDao.buscarPorId(id);
        } finally {
            trajetoDao.fecharConexao();
        }
    }

    // Atualizar (o dono do trajeto não muda)
    public Trajeto atualizarBo(int id, Trajeto trajeto)
            throws SQLException, ClassNotFoundException, RegraNegocioException, NaoEncontradoException {
        Trajeto atual = buscarPorIdBo(id);
        validar(trajeto);

        TrajetoDAO trajetoDao = new TrajetoDAO();
        try {
            trajeto.setId(id);
            trajeto.setIdUsuario(atual.getIdUsuario());
            trajetoDao.atualizar(trajeto);
            return trajetoDao.buscarPorId(id);
        } finally {
            trajetoDao.fecharConexao();
        }
    }

    // Deletar
    public void deletarBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        buscarPorIdBo(id);

        TrajetoDAO trajetoDao = new TrajetoDAO();
        try {
            trajetoDao.deletar(id);
        } finally {
            trajetoDao.fecharConexao();
        }
    }

    private void validarUsuario(int idUsuario) throws SQLException, ClassNotFoundException, RegraNegocioException {
        UsuarioDAO usuarioDao = new UsuarioDAO();
        try {
            if (usuarioDao.buscarPorId(idUsuario) == null) {
                throw new RegraNegocioException("O usuário " + idUsuario + " não existe");
            }
        } finally {
            usuarioDao.fecharConexao();
        }
    }

    // Regras do trajeto
    private void validar(Trajeto trajeto) throws SQLException, ClassNotFoundException, RegraNegocioException {
        if (trajeto.getNome() == null || trajeto.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("Dê um nome para o trajeto (ex.: Casa → Trabalho)");
        }
        if (trajeto.getNome().length() > 60) {
            throw new RegraNegocioException("O nome do trajeto pode ter no máximo 60 caracteres");
        }
        if (!trajeto.horarioValido()) {
            throw new RegraNegocioException("Horário de saída inválido. Use o formato HH:mm, ex.: 06:30");
        }
        if (trajeto.getTempoNormalMinutos() <= 0) {
            throw new RegraNegocioException("Informe quantos minutos a viagem leva num dia normal");
        }
        if (trajeto.getIdOrigem() == trajeto.getIdDestino()) {
            throw new RegraNegocioException("Origem e destino não podem ser a mesma estação");
        }
        if (trajeto.getIdsLinhas() == null || trajeto.getIdsLinhas().isEmpty()) {
            throw new RegraNegocioException("Informe as linhas do trajeto, na ordem da viagem");
        }

        // A mesma linha não pode aparecer duas vezes
        ArrayList<Integer> linhasVistas = new ArrayList<Integer>();
        for (Integer idLinha : trajeto.getIdsLinhas()) {
            if (linhasVistas.contains(idLinha)) {
                throw new RegraNegocioException("A linha " + idLinha + " foi informada mais de uma vez");
            }
            linhasVistas.add(idLinha);
        }

        // Todas as linhas precisam existir
        LinhaDAO linhaDao = new LinhaDAO();
        try {
            for (Integer idLinha : trajeto.getIdsLinhas()) {
                if (linhaDao.buscarPorId(idLinha) == null) {
                    throw new RegraNegocioException("A linha " + idLinha + " não existe");
                }
            }
        } finally {
            linhaDao.fecharConexao();
        }

        // Origem e destino precisam existir e as linhas delas precisam estar no trajeto
        EstacaoDAO estacaoDao = new EstacaoDAO();
        try {
            Estacao origem = estacaoDao.buscarPorId(trajeto.getIdOrigem());
            Estacao destino = estacaoDao.buscarPorId(trajeto.getIdDestino());

            if (origem == null) {
                throw new RegraNegocioException("A estação de origem " + trajeto.getIdOrigem() + " não existe");
            }
            if (destino == null) {
                throw new RegraNegocioException("A estação de destino " + trajeto.getIdDestino() + " não existe");
            }
            if (!trajeto.getIdsLinhas().contains(origem.getIdLinha())) {
                throw new RegraNegocioException("A linha da estação de origem (" + origem.getNomeLinha() + ") precisa estar no trajeto");
            }
            if (!trajeto.getIdsLinhas().contains(destino.getIdLinha())) {
                throw new RegraNegocioException("A linha da estação de destino (" + destino.getNomeLinha() + ") precisa estar no trajeto");
            }
        } finally {
            estacaoDao.fecharConexao();
        }
    }
}
