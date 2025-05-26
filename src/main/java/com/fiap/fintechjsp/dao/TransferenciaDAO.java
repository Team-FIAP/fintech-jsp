package com.fiap.fintechjsp.dao;

import br.com.fiap.fintechjsp.model.Historico;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransferenciaDAO {

    public boolean realizarTransferencia(Long originUserId, Long destinationUserId, BigDecimal amount, String description, String observation) {
        boolean sucesso = false;
        Connection conn = null;

        try {
            conn = ConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Verifica saldo da conta de origem (com FOR UPDATE)
            String sqlSaldo = "SELECT balance FROM T_FIN_ACCOUNT WHERE user_id = ? FOR UPDATE";
            BigDecimal saldo = BigDecimal.ZERO;

            try (PreparedStatement ps = conn.prepareStatement(sqlSaldo)) {
                ps.setLong(1, originUserId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        saldo = rs.getBigDecimal("balance");
                    } else {
                        conn.rollback();
                        return false; // Conta origem não encontrada
                    }
                }
            }

            if (saldo.compareTo(amount) < 0) {
                conn.rollback();
                return false; // Saldo insuficiente
            }

            // Debita origem
            String sqlDebita = "UPDATE T_FIN_ACCOUNT SET balance = balance - ? WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDebita)) {
                ps.setBigDecimal(1, amount);
                ps.setLong(2, originUserId);
                ps.executeUpdate();
            }

            // Credita destino
            String sqlCredita = "UPDATE T_FIN_ACCOUNT SET balance = balance + ? WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCredita)) {
                ps.setBigDecimal(1, amount);
                ps.setLong(2, destinationUserId);
                ps.executeUpdate();
            }

            // Busca os IDs das contas
            Long originAccountId = buscarAccountIdPorUserId(conn, originUserId);
            Long destinationAccountId = buscarAccountIdPorUserId(conn, destinationUserId);

            // Insere transferência
            String sqlInsert = """
                INSERT INTO T_FIN_TRANSFER (
                    id, amount, transfer_date, description, observation,
                    origin_account_id, destination_account_id, created_at
                ) VALUES (
                    SEQ_FIN_TRANSFER.NEXTVAL, ?, SYSDATE, ?, ?, ?, ?, SYSTIMESTAMP
                )
            """;

            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setBigDecimal(1, amount);
                ps.setString(2, description);
                ps.setString(3, observation);
                ps.setLong(4, originAccountId);
                ps.setLong(5, destinationAccountId);
                ps.executeUpdate();
            }

            conn.commit();
            sucesso = true;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sucesso;
    }

    // Retorna o ID da conta (T_FIN_ACCOUNT) baseado no user_id
    private Long buscarAccountIdPorUserId(Connection conn, Long userId) throws SQLException {
        String sql = "SELECT id FROM T_FIN_ACCOUNT WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }
        throw new SQLException("Conta não encontrada para o usuário ID: " + userId);
    }

    public List<Historico> obterHistorico(Long userId) {
        List<Historico> historico = new ArrayList<>();
        String sql = """
            SELECT t.transfer_date, t.amount, u.username AS destinatario
            FROM T_FIN_TRANSFER t
            JOIN T_FIN_ACCOUNT a ON t.destination_account_id = a.id
            JOIN T_FIN_USER u ON a.user_id = u.id
            WHERE t.origin_account_id = (
                SELECT id FROM T_FIN_ACCOUNT WHERE user_id = ?
            )
            ORDER BY t.transfer_date DESC
        """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Historico h = new Historico();
                    h.setData(rs.getDate("transfer_date").toLocalDate());
                    h.setValor(rs.getBigDecimal("amount"));
                    h.setUsuario(rs.getString("destinatario"));
                    historico.add(h);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return historico;
    }

    public Long buscarIdPorUsuario(String username) {
        String sql = "SELECT id FROM T_FIN_USER WHERE username = ?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal obterSaldoPorId(Long userId) {
        BigDecimal saldo = BigDecimal.ZERO;
        String sql = "SELECT balance FROM T_FIN_ACCOUNT WHERE user_id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    saldo = rs.getBigDecimal("balance");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return saldo;
    }
}
