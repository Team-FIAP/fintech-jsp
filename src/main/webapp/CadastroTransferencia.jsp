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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Resources/css/bootstrap.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Resources/css/global.css">
</head>
<body class="d-flex flex-column bg-dark text-white">

<jsp:include page="/Includes/Header.jsp"/>
<div class="d-flex flex-grow-1">
    <jsp:include page="/Includes/Sidebar.jsp"></jsp:include>

    <main class="layout">
        <div class="container">
            <c:if test="${empty sessionScope.usuarioLogado}">
                <c:redirect url="index.jsp" />
            </c:if>

            <h1 class="mb-4">Bem-vindo, ${sessionScope.usuarioLogado}!</h1>

            <div class="card p-4 mb-4">
                <h2 class="mb-3">Histórico de Transferências</h2>

                <c:if test="${empty historico}">
                    <div class="alert alert-info">Nenhuma transferência realizada ainda.</div>
                </c:if>

                <c:if test="${not empty historico}">
                    <table class="table table-dark table-hover table-bordered text-white mt-3">
                        <thead class="table-light text-dark">
                        <tr>
                            <th>#</th>
                            <th>Data</th>
                            <th>Valor</th>
                            <th>Destinatário</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="t" items="${historico}" varStatus="status">
                            <tr>
                                <td>${status.index + 1}</td>
                                <td><fmt:formatDate value="${t.data}" pattern="dd/MM/yyyy"/></td>
                                <td>R$ ${t.valor}</td>
                                <td>${t.usuario}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:if>

                <div class="mt-3">
                    <a href="EditarTransferencia.jsp" class="btn btn-light">Editar Transferência</a>
                </div>
            </div>

            <div class="card p-4">
                <h2 class="mb-3">Realizar Nova Transferência</h2>
                <form action="Transferencia" method="post" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label class="form-label fw-bold" for="valor">Valor</label>
                        <div class="input-group">
                            <span class="input-group-text">R$</span>
                            <input type="number" name="valor" id="valor" class="form-control" placeholder="Digite o valor:" min="0.01" step="0.01" required>
                            <div class="invalid-feedback">Informe um valor válido.</div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold" for="usuario">Conta de destino</label>
                        <input type="text" name="usuario" id="usuario" class="form-control" placeholder="Digite a conta de destino:" required>
                        <div class="invalid-feedback">Informe o nome do destinatário.</div>
                    </div>

                    <button type="submit" class="btn btn-light">Realizar Transferência</button>
                </form>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/Includes/Footer.jsp"/>

<script src="${pageContext.request.contextPath}/resources/js/bootstrap.bundle.js"></script>
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
