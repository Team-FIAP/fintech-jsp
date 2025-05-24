<%--
  Created by IntelliJ IDEA.
  User: mdspa
  Date: 22/05/2025
  Time: 20:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>${expense != null ? "Editar Despesa" : "Cadastrar Despesa"}</title>
    <meta charset="UTF-8">

    <%-- Bootstrap Icons --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <%-- Bootstrap --%>
    <link rel="stylesheet" href="resources/css/bootstrap.css">
    <%-- CSS Global --%>
    <link rel="stylesheet" href="resources/css/globals.css">
</head>
<body class="d-flex flex-column bg-dark text-white">
<jsp:include page="/includes/header.jsp"/>
<div class="d-flex flex-grow-1">
    <jsp:include page="/includes/sidebar.jsp"/>

    <main class="layout">
        <div class="container">
            <div class="mt-5 ms-5 me-5">
                <h1 class="mb-3 text-center">${expense != null ? "Editar Despesa" : "Cadastrar Despesa"}</h1>
                <div class="card mb-3 p-3">
                    <form class="needs-validation" novalidate action="/despesas" method="post">
                        <input type="hidden" name="id" value="${expense.id}">
                        <div class="form-group">
                            <label for="originAccountId" class="form-label fw-bold mt-3">Conta</label>
                            <select class="form-select" id="originAccountId" name="originAccountId" required>
                                <option value="0" disabled selected>Selecione uma conta</option>
                                <c:forEach var="account" items="${accounts}">

                                    <option value="${account.id}"
                                            <c:if test="${expense.originAccount.id == account.id}">selected</c:if>>${account.name}</option>
                                </c:forEach>
                            </select>
                            <div class="invalid-feedback">
                                Por favor, informe uma conta.
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="expenseCategory" class="form-label fw-bold mt-3">Categoria</label>
                            <select class="form-select" id="expenseCategory" name="expenseCategory" required>
                                <option value="0" disabled selected>Selecione uma categoria</option>
                                <c:forEach var="expenseCategory" items="${expenseCategories}">
                                    <option value="${expenseCategory.id}"
                                            <c:if test="${expense.category.id == expenseCategory.id}">selected</c:if>>${expenseCategory.name}</option>
                                </c:forEach>
                            </select>
                            <div class="invalid-feedback">
                                Por favor, informe uma categoria.
                            </div>
                        </div>

                        <label for="descricao" value="${expense.description}" class="fw-bold mt-3">Descrição</label>
                        <div class="form-floating mt-2">
                            <textarea class="form-control" placeholder="Descrição" id="id-descricao"
                                      style="height: 100px" maxlength="255">${expense.description}</textarea>
                        </div>

                        <div class="form-group mt-3">
                            <label class="fw-bold" for="id-data">Data*</label>
                            <input type="date" name="data" id="id-data" class="form-control" value="${expense.date}"
                                   required>
                        </div>

                        <div class="form-group mt-3">
                            <label class="fw-bold" for="id-observacoes">Observações</label>
                            <input type="text" name="observacoes" id="id-observacoes" class="form-control"
                                   value="${expense.observation}">
                        </div>

                        <div class="col-md-12">
                            <button type="submit" class="btn btn-dark mt-3">Salvar</button>
                            <a href="transacoes-financeiras" class="btn btn-light mt-3 ms-2">Cancelar</a>
                        </div>
                    </form>


    </main>
</div>
<jsp:include page="/includes/footer.jsp"/>

<script src="resources/js/bootstrap.bundle.js"></script>
<script src="resources/js/globals.js"></script>
<script src="resources/js/transacoes-financeiras.js"></script>
</body>
</html>