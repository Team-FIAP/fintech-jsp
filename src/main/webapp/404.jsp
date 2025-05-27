<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <title>Erro - 404 Page </title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <%--  Bootstrap Icons --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <%--  Bootstrap --%>
    <link rel="stylesheet" href="./resources/css/bootstrap.css">
    <%--  CSS Global --%>
    <link rel="stylesheet" href="./resources/css/globals.css">
</head>
<body class="d-flex flex-column min-vh-100 bg-dark text-white">

<main class="flex-grow-1 d-flex align-items-center justify-content-center text-center">
    <div class="container">
        <h2 class="mb-4">404 - Página não encontrada</h2>
        <p class="mb-4">
            Oops! A página que você procura não existe.<br/>
            Mas suas finanças continuam sob controle.
        </p>
        <a href="/" class="btn btn-warning px-4 py-2 text-dark fw-bold">Voltar à Página Inicial</a>
    </div>
</main>

<!-- Footer incluso abaixo -->
<jsp:include page="/includes/footer.jsp"/>
</body>

</html>