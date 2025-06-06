package com.fiap.fintechjsp.dao;

import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.Account;
import com.fiap.fintechjsp.model.Income;
import com.fiap.fintechjsp.model.Investment;
import com.fiap.fintechjsp.model.InvestmentType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvestmentDao implements BaseDao<Investment, Long> {
    @Override
    public Investment findById(Long id) {
        String sql = "SELECT \n" +
                "                i.ID,\n" +
                "                i.DESCRIPTION,\n" +
                "                i.OBSERVATION,\n" +
                "                i.AMOUNT,\n" +
                "                i.\"date\",\n" +
                "                i.TYPE,\n" +
                "                i.RISK,\n" +
                "                i.LIQUIDITY,\n" +
                "                i.DUE_DATE,\n" +
                "                i.PROFITABILITY,\n" +
                "                i.CREATED_AT,\n" +
                "                oa.ID origin_account_id,\n" +
                "                oa.NAME origin_account_name,\n" +
                "                oa.BALANCE origin_account_balance,\n" +
                "                oa.CREATED_AT origin_account_created_at\n" +
                "            FROM T_FIN_INVESTMENT i\n" +
                "            INNER JOIN T_FIN_ACCOUNT oa\n" +
                "            ON i.ACCOUNT_ID = oa.ID" +
                "            WHERE i.ID = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return fromResultSet(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new DBException("Erro ao buscar investimento. " + e);
        }
        return null;
    }

    public List<Investment> findAll() {

        List<Investment> investments = new ArrayList<>();
        String sql = "SELECT * FROM T_FIN_INVESTMENT";

        try (Connection conn = ConnectionManager.getInstance().getConnection()){
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    investments.add(fromResultSet(resultSet));
                }
            }

            return investments;
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar investimentos. " + e);
        }
    }

    public List<Investment> findAllByUserId(Long userId) {
        String sql = "SELECT\n" +
                "                i.ID,\n" +
                "                i.DESCRIPTION,\n" +
                "                i.OBSERVATION,\n" +
                "                i.AMOUNT,\n" +
                "                i.\"date\",\n" +
                "                i.TYPE,\n" +
                "                i.RISK,\n" +
                "                i.LIQUIDITY,\n" +
                "                i.DUE_DATE,\n" +
                "                i.PROFITABILITY,\n" +
                "                i.CREATED_AT,\n" +
                "                oa.ID origin_account_id,\n" +
                "                oa.NAME origin_account_name,\n" +
                "                oa.BALANCE origin_account_balance,\n" +
                "                oa.CREATED_AT origin_account_created_at\n" +
                "            FROM T_FIN_INVESTMENT i\n" +
                "            INNER JOIN T_FIN_ACCOUNT oa\n" +
                "            ON i.ACCOUNT_ID = oa.ID" +
                "            WHERE oa.USER_ID = ?";

        List<Investment> investments = new ArrayList<>();

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet resultSet = ps.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investments;
    }

    /**
     * Recupera todos os investimentos registrados no banco de dados, com base nos filtros fornecidos:
     * intervalo de datas, conta de origem e usuário proprietário da conta.
     *
     * <p>Os investimentos são retornados ordenados pela data da operação em ordem decrescente.</p>
     *
     * @param startDate data inicial do intervalo de busca (inclusive); se {@code null}, não aplica filtro inicial
     * @param endDate data final do intervalo de busca (inclusive); se {@code null}, não aplica filtro final
     * @param accountId ID da conta de origem do investimento; se {@code null}, busca em todas as contas
     * @param userId ID do usuário dono da conta; se {@code null}, busca investimentos de todos os usuários
     * @param redeemed indica se o investimento já foi resgatado ou não
     * @return lista de objetos {@link Investment} que correspondem aos critérios informados; lista vazia se nenhum for encontrado
     */
    public List<Investment> findAll(LocalDate startDate, LocalDate endDate, Long accountId, Long userId, boolean redeemed) {
        List<Investment> investments = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT 
                i.ID,
                i.DESCRIPTION,
                i.OBSERVATION,
                i.AMOUNT,
                i."DATE",
                i.TYPE,
                i.RISK,
                i.LIQUIDITY,
                i.PROFITABILITY,
                i.DUE_DATE,
                i.INTEREST_RATE,
                i.REDEEMED,
                i.CREATED_AT,
                oa.id origin_account_id,
                oa.name origin_account_name,
                oa.balance origin_account_balance,
                oa.created_at origin_account_created_at
            FROM T_FIN_INVESTMENT i
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

        sql.append(" AND i.REDEEMED = ?");

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
                ps.setLong(index++, userId);
            }

            ps.setBoolean(index, redeemed);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                investments.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investments;
    }

    @Override
    public Investment insert(Investment entity) throws DBException {
        if (entity == null) {
            throw new DBException("Não é possível inserir um investimento nulo");
        }

        if (entity.getOriginAccount() == null) {
            throw new DBException("A conta de origem do investimento não pode ser nula");
        }

        String updateBalanceSql = "UPDATE T_FIN_ACCOUNT SET BALANCE = BALANCE - ? WHERE ID = ?";
        String insertSql = """
        INSERT INTO T_FIN_INVESTMENT (DESCRIPTION, OBSERVATION, AMOUNT, "DATE", TYPE, RISK, LIQUIDITY, DUE_DATE, PROFITABILITY, ORIGIN_ACCOUNT_ID, INTEREST_RATE, REDEEMED) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            // 1. Atualiza o saldo da conta de origem
            try (PreparedStatement updatePs = conn.prepareStatement(updateBalanceSql)) {
                updatePs.setDouble(1, entity.getAmount());
                updatePs.setLong(2, entity.getOriginAccount().getId());
                updatePs.executeUpdate();
            }

            // 2. Insere o investimento
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setString(1, entity.getDescription());
                insertPs.setString(2, entity.getObservation());
                insertPs.setDouble(3, entity.getAmount());
                insertPs.setDate(4, Date.valueOf(entity.getDate()));
                insertPs.setString(5, entity.getInvestmentType().getDescription());
                insertPs.setString(6, entity.getRisk());
                insertPs.setString(7, entity.getLiquidity());
                insertPs.setDate(8, Date.valueOf(entity.getDueDate()));
                insertPs.setDouble(9, entity.getProfitability());
                insertPs.setLong(10, entity.getOriginAccount().getId());
                insertPs.setDouble(11, entity.getInterestRate());
                insertPs.setBoolean(12, entity.isRedeemed());

                int affectedRows = insertPs.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    throw new DBException("A criação do investimento falhou, nenhuma linha afetada.");
                }
            }

            conn.commit();
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("Erro ao criar investimento: " + e.getMessage(), e);
        }
    }

    @Override
    public Investment update(Investment entity) throws DBException {
        String sql = """
                UPDATE T_FIN_INVESTMENT SET
                AMOUNT = ?,
                "DATE" = ?,
                TYPE = ?,
                RISK = ?,
                LIQUIDITY = ?,
                PROFITABILITY = ?,
                DUE_DATE = ?,
                DESCRIPTION = ?,
                OBSERVATION = ?
                WHERE ACCOUNT_ID = ?
        """;

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setDouble(1, entity.getAmount());
            ps.setDate(2, Date.valueOf(entity.getDate()));
            ps.setString(3, entity.getInvestmentType().getDescription());
            ps.setString(4, entity.getRisk());
            ps.setString(5, entity.getLiquidity());
            ps.setDouble(6, entity.getProfitability());
            ps.setDate(7, entity.getDueDate() != null ? Date.valueOf(entity.getDueDate()) : null);
            ps.setString(9, entity.getDescription());
            ps.setString(10, entity.getObservation());

            ps.executeUpdate();

            return findById(entity.getId());
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar investimento. " + e.getMessage());
        }
    }

    @Override
    public void delete(Investment entity) throws DBException {
        String sql = "DELETE FROM T_FIN_INVESTMENT WHERE ID = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, entity.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DBException("Erro ao deletar investimento. " + e.getMessage());
        }
    }

    /**
     * Exclui um investimento do banco de dados e atualiza o saldo da conta de origem,
     * devolvendo o valor do investimento excluído.
     *
     * Esta operação realiza as seguintes etapas de forma transacional:
     * <ul>
     *   <li>Recupera os dados do investimento (valor e conta de origem)</li>
     *   <li>Adiciona o valor de volta ao saldo da conta de origem</li>
     *   <li>Remove o registro do investimento</li>
     * </ul>
     *
     * Caso qualquer etapa falhe, nenhuma modificação será persistida, garantindo a integridade
     * dos dados.
     *
     * @param id o identificador único do investimento a ser excluído
     * @throws DBException se o investimento não for encontrado ou ocorrer algum erro
     *         durante o processo de exclusão e atualização do saldo
     */
    public void delete(Long id) throws DBException {
        String selectSql = "SELECT ORIGIN_ACCOUNT_ID, AMOUNT FROM T_FIN_INVESTMENT WHERE ID = ?";
        String updateAccountSql = "UPDATE T_FIN_ACCOUNT SET BALANCE = BALANCE + ? WHERE ID = ?";
        String deleteSql = "DELETE FROM T_FIN_INVESTMENT WHERE ID = ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            Long accountId = null;
            double amount = 0;

            // 1. Buscar dados do investimento
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    accountId = rs.getLong("ORIGIN_ACCOUNT_ID");
                    amount = rs.getDouble("AMOUNT");
                } else {
                    throw new DBException("Investimento não encontrado para exclusão.");
                }
            }

            // 2. Atualizar saldo da conta (devolver valor)
            try (PreparedStatement ps = conn.prepareStatement(updateAccountSql)) {
                ps.setDouble(1, amount);
                ps.setLong(2, accountId);
                ps.executeUpdate();
            }

            // 3. Excluir o investimento
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new DBException("Erro ao excluir investimento e atualizar saldo da conta", e);
        }
    }

    private Investment fromResultSet(ResultSet rs) {
        try {
            return new Investment(
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
                InvestmentType.valueOf(rs.getString("TYPE")),
                rs.getString("RISK"),
                rs.getString("LIQUIDITY"),
                rs.getDouble("PROFITABILITY"),
                rs.getDate("DUE_DATE") != null
                    ? rs.getDate("DUE_DATE").toLocalDate()
                    : null,
                rs.getDouble("INTEREST_RATE"),
                rs.getBoolean("REDEEMED")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
