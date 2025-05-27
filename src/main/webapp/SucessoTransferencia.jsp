<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    String usuarioLogado = (String) session.getAttribute("usuarioLogado");
    java.math.BigDecimal novoSaldo = (java.math.BigDecimal) request.getAttribute("novoSaldo");
    if (novoSaldo == null) {
        novoSaldo = java.math.BigDecimal.ZERO;
    }
%>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Transferência Concluída</title>
    <meta charset="UTF-8">

    <%-- Bootstrap e ícones --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Resources/css/bootstrap.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Resources/css/global.css">
</head>
<body class="d-flex flex-column bg-dark text-white">

<jsp:include page="/Includes/Header.jsp"/>
<div class="d-flex flex-grow-1">
<%--    <jsp:include page="/includes/sidebar.jsp"/>--%>

    <main class="layout">
        <div class="container mt-5">
            <div class="card p-4 text-center">
                <h1 class="text-success mb-3">✅ Transferência efetuada com sucesso!</h1>
                <p class="fs-5">Olá <strong><%= (usuarioLogado != null ? usuarioLogado : "Usuário") %></strong>,</p>
                <p class="fs-5">Seu novo saldo é: <strong>R$ <fmt:formatNumber value="${novoSaldo}" type="number" minFractionDigits="2" /></strong></p>

                <div class="mt-4">
                    <a href="cadastrar-transferencia.jsp" class="btn btn-light me-2">Voltar</a>
                    <a href="cadastrar-transferencia.jsp" class="btn btn-success">Página Inicial</a>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/Includes/Footer.jsp"/>

<script src="${pageContext.request.contextPath}/Resources/js/bootstrap.bundle.js"></script>
</body>
</html>
