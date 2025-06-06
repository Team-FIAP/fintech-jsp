<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Lista de Contas</title>
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
        <h2>Minhas Contas</h2>

        <%-- Display success message if exists --%>
        <c:if test="${not empty message}">
            <div class="alert alert-success mt-3">
                    ${message}
            </div>
        </c:if>

        <%-- Display error message if exists --%>
        <c:if test="${not empty error}">
            <div class="alert alert-danger mt-3">
                    ${error}
            </div>
        </c:if>

        <div class="d-flex justify-content-end mb-3">
            <a class="btn btn-dark" href="contas?action=cadastrar">Cadastrar conta</a>
        </div>

        <c:if test="${not empty accounts}">
            <div class="table-responsive mt-4">
                <table class="table table-dark table-bordered align-middle">
                    <thead>
                    <tr>
                        <th>Nome da Conta</th>
                        <th>Saldo</th>
                        <th class="text-center">Ações</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="account" items="${accounts}">
                        <tr>
                            <td>${account.name}</td>
                            <td>R$ <fmt:formatNumber value="${account.balance}" type="number" minFractionDigits="2" maxFractionDigits="2" /></td>
                            <td class="text-nowrap text-center">
                                <a href="contas?action=editar&id=${account.id}" type="submit" class="btn btn-light btn-sm me-1">
                                    Editar
                                </a>
                                <form action="contas" method="post" class="d-inline" onsubmit="return confirm('Tem certeza que deseja excluir esta conta?');">
                                    <input type="hidden" name="action" value="remover">
                                    <input type="hidden" name="accountId" value="${account.id}">
                                    <button type="submit" class="btn btn-danger btn-sm">
                                        Remover
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr>
                            <th class="text-end" colspan="2">Saldo total:</th>
                            <td>R$ <fmt:formatNumber value="${totalBalance}" type="number" minFractionDigits="2" maxFractionDigits="2" /></td>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </c:if>

        <c:if test="${empty accounts}">
            <div class="mt-4">
                <p>Você ainda não possui contas cadastradas.</p>
                <a href="cadastrar-conta.jsp" class="btn btn-dark">Cadastrar uma conta</a>
            </div>
        </c:if>
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