package com.fiap.fintechjsp.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


@WebFilter("/*")
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpSession session = req.getSession();
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();

        boolean isLoggedIn = session != null && session.getAttribute("loggedUser") != null;
        boolean isLoginPage = url.equals(contextPath + "/") || url.equals(contextPath + "/login") || url.equals(contextPath + "/index.jsp");
        boolean isRegisterPage = url.equals(contextPath + "/usuarios");
        boolean isResource = url.contains("/resources/");

        if (!isLoggedIn && !isLoginPage && !isResource && !isRegisterPage) {
            servletRequest.setAttribute("error", "Entre com o usuário e senha!");
            servletRequest.getRequestDispatcher("index.jsp").forward(servletRequest, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
