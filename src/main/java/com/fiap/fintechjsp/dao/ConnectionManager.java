package com.fiap.fintechjsp.dao;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados.
 * Utiliza o padrão Singleton para garantir que apenas uma instância da classe exista.
 * As variáveis de ambiente são carregadas dinamicamente de acordo com o ambiente (ex: desenvolvimento, produção).
 * <p>
 * A configuração de conexão é feita via arquivos `.env`, como `.env.development`, `.env.production`, etc.
 * <p>
 * Variáveis esperadas no arquivo `.env.{ENV}`:
 * - URL: URL de conexão JDBC
 * - USER: usuário do banco de dados
 * - PASSWORD: senha do banco de dados
 */
public class ConnectionManager {

    /**
     * Objeto que carrega as variáveis de ambiente do arquivo `.env.{ENV}`
     */
    private static final Dotenv dotenv = loadDotenv();

    /**
     * URL JDBC para conexão com o banco de dados
     */
    private static final String URL = dotenv.get("DB_URL");

    /**
     * Nome de usuário para conexão com o banco de dados
     */
    private static final String USER = dotenv.get("DB_USER");

    /**
     * Senha para conexão com o banco de dados
     */
    private static final String PASS = dotenv.get("DB_PASS");

    /**
     * Instância única da classe (Singleton)
     */
    private static ConnectionManager connectionManager;

    /**
     * Carrega o arquivo de variáveis de ambiente de acordo com a variável de sistema ENV.
     * Exemplo: se ENV=production, carrega o arquivo `.env.production`.
     * Caso ENV não esteja definido, o padrão será `.env.development`.
     *
     * @return Dotenv com as variáveis carregadas
     */
    private static Dotenv loadDotenv() {
        String env = System.getenv("ENV");

        if (env == null) {
            env = "development";
        }

        String envFile = ".env." + env;

        return Dotenv.configure()
                .filename(envFile)
                .load();
    }

    public ConnectionManager() {
    }

    /**
     * Retorna uma nova conexão com o banco de dados utilizando os parâmetros definidos nas variáveis de ambiente.
     *
     * @return Objeto {@link Connection} se a conexão for bem-sucedida, ou null se houver erro
     */
    public Connection getConnection() {
        Connection connection = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace(); // Para produção, substitua por logger apropriado
        }

        return connection;
    }

    /**
     * Retorna a instância única de {@link ConnectionManager}.
     * Caso ainda não exista, cria uma nova.
     *
     * @return Instância única de {@link ConnectionManager}
     */
    public static ConnectionManager getInstance() {
        if (connectionManager == null) {
            connectionManager = new ConnectionManager();
        }

        return connectionManager;
    }
}
