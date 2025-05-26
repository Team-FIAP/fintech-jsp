package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.exception.EntityNotFoundException;
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
            WHERE a.ID = ?
        """;

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                ResultSet resultSet = stmt.executeQuery();
                if (!resultSet.next()) {
                    throw new EntityNotFoundException(id);
                }
                return fromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM T_FIN_ACCOUNT";

        try (Connection conn = ConnectionManager.getInstance().getConnection()){
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet result = stmt.executeQuery();
                while (result.next()) {
                    accounts.add(fromResultSet(result));
                }
            }

            return accounts;
        } catch (SQLException e) {
            throw new DBException(e);
        }

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

    public Account insert(Account account) throws DBException {
        String sql = "INSERT INTO T_FIN_ACCOUNT (NAME, BALANCE, USER_ID) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account.getName());
            ps.setDouble(2, account.getBalance());
            ps.setLong(3, account.getUser().getId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                long generatedId = rs.getLong(1);
                return findById(generatedId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException(e);
        }
        return null;
    }

    @Override
    public Account update(Account account) {
        String sql = "UPDATE T_FIN_ACCOUNT SET NAME = ?, BALANCE  = ? WHERE ID = ? ";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, account.getName());
                ps.setDouble(2, account.getBalance());
                ps.setLong(3, account.getId());

                ps.executeUpdate();
                return (findById(account.getId()));

            } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public void delete(Account account) throws DBException {
        String sql = "DELETE FROM T_FIN_ACCOUNT WHERE ID = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, account.getId());
                ps.executeUpdate();

            } catch (SQLException e) {
            throw new DBException(e);
        }
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

    public boolean existsByName(String name) {
        String sql = "SELECT * FROM T_FIN_ACCOUNT WHERE UPPER(NAME) = UPPER(?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ResultSet resultSet = ps.executeQuery();
                return resultSet.next();

            } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}