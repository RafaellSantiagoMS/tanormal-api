package br.com.fiap.excecoes;

// Lançada quando o id informado não existe no banco (o controller devolve 404)
public class NaoEncontradoException extends Exception {

    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
