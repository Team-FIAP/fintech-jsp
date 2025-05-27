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
    <title>${investment != null ? "Editar Investimento" : "Cadastrar Investimento"}</title>
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
                <h1 class="mb-3 text-center">${investment != null ? "Editar Investimento" : "Cadastrar Investimento"}</h1>
                <div class="card mb-3 p-3">
                    <c:if test="${not empty error}">
                    <div class="alert alert-danger ms-2 me-2 m-auto">${error}</div>
                    </c:if>
                    <form class="needs-validation" novalidate action="investimentos" id="postForm" method="post">
                        <input type="hidden" name="action" value="${investment != null ? 'editar' : 'cadastrar'}">
                        <input type="hidden" name="id" value="${investment.id}">
                        <div class="form-group mb-3">
                            <label for="originAccountId" class="form-label mt-3">Conta*</label>
                            <select class="form-select" id="originAccountId" name="originAccountId" required>
                                <option value=""></option>
                                <c:forEach var="account" items="${accounts}">
                                    <option value="${account.id}"
                                            <c:if test="${investment.originAccount.id == account.id}">selected</c:if>>${account.name}</option>
                                </c:forEach>
                            </select>
                            <div class="invalid-feedback">
                                Por favor, informe uma conta.
                            </div>
                        </div>
                        <div class=" form-group mb-3">
                            <label class="form-label" for="amount" >Valor*</label>
                            <div class="input-group">
                                <span class="input-group-text">R$</span>
                                <input
                                    type="number"
                                    name="amount"
                                    id="amount"
                                    class="form-control"
                                    min="0.01"
                                    step="0.01"
                                    inputmode="decimal"
                                    value="${investment.amount}"
                                    required
                                >
                                <div class="invalid-feedback">
                                    Por favor, insira um valor monetário positivo (Ex: 123.45).
                                </div>
                            </div>
                        </div>
                        <div class="form-group mb-3">
                            <label for="description" value="${investment.description}" id="description" class="form-label mt-3">Descrição*</label>
                            <input class="form-control" id="description" name="description" required maxlength="255" value="${investment.description}">
                            <div class="invalid-feedback">
                                Por favor, informe a descrição do investimento.
                            </div>
                        </div>
                        <div class="form-group mb-3">
                            <label class="form-label" for="data">Data*</label>
                            <input type="date" name="date" id="data" class="form-control" value="${investment.date}"
                                   required>

                            <div class="invalid-feedback">
                                Por favor, informe a data da despesa.
                            </div>
                        </div>
                        <div class="form-group mb-3">
                            <label for="dueDate" class="form-label">Data de Vencimento*</label>
                            <input type="date" name="dueDate" class="form-control" id="dueDate" value="${investment.dueDate}" required>
                        </div>
                        <div class="form-group mb-3">
                            <label for="risk" class="form-label">Risco*</label>
                            <select name="risk" class="form-select" id="risk" required>
                                <option value="">Selecione o Risco</option>
                                <option value="BAIXO" ${investment.risk == 'BAIXO' ? 'selected' : ''}>Baixo</option>
                                <option value="MEDIO" ${investment.risk == 'MEDIO' ? 'selected' : ''}>Médio</option>
                                <option value="ALTO" ${investment.risk == 'ALTO' ? 'selected' : ''}>Alto</option>
                            </select>
                        </div>
                        <div class="form-group mb-3">
                            <label for="liquidity" class="form-label">Liquidez*</label>
                            <select name="liquidity" class="form-select" id="liquidity" required>
                                <option value="">Selecione a Liquidez</option>
                                <option value="DIARIA" ${investment.liquidity == 'DIARIA' ? 'selected' : ''}>Diária</option>
                            </select>
                        </div>
                        <div class="form-group mb-3">
                            <label for="type" class="form-label">Tipo*</label>
                            <select name="type" class="form-select" id="type" required>
                                <option value="">Selecione o tipo</option>
                                <option value="CDB" ${investment.type == 'CDB' ? 'selected' : ''}>CDB</option>
                                <option value="LCI" ${investment.type == 'LCI' ? 'selected' : ''}>LCI</option>
                                <option value="LCA" ${investment.type == 'LCA' ? 'selected' : ''}>LCA</option>
                            </select>
                            <div class="invalid-feedback">
                                Por favor, insira um valor válido para o tipo do investimento.
                            </div>
                        </div>
                        <div class=" form-group mb-3">
                            <label class="form-label" for="interestRate">Taxa de juros*</label>
                            <div class="input-group">
                                <span class="input-group-text">%</span>
                                <input
                                        type="number"
                                        name="interestRate"
                                        id="interestRate"
                                        class="form-control"
                                        min="0.01"
                                        step="0.01"
                                        inputmode="decimal"
                                        value="${investment.interest_rate}"
                                        required
                                >
                                <div class="invalid-feedback">
                                    Por favor, insira um valor válido para a taxa de juros.
                                </div>
                            </div>
                        </div>

                        <div class="form-group mb-3">
                            <label class="form-label" for="observation" id="observation">Observações</label>
                            <textarea type="text" name="observation" id="observation" class="form-control">${investment.observation}</textarea>
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