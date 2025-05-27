package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.UserDao;
import com.fiap.fintechjsp.exception.DBException;
import com.fiap.fintechjsp.model.User;
import com.fiap.fintechjsp.utils.AuthUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

@WebServlet(name = "UserServlet", urlPatterns = {"/usuarios"})
public class UserServlet extends HttpServlet {
    private UserDao userDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        switch (action) {
            case "cadastrar" -> {
                req.getRequestDispatcher("cadastrar-usuario.jsp").forward(req, resp);
            }
            default -> {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Página não encontrada");
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userDao = new UserDao();
    }

    /**
     * Processa requisições POST e executa a ação com base no parâmetro "action".
     *
     * @param req  a requisição HTTP recebida
     * @param resp a resposta HTTP a ser enviada
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        switch (action) {
            case "create" -> createUser(req, resp);
            case "update" -> updateUser(req, resp);
            default -> {
                req.setAttribute("error", "Erro ao processar a requisição");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        }
    }

    private void updateUser(HttpServletRequest req, HttpServletResponse resp) {
        throw new RuntimeException("Não implementado");
    }

    /**
     * Processa o cadastro de um novo usuário, validando CPF e e-mail e aplicando hash na senha.
     *
     * @param req  a requisição HTTP com os dados do formulário
     * @param resp a resposta HTTP a ser enviada
     * @throws ServletException em caso de erro de servlet
     * @throws IOException      em caso de erro de entrada/saída
     */
    private void createUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String username = req.getParameter("username");
        String cpf = req.getParameter("cpf");
        String password = req.getParameter("password");

        try {
            // Verifica duplicidade de cpf
            if (userDao.existsByCpf(cpf)) {
                req.setAttribute("error", "Já existe um usuário cadastrado com esse cpf.");
                req.getRequestDispatcher("cadastrar-usuario.jsp").forward(req, resp);
                return;
            }

            // Verifica duplicidade de e-mail
            if (userDao.existsByUsername(username)) {
                req.setAttribute("error", "Já existe um usuário cadastrado com esse e-mail.");
                req.getRequestDispatcher("cadastrar-usuario.jsp").forward(req, resp);
                return;
            }

            // Hash da senha
            String hashedPassword = AuthUtils.hashPassword(password);

            // Criação do usuário e persistência/listar-contas
            User user = new User(name, username, hashedPassword, cpf);
            userDao.insert(user);

            req.setAttribute("message", "Usuário cadastrado com sucesso!");
            req.getRequestDispatcher("/").forward(req, resp);
        } catch (DBException e) {
            req.setAttribute("error", "Erro ao cadastrar o usuário no banco de dados.");
            req.getRequestDispatcher("cadastrar-usuario.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Por favor, valide os campos e tente novamente.");
            req.getRequestDispatcher("cadastrar-usuario.jsp").forward(req, resp);
        }
    }
}
