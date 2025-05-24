package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.AccountDao;
import com.fiap.fintechjsp.dao.ExpenseCategoryDao;
import com.fiap.fintechjsp.dao.ExpenseDao;
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
import java.util.List;

@WebServlet(name = "ExpenseServlet", urlPatterns = {"/despesas"})
public class ExpenseServlet extends HttpServlet {

    private ExpenseCategoryDao expenseCategoryDao;
    private AccountDao accountDao;
    private ExpenseDao expenseDao;


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
}
