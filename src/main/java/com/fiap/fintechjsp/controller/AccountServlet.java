package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.AccountDao;
import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.Account;
import com.fiap.fintechjsp.model.User;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AccountServlet", urlPatterns = {"/contas"})
public class AccountServlet extends HttpServlet {
    private AccountDao accountDao;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        switch (action){
            case "createAccount" -> createAccount(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.equals("list")) {
            listAccounts(req, resp);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        accountDao = new AccountDao();
    }

    private void createAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        User loggedUser = (User) req.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            resp.sendRedirect("login");
            return;
        }
        String name = req.getParameter("nome");
        String strBalance = req.getParameter("saldo");

        // Checks if str value of balance is null or empty
        if (strBalance == null || strBalance.isEmpty()) {
            req.setAttribute("error", "Insira um saldo válido");
            req.getRequestDispatcher("cadastrar-conta.jsp").forward(req, resp);
            return;
        }

        try {
            // Converts strBalance to Double
            Double balance;
            balance = Double.parseDouble(strBalance);

            // Checks if balance is positive
            if (balance < 0){
                req.setAttribute("error", "O saldo deve ser positívo");
                req.getRequestDispatcher("cadastrar-conta.jsp").forward(req, resp);
                return;
            }

            // Checks if there are accounts with duplicate names
            if(accountDao.existsByName(name)) {
                req.setAttribute("error", "Já existe uma conta cadastrada com essse nome");
                req.getRequestDispatcher("cadastrar-conta.jsp").forward(req, resp);
                return;
            }

            // Creates account + persistence
            Account account = new Account(name, balance, loggedUser);
            accountDao.insert(account);

            // Load accounts
            List<Account> accounts = accountDao.getByUserId(loggedUser.getId());
            req.setAttribute("accounts", accounts);

            // Set success message
            req.setAttribute("message", "Conta criada com sucesso!");


            req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Formato de saldo inválido.");
            req.getRequestDispatcher("cadastrar-conta.jsp").forward(req, resp);
        } catch (DBException e){
            req.setAttribute("error", "Erro ao cadastrar a conta no banco de dados.");
            req.getRequestDispatcher("cadastrar-conta.jsp").forward(req, resp);
        } catch (Exception e){
            e.printStackTrace();
            req.setAttribute("error", "Por favor, valide os campos e tente novamente.");
            req.getRequestDispatcher("cadastrar-conta.jsp").forward(req, resp);
        }
    }

    private void listAccounts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loggedUser = (User) req.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        try {
            // Get accounts for logged-in user
            List<Account> accounts = accountDao.getByUserId(loggedUser.getId());

            // Add debug message
            System.out.println("Found " + accounts.size() + " accounts for user ID: " + loggedUser.getId());

            // Get message from session if exists
            String message = (String) req.getSession().getAttribute("message");
            if (message != null) {
                req.setAttribute("message", message);
                // Remove from session after retrieving
                req.getSession().removeAttribute("message");
            }

            // Set as request attribute
            req.setAttribute("accounts", accounts);

            // Forward to JSP
            req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Erro ao carregar as contas.");
            req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
        }
    }
}