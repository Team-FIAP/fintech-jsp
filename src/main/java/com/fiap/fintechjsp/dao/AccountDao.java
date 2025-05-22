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

    @Override
    public Account findById(Long id) {
        String sql = "SELECT\n" +
                "                a.ID,\n" +
                "                a.NAME,\n" +
                "                a.BALANCE,\n" +
                "                a.CREATED_AT,\n" +
                "                u.ID user_id,\n" +
                "                u.NAME user_name,\n" +
                "                u.USERNAME user_username,\n" +
                "                u.PASSWORD user_password,\n" +
                "                u.CPF user_cpf,\n" +
                "                u.CREATED_AT user_created_at\n" +
                "            FROM T_FIN_ACCOUNT a\n" +
                "            INNER JOIN T_FIN_USER u ON a.USER_ID = u.ID " +
                "WHERE a.ID = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
            PreparedStatement ps= conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    return fromResultSet(resultSet);
                }

            } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account insert(Account account) throws DBException {
        String sql = "INSERT INTO T_FIN_ACCOUNT (NAME, BALANCE, USER_ID) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("Inserting account:");
            System.out.println("Name: " + account.getName());
            System.out.println("Balance: " + account.getBalance());
            System.out.println("User ID: " + account.getUser().getId());

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

    public List<Account> findAllByUserId(Long userId) {
        String sql = "SELECT\n" +
                "                a.ID,\n" +
                "                a.NAME,\n" +
                "                a.BALANCE,\n" +
                "                a.CREATED_AT,\n" +
                "                u.ID user_id,\n" +
                "                u.NAME user_name,\n" +
                "                u.USERNAME user_username,\n" +
                "                u.PASSWORD user_password,\n" +
                "                u.CPF user_cpf,\n" +
                "                u.CREATED_AT user_created_at\n" +
                "            FROM T_FIN_ACCOUNT a\n" +
                "            INNER JOIN T_FIN_USER u ON a.USER_ID = u.ID\n" +
                "            WHERE u.ID = ?";

        List<Account> accounts = new ArrayList<>();

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet resultSet = ps.executeQuery();

            System.out.println("Querying accounts for userId: " + userId); // Debug log
            int count = 0; // Counter for logging

            while (resultSet.next()) {
                count++;
                Account account = fromResultSet(resultSet);
                accounts.add(account);
                // Debug log
                System.out.println("Found account: ID=" + account.getId() + ", Name=" + account.getName() + ", Balance=" + account.getBalance());
            }

            System.out.println("Total accounts found: " + count); // Debug log

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }
}