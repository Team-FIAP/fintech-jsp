<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
  <title>Criar Conta</title>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <%--  Bootstrap Icons --%>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
  <%--  Bootstrap --%>
  <link rel="stylesheet" href="./resources/css/bootstrap.css">
  <%--  CSS Global --%>
  <link rel="stylesheet" href="./resources/css/globals.css">
</head>
<body class="d-flex flex-column bg-dark text-white">

<jsp:include page="/includes/header.jsp" />

<div class="d-flex flex-grow-1">
  <jsp:include page="/includes/sidebar.jsp" />

  <main class="layout">
    <h2>Criar Nova Conta Bancária</h2>
    <form action="contas" method="post" class="mt-4">
      <input type="hidden" name="action" value="createAccount">
      <div class="mb-3">
        <label for="nome" class="form-label">Nome</label>
        <input type="text" class="form-control" id="nome" name="nome" required>
      </div>
      <div class="mb-3">
        <label for="saldo" class="form-label">Saldo Inicial</label>
        <input type="number" class="form-control" id="saldo" name="saldo" step="0.01" required>
      </div>
      <button type="submit" class="btn btn-dark me-2">Criar Conta</button>
      <a href="dashboard.jsp" class="btn btn-secondary">Cancelar</a>
    </form>
  </main>
</div>

<jsp:include page="/includes/footer.jsp" />

<%-- Bootstrap JS --%>
<script src="resources/js/bootstrap.bundle.js"></script>

<%-- Ativando tooltips no menu lateral --%>
<script>
  const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
  tooltipTriggerList.forEach(el => new bootstrap.Tooltip(el));
</script>
</body>
</html>
