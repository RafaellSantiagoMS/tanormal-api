package br.com.fiap.conexoes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoFactory {

    private final static String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl";

    // O usuário e a senha do Oracle não ficam escritos no código, porque o repositório é público.
    // Eles vêm das variáveis de ambiente DB_USERNAME e DB_PASSWORD
    // (no IntelliJ: Run > Edit Configurations > Environment variables; no Render: Environment).
    private final static String USUARIO = System.getenv("DB_USERNAME");
    private final static String SENHA = System.getenv("DB_PASSWORD");

    // metodo de conexão com o banco de dados
    public Connection conexao() throws ClassNotFoundException, SQLException {

        // Driver oracle
        Class.forName("oracle.jdbc.driver.OracleDriver");

        // Retornar conexão
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
