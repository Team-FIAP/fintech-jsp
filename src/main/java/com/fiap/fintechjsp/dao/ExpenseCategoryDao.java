package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.exception.EntityNotFoundException;
import com.fiap.fintechjsp.model.Account;
import com.fiap.fintechjsp.model.ExpenseCategory;
import com.fiap.fintechjsp.model.ExpenseCategoryType;
import com.fiap.fintechjsp.model.Income;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseCategoryDao implements BaseDao<ExpenseCategory, Long> {

    @Override
    public ExpenseCategory findById(Long id) {
        String sql = "SELECT * FROM T_FIN_EXPENSE_CATEGORY WHERE ID = ?";

        try (Connection connection = ConnectionManager.getInstance().getConnection()) {
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            ResultSet result = stm.executeQuery();
            if (!result.next()) {
                throw new EntityNotFoundException(id);
            }
            return fromResultSet(result);

        }
    } catch (SQLException e) {
        throw new DBException(e);
    }
}

    @Override
    public List<ExpenseCategory> findAll() {
        List<ExpenseCategory> expenseCategories = new ArrayList<>();
        String sql = "SELECT * FROM T_FIN_EXPENSE_CATEGORY";

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                expenseCategories.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expenseCategories;
    }

    @Override
    public ExpenseCategory insert(ExpenseCategory entity) throws DBException {
        return null;
    }

    @Override
    public ExpenseCategory update(ExpenseCategory entity) throws DBException {
        return null;
    }

    @Override
    public void delete(ExpenseCategory entity) throws DBException {

    }

    private ExpenseCategory fromResultSet(ResultSet rs) {
        try {
            return new ExpenseCategory(
                    rs.getLong("ID"),
                    rs.getString("NAME"),
                    ExpenseCategoryType.valueOf(rs.getString("TYPE")),
                    rs.getString("ICON"),
                    rs.getString("COLOR"),
                    rs.getTimestamp("CREATED_AT").toLocalDateTime()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

