package com.fiap.fintechjsp.dao;

import br.com.fiap.fintechjsp.dao.ConnectionManager;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditarTransferenciaDAO {

    public boolean editarTransferencia(String usuarioRemetente, String usuarioDestino, BigDecimal valor) {
        boolean sucesso = false;
        Connection connection = null;

        System.out.println("DEBUG - iniciar Transferencia: remetente = " + usuarioRemetente + ", destino = " + usuarioDestino + ", valor = " + valor);

        try {
            connection = ConnectionManager.getInstance().getConnection();
            connection.setAutoCommit(false);  // iniciar transação

            // 1. Verificar se conta destino existe
            String sqlVerificaDestino = "SELECT * FROM usuario WHERE usuario = ?";
            try (PreparedStatement psVerificaDestino = connection.prepareStatement(sqlVerificaDestino)) {
                psVerificaDestino.setString(1, usuarioDestino);
                try (ResultSet rsDestino = psVerificaDestino.executeQuery()) {
                    if (!rsDestino.next()) {
                        System.out.println("DEBUG: Usuário destino não encontrado: " + usuarioDestino);
                        connection.rollback();
                        return false;
                    }
                }
            }

            // 2. Verificar saldo do remetente
            String sqlVerificaSaldo = "SELECT saldo FROM usuario WHERE usuario = ?";
            BigDecimal saldoRemetente;
            try (PreparedStatement psVerificaSaldo = connection.prepareStatement(sqlVerificaSaldo)) {
                psVerificaSaldo.setString(1, usuarioRemetente);
                try (ResultSet rsSaldo = psVerificaSaldo.executeQuery()) {
                    if (!rsSaldo.next()) {
                        System.out.println("DEBUG: Usuário remetente não encontrado: " + usuarioRemetente);
                        connection.rollback();
                        return false;
                    }
                    saldoRemetente = rsSaldo.getBigDecimal("saldo");
                }
            }

            if (saldoRemetente.compareTo(valor) < 0) {
                System.out.println("DEBUG: Saldo insuficiente. Saldo do remetente: " + saldoRemetente + ", valor transferência: " + valor);
                connection.rollback();
                return false;
            }

            // 3. Debitar do remetente
            String sqlDebita = "UPDATE usuario SET saldo = saldo - ? WHERE usuario = ?";
            int linhasAfetadasRemetente;
            try (PreparedStatement psDebita = connection.prepareStatement(sqlDebita)) {
                psDebita.setBigDecimal(1, valor);
                psDebita.setString(2, usuarioRemetente);
                linhasAfetadasRemetente = psDebita.executeUpdate();
                System.out.println("DEBUG: Linhas afetadas no débito: " + linhasAfetadasRemetente);
                if (linhasAfetadasRemetente == 0) {
                    System.out.println("DEBUG: Falha ao debitar saldo do remetente.");
                    connection.rollback();
                    return false;
                }
            }

            // 4. Creditar no destino
            String sqlCredita = "UPDATE usuario SET saldo = saldo + ? WHERE usuario = ?";
            int linhasAfetadasDestino;
            try (PreparedStatement psCredita = connection.prepareStatement(sqlCredita)) {
                psCredita.setBigDecimal(1, valor);
                psCredita.setString(2, usuarioDestino);
                linhasAfetadasDestino = psCredita.executeUpdate();
                System.out.println("DEBUG: Linhas afetadas no crédito: " + linhasAfetadasDestino);
                if (linhasAfetadasDestino == 0) {
                    System.out.println("DEBUG: Falha ao creditar saldo no destino.");
                    connection.rollback();
                    return false;
                }
            }

            connection.commit(); // confirmar transação
            System.out.println("DEBUG: Transferência concluída com sucesso.");
            sucesso = true;

        } catch (Exception e) {
            System.out.println("DEBUG: Exceção durante transferência:");
            e.printStackTrace();
            try {
                if (connection != null)
                    connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sucesso;
    }

    public BigDecimal obterSaldo(String usuario) {
        BigDecimal saldo = BigDecimal.ZERO;
        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT saldo FROM usuario WHERE usuario = ?")) {
            stmt.setString(1, usuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    saldo = rs.getBigDecimal("saldo");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saldo;
    }

}
