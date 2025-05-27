package com.fiap.fintechjsp.controller;

import com.fiap.fintechjsp.dao.LoginDAO;
import com.fiap.fintechjsp.dao.UserDao;
import com.fiap.fintechjsp.model.User;
import com.fiap.fintechjsp.utils.AuthUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDao userDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userDao = new UserDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.invalidate();
        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            User user = userDao.findByUsername(username);

            if (user == null) {
                req.setAttribute("error", "Usuário ou senha inválidos.");
                req.getRequestDispatcher("/").forward(req, resp);
                return;
            }

            if (!AuthUtils.checkPassword(password, user.getPassword())) {
                req.setAttribute("error", "Usuário ou senha inválidos.");
                req.getRequestDispatcher("/").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("loggedUser", user);

            resp.sendRedirect("dashboard");
        } catch (Exception e) {
            req.setAttribute("error", "Falha ao realizar o login, confira os dados.");
            req.getRequestDispatcher("/").forward(req, resp);
        }
    }
}

