package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.*;
import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@WebServlet(name = "TransactionServlet", urlPatterns = {"/transacoes-financeiras"})
public class TransactionServlet extends HttpServlet {
    private IncomeDao incomeDao;
    private ExpenseDao expenseDao;
    private InvestmentDao investmentDao;
    private TransferDao transferDao;
    private AccountDao accountDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.incomeDao = new IncomeDao();
        this.expenseDao = new ExpenseDao();
        this.investmentDao = new InvestmentDao();
        this.transferDao = new TransferDao();
        this.accountDao = new AccountDao();
    }

    /**
     * Processa requisições HTTP GET para exibir uma lista de transações financeiras
     * (receitas, despesas, transferências e investimentos) do usuário logado.
     *
     * Permite filtragem opcional por:
     * <ul>
     *   <li>Data de início (<code>startDate</code>)</li>
     *   <li>Data de fim (<code>endDate</code>)</li>
     *   <li>Conta (<code>accountId</code>)</li>
     *   <li>Tipo de transação (<code>type</code>) — RECEITA, DESPESA, TRANSFERENCIA ou INVESTIMENTO</li>
     * </ul>
     *
     * Se as datas não forem informadas, utiliza como padrão o primeiro e o último dia do mês atual.
     *
     * A lista de transações é ordenada pela data (mais recentes primeiro) e encaminhada
     * para a JSP <code>transacoes-financeiras.jsp</code>.
     *
     * Caso o usuário não esteja logado, redireciona para a página de login.
     *
     * @param req  requisição HTTP contendo os parâmetros opcionais de filtro
     * @param resp resposta HTTP a ser enviada ao cliente
     * @throws ServletException se ocorrer um erro de servlet
     * @throws IOException se ocorrer um erro de entrada/saída
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String startDateParam = req.getParameter("startDate");
            String endDateParam = req.getParameter("endDate");
            String accountIdParam = req.getParameter("accountId");
            String type = req.getParameter("type");

            LocalDate startDate = null;
            LocalDate endDate = null;
            Long accountId = null;


            try {
                if (startDateParam != null && !startDateParam.isEmpty()) {
                    startDate = LocalDate.parse(startDateParam);
                }

                if (endDateParam != null && !endDateParam.isEmpty()) {
                    endDate = LocalDate.parse(endDateParam);
                }

                // Caso uma data não for informada, usar o mês atual
                if (startDate == null && endDate == null) {
                    LocalDate now = LocalDate.now();
                    startDate = now.withDayOfMonth(1);
                    endDate = now.withDayOfMonth(now.lengthOfMonth());
                }

                if (accountIdParam != null && !accountIdParam.isEmpty()) {
                    accountId = Long.parseLong(accountIdParam);
                }

                // Passando a data para o template
                req.setAttribute("startDate", startDate);
                req.setAttribute("endDate", endDate);
            } catch (Exception e) {
                req.setAttribute("error", "Erro ao processar filtros de data ou conta.");
                req.getRequestDispatcher("transacoes-financeiras.jsp").forward(req, resp);
                return;
            }

            // TODO: Remover as próximas duas linhas (Apenas para testes)
            User testUser = new User(1L, "Will", "will@email.com", "123", "111.111.111-11", LocalDateTime.now());
            req.getSession().setAttribute("loggedUser", testUser);

            // Obtendo o usuário da sessão
            User loggedUser = (User) req.getSession().getAttribute("loggedUser");
            if (loggedUser == null) {
                resp.sendRedirect("login");
                return;
            }

            // Obtendo a lista de contas do usuário
            List<Account> accounts = accountDao.findAllByUserId(loggedUser.getId());
            req.setAttribute("accounts", accounts);

            // Carregando as transações
            List<Transaction> transactions = loadTransactions(startDate, endDate, accountId, type, loggedUser.getId());

            // Ordenando as transações
            transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

            String success = req.getParameter("success");
            if (success != null) {
                req.setAttribute("success", success);
            }

            req.setAttribute("transactions", transactions);
            req.getRequestDispatcher("transacoes-financeiras.jsp").forward(req, resp);
        } catch (DBException e) {
            e.printStackTrace(); // ou use um logger
            req.setAttribute("error", "Ocorreu um erro ao carregar as transações.");
            req.getRequestDispatcher("transacoes-financeiras.jsp").forward(req, resp);
        }
    }

    /**
     * Carrega uma lista de transações financeiras do usuário com base nos filtros fornecidos.
     *
     * Este método consulta diferentes tipos de transações (receitas, despesas, transferências e investimentos)
     * de acordo com o parâmetro {@code type}. Se {@code type} for nulo, vazio ou não especificado,
     * todas as categorias de transações serão incluídas.
     *
     * @param startDate data de início do intervalo de filtro (inclusive)
     * @param endDate data de fim do intervalo de filtro (inclusive)
     * @param accountId ID da conta financeira a ser usada como filtro (opcional; se {@code null}, todas as contas são consideradas)
     * @param type tipo de transação a ser filtrado: RECEITA, DESPESA, TRANSFERENCIA ou INVESTIMENTO;
     *             se {@code null} ou em branco, todas as categorias são retornadas
     * @param userId ID do usuário logado, cujas transações devem ser buscadas
     * @return uma lista de transações {@link Transaction} que atendem aos critérios de filtro
     */
    private List<Transaction> loadTransactions(LocalDate startDate, LocalDate endDate, Long accountId, String type, Long userId) {
        List<Transaction> transactions = new ArrayList<>();

        if (type == null || type.isBlank() || type.equalsIgnoreCase("RECEITA")) {
            transactions.addAll(incomeDao.findAll(startDate, endDate, accountId, userId));
        }

        if (type == null || type.isBlank() || type.equalsIgnoreCase("DESPESA")) {
            transactions.addAll(expenseDao.findAll(startDate, endDate, accountId, userId));
        }

        if (type == null || type.isBlank() || type.equalsIgnoreCase("TRANSFERENCIA")) {
            transactions.addAll(transferDao.findAll(startDate, endDate, accountId, userId));
        }

        if (type == null || type.isBlank() || type.equalsIgnoreCase("INVESTIMENTO")) {
            transactions.addAll(investmentDao.findAll(startDate, endDate, accountId, userId));
        }

        return transactions;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("excluir".equalsIgnoreCase(action)) {
            handleDelete(req, resp);
        } else {
            resp.sendRedirect("transacoes-financeiras");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String type = req.getParameter("type");
        String idParam = req.getParameter("id");

        try {
            Long id = Long.parseLong(idParam);

            switch (type) {
                case "EXPENSE":
                    expenseDao.delete(id);
                    req.setAttribute("success", "Despesa removida com sucesso");
                    break;
                case "INCOME":
                    incomeDao.delete(id);
                    req.setAttribute("success", "Receita removida com sucesso");
                    break;
                case "TRANSFER":
                    transferDao.delete(id);
                    req.setAttribute("success", "Transferência removida com sucesso");
                    break;
                case "INVESTMENT":
                    investmentDao.delete(id);
                    req.setAttribute("success", "Investimento removido com sucesso");
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de transação inválido: " + type);
            }

            resp.sendRedirect("transacoes-financeiras?success=true");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID inválido para exclusão.");
            req.getRequestDispatcher("transacoes-financeiras.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Erro ao excluir a transação: " + e.getMessage());
            req.getRequestDispatcher("transacoes-financeiras.jsp").forward(req, resp);
        }
    }
}
