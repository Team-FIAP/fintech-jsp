package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.Account;
import com.fiap.fintechjsp.model.Transfer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável pelas operações de persistência da entidade {@link com.fiap.fintechjsp.model.Transfer}.
 * Implementa as operações básicas de CRUD e verificação de existência por CPF e username.
 */
public class TransferDao implements BaseDao<Transfer, Long> {

    /**
     * Busca uma transferência pelo seu ID.
     *
     * @param id Identificador da transferência
     * @return Objeto {@link Transfer} correspondente, ou {@code null} se não encontrado
     */
    @Override
    public Transfer findById(Long id) {
        throw new RuntimeException("Não implementado");
    }

    @Override
    public List<Transfer> findAll() {
        return List.of();
    }

    /**
     * Retorna uma lista com todas as transferências cadastradas.
     *
     * @return Lista de transferências
     */
    public List<Transfer> findAll(LocalDate startDate, LocalDate endDate, Long accountId, Long userId) {
        List<Transfer> transfers = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT
                t.ID,
                t.DESCRIPTION,
                t.OBSERVATION,
                t.AMOUNT,
                t."DATE",
                t.CREATED_AT,
                oa.ID origin_account_id,
                oa.NAME origin_account_name,
                oa.BALANCE origin_account_balance,
                oa.CREATED_AT origin_account_created_at,
                da.ID destination_account_id,
                da.NAME destination_account_name,
                da.BALANCE destination_account_balance,
                da.CREATED_AT destination_account_created_at
            FROM T_FIN_TRANSFER t
            INNER JOIN T_FIN_ACCOUNT oa ON t.ORIGIN_ACCOUNT_ID = oa.ID
            INNER JOIN T_FIN_ACCOUNT da ON t.DESTINATION_ACCOUNT_ID = da.ID
            WHERE 1=1
        """);

        if (startDate != null) {
            sql.append(" AND TRUNC(t.\"DATE\") >= ?");
        }

        if (endDate != null) {
            sql.append(" AND TRUNC(t.\"DATE\") <= ?");
        }

        if (accountId != null) {
            sql.append(" AND (t.ORIGIN_ACCOUNT_ID = ? OR t.DESTINATION_ACCOUNT_ID = ?)");
        }

        if (userId != null) {
            sql.append(" AND (oa.USER_ID = ? OR da.USER_ID = ?)");
        }

        sql.append(" ORDER BY t.\"DATE\" DESC");

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
                ps.setLong(index++, accountId);
            }

            if (userId != null) {
                ps.setLong(index++, userId);
                ps.setLong(index, userId);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                transfers.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transfers;
    }

    /**
     * Insere uma nova transferência no banco de dados.
     *
     * @param entity Objeto {@link Transfer} a ser inserido
     * @return A transferência inserida (com dados completos), ou {@code null} se houve falha
     * @throws DBException Em caso de erro de banco de dados
     */
    @Override
    public Transfer insert(Transfer entity) throws DBException {
        String insertSql = """
            INSERT INTO T_FIN_TRANSFER (DESCRIPTION, AMOUNT, "DATE", ORIGIN_ACCOUNT_ID, DESTINATION_ACCOUNT_ID) 
            VALUES (?, ?, ?, ?, ?)
        """;

        String updateOriginSql = "UPDATE T_FIN_ACCOUNT SET BALANCE = BALANCE - ? WHERE ID = ?";
        String updateDestSql = "UPDATE T_FIN_ACCOUNT SET BALANCE = BALANCE + ? WHERE ID = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Inicia transação

            // 1. Subtrair saldo da conta de origem
            try (PreparedStatement ps = conn.prepareStatement(updateOriginSql)) {
                ps.setDouble(1, entity.getAmount());
                ps.setLong(2, entity.getOriginAccount().getId());
                ps.executeUpdate();
            }

            // 2. Adicionar saldo na conta de destino
            try (PreparedStatement ps = conn.prepareStatement(updateDestSql)) {
                ps.setDouble(1, entity.getAmount());
                ps.setLong(2, entity.getDestinationAccount().getId());
                ps.executeUpdate();
            }

            // 3. Inserir a transferência
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, entity.getDescription());
                ps.setDouble(2, entity.getAmount());
                ps.setDate(3, java.sql.Date.valueOf(entity.getDate()));
                ps.setLong(4, entity.getOriginAccount().getId());
                ps.setLong(5, entity.getDestinationAccount().getId());

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    long generatedId = rs.getLong(1);
                    entity.setId(generatedId);
                }
            }

            conn.commit(); // Confirma transação
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("Erro ao inserir transferência e atualizar saldos", e);
        }
    }

    /**
     * Atualiza os dados de uma transferência existente.
     *
     * @param entity Objeto {@link Transfer} com os novos dados
     * @return A transferência atualizada, ou {@code null} se não encontrada
     * @throws DBException Em caso de erro de banco de dados
     */
    @Override
    public Transfer update(Transfer entity) throws DBException {
        throw new RuntimeException("Não implementado");
    }

    /**
     * Remove uma transferência do banco de dados.
     *
     * @param entity Transferência a ser removida
     * @throws DBException Em caso de erro de banco de dados
     */
    @Override
    public void delete(Transfer entity) throws DBException {
        throw new RuntimeException("Não implementado");
    }

    /**
     * Exclui uma transferência existente do banco de dados, revertendo o impacto financeiro
     * nas contas envolvidas.
     *
     * Esta operação realiza as seguintes etapas de forma transacional:
     * <ul>
     *   <li>Recupera os dados da transferência (contas de origem e destino, valor)</li>
     *   <li>Adiciona o valor transferido de volta à conta de origem</li>
     *   <li>Subtrai o valor transferido da conta de destino</li>
     *   <li>Remove o registro da transferência</li>
     * </ul>
     *
     * Caso qualquer etapa falhe, nenhuma modificação será persistida, garantindo integridade
     * dos dados.
     *
     * @param id o identificador único da transferência a ser excluída
     * @throws DBException se a transferência não for encontrada ou ocorrer algum erro
     *         durante o processo de exclusão e atualização de saldos
     */
    public void delete(Long id) throws DBException {
        String selectSql = "SELECT ORIGIN_ACCOUNT_ID, DESTINATION_ACCOUNT_ID, AMOUNT FROM T_FIN_TRANSFER WHERE ID = ?";
        String updateOriginSql = "UPDATE T_FIN_ACCOUNT SET BALANCE = BALANCE + ? WHERE ID = ?";
        String updateDestSql = "UPDATE T_FIN_ACCOUNT SET BALANCE = BALANCE - ? WHERE ID = ?";
        String deleteSql = "DELETE FROM T_FIN_TRANSFER WHERE ID = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Inicia transação

            Long originAccountId = null;
            Long destinationAccountId = null;
            double amount = 0;

            // 1. Buscar os dados da transferência
            try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                selectPs.setLong(1, id);
                ResultSet rs = selectPs.executeQuery();

                if (rs.next()) {
                    originAccountId = rs.getLong("ORIGIN_ACCOUNT_ID");
                    destinationAccountId = rs.getLong("DESTINATION_ACCOUNT_ID");
                    amount = rs.getDouble("AMOUNT");
                } else {
                    throw new DBException("Transferência não encontrada para exclusão.");
                }
            }

            // 2. Devolver saldo à conta de origem
            try (PreparedStatement ps = conn.prepareStatement(updateOriginSql)) {
                ps.setDouble(1, amount);
                ps.setLong(2, originAccountId);
                ps.executeUpdate();
            }

            // 3. Subtrair saldo da conta de destino
            try (PreparedStatement ps = conn.prepareStatement(updateDestSql)) {
                ps.setDouble(1, amount);
                ps.setLong(2, destinationAccountId);
                ps.executeUpdate();
            }

            // 4. Excluir a transferência
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            conn.commit(); // Confirma transação

        } catch (SQLException e) {
            throw new DBException("Erro ao excluir transferência e atualizar saldos", e);
        }
    }

    /**
     * Constrói um objeto {@link Transfer} a partir de um {@link ResultSet}.
     *
     * @param rs Resultado da consulta
     * @return Objeto {@link Transfer} preenchido
     */
    private Transfer fromResultSet(ResultSet rs) {
        try {
            return new Transfer(
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
                new Account(
                    rs.getLong("destination_account_id"),
                    rs.getString("destination_account_name"),
                    rs.getDouble("destination_account_balance"),
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
