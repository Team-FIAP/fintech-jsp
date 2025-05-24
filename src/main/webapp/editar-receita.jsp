<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Editar Receita</title>
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

<jsp:include page="/includes/header.jsp"/>

<div class="d-flex flex-grow-1">
    <jsp:include page="/includes/sidebar.jsp"/>

    <main class="layout">
        <div class="container">
            <div class="mt-5 ms-5 me-5">
                <h2 class="text-center">Editar Receita</h2>
                <div class="card mb-3">
                    <div class="card-header">
                        <c:if test="${not empty mensagem}">
                            <div class="alert alert-success ms-2 me-2 m-auto">${mensagem}</div>
                        </c:if>
                        <c:if test="${not empty erro}">
                            <div class="alert alert-danger ms-2 me-2 m-auto">${erro}</div>
                        </c:if>
                    </div>
                    <div class="card-body">
                        <form action="receita?action=editar" method="post">
                            <input type="hidden" name="action" value="editar">
                            <input type="hidden" name="id-income" value="${income.id}">

                            <div class="form-group">
                                <div class="form-group">
                                    <label class="fw-bold" for="id-conta">Conta</label>
                                    <select name="conta" id="conta" class="form-control">
                                        <option value="0">Selecione</option>
                                        <c:forEach items="${conta}" var="c">
                                            <option value="${c.id}"> ${c.nome}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <label class="mt-3 fw-bold" for="id-descricao">Descrição</label>
                            <div class="form-floating">
                                    <textarea class="form-control" placeholder="Descrição" id="id-descricao"
                                              style="height: 100px" maxlength="255">${income.description}</textarea>
                                <label for="descricao" value="${income.description}">Descrição</label>
                            </div>

                            <div class=" form-group mt-3">
                                <label class="fw-bold" for="id-valor">Valor*</label>
                                <div class="input-group"><span class="input-group-text">R$</span> <input
                                        type="number"
                                        name="valor"
                                        id="id-valor"
                                        class="form-control"
                                        min="0.01"
                                        step="0.01"
                                        inputmode="decimal"
                                        value="${income.amount}"
                                >
                                    <div class="invalid-feedback">
                                        Por favor, insira um valor monetário positivo (Ex: 123.45).
                                    </div>
                                </div>
                            </div>

                            <div class="form-group mt-3">
                                <label class="fw-bold" for="id-data">Data*</label>
                                <input type="date" name="data" id="id-data" class="form-control" value="${income.date}">
                            </div>

                            <div class="form-group mt-3">
                                <label class="fw-bold" for="id-observacoes">Observações</label>
                                <input type="text" name="observacoes" id="id-observacoes" class="form-control"
                                       value="${income.observation}">
                            </div>
                            <input type="submit" value="Salvar" class="btn btn-dark mt-3">
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>
<jsp:include page="/includes/footer.jsp"/>

<%-- Bootstrap JS --%>
<script src="resources/js/bootstrap.bundle.js"></script>

<%-- Ativando tooltips no menu lateral --%>
<script>
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipTriggerList.forEach(el => new bootstrap.Tooltip(el));
</script>
</body>
</html>
