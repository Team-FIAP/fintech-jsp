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
import java.util.Comparator;
import java.util.List;

@WebServlet(name = "AccountServlet", urlPatterns = {"/contas", "/contas/listar-contas"})
public class AccountServlet extends HttpServlet {
    private AccountDao accountDao;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        switch (action) {
            case "createAccount" -> createAccount(req, resp);
            case "removeAccount" -> removeAccount(req, resp);
            case "editAccount" -> editAccount(req, resp);
            case "listAccounts" -> listAccounts(req, resp);
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

        else if ("editAccount".equals(action)){
            showEditForm(req, resp);
        }
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        accountDao = new AccountDao();
    }

    private void createAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        UserDao userDao = new UserDao();

        // Create test user
        User testUser = new User(1l, "WIll", "will@email.com", "123", "111.111.111-11", LocalDateTime.now());

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
            if (balance < 0) {
                req.setAttribute("error", "O saldo deve ser positívo");
                req.getRequestDispatcher("cadastrar-conta.jsp").forward(req, resp);
                return;
            }

            // Checks if there are accounts with duplicate names
            if (accountDao.existsByName(name)) {
                req.setAttribute("error", "Já existe uma conta cadastrada com esse nome");
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
        } catch (DBException e) {
            e.printStackTrace();
            req.setAttribute("error", "Erro ao cadastrar a conta no banco de dados.");
            req.getRequestDispatcher("cadastrar-conta.jsp").forward(req, resp);
        } catch (Exception e) {
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

            // Order accounts alphabetically
            accounts.sort(Comparator.comparing(Account::getName, String.CASE_INSENSITIVE_ORDER));

            // Calculate total balance
            double totalBalance = accounts.stream()
                    .mapToDouble(Account::getBalance)
                    .sum();

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
            req.setAttribute("totalBalance", totalBalance);

            // Forward to JSP
            req.getRequestDispatcher("/listar-contas.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Erro ao carregar as contas.");
            req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
        }
    }

    private void removeAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loggedUser = (User) req.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        try {
            // Get account ID
            String accountIdStr = req.getParameter("accountId");
            if (accountIdStr == null || accountIdStr.isEmpty()) {
                req.setAttribute("error", "ID da conta não fornecido.");
                req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
                return;
            }

            Long accountId = Long.parseLong(accountIdStr);

            // Find account to remove
            Account accountToRemove = accountDao.findById(accountId);

            if (accountToRemove == null) {
                req.setAttribute("error", "Conta não encontrada.");
                req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
                return;
            }


            // Delete the account
            accountDao.delete(accountToRemove);

            // Debug Statement
            System.out.println("Account removed successfully: " + accountToRemove.getName());

            // Set success message in session (so it persists through redirect)
            req.getSession().setAttribute("message", "Conta removida com sucesso!");

            // Redirect to avoid form resubmission on refresh
            resp.sendRedirect("contas?action=list");

        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID da conta inválido.");
            req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
        } catch (DBException e) {
            e.printStackTrace();
            req.setAttribute("error", "Erro ao remover a conta do banco de dados.");
            req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Erro interno. Tente novamente.");
            req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
        }
    }

    private void editAccount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loggedUser = (User) req.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String name = req.getParameter("nome");

        if (name == null || name.trim().isEmpty()) {
            req.setAttribute("error", "Nome da conta é obrigatório.");
            req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
            return;
        }

        try {
            // Get account ID
            String accountIdStr = req.getParameter("accountId");
            if (accountIdStr == null || accountIdStr.isEmpty()) {
                req.setAttribute("error", "ID da conta não fornecido.");
                req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
                return;
            }

            Long accountId = Long.parseLong(accountIdStr);

            // Find account to edit
            Account accountToEdit = accountDao.findById(accountId);

            if (accountToEdit == null) {
                req.setAttribute("error", "Conta não encontrada.");
                req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
                return;
            }

            // Checks if there are accounts with duplicate names
            if (accountDao.existsByName(name)) {
                req.setAttribute("error", "Já existe uma conta cadastrada com esse nome");
                req.getRequestDispatcher("editar-conta.jsp").forward(req, resp);
                return;
            }

            //change account name
            accountToEdit.setName(name);
            accountDao.update(accountToEdit);

            // Debug Statement
            System.out.println("Account name successfully edited to : " + accountToEdit.getName());

            // Set success message in session
            req.getSession().setAttribute("message", "Conta editada com sucesso!");

            // Redirect to avoid form resubmission on refresh
            resp.sendRedirect("contas?action=list");


        } catch (DBException e) {
            e.printStackTrace();
            req.setAttribute("error", "Erro ao editar a conta no banco de dados.");
            req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Erro interno. Tente novamente.");
            req.getRequestDispatcher("listar-contas.jsp").forward(req, resp);
        }
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get account by ID and forward to edit form JSP
        String accountIdStr = req.getParameter("accountId");
        Long accountId = Long.parseLong(accountIdStr);
        Account account = accountDao.findById(accountId);

        req.setAttribute("account", account);
        req.getRequestDispatcher("editar-conta.jsp").forward(req, resp);
    }
}