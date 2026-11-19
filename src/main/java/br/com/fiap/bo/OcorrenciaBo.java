package br.com.fiap.bo;

import br.com.fiap.dao.LinhaDAO;
import br.com.fiap.dao.OcorrenciaDAO;
import br.com.fiap.entities.Ocorrencia;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;

import java.sql.SQLException;
import java.util.ArrayList;

// Histórico de ocorrências. Uma ocorrência OFICIAL também muda o status da linha;
// quando a última ocorrência oficial aberta é encerrada, a linha volta ao normal.
public class OcorrenciaBo {

    // Uma ocorrência sempre descreve um problema, por isso NORMAL não entra aqui
    public static final String[] STATUS_VALIDOS = {"VELOCIDADE_REDUZIDA", "OPERACAO_PARCIAL", "PARALISADA"};
    public static final String[] FONTES_VALIDAS = {"OFICIAL", "PASSAGEIROS"};

    // Selecionar
    public ArrayList<Ocorrencia> selecionarBo() throws SQLException, ClassNotFoundException {
        OcorrenciaDAO ocorrenciaDao = new OcorrenciaDAO();
        try {
            return ocorrenciaDao.selecionar();
        } finally {
            ocorrenciaDao.fecharConexao();
        }
    }

    // Só as abertas
    public ArrayList<Ocorrencia> selecionarAbertasBo() throws SQLException, ClassNotFoundException {
        OcorrenciaDAO ocorrenciaDao = new OcorrenciaDAO();
        try {
            return ocorrenciaDao.selecionarAbertas();
        } finally {
            ocorrenciaDao.fecharConexao();
        }
    }

    // Histórico de uma linha
    public ArrayList<Ocorrencia> selecionarPorLinhaBo(int idLinha) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        new LinhaBo().buscarPorIdBo(idLinha);

        OcorrenciaDAO ocorrenciaDao = new OcorrenciaDAO();
        try {
            return ocorrenciaDao.selecionarPorLinha(idLinha);
        } finally {
            ocorrenciaDao.fecharConexao();
        }
    }

    // Buscar por id
    public Ocorrencia buscarPorIdBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        OcorrenciaDAO ocorrenciaDao = new OcorrenciaDAO();
        try {
            Ocorrencia ocorrencia = ocorrenciaDao.buscarPorId(id);
            if (ocorrencia == null) {
                throw new NaoEncontradoException("Ocorrência " + id + " não encontrada");
            }
            return ocorrencia;
        } finally {
            ocorrenciaDao.fecharConexao();
        }
    }

    // Inserir
    public Ocorrencia inserirBo(Ocorrencia ocorrencia) throws SQLException, ClassNotFoundException, RegraNegocioException {
        validar(ocorrencia);
        if (!LinhaBo.contem(FONTES_VALIDAS, ocorrencia.getFonte())) {
            throw new RegraNegocioException("Fonte inválida. Use OFICIAL ou PASSAGEIROS");
        }

        LinhaDAO linhaDao = new LinhaDAO();
        OcorrenciaDAO ocorrenciaDao = new OcorrenciaDAO();
        try {
            if (linhaDao.buscarPorId(ocorrencia.getIdLinha()) == null) {
                throw new RegraNegocioException("A linha " + ocorrencia.getIdLinha() + " não existe");
            }

            int id = ocorrenciaDao.inserir(ocorrencia);

            // Regra de negocios: ocorrência oficial muda o status da linha
            if (ocorrencia.oficial()) {
                linhaDao.atualizarStatus(ocorrencia.getIdLinha(), ocorrencia.getStatus());
            }
            return ocorrenciaDao.buscarPorId(id);
        } finally {
            linhaDao.fecharConexao();
            ocorrenciaDao.fecharConexao();
        }
    }

    // Atualizar (status, descrição e chuva; a linha e a fonte não mudam)
    public Ocorrencia atualizarBo(int id, Ocorrencia ocorrencia)
            throws SQLException, ClassNotFoundException, RegraNegocioException, NaoEncontradoException {
        Ocorrencia atual = buscarPorIdBo(id);
        validar(ocorrencia);

        LinhaDAO linhaDao = new LinhaDAO();
        OcorrenciaDAO ocorrenciaDao = new OcorrenciaDAO();
        try {
            ocorrencia.setId(id);
            ocorrenciaDao.atualizar(ocorrencia);

            if (atual.isAberta() && atual.oficial()) {
                linhaDao.atualizarStatus(atual.getIdLinha(), ocorrencia.getStatus());
            }
            return ocorrenciaDao.buscarPorId(id);
        } finally {
            linhaDao.fecharConexao();
            ocorrenciaDao.fecharConexao();
        }
    }

    // Encerrar a ocorrência (se era a última oficial aberta, a linha volta ao normal)
    public Ocorrencia encerrarBo(int id) throws SQLException, ClassNotFoundException, RegraNegocioException, NaoEncontradoException {
        Ocorrencia atual = buscarPorIdBo(id);
        if (!atual.isAberta()) {
            throw new RegraNegocioException("Essa ocorrência já foi encerrada em " + atual.getFim());
        }

        OcorrenciaDAO ocorrenciaDao = new OcorrenciaDAO();
        try {
            ocorrenciaDao.encerrar(id);
            if (atual.oficial()) {
                voltarAoNormalSePuder(ocorrenciaDao, atual);
            }
            return ocorrenciaDao.buscarPorId(id);
        } finally {
            ocorrenciaDao.fecharConexao();
        }
    }

    // Deletar
    public void deletarBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        Ocorrencia atual = buscarPorIdBo(id);

        OcorrenciaDAO ocorrenciaDao = new OcorrenciaDAO();
        try {
            ocorrenciaDao.deletar(id);
            if (atual.isAberta() && atual.oficial()) {
                voltarAoNormalSePuder(ocorrenciaDao, atual);
            }
        } finally {
            ocorrenciaDao.fecharConexao();
        }
    }

    // Se não sobrou nenhuma ocorrência oficial aberta na linha, o status volta para NORMAL
    private void voltarAoNormalSePuder(OcorrenciaDAO ocorrenciaDao, Ocorrencia encerrada)
            throws SQLException, ClassNotFoundException {
        if (ocorrenciaDao.contarOficiaisAbertas(encerrada.getIdLinha(), encerrada.getId()) == 0) {
            LinhaDAO linhaDao = new LinhaDAO();
            try {
                linhaDao.atualizarStatus(encerrada.getIdLinha(), "NORMAL");
            } finally {
                linhaDao.fecharConexao();
            }
        }
    }

    private void validar(Ocorrencia ocorrencia) throws RegraNegocioException {
        if (!LinhaBo.contem(STATUS_VALIDOS, ocorrencia.getStatus())) {
            throw new RegraNegocioException("Status inválido. Use VELOCIDADE_REDUZIDA, OPERACAO_PARCIAL ou PARALISADA");
        }
        if (ocorrencia.getDescricao() != null && ocorrencia.getDescricao().length() > 300) {
            throw new RegraNegocioException("A descrição pode ter no máximo 300 caracteres");
        }
    }
}
