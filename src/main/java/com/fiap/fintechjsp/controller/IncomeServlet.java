package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.AccountDao;
import com.fiap.fintechjsp.dao.IncomeDao;
import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.*;
import com.fiap.fintechjsp.utils.AuthUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "IncomeServlet", urlPatterns = {"/receitas"})
public class IncomeServlet extends HttpServlet {
    private IncomeDao incomeDao;
    private AccountDao accountDao;

    public void init() throws ServletException {
        incomeDao = new IncomeDao();
        accountDao = new AccountDao();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String acao = req.getParameter("action");
        String action = req.getParameter("action");
        User loggedUser = AuthUtils.getUserFromSession(req);
        List<Account> accounts = accountDao.findAllByUserId(loggedUser.getId());

        req.setAttribute("accounts", accounts);

        switch (action) {
            case "cadastrar" -> {
                req.getRequestDispatcher("formulario-receita.jsp").forward(req, resp);
            }
            case "editar" -> {
                Long id = Long.parseLong(req.getParameter("id"));
                Income income = incomeDao.findById(id);
                req.setAttribute("income", income);
                req.getRequestDispatcher("formulario-receita.jsp").forward(req, resp);
            }
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Página não encontrada");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
    }
}

