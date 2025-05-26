<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Editar Conta</title>
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
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Editar Conta</h2>
            <a href="contas?action=list" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Voltar
            </a>
        </div>

        <%-- Display error message if exists --%>
        <c:if test="${not empty error}">
            <div class="alert alert-danger mt-3">
                    ${error}
            </div>
        </c:if>

        <c:if test="${not empty account}">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card bg-secondary">
                        <div class="card-header">
                            <h5 class="card-title mb-0">
                                <i class="bi bi-pencil-square"></i> Dados da Conta
                            </h5>
                        </div>
                        <div class="card-body">
                            <form action="contas" method="post">
                                <input type="hidden" name="action" value="editAccount">
                                <input type="hidden" name="accountId" value="${account.id}">

                                <div class="mb-3">
                                    <label for="nome" class="form-label">Nome da Conta</label>
                                    <input type="text"
                                           class="form-control"
                                           id="nome"
                                           name="nome"
                                           value="${account.name}"
                                           required
                                           maxlength="100"
                                           placeholder="Digite o nome da conta">
                                    <div class="form-text">
                                        Nome atual: <strong>${account.name}</strong>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Saldo Atual</label>
                                    <div class="form-control bg-dark text-white" style="border: 1px solid #6c757d;">
                                        R$ <fmt:formatNumber value="${account.balance}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                    </div>
                                    <div class="form-text">
                                        O saldo não pode ser alterado.
                                    </div>
                                </div>

                                <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                    <a href="contas?action=list" class="btn btn-outline-secondary me-md-2">
                                        <i class="bi bi-x-circle"></i> Cancelar
                                    </a>
                                    <button type="submit" class="btn btn-warning">
                                        <i class="bi bi-check-circle"></i> Salvar Alterações
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <c:if test="${empty account}">
            <div class="alert alert-danger">
                <h4>Conta não encontrada</h4>
                <p>A conta que você está tentando editar não foi encontrada.</p>
                <a href="contas?action=list" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> Voltar para lista de contas
                </a>
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

<%-- Form validation --%>
<script>
    // Simple client-side validation
    document.querySelector('form').addEventListener('submit', function(e) {
        const nome = document.getElementById('nome').value.trim();

        if (nome === '') {
            e.preventDefault();
            alert('Por favor, digite um nome para a conta.');
            document.getElementById('nome').focus();
            return false;
        }

        if (nome.length < 2) {
            e.preventDefault();
            alert('O nome da conta deve ter pelo menos 2 caracteres.');
            document.getElementById('nome').focus();
            return false;
        }
    });
</script>

</body>
</html>