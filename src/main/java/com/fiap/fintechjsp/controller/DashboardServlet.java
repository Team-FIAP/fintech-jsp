package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.ExpenseDao;
import com.fiap.fintechjsp.dao.IncomeDao;
import com.fiap.fintechjsp.dao.InvestmentDao;
import com.fiap.fintechjsp.dao.UserDao;
import com.fiap.fintechjsp.model.Investment;
import com.fiap.fintechjsp.model.User;
import com.fiap.fintechjsp.utils.AuthUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private UserDao userDao;
    private ExpenseDao expenseDao;
    private IncomeDao incomeDao;
    private InvestmentDao investmentDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.userDao = new UserDao();
        this.expenseDao = new ExpenseDao();
        this.incomeDao = new IncomeDao();
        this.investmentDao = new InvestmentDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Ano atual
        int currentYear = LocalDate.now().getYear();

        // Mês atual
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        // Obtendo o usuário da sessão
        User loggedUser = AuthUtils.getUserFromSession(req);
        if (loggedUser == null) {
            resp.sendRedirect("login");
            return;
        }

        // Obtendo o saldo atual do usuário
        double totalBalance = userDao.getTotalBalance(loggedUser);

        // Obtendo o total de receitas
        double totalIncomes = incomeDao.getTotalIncomesForUserByPeriod(loggedUser, startDate, endDate);

        // Obtendo o total de despesas
        double totalExpenses = expenseDao.getTotalExpensesForUserByPeriod(loggedUser, startDate, endDate);

        List<Investment> investments = investmentDao.findAll(startDate, null, null, loggedUser.getId(), false);
        double totalGrossInvestments = investments.stream().mapToDouble(Investment::getCurrentGrossValue).sum();
        double totalAccumulatedInvestments = investments.stream().mapToDouble(Investment::getCurrentAccumulatedEarnings).sum();

        // Receitas por categoria
        List<Map<String, Object>> expensesByCategory = expenseDao.getTotalExpensesByCategoryForUserInPeriod(loggedUser, startDate, endDate);

        // Comparativo despesas essenciais vs não essenciais
        Map<String, Double> expensesTypeComparison = expenseDao.getTotalExpensesByCategoryTypeForUserInPeriod(loggedUser, startDate, endDate);

        // Fluxo de caixa mensal
        List<Double> monthlyIncomes = incomeDao.getMonthlyIncomesForUserByYear(loggedUser, currentYear);
        List<Double> monthlyExpenses = expenseDao.getMonthlyExpensesForUserByYear(loggedUser, currentYear);

        // Verificando se existe receitas ou despesas no ano
        boolean hasIncomes = monthlyIncomes.stream().anyMatch(i -> i != 0);
        boolean hasExpenses = monthlyExpenses.stream().anyMatch(e -> e != 0);

        // Atribuir os dados ao request
        req.setAttribute("totalBalance", totalBalance);
        req.setAttribute("totalIncomes", totalIncomes);
        req.setAttribute("totalExpenses", totalExpenses);
        req.setAttribute("totalGrossInvestments", totalGrossInvestments);
        req.setAttribute("totalAccumulatedInvestments", totalAccumulatedInvestments);
        req.setAttribute("expensesByCategory", expensesByCategory);
        req.setAttribute("expensesTypeComparison", expensesTypeComparison);
        req.setAttribute("monthlyIncomes", monthlyIncomes);
        req.setAttribute("monthlyExpenses", monthlyExpenses);
        req.setAttribute("hasIncomes", hasIncomes);
        req.setAttribute("hasExpenses", hasExpenses);

        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
