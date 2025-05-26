package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.Account;
import com.fiap.fintechjsp.model.Income;
import com.fiap.fintechjsp.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IncomeDao implements BaseDao<Income, Long> {
    @Override
    public Income findById(Long id) {
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
                    WHERE i.ID = ?
                """);

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return fromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public Income insert(Income income) throws DBException {
        String sql = """
            INSERT INTO T_FIN_INCOME (AMOUNT, "DATE", DESCRIPTION, OBSERVATION, ORIGIN_ACCOUNT_ID) VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection connection = ConnectionManager.getInstance().getConnection()) {
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setDouble(1, income.getAmount());
                stm.setDate(2, java.sql.Date.valueOf(income.getDate()));
                stm.setString(3, income.getDescription());
                stm.setString(4, income.getObservation());
                stm.setLong(5, income.getOriginAccount().getId());

                stm.executeUpdate();

                ResultSet rs = stm.getGeneratedKeys();
                if (rs.next()) {
                    long generatedId = rs.getLong(1);
                    return findById(generatedId);
                }
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
        return null;
    }

    @Override
    public Income update(Income income) throws DBException {
        String sql = """
            UPDATE T_FIN_INCOME SET AMOUNT = ?, "DATE" = ?, DESCRIPTION = ?, OBSERVATION = ? WHERE ID = ?
        """;

        try (Connection connection = ConnectionManager.getInstance().getConnection()) {
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setDouble(1, income.getAmount());
                stm.setDate(2, java.sql.Date.valueOf(income.getDate()));
                stm.setString(3, income.getDescription());
                stm.setString(4, income.getObservation());
                stm.setLong(5, income.getId());

                stm.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
        return findById(income.getId());
    }

    @Override
    public void delete(Income entity) throws DBException {

    }

    /**
     * Exclui uma receita (income) do banco de dados e atualiza o saldo da conta de origem,
     * subtraindo o valor da receita excluída.
     * <p>
     * Esta operação realiza as seguintes etapas de forma transacional:
     * <ul>
     *   <li>Recupera os dados da receita (valor e conta de origem)</li>
     *   <li>Subtrai o valor do saldo da conta de origem</li>
     *   <li>Remove o registro da receita</li>
     * </ul>
     * <p>
     * Caso qualquer etapa falhe, nenhuma modificação será persistida, garantindo a integridade
     * dos dados.
     *
     * @param id o identificador único da receita a ser excluída
     * @throws DBException se a receita não for encontrada ou ocorrer algum erro
     *                     durante o processo de exclusão e atualização do saldo
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

    /**
     * Retorna o total de receitas de um usuário dentro de um período específico.
     *
     * @param loggedUser O usuário cujas receitas devem ser somadas.
     * @param startDate  A data inicial do período (inclusive).
     * @param endDate    A data final do período (inclusive).
     * @return O valor total das receitas no período informado. Retorna 0.0 se não houver receitas ou em caso de erro.
     */
    public double getTotalIncomesForUserByPeriod(User loggedUser, LocalDate startDate, LocalDate endDate) {
        String sql = """
                    SELECT SUM(i.AMOUNT)
                    FROM T_FIN_INCOME i
                    INNER JOIN T_FIN_ACCOUNT a ON i.ORIGIN_ACCOUNT_ID = a.ID
                    WHERE a.USER_ID = ?
                    AND i."DATE" BETWEEN ? AND ?
                """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, loggedUser.getId());
            ps.setDate(2, java.sql.Date.valueOf(startDate));
            ps.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1); // índice começa em 1
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Retorna o total de receitas mês a mês para um usuário em um ano específico.
     *
     * @param loggedUser O usuário logado.
     * @param year       O ano de referência (ex: 2025).
     * @return Lista com 12 posições representando o total de receitas de janeiro a dezembro.
     */
    public List<Double> getMonthlyIncomesForUserByYear(User loggedUser, int year) {
        List<Double> monthlyIncomes = new ArrayList<>(Collections.nCopies(12, 0.0));

        String sql = """
                    SELECT EXTRACT(MONTH FROM i."DATE") month, SUM(i.AMOUNT) total
                    FROM T_FIN_INCOME i
                    INNER JOIN T_FIN_ACCOUNT a ON i.ORIGIN_ACCOUNT_ID = a.ID
                    WHERE a.USER_ID = ?
                    AND EXTRACT(YEAR FROM i."DATE") = ?
                    GROUP BY EXTRACT(MONTH FROM i."DATE")
                """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, loggedUser.getId());
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int month = rs.getInt("month");
                double total = rs.getDouble("total");
                monthlyIncomes.set(month - 1, total);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return monthlyIncomes;
    }
}
