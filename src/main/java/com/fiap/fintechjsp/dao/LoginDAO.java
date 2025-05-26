package com.fiap.fintechjsp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDAO {

    public boolean validarLogin(String usuario, String senha) {
        boolean loginValido = false;

        String sql = "SELECT COUNT(1) FROM T_FIN_USER WHERE UPPER(USERNAME) = UPPER(?) AND PASSWORD = ?";

        try (
                Connection connection = ConnectionManager.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            // Remove espaços em branco
            usuario = usuario.trim();
            senha = senha.trim();

            // Exibe dados para debug
            System.out.println("Username recebido: [" + usuario + "] length: " + usuario.length());
            System.out.println("Password recebido: [" + senha + "] length: " + senha.length());

            // Define os parâmetros
            stmt.setString(1, usuario);
            stmt.setString(2, senha);

            // Executa a consulta
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Resultado do COUNT: " + count);
                    loginValido = count > 0;
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Erro ao validar login:");
            e.printStackTrace();
        }

        System.out.println("✅ Login válido? " + loginValido);
        return loginValido;
    }
}




























//package br.com.fiap.fintechjsp.dao;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//public class LoginDAO {
//
//    public boolean validarLogin(String usuario, String senha) {
//        boolean loginValido = false;
//
//        Connection connection = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//
//        try {
//            connection = ConnectionManager.getInstance().getConnection();
//
//            String sql = "SELECT * FROM usuarios WHERE usuario = ? AND senha = ?";
//            stmt = connection.prepareStatement(sql);
//            stmt.setString(1, usuario);
//            stmt.setString(2, senha);
//
//            rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                // Usuário e senha válidos
//                loginValido = true;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (rs != null) rs.close();
//                if (stmt != null) stmt.close();
//                if (connection != null) connection.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return loginValido;
//    }
//}
