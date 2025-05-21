package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO responsável pelas operações de persistência da entidade {@link User}.
 * Implementa as operações básicas de CRUD e verificação de existência por CPF e username.
 */
public class UserDao implements BaseDao<User, Long> {

    /**
     * Busca um usuário pelo seu ID.
     *
     * @param id Identificador do usuário
     * @return Objeto {@link User} correspondente, ou {@code null} se não encontrado
     */
    @Override
    public User findById(Long id) {
        String sql = "SELECT * FROM t_fin_user WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

    /**
     * Verifica se existe um usuário cadastrado com o username (e-mail) informado.
     *
     * @param username Nome de usuário (e-mail)
     * @return {@code true} se o usuário existir, {@code false} caso contrário
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT id FROM t_fin_user WHERE username = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Verifica se existe um usuário cadastrado com o CPF informado.
     *
     * @param cpf CPF do usuário
     * @return {@code true} se o CPF estiver cadastrado, {@code false} caso contrário
     */
    public boolean existsByCpf(String cpf) {
        String sql = "SELECT id FROM t_fin_user WHERE cpf = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cpf);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Retorna uma lista com todos os usuários cadastrados.
     *
     * @return Lista de usuários (atualmente retorna lista vazia)
     */
    @Override
    public List<User> findAll() {
        // Não implementado ainda
        return List.of();
    }

    /**
     * Insere um novo usuário no banco de dados.
     *
     * @param entity Objeto {@link User} a ser inserido
     * @return O usuário inserido (com dados completos), ou {@code null} se houve falha
     * @throws DBException Em caso de erro de banco de dados
     */
    @Override
    public User insert(User entity) throws DBException {
        String sql = "INSERT INTO t_fin_user (name, username, password, cpf) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setString(2, entity.getUsername());
            ps.setString(3, entity.getPassword());
            ps.setString(4, entity.getCpf());
            ps.executeUpdate();

            // Recupera o último ID gerado (via sequence + trigger)
            return findByUsername(entity.getUsername());

        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    /**
     * Atualiza os dados de um usuário existente.
     *
     * @param entity Objeto {@link User} com os novos dados
     * @return O usuário atualizado, ou {@code null} se não encontrado
     * @throws DBException Em caso de erro de banco de dados
     */
    @Override
    public User update(User entity) throws DBException {
        String sql = "UPDATE t_fin_user SET name = ?, username = ?, password = ?, cpf = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setString(2, entity.getUsername());
            ps.setString(3, entity.getPassword());
            ps.setString(4, entity.getCpf());
            ps.setLong(5, entity.getId());

            ps.executeUpdate();
            return findById(entity.getId());

        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    /**
     * Remove um usuário do banco de dados.
     *
     * @param entity Usuário a ser removido
     * @throws DBException Em caso de erro de banco de dados
     */
    @Override
    public void delete(User entity) throws DBException {
        String sql = "DELETE FROM t_fin_user WHERE id = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, entity.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    /**
     * Constrói um objeto {@link User} a partir de um {@link ResultSet}.
     *
     * @param rs Resultado da consulta
     * @return Objeto {@link User} preenchido
     */
    private User fromResultSet(ResultSet rs) {
        try {
            return new User(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("cpf"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Busca um usuário pelo username (e-mail).
     *
     * @param username E-mail do usuário
     * @return Objeto {@link User} correspondente ou {@code null}
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM t_fin_user WHERE username = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return fromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
