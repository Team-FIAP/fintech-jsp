package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.AccountDao;
import com.fiap.fintechjsp.dao.UserDao;
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
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "AccountServlet", urlPatterns = {"/contas", "/contas/listar-contas"})
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
        String path = req.getServletPath();

        // Acessado por /contas?action=list
        if ((action == null || action.equals("list")) && path.equals("/contas")) {
            listAccounts(req, resp);
        }

        // Acessado por /contas/listar-contas
        else if (path.equals("/contas/listar-contas")) {
            listAccounts(req, resp);
        }
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        accountDao = new AccountDao();
    }

    private void createAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{

        UserDao userDao = new UserDao();

        // Create test user
        User testUser = new User(1l, "WIll", "will@email.com", "123", "111.111.111-11", LocalDateTime.now());

//        try {
//            // Check if test user exists in database, if not, insert it
//
//            if (testUser == null) {
//                System.out.println("Test user not found, inserting into database...");
//                userDao.insert(testUser);
//                System.out.println("Test user inserted successfully");
//            } else {
//                System.out.println("Test user already exists in database");
//                testUser = testUser; // Use the existing user from database
//            }
//        } catch (DBException e) {
//            System.out.println("Error checking/inserting test user: " + e.getMessage());
//            e.printStackTrace();
//        }

        req.getSession().setAttribute("loggedUser", testUser);

        User loggedUser = (User) req.getSession().getAttribute("loggedUser");

        System.out.println("USER ID: " + loggedUser.getId());

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
            List<Account> accounts = accountDao.findAllByUserId(loggedUser.getId());
            req.setAttribute("accounts", accounts);

            // Set success message
            req.setAttribute("message", "Conta criada com sucesso!");

            resp.sendRedirect("contas?action=list");

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Formato de saldo inválido.");
            req.getRequestDispatcher("cadastrar-conta.jsp").forward(req, resp);
        } catch (DBException e){
            e.printStackTrace();
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
            List<Account> accounts = accountDao.findAllByUserId(loggedUser.getId());

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
            req.getRequestDispatcher("/listar-contas.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Erro ao carregar as contas.");
            req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
        }
    }
}