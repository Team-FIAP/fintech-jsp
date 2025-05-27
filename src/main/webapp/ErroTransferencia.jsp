<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
  <title>Erro na Transferência</title>
  <meta charset="UTF-8">

  <%-- Bootstrap e ícones --%>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/Resources/css/bootstrap.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/Resources/css/global.css">
</head>
<body class="d-flex flex-column bg-dark text-white">

<jsp:include page="/Includes/Header.jsp"/>
<div class="d-flex flex-grow-1">
<%--  <jsp:include page="/includes/sidebar.jsp"/>--%>

  <main class="layout">
    <div class="container mt-5">
      <div class="card border-danger p-4 bg-dark text-white">
        <div class="alert alert-danger" role="alert">
          <h4 class="alert-heading">
            <i class="bi bi-x-circle-fill text-black me-2"></i> Erro ao realizar a transferência!
          </h4>
          <p class="mt-2">
            <c:out value="${erro != null ? erro : 'Ocorreu um erro desconhecido.'}" />
          </p>
          <hr>
          <a href="cadastrar-transferencia.jsp" class="btn btn-light">Voltar</a>
        </div>
      </div>
    </div>
  </main>
</div>

<jsp:include page="/Includes/Footer.jsp"/>

<script src="${pageContext.request.contextPath}/Resources/js/bootstrap.bundle.js"></script>
</body>
</html>
