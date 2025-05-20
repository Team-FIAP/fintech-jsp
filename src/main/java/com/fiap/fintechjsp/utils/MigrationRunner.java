package com.fiap.fintechjsp.utils;

import com.fiap.fintechjsp.dao.ConnectionManager;
import com.fiap.fintechjsp.exception.MigrationException;
import jakarta.servlet.ServletContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class MigrationRunner {
    public static void runMigrations(ServletContext context) throws Exception {
        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            createMigrationsTableIfNotExist(conn);
            Set<String> applied = getAppliedMigrations(conn);

            String realPath = context.getRealPath("/WEB-INF/migrations");
            Path path = Paths.get(realPath);
            Files.list(path)
                    .filter(p -> p.toString().endsWith(".sql"))
                    .sorted()
                    .forEach(file -> {
                        String filename = file.getFileName().toString();
                        if (!applied.contains(filename)) {
                            System.out.println("Rodando migration: " + filename);
                            try {
                                String sql = Files.readString(file);

                                String[] statements = sql.split(";");
                                for (String statement : statements) {
                                    String trimmed = statement.trim();
                                    if (!trimmed.isEmpty()) {
                                        try (Statement stmt = conn.createStatement()) {
                                            stmt.execute(trimmed);
                                        }
                                    }
                                }

                                saveMigrations(conn, filename);
                            } catch (Exception e) {
                                throw new MigrationException(filename, e.getMessage());
                            }
                        }
                    });
        }
    }

    private static void createMigrationsTableIfNotExist(Connection conn) throws SQLException {
        String sql = "BEGIN " +
                "EXECUTE IMMEDIATE 'CREATE TABLE MIGRATIONS (filename VARCHAR2(255) PRIMARY KEY, applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)'; " +
                "EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF; " +
                "END;";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static Set<String> getAppliedMigrations(Connection conn) throws SQLException {
        Set<String> applied = new HashSet<>();
        String sql = "SELECT filename FROM MIGRATIONS";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                applied.add(rs.getString("filename"));
            }
        }

        return applied;
    }

    private static void saveMigrations(Connection conn, String filename) throws SQLException {
        String sql = "INSERT INTO MIGRATIONS (filename) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, filename);
            stmt.executeUpdate();
        }
    }
}
