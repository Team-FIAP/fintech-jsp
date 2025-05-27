<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Login - Fintech</title>

    <%-- Bootstrap Icons --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <%--  Bootstrap --%>
    <link rel="stylesheet" href="./resources/css/bootstrap.css">
    <%--  CSS Global --%>
    <link rel="stylesheet" href="./resources/css/globals.css">
</head>
<body class="d-flex flex-column bg-dark text-white min-vh-100">

<main class="d-flex flex-column justify-content-center align-items-center flex-grow-1 w-100">
    <div class="text-center mb-4">
        <h1 class="text-light">Seja bem-vindo à FinCon!</h1>
    </div>

    <div class="card bg-secondary text-white p-4 shadow" style="min-width: 350px; max-width: 400px;">
        <h2 class="text-center mb-4">Acessar</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="login" method="post" class="needs-validation" novalidate>
            <div class="form-group mb-3">
                <label for="usuario" class="form-label fw-bold">Usuário</label>
                <input type="email" class="form-control" id="usuario" name="username" required>
                <div class="invalid-feedback">Informe seu nome de usuário.</div>
            </div>

            <div class="form-group mb-3">
                <label for="senha" class="form-label fw-bold">Senha</label>
                <input type="password" class="form-control" id="senha" name="password" required>
                <div class="invalid-feedback">Informe sua senha.</div>
            </div>

            <div class="d-grid mb-3">
                <button type="submit" class="btn btn-dark">Entrar</button>
            </div>
            <a class="d-block text-center text-light" href="usuarios?action=cadastrar">Não possui uma conta? <span class="fw-bold">Cadastre-se</span></a>
        </form>

    </div>
</main>

<jsp:include page="/includes/footer.jsp"/>

<script src="resources/js/bootstrap.bundle.js"></script>
<script src="resources/js/globals.js"></script>
</body>
</html>
