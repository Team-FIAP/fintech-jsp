<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Fintech | Movimentações financeiras</title>
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
        <h2 class="mb-4">Transações Financeiras</h2>

        <%-- Filtros --%>
        <form method="get" class="row g-3 mb-4 needs-validation" novalidate>
            <div class="col-md-3">
                <label for="startDate" class="form-label">Data Início</label>
                <input required type="date" class="form-control" id="startDate" name="startDate" value="${startDate}">
                <div class="invalid-feedback">
                    Informe a data de início.
                </div>
            </div>
            <div class="col-md-3">
                <label for="endDate" class="form-label">Data Fim</label>
                <input required type="date" class="form-control" id="endDate" name="endDate" value="${endDate}">
                <div class="invalid-feedback">
                    Informe a data de fim.
                </div>
            </div>
            <div class="col-md-3">
                <label for="type" class="form-label">Tipo</label>
                <select class="form-select" id="type" name="type">
                    <option value="">Todos</option>
                    <option value="RECEITA" <c:if test="${param.type == 'RECEITA'}">selected</c:if>>Receita</option>
                    <option value="DESPESA" <c:if test="${param.type == 'DESPESA'}">selected</c:if>>Despesa</option>
                    <option value="TRANSFERENCIA" <c:if test="${param.type == 'TRANSFERENCIA'}">selected</c:if>>
                        Transferência
                    </option>
                    <option value="INVESTIMENTO" <c:if test="${param.type == 'INVESTIMENTO'}">selected</c:if>>
                        Investimento
                    </option>
                </select>
            </div>
            <div class="col-md-3">
                <label for="accountId" class="form-label">Conta</label>
                <select class="form-select" id="accountId" name="accountId">
                    <option value="">Todas</option>
                    <c:forEach var="account" items="${accounts}">
                        <option value="${account.id}" <c:if test="${param.accountId == account.id}">selected</c:if>>${account.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-12 d-flex justify-content-end">
                <button type="submit" class="btn btn-dark">Filtrar</button>
            </div>
        </form>

        <%-- Alerta de erro --%>
        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                    ${error}
            </div>
        </c:if>

        <%-- Tabela --%>
        <div class="table-responsive table-wrapper">
            <table class="table table-dark table-hover table-bordered align-middle">
                <thead>
                <tr>
                    <th>Tipo de Movimentação</th>
                    <th>Valor</th>
                    <th>Descrição</th>
                    <th>Data</th>
                    <th>Conta</th>
                </tr>
                </thead>
                <tbody>
                <c:if test="${empty transactions}">
                    <tr>
                        <td colspan="5" class="text-center">Nenhuma movimentação financeira cadastrada para os filtros aplicados.</td>
                    </tr>
                </c:if>
                <c:forEach var="trans" items="${transactions}">
                    <c:choose>
                        <c:when test="${trans.type == 'TRANSFER'}">
                            <tr>
                                <td>
                                    <span class="badge bg-primary text-white rounded-pill">Transferência</span>
                                </td>
                                    <td class="text-danger">
                                        - R$ <fmt:formatNumber type="number" value="${trans.amount}" pattern="0.00"/>
                                    </td>
                                <td>${trans.description}</td>
                                <fmt:parseDate value="${trans.date}" pattern="yyyy-MM-dd" var="parsedDate" type="date"/>
                                <td><fmt:formatDate pattern="dd/MM/yyyy" value="${parsedDate}"/></td>
                                <td>${trans.originAccount.name}</td>
                            </tr>
                            <tr>
                                <td>
                                    <span class="badge bg-primary text-white rounded-pill">Transferência</span>
                                </td>
                                    <td class="text-success">
                                        + R$ <fmt:formatNumber type="number" value="${trans.amount}" pattern="0.00"/>
                                    </td>
                                <td>${trans.description}</td>
                                <fmt:parseDate value="${trans.date}" pattern="yyyy-MM-dd" var="parsedDate" type="date"/>
                                <td><fmt:formatDate pattern="dd/MM/yyyy" value="${parsedDate}"/></td>
                                <td>${trans.destinationAccount.name}</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td>
                                    <c:choose>
                                        <c:when test="${trans.type == 'INCOME'}">
                                            <span class="badge bg-success text-dark rounded-pill">Receita</span>
                                        </c:when>
                                        <c:when test="${trans.type == 'EXPENSE'}">
                                            <span class="badge bg-danger text-white rounded-pill">Despesa</span>
                                        </c:when>
                                        <c:when test="${trans.type == 'INVESTMENT'}">
                                            <span class="badge bg-warning text-dark rounded-pill">Investimento</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary text-white rounded-pill">${trans.type}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <c:choose>
                                    <c:when test="${trans.type == 'INCOME'}">
                                        <td class="text-success">+ R$ <fmt:formatNumber type="number"
                                                                                        value="${trans.amount}"
                                                                                        pattern="0.00"/></td>
                                    </c:when>
                                    <c:otherwise>
                                        <td class="text-danger">- R$ <fmt:formatNumber type="number"
                                                                                       value="${trans.amount}"
                                                                                       pattern="0.00"/></td>
                                    </c:otherwise>
                                </c:choose>
                                <td>${trans.description}</td>
                                <fmt:parseDate value="${trans.date}" pattern="yyyy-MM-dd" var="parsedDate" type="date"/>
                                <td><fmt:formatDate pattern="dd/MM/yyyy" value="${parsedDate}"/></td>
                                <td>${trans.originAccount.name}</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </main>
</div>
<jsp:include page="/includes/footer.jsp"/>

<script src="resources/js/bootstrap.bundle.js"></script>
<script src="resources/js/globals.js"></script>
</body>
</html>
