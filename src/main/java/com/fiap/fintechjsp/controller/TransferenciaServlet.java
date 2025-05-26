package com.fiap.fintechjsp.controller;

import br.com.fiap.fintechjsp.dao.TransferenciaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/Transferencia")
public class TransferenciaServlet extends HttpServlet {

    private static final String ERRO_JSP = "ErroTransferencia.jsp";
    private static final String SUCESSO_JSP = "SucessoTransferencia.jsp";
    private static final String LOGIN_JSP = "index.jsp";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String usuarioRemetente = (String) req.getSession().getAttribute("usuarioLogado");
        System.out.println("Remetente logado na sessão: " + usuarioRemetente);

        if (usuarioRemetente == null) {
            req.setAttribute("erro", "Sessão expirada. Faça login novamente.");
            req.getRequestDispatcher(LOGIN_JSP).forward(req, resp);
            return;
        }

        String usuarioDestino = req.getParameter("usuario");
        if (usuarioDestino != null) {
            usuarioDestino = usuarioDestino.trim();
        }

        if (usuarioDestino == null || usuarioDestino.isEmpty()) {
            req.setAttribute("erro", "Usuário destinatário inválido.");
            req.getRequestDispatcher(ERRO_JSP).forward(req, resp);
            return;
        }

        String valorStr = req.getParameter("valor");
        BigDecimal valor;
        try {
            valor = new BigDecimal(valorStr);
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Valor deve ser maior que zero");
            }
        } catch (NumberFormatException e) {
            req.setAttribute("erro", "Valor inválido para a transferência.");
            req.getRequestDispatcher(ERRO_JSP).forward(req, resp);
            return;
        }

        TransferenciaDAO dao = new TransferenciaDAO();

        Long remetenteId = dao.buscarIdPorUsuario(usuarioRemetente);
        Long destinatarioId = dao.buscarIdPorUsuario(usuarioDestino);

        if (remetenteId == null) {
            req.setAttribute("erro", "Usuário remetente não encontrado.");
            req.getRequestDispatcher(ERRO_JSP).forward(req, resp);
            return;
        }

        if (destinatarioId == null) {
            req.setAttribute("erro", "Usuário destinatário não encontrado.");
            req.getRequestDispatcher(ERRO_JSP).forward(req, resp);
            return;
        }

        // Montar descrição e observação
        String descricao = "Transferência para " + usuarioDestino;
        String observacao = "Transferência realizada por " + usuarioRemetente;

        // Chamar método com os 5 argumentos necessários
        boolean sucesso = dao.realizarTransferencia(remetenteId, destinatarioId, valor, descricao, observacao);

        if (sucesso) {
            BigDecimal novoSaldo = dao.obterSaldoPorId(remetenteId);
            req.setAttribute("novoSaldo", novoSaldo);
            req.getRequestDispatcher(SUCESSO_JSP).forward(req, resp);
        } else {
            req.setAttribute("erro", "Falha na transferência: saldo insuficiente ou erro de sistema.");
            req.getRequestDispatcher(ERRO_JSP).forward(req, resp);
        }
    }
}
