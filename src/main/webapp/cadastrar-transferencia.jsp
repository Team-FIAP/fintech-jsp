<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Transferência</title>
    <meta charset="UTF-8">

    <%-- Bootstrap e ícones --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <%--  Bootstrap --%>
    <link rel="stylesheet" href="./resources/css/bootstrap.css">
    <%--  CSS Global --%>
    <link rel="stylesheet" href="./resources/css/globals.css">
</head>
<body class="d-flex flex-column bg-dark text-white">

<jsp:include page="/includes/header.jsp"/>
<div class="d-flex flex-grow-1">
    <jsp:include page="/includes/sidebar.jsp"/>

    <main class="layout">
        <div class="container">
            <h1 class="mb-4">Cadastrar transferência</h1>

            <%-- Alerta de erro --%>
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                        ${error}
                </div>
            </c:if>

            <div class="card p-4">
                <form action="transferencias" method="post" class="needs-validation" novalidate>
                    <input type="hidden" name="action" value="cadastrar">
                    <div class="mb-3">
                        <label for="description" class="form-label">Descrição</label>
                        <input name="description" required type="text" class="form-control" id="description" maxlength="255">
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="amount">Valor</label>
                        <div class="input-group">
                            <span class="input-group-text">R$</span>
                            <input type="number" name="amount" id="amount" class="form-control" required>
                            <div class="invalid-feedback">Informe um valor válido.</div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="date">Data</label>
                        <input type="date" name="date" id="date" class="form-control" required>
                        <div class="invalid-feedback">Informe uma data válida.</div>
                    </div>

                    <div class="mb-3">
                        <label for="observation" class="form-label">Observação</label>
                        <textarea name="observation" type="text" class="form-control" id="observation" maxlength="255"></textarea>
                    </div>

                    <div class="form-group mb-3">
                        <label for="originAccountId" class="form-label">Conta origem</label>
                        <select class="form-select" id="originAccountId" name="originAccountId" required>
                            <option value=""></option>
                            <c:forEach var="account" items="${accounts}">

                                <option value="${account.id}"
                                        <c:if test="${expense.originAccount.id == account.id}">selected</c:if>>${account.name}</option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback">
                            Por favor, informe uma conta.
                        </div>
                    </div>

                    <div class="form-group mb-3">
                        <label for="destinationAccountId" class="form-label">Conta destino</label>
                        <select class="form-select" id="destinationAccountId" name="destinationAccountId" required>
                            <option value=""></option>
                            <c:forEach var="account" items="${accounts}">

                                <option value="${account.id}"
                                        <c:if test="${expense.originAccount.id == account.id}">selected</c:if>>${account.name}</option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback">
                            Por favor, informe uma conta.
                        </div>
                    </div>

                    <div class="d-flex justify-content-end">
                        <button type="submit" class="btn btn-dark">Cadastrar Transferência</button>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/includes/footer.jsp"/>
<script src="resources/js/bootstrap.bundle.js"></script>
<script src="resources/js/globals.js"></script>
</body>
</html>
