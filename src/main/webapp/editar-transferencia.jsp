<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${empty sessionScope.usuarioLogado}">
    <c:redirect url="CadastroTransferencia.jsp" />
</c:if>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Editar Transferência</title>
    <meta charset="UTF-8">

    <%-- Bootstrap e ícones --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Resources/css/bootstrap.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Resources/css/global.css">
</head>
<body class="d-flex flex-column bg-dark text-white min-vh-100">

<jsp:include page="/Includes/Header.jsp"/>

<div class="d-flex flex-grow-1">
    <jsp:include page="/Includes/Sidebar.jsp" />

    <main class="layout">
        <div class="container">
            <h1 class="mb-4">Editar Transferência</h1>

            <form action="Transferencia" method="post" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label for="valor" class="form-label fw-bold">Valor</label>
                    <input type="number" name="valor" id="valor" class="form-control" placeholder="Digite o valor:" min="0.01" step="0.01" required>
                    <div class="invalid-feedback">Informe um valor válido.</div>
                </div>

                <div class="mb-3">
                    <label for="usuario" class="form-label fw-bold">Conta de destino</label>
                    <input type="text" name="usuario" id="usuario" class="form-control" placeholder="Digite a conta de destino:" required>
                    <div class="invalid-feedback">Informe o nome do destinatário.</div>
                </div>

                <button type="submit" class="btn btn-light">Editar Transferência</button>
            </form>
        </div>
    </main>
</div>

<jsp:include page="/Includes/Footer.jsp"/>

<script src="${pageContext.request.contextPath}/Resources/js/bootstrap.bundle.js"></script>
<script>
    // Validação Bootstrap customizada
    (() => {
        'use strict'
        const forms = document.querySelectorAll('.needs-validation')
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault()
                    event.stopPropagation()
                }
                form.classList.add('was-validated')
            }, false)
        })
    })()
</script>

</body>
</html>
