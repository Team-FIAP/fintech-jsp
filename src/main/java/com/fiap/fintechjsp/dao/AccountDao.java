package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.Account;
import com.fiap.fintechjsp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDao implements BaseDao<Account, Long> {
    @Override
    public Account findById(Long id) {
        if (id == null) {
            return null;
        }

        String sql = "SELECT\n" +
                "a.ID,\n" +
                "a.NAME,\n" +
                "a.BALANCE,\n" +
                " a.CREATED_AT,\n" +
                "u.ID user_id,\n" +
                "u.NAME user_name,\n" +
                "u.USERNAME user_username,\n" +
                "u.PASSWORD user_password,\n" +
                "u.CPF user_cpf,\n" +
                "u.CREATED_AT user_created_at\n" +
                "FROM T_FIN_ACCOUNT a\n" +
                "INNER JOIN T_FIN_USER u ON a.USER_ID = u.ID\n" +
                "WHERE a.ID = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    Account account = fromResultSet(resultSet);
                    System.out.println("Conta encontrada: " + account.getId() + " - " + account.getName());
                    return account;
                } else {
                    System.out.println("Nenhuma conta encontrada com ID: " + id);
                    return null; // Não lançar exceção, apenas retornar null
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRO SQL ao buscar conta: " + e.getMessage());
            e.printStackTrace();
            return null; // Retornar null em vez de lançar exceção
        } catch (Exception e) {
            System.err.println("ERRO GERAL ao buscar conta: " + e.getMessage());
            e.printStackTrace();
            return null; // Retornar null em vez de lançar exceção
        }
    }

    public interface AccountDAO {
        Account findById(Long id);
        Account insert(Account account);
        // outros métodos necessários...
    }

    @Override
    public List<Account> findAll() {
        return List.of();
    }

    public List<Account> findAllByUserId(Long userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = """
            SELECT
                a.ID,
                a.NAME,
                a.BALANCE,
                a.CREATED_AT,
                u.ID user_id,
                u.NAME user_name,
                u.USERNAME user_username,
                u.PASSWORD user_password,
                u.CPF user_cpf,
                u.CREATED_AT user_created_at
            FROM T_FIN_ACCOUNT a
            INNER JOIN T_FIN_USER u ON a.USER_ID = u.ID
            WHERE a.USER_ID = ?
        """;

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                accounts.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    @Override
    public Account insert(Account entity) throws DBException {
        return null;
    }

    @Override
    public Account update(Account entity) throws DBException {
        return null;
    }

    @Override
    public void delete(Account entity) throws DBException {

    }

    private Account fromResultSet(ResultSet rs) {
        try {
            return new Account(
                    rs.getLong("ID"),
                    rs.getString("NAME"),
                    rs.getDouble("BALANCE"),
                    new User(
                            rs.getLong("user_id"),
                            rs.getString("user_name"),
                            rs.getString("user_username"),
                            rs.getString("user_password"),
                            rs.getString("user_cpf"),
                            rs.getTimestamp("user_created_at").toLocalDateTime()
                    ),
                    rs.getTimestamp("CREATED_AT").toLocalDateTime()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
