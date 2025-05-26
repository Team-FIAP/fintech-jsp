package com.fiap.fintechjsp.controller;

import br.com.fiap.fintechjsp.dao.EditarTransferenciaDAO;
import br.com.fiap.fintechjsp.dao.TransferenciaDAO;
import br.com.fiap.fintechjsp.model.EditarTransferencia;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/EditarTransferencia")
public class EditarTransferenciaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usuarioRemetente = (String) req.getSession().getAttribute("usuarioLogado");
        String usuarioDestino = req.getParameter("usuario");
        String valorStr = req.getParameter("valor");

        if (usuarioDestino == null || usuarioDestino.trim().isEmpty()) {
            req.setAttribute("erro", "Usuário destinatário inválido.");
            req.getRequestDispatcher("ErroTransferencia.jsp").forward(req, resp);
            return;
        }

        BigDecimal valor;
        try {
            valor = new BigDecimal(valorStr);
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Valor deve ser maior que zero");
            }
        } catch (NumberFormatException e) {
            req.setAttribute("erro", "Valor inválido para a transferência.");
            req.getRequestDispatcher("ErroTransferencia.jsp").forward(req, resp);
            return;
        }

        EditarTransferenciaDAO dao = new EditarTransferenciaDAO();
        boolean sucesso = dao.editarTransferencia(usuarioRemetente, usuarioDestino, valor);

        if (sucesso) {
            req.getRequestDispatcher("SucessoTransferencia.jsp").forward(req, resp);
        } else {
            req.setAttribute("erro", "Não foi possível editar a transferência.");
            req.getRequestDispatcher("ErroTransferencia.jsp").forward(req, resp);
        }

    }
}

