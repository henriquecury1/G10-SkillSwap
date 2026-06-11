package com.skillswap.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL = "jdbc:postgresql://trabalho-pucmg.postgres.database.azure.com:5432/postgres?sslmode=require";
    private static final String USER = "henriquecury";
    private static final String PASSWORD = "Botafogo2024.";

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("FALHA CRÍTICA DE CONEXÃO COM O BANCO DE DADOS");
            System.err.println("Verifique: IP, porta, nome do banco, usuário, senha, firewall e PostgreSQL ligado.");
            e.printStackTrace();
            throw e;
        }
    }
}