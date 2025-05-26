<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Login - Fintech</title>

    <%-- Bootstrap Icons --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <%-- Bootstrap --%>
    <link rel="stylesheet" href="Resources/css/bootstrap.css">
    <%-- CSS Global --%>
    <link rel="stylesheet" href="Resources/css/global.css">
</head>
<body class="d-flex flex-column bg-dark text-white min-vh-100">

<jsp:include page="/Includes/Header.jsp"/>

<main class="d-flex flex-column justify-content-center align-items-center flex-grow-1 w-100">
    <div class="text-center mb-4">
        <h1 class="text-success">Seja bem-vindo à Fintech!</h1>
    </div>

    <div class="card bg-secondary text-white p-4 shadow" style="min-width: 350px; max-width: 400px;">
        <h2 class="text-center mb-4">Acessar</h2>

        <c:if test="${not empty erro}">
            <div class="alert alert-danger">${erro}</div>
        </c:if>

        <form action="Login" method="post" class="needs-validation" novalidate>
            <div class="form-group mb-3">
                <label for="usuario" class="form-label fw-bold">Usuário</label>
                <input type="text" class="form-control" id="usuario" name="usuario" required>
                <div class="invalid-feedback">Informe seu nome de usuário.</div>
            </div>

            <div class="form-group mb-3">
                <label for="senha" class="form-label fw-bold">Senha</label>
                <input type="password" class="form-control" id="senha" name="senha" required>
                <div class="invalid-feedback">Informe sua senha.</div>
            </div>

            <div class="d-grid">
                <button type="submit" class="btn btn-dark">Entrar</button>
            </div>
        </form>

    </div>
</main>

<jsp:include page="/Includes/Footer.jsp"/>

<script src="resources/js/bootstrap.bundle.js"></script>
<script src="resources/js/globals.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", () => {
        const forms = document.querySelectorAll('.needs-validation');
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            });
        });
    });
</script>
</body>
</html>
