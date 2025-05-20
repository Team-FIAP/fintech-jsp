<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String currentUri = request.getRequestURI();
%>

<div class="sidebar offcanvas-lg offcanvas-start bg-secondary text-white border-end-0" tabindex="-1" id="sidebar">
  <div class="offcanvas-header d-lg-none">
    <h5 class="offcanvas-title text-white">Menu</h5>
    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="offcanvas" aria-label="Fechar"></button>
  </div>

  <div class="offcanvas-body d-flex flex-column p-0">
    <ul class="nav nav-pills nav-flush flex-column text-center mb-auto">
      <li class="nav-item">
        <a href="#" class="nav-link text-white py-3 border-0 rounded-0 active <%= currentUri.contains("dashboard.jsp") ? "active bg-dark" : "" %>" title="Dashboard" data-bs-toggle="tooltip">
          <i class="bi bi-speedometer2 fs-5"></i>
          <span class="d-none ms-2">Dashboard</span>
        </a>
      </li> <li class="nav-item">
        <a href="#" class="nav-link text-white py-3 border-0 rounded-0 <%= currentUri.contains("movimentacoes.jsp") ? "active bg-dark" : "" %>" title="Movimentações" data-bs-toggle="tooltip">
          <i class="bi bi-arrow-left-right fs-5"></i>
          <span class="d-none ms-2">Movimentações</span>
        </a>
      </li>
      <li>
        <a href="#" class="nav-link text-white py-3 border-0 rounded-0 hover-dark <%= currentUri.contains("investimentos.jsp") ? "active bg-dark" : "" %>" title="Investimentos" data-bs-toggle="tooltip">
          <i class="bi bi-bar-chart-line fs-5"></i>
          <span class="d-none ms-2">Investimentos</span>
        </a>
      </li>
      <li>
        <a href="#" class="nav-link text-white py-3 border-0 rounded-0 hover-dark <%= currentUri.contains("contas.jsp") ? "active bg-dark" : "" %>" title="Contas" data-bs-toggle="tooltip">
          <i class="bi bi-bank fs-5"></i>
          <span class="d-none ms-2">Contas</span>
        </a>
      </li>
    </ul>
  </div>
</div>
