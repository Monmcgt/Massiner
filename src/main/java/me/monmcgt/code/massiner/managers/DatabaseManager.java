package me.monmcgt.code.massiner.managers;

import java.sql.*;

public class DatabaseManager {
    public static DatabaseManager instance;

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    private Connection connection;

    public DatabaseManager(String host, String port, String database, String username, String password) {
        instance = this;

        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
        this.connection = DriverManager.getConnection(url);

        this.execute("SET NAMES utf8");
    }

    public boolean execute(String query) throws SQLException {
        try (Statement statement = this.connection.createStatement()) {
            statement.execute("SET NAMES utf8");

            return statement.execute(query);
        }
    }

    public ExecuteQueryClass createExecuteQueryClass(String query) throws SQLException {
        return new ExecuteQueryClass(this.connection.createStatement(), query);
    }

    public int executeUpdate(String query) throws SQLException {
        try (Statement statement = this.connection.createStatement()) {
            return statement.executeUpdate(query);
        }
    }

    public void close() throws SQLException {
        this.connection.close();
    }

    public static class ExecuteQueryClass {
        public Statement statement;
        public ResultSet resultSet;

        public String query;

        public ExecuteQueryClass(Statement statement, String query) throws SQLException {
            this.statement = statement;
            this.query = query;

            this.statement.execute("SET NAMES utf8");

            this.executeQuery(this.query);
        }

        public void executeQuery(String query) throws SQLException {
            this.resultSet = this.statement.executeQuery(query);
        }

        public void close() throws SQLException {
            this.resultSet.close();
            this.statement.close();
        }
    }
}
