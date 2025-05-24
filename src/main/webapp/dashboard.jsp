<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Dashboard</title>
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
        <h1>Dashboard Financeiro</h1>

        <!-- Resumo financeiro -->
        <div class="row mb-3 gap-3 gap-md-0">
            <div class="col-md-3">
                <div class="card text-bg-primary">
                    <div class="card-body">
                        <h5 class="card-title">Saldo Total</h5>
                        <p class="card-text fs-2">R$ <fmt:formatNumber pattern="0.00" value="${totalBalance}"/></p>
                        <small class="text-white-50">Valor atual</small>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-bg-success">
                    <div class="card-body">
                        <h5 class="card-title">Receitas</h5>
                        <p class="card-text fs-2">R$ <fmt:formatNumber pattern="0.00" value="${totalIncomes}"/></p>
                        <small class="text-white-50">Valores do mês atual</small>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-bg-danger">
                    <div class="card-body">
                        <h5 class="card-title">Despesas</h5>
                        <p class="card-text fs-2">R$ <fmt:formatNumber pattern="0.00" value="${totalExpenses}"/></p>
                        <small class="text-white-50">Valores do mês atual</small>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-bg-warning">
                    <div class="card-body">
                        <h5 class="card-title">Investimentos</h5>
                        <p class="card-text fs-2">R$ <fmt:formatNumber pattern="0.00"
                                                                       value="${totalAccumulatedInvestments}"/></p>
                        <small class="text-black-50">Valores acumulados</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- Gráficos principais -->
        <div class="row mb-4 gap-3 gap-md-0">
            <div class="col-md-6 h-100">
                <div class="card text-bg-dark h-100">
                    <div class="card-header">Despesas por Categoria (Mês Atual)</div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty expensesByCategory}">
                                <div class="text-white mt-3 d-flex flex-column text-center" role="alert">
                                    Nenhuma despesa cadastrada no mês atual.
                                    <a href="despesas" class="mt-3 btn btn-light">Cadastrar nova despesa</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <canvas id="expensesByCategoryChart" class="h-100 w-100"></canvas>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
            <div class="col-md-6 d-flex flex-column gap-3 h-100">
                <div class="card text-bg-dark flex-grow-1">
                    <div class="card-header">Receitas vs Despesas (Mês Atual)</div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${totalExpenses == 0}">
                                <div class="text-white mt-3 d-flex flex-column text-center" role="alert">
                                    Nenhuma despesa cadastrada no mês atual.
                                    <a href="despesas" class="mt-3 btn btn-light">Cadastrar nova despesa</a>
                                </div>
                            </c:when><c:when test="${totalIncomes == 0}">
                                <div class="text-white mt-3 d-flex flex-column text-center" role="alert">
                                    Nenhuma receita cadastrada no mês atual.
                                    <a href="receitas" class="mt-3 btn btn-light">Cadastrar nova receita</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <canvas id="incomeExpenseChart" style="max-height: 225px;"></canvas>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="card text-bg-dark flex-grow-1">
                    <div class="card-header">Despesas Essenciais vs Não Essenciais (Mês Atual)</div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${totalExpenses == 0}">
                                <div class="text-white mt-3 d-flex flex-column text-center" role="alert">
                                    Nenhuma despesa cadastrada no mês atual.
                                    <a href="despesas" class="mt-3 btn btn-light">Cadastrar nova despesa</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <canvas id="expensesByCategoryTypeChart" style="max-height: 225px;"></canvas>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <!-- Gráfico de fluxo de caixa mensal -->
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="card text-bg-dark">
                    <div class="card-header">Fluxo de Caixa Anual</div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${!hasExpenses}">
                                <div class="text-white mt-3 d-flex flex-column text-center" role="alert">
                                    Nenhuma despesa cadastrada no ano atual.
                                    <a href="despesas" class="mt-3 btn btn-light">Cadastrar nova despesa</a>
                                </div>
                            </c:when>
                            <c:when test="${!hasIncomes}">
                                <div class="text-white mt-3 d-flex flex-column text-center" role="alert">
                                    Nenhuma receita cadastrada no ano atual.
                                    <a href="receitas" class="mt-3 btn btn-light">Cadastrar nova receita</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <canvas id="cashFlowChart" style="min-height: 350px;"></canvas>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/includes/footer.jsp"/>

