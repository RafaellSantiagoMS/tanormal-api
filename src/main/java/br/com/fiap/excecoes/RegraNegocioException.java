package br.com.fiap.excecoes;

// Lançada pelos BOs quando algum dado não segue as regras do Tá Normal? (o controller devolve 400)
public class RegraNegocioException extends Exception {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
