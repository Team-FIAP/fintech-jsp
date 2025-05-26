package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.AccountDao;
import com.fiap.fintechjsp.dao.ExpenseCategoryDao;
import com.fiap.fintechjsp.dao.ExpenseDao;
import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.Account;
import com.fiap.fintechjsp.model.Expense;
import com.fiap.fintechjsp.model.ExpenseCategory;
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
import java.util.List;

@WebServlet(name = "ExpenseServlet", urlPatterns = {"/despesas"})
public class ExpenseServlet extends HttpServlet {

    private ExpenseCategoryDao expenseCategoryDao;
    private AccountDao accountDao;
    private ExpenseDao expenseDao;

    Long originAccountId;
    Long expenseCategoryÍd;
    double amount;
    String description;
    LocalDate data;
    String observation;
    LocalDateTime createdAt;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        expenseCategoryDao = new ExpenseCategoryDao();
        accountDao = new AccountDao();
        expenseDao = new ExpenseDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        List<ExpenseCategory> expenseCategories = expenseCategoryDao.findAll();
        User loggedUser = AuthUtils.getUserFromSession(req);
        List<Account> accounts = accountDao.findAllByUserId(loggedUser.getId());

        req.setAttribute("expenseCategories", expenseCategories);
        req.setAttribute("accounts", accounts);

        switch (action) {
            case "cadastrar" -> {
                req.getRequestDispatcher("formulario-despesa.jsp").forward(req, resp);
            }
            case "editar" -> {
                Long id = Long.parseLong(req.getParameter("id"));
                Expense expense = expenseDao.findById(id);
                req.setAttribute("expense", expense);
                req.getRequestDispatcher("formulario-despesa.jsp").forward(req, resp);
            }
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Página não encontrada");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loggedUser = AuthUtils.getUserFromSession(req);
        String expense = req.getParameter("id");
        String action = req.getParameter("action");

        if (expense.equals("")) {
            createExpense(req, resp);
            return;
        }
        updateExpense(req, resp);
    }

    private void createExpense(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            obterDadosExpense(req);
            createdAt = LocalDateTime.now();

            Account account = accountDao.findById(originAccountId);
            ExpenseCategory expenseCategory = expenseCategoryDao.findById(expenseCategoryÍd);

            Expense expense = new Expense(0L, amount, data, description, observation, account, createdAt, expenseCategory);

            Expense expenseInsert = expenseDao.insert(expense);
            req.setAttribute("expense", expenseInsert);
            req.getRequestDispatcher("transacoes-financeiras.jsp").forward(req, resp);
        } catch (DBException e) {
            req.setAttribute("error", "Erro ao cadastrar despesa" + e.getMessage());
            req.getRequestDispatcher("formulario-despesa.jsp").forward(req, resp);
        }
    }

    private void obterDadosExpense(HttpServletRequest req) {
        originAccountId = Long.parseLong(req.getParameter("originAccountId"));
        expenseCategoryÍd = Long.parseLong(req.getParameter("expenseCategory"));
        amount = Double.parseDouble(req.getParameter("amount"));
        description = req.getParameter("description");
        data = LocalDate.parse(req.getParameter("data"));
        observation = req.getParameter("observation");
    }

    private void updateExpense(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            Long expenseId = Long.parseLong(req.getParameter("id"));
            Expense expense = expenseDao.findById(expenseId);
            obterDadosExpense(req);
            Account account = accountDao.findById(originAccountId);
            ExpenseCategory expenseCategory = expenseCategoryDao.findById(expenseCategoryÍd);
            Expense expenseModify = new Expense(expense.getId(), amount, data, description, observation, account, createdAt, expenseCategory);

            Expense expenseUpdate = expenseDao.update(expenseModify);
            req.setAttribute("expense", expenseUpdate);
            req.getRequestDispatcher("transacoes-financeiras.jsp").forward(req, resp);
        } catch (DBException e) {
            req.setAttribute("error", "Erro ao editar despesa" + e.getMessage());
            req.getRequestDispatcher("formulario-despesa.jsp").forward(req, resp);
        }
    }
}