<%-- Bootstrap JS --%>
<script src="resources/js/bootstrap.bundle.js"></script>
<script src="resources/js/globals.js"></script>

<%-- Chart.js --%>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js"
        integrity="sha512-CQBWl4fJHWbryGE+Pc7UAxWMUMNMWzWxF4SQo9CgkJIN1kx6djDQZjh3Y8SZ1d+6I+1zze6Z7kHXO7q3UyZAWw=="
        crossorigin="anonymous" referrerpolicy="no-referrer"></script>

<!-- Scripts para gráficos -->
<script>
    // Depesas por Categoria
    new Chart(document.getElementById('expensesByCategoryChart'), {
        type: 'pie',
        data: {
            labels: [
                <c:forEach var="item" items="${expensesByCategory}" varStatus="loop">
                "${item.category}"<c:if test="${!loop.last}">, </c:if>
                </c:forEach>
            ],
            datasets: [{
                data: [
                    <c:forEach var="item" items="${expensesByCategory}" varStatus="loop">
                        ${item.total}<c:if test="${!loop.last}">, </c:if>
                    </c:forEach>
                ],
                backgroundColor: [
                    <c:forEach var="item" items="${expensesByCategory}" varStatus="loop">
                        "${item.color}"<c:if test="${!loop.last}">, </c:if>
                    </c:forEach>
                ]
            }],
        },
        options: {
            maintainAspectRatio: false,
        }
    });

    // Receitas vs Despesas
    new Chart(document.getElementById('incomeExpenseChart'), {
        type: 'bar',
        data: {
            labels: ['Receitas', 'Despesas'],
            datasets: [{
                label: 'Valores (R$)',
                data: [${totalIncomes}, ${totalExpenses}],
                backgroundColor: ['#198754', '#dc3545']
            }]
        },
        options: {
            maintainAspectRatio: false,
        }
    });

    // Despesas Essenciais vs Não Essenciais
    new Chart(document.getElementById('expensesByCategoryTypeChart'), {
        type: 'bar',
        data: {
            labels: ['Essenciais', 'Não Essenciais'],
            datasets: [{
                label: 'Total em Despesas (R$)',
                data: [${expensesTypeComparison["ESSENTIAL"]}, ${expensesTypeComparison["NON_ESSENTIAL"]}],
                backgroundColor: ['#0d6efd', '#ffc107']
            }]
        },
        options: {
            maintainAspectRatio: false,
        }
    });

    // Fluxo de Caixa Mensal (Receitas e Despesas por mês)
    new Chart(document.getElementById('cashFlowChart'), {
        type: 'bar',
        data: {
            labels: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
            datasets: [
                {
                    label: 'Receitas',
                    data: [
                        <c:forEach var="val" items="${monthlyIncomes}" varStatus="loop">
                        ${val}<c:if test="${!loop.last}">, </c:if>
                        </c:forEach>
                    ],
                    backgroundColor: '#198754'
                },
                {
                    label: 'Despesas',
                    data: [
                        <c:forEach var="val" items="${monthlyExpenses}" varStatus="loop">
                        ${val}<c:if test="${!loop.last}">, </c:if>
                        </c:forEach>
                    ],
                    backgroundColor: '#dc3545'
                }
            ]
        },
        options: {
            maintainAspectRatio: false,
            responsive: true,
            plugins: {
                legend: {position: 'top'},
                title: {display: false}
            }
        }
    });
</script>
</body>
</html>
