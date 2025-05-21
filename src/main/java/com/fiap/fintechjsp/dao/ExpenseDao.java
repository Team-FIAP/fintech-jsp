package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDao implements BaseDao<Expense, Long> {
    @Override
    public Expense findById(Long aLong) {
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
    public Expense insert(Expense entity) throws DBException {
        return null;
    }

    @Override
    public Expense update(Expense entity) throws DBException {
        return null;
    }

    @Override
    public void delete(Expense entity) throws DBException {

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
}
