package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.Account;
import com.fiap.fintechjsp.model.Income;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncomeDao implements BaseDao<Income, Long> {
    @Override
    public Income findById(Long aLong) {
        return null;
    }

    @Override
    public List<Income> findAll() {
        return List.of();
    }

    public List<Income> findAll(LocalDate startDate, LocalDate endDate, Long accountId, Long userId) {
        List<Income> incomes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT 
                i.ID,
                i.DESCRIPTION,
                i.OBSERVATION,
                i.AMOUNT,
                i."DATE",
                i.CREATED_AT,
                oa.ID origin_account_id,
                oa.NAME origin_account_name,
                oa.BALANCE origin_account_balance,
                oa.CREATED_AT origin_account_created_at
            FROM T_FIN_INCOME i
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
                incomes.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return incomes;
    }

    @Override
    public Income insert(Income entity) throws DBException {
        return null;
    }

    @Override
    public Income update(Income entity) throws DBException {
        return null;
    }

    @Override
    public void delete(Income entity) throws DBException {

    }

    /**
     * Exclui uma receita (income) do banco de dados e atualiza o saldo da conta de origem,
     * subtraindo o valor da receita excluída.
     *
     * Esta operação realiza as seguintes etapas de forma transacional:
     * <ul>
     *   <li>Recupera os dados da receita (valor e conta de origem)</li>
     *   <li>Subtrai o valor do saldo da conta de origem</li>
     *   <li>Remove o registro da receita</li>
     * </ul>
     *
     * Caso qualquer etapa falhe, nenhuma modificação será persistida, garantindo a integridade
     * dos dados.
     *
     * @param id o identificador único da receita a ser excluída
     * @throws DBException se a receita não for encontrada ou ocorrer algum erro
     *         durante o processo de exclusão e atualização do saldo
     */
    public void delete(Long id) throws DBException {
        String selectSql = "SELECT ORIGIN_ACCOUNT_ID, AMOUNT FROM T_FIN_INCOME WHERE ID = ?";
        String updateAccountSql = "UPDATE T_FIN_ACCOUNT SET BALANCE = BALANCE - ? WHERE ID = ?";
        String deleteSql = "DELETE FROM T_FIN_INCOME WHERE ID = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            Long accountId = null;
            double amount = 0;

            // 1. Buscar dados da receita
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    accountId = rs.getLong("ORIGIN_ACCOUNT_ID");
                    amount = rs.getDouble("AMOUNT");
                } else {
                    throw new DBException("Receita não encontrada para exclusão.");
                }
            }

            // 2. Atualizar saldo da conta (subtrair valor da receita)
            try (PreparedStatement ps = conn.prepareStatement(updateAccountSql)) {
                ps.setDouble(1, amount);
                ps.setLong(2, accountId);
                ps.executeUpdate();
            }

            // 3. Excluir a receita
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new DBException("Erro ao excluir receita e atualizar saldo da conta", e);
        }
    }

    public Income fromResultSet(ResultSet rs) {
        try {
            return new Income(
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
                rs.getTimestamp("CREATED_AT").toLocalDateTime()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
