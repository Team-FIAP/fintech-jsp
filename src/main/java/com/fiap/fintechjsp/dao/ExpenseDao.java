package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.*;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class ExpenseDao implements BaseDao<Expense, Long> {
    @Override
    public Expense findById(Long id) {

        StringBuilder sql = new StringBuilder("""
                SELECT
                e.ID,
                e.DESCRIPTION,
                e.OBSERVATION,
                e.AMOUNT,
                e."date",
                e.CREATED_AT,
                oa.ID origin_account_id,
                oa.NAME origin_account_name,
                oa.BALANCE origin_account_balance,
                oa.CREATED_AT origin_account_created_at,
                ec.ID expense_category_id,
                ec.NAME expense_category_name,
                ec.TYPE expense_category_type,
                ec.ICON expense_category_icon,
                ec.COLOR expense_category_color,
                ec.CREATED_AT expense_category_created_at
                FROM T_FIN_EXPENSE e
                INNER JOIN T_FIN_ACCOUNT oa ON e.ACCOUNT_ID = oa.ID
                INNER JOIN T_FIN_EXPENSE_CATEGORY ec ON e.CATEGORY_ID = ec.ID
                WHERE e.ID = ?
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
    public List<Expense> findAll() {
        return List.of();
    }

    public List<Expense> findAll(LocalDate startDate, LocalDate endDate, Long accountId, Long userId) {
        List<Expense> expenses = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                    SELECT 
                        e.ID,
                        e.DESCRIPTION,
                        e.OBSERVATION,
                        e.AMOUNT,
                        e."DATE",
                        e.CREATED_AT,
                        oa.ID origin_account_id,
                        oa.NAME origin_account_name,
                        oa.BALANCE origin_account_balance,
                        oa.CREATED_AT origin_account_created_at,
                        ec.ID expense_category_id,
                        ec.NAME expense_category_name,
                        ec.TYPE expense_category_type,
                        ec.ICON expense_category_icon,
                        ec.COLOR expense_category_color,
                        ec.CREATED_AT expense_category_created_at
                    FROM T_FIN_EXPENSE e
                    INNER JOIN T_FIN_ACCOUNT oa ON e.ORIGIN_ACCOUNT_ID = oa.ID
                    INNER JOIN T_FIN_EXPENSE_CATEGORY ec ON e.CATEGORY_ID = ec.ID
                    WHERE 1=1
                """);

        if (startDate != null) {
            sql.append(" AND TRUNC(e.\"DATE\") >= ?");
        }

        if (endDate != null) {
            sql.append(" AND TRUNC(e.\"DATE\") <= ?");
        }

        if (accountId != null) {
            sql.append(" AND e.ORIGIN_ACCOUNT_ID = ?");
        }

        if (userId != null) {
            sql.append(" AND oa.USER_ID = ?");
        }

        sql.append(" ORDER BY e.\"DATE\" DESC");

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
                expenses.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expenses;
    }

    @Override
    public Expense insert(Expense expense) throws DBException {
        String sql = """
                    INSERT INTO T_FIN_EXPENSE (AMOUNT, "DATE", DESCRIPTION, OBSERVATION, ORIGIN_ACCOUNT_ID, CATEGORY_ID) 
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConnectionManager.getInstance().getConnection()) {
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setDouble(1, expense.getAmount());
                stm.setDate(2, java.sql.Date.valueOf(expense.getDate()));
                stm.setString(3, expense.getDescription());
                stm.setString(4, expense.getObservation());
                stm.setLong(5, expense.getOriginAccount().getId());
                stm.setLong(6, expense.getCategory().getId());
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
    public Expense update(Expense expense) throws DBException {
        String sql = """
                    UPDATE T_FIN_EXPENSE SET AMOUNT = ?, "DATE" = ?, DESCRIPTION = ?, OBSERVATION = ?, ORIGIN_ACCOUNT_ID = ?, CATEGORY_ID = ?
                    WHERE ID = ?
                """;
        try (Connection connection = ConnectionManager.getInstance().getConnection()) {
            try (PreparedStatement stm = connection.prepareStatement(sql)) {
                stm.setDouble(1, expense.getAmount());
                stm.setDate(2, java.sql.Date.valueOf(expense.getDate()));
                stm.setString(3, expense.getDescription());
                stm.setString(4, expense.getObservation());
                stm.setLong(5, expense.getOriginAccount().getId());
                stm.setLong(6, expense.getCategory().getId());
                stm.setLong(7, expense.getId());

                stm.executeUpdate();
            }
            return findById(expense.getId());
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }


    //TODO: Acredito que seja necessário modificar a interface do baseDao referente ao delete, não alterei devido ao receio de ser uma parte crucial que vale o debate.
    @Override
    public void delete(Expense entity) throws DBException {

    }


    /**
     * Exclui uma despesa do banco de dados e atualiza o saldo da conta de origem,
     * adicionando o valor da despesa de volta à conta.
     * <p>
     * Esta operação realiza as seguintes etapas de forma transacional:
     * <ul>
     *   <li>Recupera os dados da despesa (valor e conta de origem)</li>
     *   <li>Adiciona o valor ao saldo da conta de origem</li>
     *   <li>Remove o registro da despesa</li>
     * </ul>
     * <p>
     * Caso qualquer etapa falhe, nenhuma modificação será persistida, garantindo a integridade
     * dos dados.
     *
     * @param id o identificador único da despesa a ser excluída
     * @throws DBException se a despesa não for encontrada ou ocorrer algum erro
     *                     durante o processo de exclusão e atualização do saldo
     */
    public void delete(Long id) throws DBException {
        String selectSql = "SELECT ORIGIN_ACCOUNT_ID, AMOUNT FROM T_FIN_EXPENSE WHERE ID = ?";
        String updateAccountSql = "UPDATE T_FIN_ACCOUNT SET BALANCE = BALANCE - ? WHERE ID = ?";
        String deleteSql = "DELETE FROM T_FIN_EXPENSE WHERE ID = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            Long accountId = null;
            double amount = 0;

            // 1. Buscar dados da despesa
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    accountId = rs.getLong("ORIGIN_ACCOUNT_ID");
                    amount = rs.getDouble("AMOUNT");
                } else {
                    throw new DBException("Despesa não encontrada para exclusão.");
                }
            }

            // 2. Atualizar saldo da conta (restituir o valor)
            try (PreparedStatement ps = conn.prepareStatement(updateAccountSql)) {
                ps.setDouble(1, amount);
                ps.setLong(2, accountId);
                ps.executeUpdate();
            }

            // 3. Excluir a despesa
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new DBException("Erro ao excluir despesa e atualizar saldo da conta", e);
        }
    }

    public Expense fromResultSet(ResultSet rs) {
        try {
            return new Expense(
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
                    new ExpenseCategory(
                            rs.getLong("expense_category_id"),
                            rs.getString("expense_category_name"),
                            ExpenseCategoryType.valueOf(rs.getString("expense_category_type")),
                            rs.getString("expense_category_color"),
                            rs.getString("expense_category_icon"),
                            rs.getTimestamp("expense_category_created_at").toLocalDateTime()
                    )
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retorna o total de despesas de um usuário em um determinado período.
     *
     * @param user O usuário cujas despesas serão consultadas.
     * @param startDate A data inicial do período.
     * @param endDate A data final do período.
     * @return O valor total das despesas no intervalo especificado. Retorna 0.0 se não houver despesas ou em caso de erro.
     */
    public double getTotalExpensesForUserByPeriod(User user, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT SUM(e.AMOUNT)
            FROM T_FIN_EXPENSE e
            INNER JOIN T_FIN_ACCOUNT a ON e.ORIGIN_ACCOUNT_ID = a.ID 
            WHERE a.USER_ID = ?
            AND e."DATE" BETWEEN ? AND ?
        """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, user.getId());
            ps.setDate(2, java.sql.Date.valueOf(startDate));
            ps.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public List<Map<String, Object>> getTotalExpensesByCategoryForUserInPeriod(User loggedUser, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> expensesByCategory = new ArrayList<>();
        String sql = """
            SELECT ec.NAME category, ec.COLOR color, SUM(e.AMOUNT) total
            FROM T_FIN_EXPENSE e
            INNER JOIN T_FIN_EXPENSE_CATEGORY ec ON e.CATEGORY_ID = ec.ID
            INNER JOIN T_FIN_ACCOUNT a ON e.ORIGIN_ACCOUNT_ID = a.ID
            WHERE a.USER_ID = ?
            AND e."DATE" BETWEEN ? AND ?
            GROUP BY ec.NAME, ec.COLOR
        """;

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setLong(1, loggedUser.getId());
            ps.setDate(2, java.sql.Date.valueOf(startDate));
            ps.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("category", rs.getString("category"));
                item.put("total", rs.getDouble("total"));
                item.put("color", rs.getString("color"));

                expensesByCategory.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return expensesByCategory;
    }

    public Map<String, Double> getTotalExpensesByCategoryTypeForUserInPeriod(User loggedUser, LocalDate startDate, LocalDate endDate) {
        Map<String, Double> expensesByCategoryType = new HashMap<>();

        String sql = """
            SELECT ec.TYPE category_type, SUM(e.AMOUNT) total
            FROM T_FIN_EXPENSE e
            INNER JOIN T_FIN_EXPENSE_CATEGORY ec ON e.CATEGORY_ID = ec.ID
            INNER JOIN T_FIN_ACCOUNT a ON e.ORIGIN_ACCOUNT_ID = a.ID
            WHERE a.USER_ID = ?
            AND e."DATE" BETWEEN ? AND ?
            GROUP BY ec.TYPE
        """;

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setLong(1, loggedUser.getId());
            ps.setDate(2, java.sql.Date.valueOf(startDate));
            ps.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                expensesByCategoryType.put(rs.getString("category_type"), rs.getDouble("total"));
            }

            // Garante que ambas as chaves estejam presentes
            expensesByCategoryType.putIfAbsent("ESSENTIAL", 0.0);
            expensesByCategoryType.putIfAbsent("NON_ESSENTIAL", 0.0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return expensesByCategoryType;
    }

    /**
     * Retorna o total de despesas mês a mês para um usuário em um ano específico.
     *
     * @param loggedUser O usuário logado.
     * @param year O ano de referência (ex: 2025).
     * @return Lista com 12 posições representando o total de despesas de janeiro a dezembro.
     */
    public List<Double> getMonthlyExpensesForUserByYear(User loggedUser, int year) {
        List<Double> monthlyExpenses = new ArrayList<>(Collections.nCopies(12, 0.0));

        String sql = """
            SELECT EXTRACT(MONTH FROM e."DATE") month, SUM(e.AMOUNT) total
            FROM T_FIN_EXPENSE e
            INNER JOIN T_FIN_ACCOUNT a ON e.ORIGIN_ACCOUNT_ID = a.ID
            WHERE a.USER_ID = ?
            AND EXTRACT(YEAR FROM e."DATE") = ?
            GROUP BY EXTRACT(MONTH FROM e."DATE")
        """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, loggedUser.getId());
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int month = rs.getInt("month");
                double total = rs.getDouble("total");
                monthlyExpenses.set(month - 1, total);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return monthlyExpenses;
    }
}
