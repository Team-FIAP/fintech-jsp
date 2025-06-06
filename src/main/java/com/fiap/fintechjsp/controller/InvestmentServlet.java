package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.AccountDao;
import com.fiap.fintechjsp.dao.InvestmentDao;
import com.fiap.fintechjsp.dao.TransferDao;
import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.*;
import com.fiap.fintechjsp.utils.AuthUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/investimentos")
public class InvestmentServlet extends HttpServlet {
    private TransferDao transferDao;
    private AccountDao accountDao;
    private InvestmentDao investmentDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        investmentDao = new InvestmentDao();
        accountDao = new AccountDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        User loggedUser = AuthUtils.getUserFromSession(req);
        List<Account> accounts = accountDao.findAllByUserId(loggedUser.getId());
        req.setAttribute("accounts", accounts);

        switch (action) {
            case "cadastrar" -> {
                req.getRequestDispatcher("formulario-investimento.jsp").forward(req, resp);
            }
            default -> {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Página não encontrada");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        switch (action) {
            case "cadastrar" -> createInvestment(req, resp);
            default -> {
                req.setAttribute("error", "Erro ao processar a requisição");
                req.getRequestDispatcher("formulario-investimento.jsp").forward(req, resp);
            }
        }
    }

    private void createInvestment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long originAccountId = Long.parseLong(req.getParameter("originAccountId"));
            double amount = Double.parseDouble(req.getParameter("amount"));
            String description = req.getParameter("description");
            LocalDate date = LocalDate.parse(req.getParameter("date"));
            String observation = req.getParameter("observation");
            InvestmentType investmentType = InvestmentType.valueOf(req.getParameter("type"));
            String liquidity = req.getParameter("liquidity");
            LocalDate dueDate = LocalDate.parse(req.getParameter("dueDate"));
            String risk = req.getParameter("risk");
            double interestRate = Double.parseDouble(req.getParameter("interestRate"));

            Account originAccount = accountDao.findById(originAccountId);

            Investment investment = new Investment(amount, date, description, observation, originAccount, investmentType, risk, liquidity, dueDate, interestRate);
            investmentDao.insert(investment);

            req.getRequestDispatcher("transacoes-financeiras").forward(req, resp);
        } catch (DBException e) {
            req.setAttribute("error", "Erro ao cadastrar o investimento no banco de dados.");
            req.getRequestDispatcher("formulario-investimento.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Por favor, valide os campos e tente novamente.");
            req.getRequestDispatcher("formulario-investimento.jsp").forward(req, resp);
        }
    }
}
