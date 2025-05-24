package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.Account;
import com.fiap.fintechjsp.model.Income;
import com.fiap.fintechjsp.model.Investment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvestmentDao implements BaseDao<Investment, Long> {
    @Override
    public Investment findById(Long aLong) {
        return null;
    }

    @Override
    public List<Investment> findAll() {
        return List.of();
    }

    public List<Investment> findAll(LocalDate startDate, LocalDate endDate, Long accountId, Long userId) {
        List<Investment> investments = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT 
                i.ID,
                i.DESCRIPTION,
                i.OBSERVATION,
                i.AMOUNT,
                i."DATE",
                i.TYPE,
                i.RISK,
                i.LIQUIDITY,
                i.PROFITABILITY,
                i.DUE_DATE,
                i.INTEREST_RATE,
                i.REDEEMED,
                i.CREATED_AT,
                oa.id origin_account_id,
                oa.name origin_account_name,
                oa.balance origin_account_balance,
                oa.created_at origin_account_created_at
            FROM T_FIN_INVESTMENT i
            INNER JOIN T_FIN_ACCOUNT oa
            ON i.ORIGIN_ACCOUNT_ID = oa.ID
            WHERE 1=1
        """);

        if (startDate != null) {
            sql.append(" AND TRUNC(i.\"DATE\") >= ?");
        }

        if (endDate != null) {
            sql.append(" AND TRUNC(i.\"DATE\") <= ?");
        }

        if (accountId != null) {
            sql.append(" AND i.ORIGIN_ACCOUNT_ID = ?");
        }

        if (userId != null) {
            sql.append(" AND oa.USER_ID = ?");
        }

        sql.append(" ORDER BY i.\"DATE\" DESC");

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql.toString());

            int index = 1;

            if (startDate != null) {
                ps.setDate(index++, Date.valueOf(startDate));
            }

            if (endDate != null) {
                ps.setDate(index++, Date.valueOf(endDate));
            }

            if (accountId != null) {
                ps.setLong(index++, accountId);
            }

            if (userId != null) {
                ps.setLong(index, userId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                investments.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investments;
    }

    @Override
    public Investment insert(Investment entity) throws DBException {
        return null;
    }

    @Override
    public Investment update(Investment entity) throws DBException {
        return null;
    }

    @Override
    public void delete(Investment entity) throws DBException {

    }

    /**
     * Exclui um investimento do banco de dados e atualiza o saldo da conta de origem,
     * devolvendo o valor do investimento excluído.
     *
     * Esta operação realiza as seguintes etapas de forma transacional:
     * <ul>
     *   <li>Recupera os dados do investimento (valor e conta de origem)</li>
     *   <li>Adiciona o valor de volta ao saldo da conta de origem</li>
     *   <li>Remove o registro do investimento</li>
     * </ul>
     *
     * Caso qualquer etapa falhe, nenhuma modificação será persistida, garantindo a integridade
     * dos dados.
     *
     * @param id o identificador único do investimento a ser excluído
     * @throws DBException se o investimento não for encontrado ou ocorrer algum erro
     *         durante o processo de exclusão e atualização do saldo
     */
    public void delete(Long id) throws DBException {
        String selectSql = "SELECT ORIGIN_ACCOUNT_ID, AMOUNT FROM T_FIN_INVESTMENT WHERE ID = ?";
        String updateAccountSql = "UPDATE T_FIN_ACCOUNT SET BALANCE = BALANCE + ? WHERE ID = ?";
        String deleteSql = "DELETE FROM T_FIN_INVESTMENT WHERE ID = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            Long accountId = null;
            double amount = 0;

            // 1. Buscar dados do investimento
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    accountId = rs.getLong("ORIGIN_ACCOUNT_ID");
                    amount = rs.getDouble("AMOUNT");
                } else {
                    throw new DBException("Investimento não encontrado para exclusão.");
                }
            }

            // 2. Atualizar saldo da conta (devolver valor)
            try (PreparedStatement ps = conn.prepareStatement(updateAccountSql)) {
                ps.setDouble(1, amount);
                ps.setLong(2, accountId);
                ps.executeUpdate();
            }

            // 3. Excluir o investimento
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new DBException("Erro ao excluir investimento e atualizar saldo da conta", e);
        }
    }

    private Investment fromResultSet(ResultSet rs) {
        try {
            return new Investment(
                rs.getLong("ID"),
                rs.getDouble("AMOUNT"),
                rs.getDate("DATE").toLocalDate(),
                rs.getString("DESCRIPTION"),
                rs.getString("OBSERVATION"),
                new Account(
                        rs.getLong("origin_account_id"),
                        rs.getString("origin_account_name"),
                        rs.getDouble("origin_account_balance"),
                        null,
                        rs.getTimestamp("origin_account_created_at").toLocalDateTime()
                ),
                rs.getTimestamp("CREATED_AT").toLocalDateTime(),
                rs.getString("TYPE"),
                rs.getString("RISK"),
                rs.getString("LIQUIDITY"),
                rs.getDouble("PROFITABILITY"),
                rs.getDate("DUE_DATE") != null
                    ? rs.getDate("DUE_DATE").toLocalDate()
                    : null,
                rs.getDouble("INTEREST_RATE"),
                rs.getBoolean("REDEEMED")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
