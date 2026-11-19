package br.com.fiap.bo;

import br.com.fiap.dao.EstacaoDAO;
import br.com.fiap.dao.LinhaDAO;
import br.com.fiap.entities.Estacao;
import br.com.fiap.entities.Linha;
import br.com.fiap.excecoes.NaoEncontradoException;
import br.com.fiap.excecoes.RegraNegocioException;

import java.sql.SQLException;
import java.util.ArrayList;

public class LinhaBo {

    // Valores aceitos (os mesmos que o site do Metrô e da CPTM mostram)
    public static final String[] STATUS_VALIDOS = {"NORMAL", "VELOCIDADE_REDUZIDA", "OPERACAO_PARCIAL", "PARALISADA"};
    public static final String[] OPERADORAS_VALIDAS = {"METRO", "CPTM", "VIAQUATRO", "VIAMOBILIDADE"};

    // Selecionar
    public ArrayList<Linha> selecionarBo() throws SQLException, ClassNotFoundException {
        LinhaDAO linhaDao = new LinhaDAO();
        try {
            return linhaDao.selecionar();
        } finally {
            linhaDao.fecharConexao();
        }
    }

    // Buscar por id
    public Linha buscarPorIdBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        LinhaDAO linhaDao = new LinhaDAO();
        try {
            Linha linha = linhaDao.buscarPorId(id);
            if (linha == null) {
                throw new NaoEncontradoException("Linha " + id + " não encontrada");
            }
            return linha;
        } finally {
            linhaDao.fecharConexao();
        }
    }

    // Estações da linha
    public ArrayList<Estacao> selecionarEstacoesBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        buscarPorIdBo(id);

        EstacaoDAO estacaoDao = new EstacaoDAO();
        try {
            return estacaoDao.selecionarPorLinha(id);
        } finally {
            estacaoDao.fecharConexao();
        }
    }

    // Inserir
    public Linha inserirBo(Linha linha) throws SQLException, ClassNotFoundException, RegraNegocioException {
        LinhaDAO linhaDao = new LinhaDAO();
        try {
            // Regra de negocios
            validar(linha);
            if (linhaDao.buscarPorNumero(linha.getNumero()) != null) {
                throw new RegraNegocioException("Já existe uma linha com o número " + linha.getNumero());
            }

            int id = linhaDao.inserir(linha);
            return linhaDao.buscarPorId(id);
        } finally {
            linhaDao.fecharConexao();
        }
    }

    // Atualizar
    public Linha atualizarBo(int id, Linha linha)
            throws SQLException, ClassNotFoundException, RegraNegocioException, NaoEncontradoException {
        LinhaDAO linhaDao = new LinhaDAO();
        try {
            if (linhaDao.buscarPorId(id) == null) {
                throw new NaoEncontradoException("Linha " + id + " não encontrada");
            }

            // Regra de negocios
            validar(linha);
            Linha mesmaNumeracao = linhaDao.buscarPorNumero(linha.getNumero());
            if (mesmaNumeracao != null && mesmaNumeracao.getId() != id) {
                throw new RegraNegocioException("Já existe outra linha com o número " + linha.getNumero());
            }

            linha.setId(id);
            linhaDao.atualizar(linha);
            return linhaDao.buscarPorId(id);
        } finally {
            linhaDao.fecharConexao();
        }
    }

    // Atualizar só o status oficial (o leitor do site da operadora chama este)
    public Linha atualizarStatusBo(int id, String status)
            throws SQLException, ClassNotFoundException, RegraNegocioException, NaoEncontradoException {
        LinhaDAO linhaDao = new LinhaDAO();
        try {
            if (linhaDao.buscarPorId(id) == null) {
                throw new NaoEncontradoException("Linha " + id + " não encontrada");
            }
            if (!contem(STATUS_VALIDOS, status)) {
                throw new RegraNegocioException("Status inválido. Use NORMAL, VELOCIDADE_REDUZIDA, OPERACAO_PARCIAL ou PARALISADA");
            }

            linhaDao.atualizarStatus(id, status);
            return linhaDao.buscarPorId(id);
        } finally {
            linhaDao.fecharConexao();
        }
    }

    // Deletar
    public void deletarBo(int id) throws SQLException, ClassNotFoundException, NaoEncontradoException {
        LinhaDAO linhaDao = new LinhaDAO();
        try {
            if (linhaDao.buscarPorId(id) == null) {
                throw new NaoEncontradoException("Linha " + id + " não encontrada");
            }
            linhaDao.deletar(id);
        } finally {
            linhaDao.fecharConexao();
        }
    }

    private void validar(Linha linha) throws RegraNegocioException {
        if (linha.getNumero() <= 0) {
            throw new RegraNegocioException("Informe o número da linha");
        }
        if (linha.getNome() == null || linha.getNome().trim().isEmpty()) {
            throw new RegraNegocioException("Informe o nome da linha");
        }
        if (!contem(OPERADORAS_VALIDAS, linha.getOperadora())) {
            throw new RegraNegocioException("Operadora inválida. Use METRO, CPTM, VIAQUATRO ou VIAMOBILIDADE");
        }
    }

    // Procura o texto dentro do vetor
    public static boolean contem(String[] valores, String procurado) {
        if (procurado == null) {
            return false;
        }
        for (String valor : valores) {
            if (valor.equals(procurado)) {
                return true;
            }
        }
        return false;
    }
}
