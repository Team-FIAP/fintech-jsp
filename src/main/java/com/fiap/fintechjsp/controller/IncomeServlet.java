package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.AccountDao;
import com.fiap.fintechjsp.dao.IncomeDao;
import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.Account;
import com.fiap.fintechjsp.model.Income;
import com.fiap.fintechjsp.model.User;
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

@WebServlet("/receita")
public class IncomeServlet extends HttpServlet {
    private IncomeDao incomeDao;
    private AccountDao accountDao;

    public void init() throws ServletException{
        incomeDao = new IncomeDao();
        accountDao = new AccountDao();
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String acao = req.getParameter("action");

        switch (acao) {
            case "cadastrar":
                cadastrar(req, resp);
                break;
            case "editar":
                editar(req, resp);
                break;
            case "excluir":
                excluir(req, resp);
        }
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp) {

    }

    private void cadastrar(HttpServletRequest req, HttpServletResponse resp) {

        try {
            Long id = 0L;
            Long codigoConta = Long.parseLong(req.getParameter("conta"));

            Account account = accountDao.findById(codigoConta);

            String descricao = req.getParameter("descricao");
            Double valor = Double.parseDouble(req.getParameter("valor"));
            LocalDate data = LocalDate.parse(req.getParameter("data"));
            String observacoes = req.getParameter("observacoes");
            LocalDateTime atual = LocalDateTime.now();

            Income income = new Income(id, valor, data, descricao, observacoes, account, atual);
            incomeDao.insert(income);
            req.setAttribute("mensagem", "Produto cadastrado!");
        } catch (DBException db) {
            db.printStackTrace();
            req.setAttribute("erro", "Erro ao cadastrar no banco de dados,");
        } catch (Exception e) {
            req.setAttribute("erro", "Por favor valide os dados.");
        }
    }

        private User getLoggedUser(HttpServletRequest req){

            User testUser = new User(1L, "Will", "will@email.com", "123", "111.111.111-11", LocalDateTime.now());
            req.getSession().setAttribute("loggedUser", testUser);

            return (User) req.getSession().getAttribute("loggedUser");


        }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String acao = req.getParameter("action");
//        List<Account>accounts = getUserAccounts(getLoggedUser());

        switch (acao) {
            case "cadastrar":
                break;
            case "editar":
                editar(req, resp);
                break;
            case "abrir-form-cadastro":
                abrirFormCadastro(req, resp);
                break;

        }
    }

    private void abrirFormCadastro(HttpServletRequest req, HttpServletResponse resp) {
    }

//    private void carregarContas(HttpServletRequest req){
//////        List<String> lista = accountDao.findNames();
////        req.setAttribute("contas", lista);
////
////    }

    private void editar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.parseLong(req.getParameter("id-income"));
        Income income = incomeDao.findById(id);
        req.setAttribute("income", income);
//        carregarContas(req);

        req.getRequestDispatcher("editar-receita.jsp").forward(req, resp);
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Income> lista = incomeDao.findAll();
        req.setAttribute("incomes", lista);

        req.getRequestDispatcher("receita.jsp").forward(req, resp);
    }
}