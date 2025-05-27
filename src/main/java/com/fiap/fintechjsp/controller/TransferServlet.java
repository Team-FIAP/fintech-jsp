package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.AccountDao;
import com.fiap.fintechjsp.dao.TransferDao;
import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.Account;
import com.fiap.fintechjsp.model.Transfer;
import com.fiap.fintechjsp.model.User;
import com.fiap.fintechjsp.utils.AuthUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@WebServlet("/transferencias")
public class TransferServlet extends HttpServlet {
    private TransferDao transferDao;
    private AccountDao accountDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        transferDao = new TransferDao();
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
                req.getRequestDispatcher("cadastrar-transferencia.jsp").forward(req, resp);
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
            case "cadastrar" -> createTransfer(req, resp);
            default -> {
                req.setAttribute("error", "Erro ao processar a requisição");
                req.getRequestDispatcher("cadastrar-transferencia").forward(req, resp);
            }
        }
    }

    private void createTransfer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long originAccountId = Long.parseLong(req.getParameter("originAccountId"));
            Long destinationAccountId = Long.parseLong(req.getParameter("destinationAccountId"));
            double amount = Double.parseDouble(req.getParameter("amount"));
            String description = req.getParameter("description");
            LocalDate date = LocalDate.parse(req.getParameter("date"));
            String observation = req.getParameter("observation");

            Account originAccount = accountDao.findById(originAccountId);
            Account destinationAccount = accountDao.findById(destinationAccountId);

            if (originAccountId.equals(destinationAccountId)) {
                req.setAttribute("error", "A conta de destino não pode ser igual a origem.");
                req.getRequestDispatcher("cadastrar-transferencia.jsp").forward(req, resp);
                return;
            }

            Transfer transfer = new Transfer(null, amount, date, description, observation, originAccount, destinationAccount, null);
            transferDao.insert(transfer);

            req.getRequestDispatcher("transacoes-financeiras").forward(req, resp);
        } catch (DBException e) {
            req.setAttribute("error", "Erro ao cadastrar a transferência no banco de dados.");
            req.getRequestDispatcher("cadastrar-transferencia.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Por favor, valide os campos e tente novamente.");
            req.getRequestDispatcher("cadastrar-transferencia.jsp").forward(req, resp);
        }
    }
}

