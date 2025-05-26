package com.fiap.fintechjsp.controller;

import br.com.fiap.fintechjsp.dao.LoginDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String senha = request.getParameter("senha");

        usuario = (usuario != null) ? usuario.trim() : "";
        senha = (senha != null) ? senha.trim() : "";

        System.out.println("[LoginServlet] Usuário recebido: [" + usuario + "]");
        System.out.println("[LoginServlet] Senha recebida: [" + senha + "]");

        LoginDAO dao = new LoginDAO();
        boolean autenticado = dao.validarLogin(usuario, senha);

        if (autenticado) {
            HttpSession session = request.getSession(true); // Cria sessão se não existir
            session.setAttribute("usuarioLogado", usuario); // Aqui pode ser o username mesmo

            response.sendRedirect("CadastroTransferencia.jsp");
        } else {
            request.setAttribute("erro", "Usuário ou senha inválidos.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
        }
    }
}

