<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Fintech | Cadastro de Usuário</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Bootstrap 5 CSS -->
    <link href="resources/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #121212;
            color: #f1f1f1;
        }
        .form-container {
            background-color: #1f1f1f;
            border: 1px solid #333;
        }
        .form-control {
            background-color: #2a2a2a;
            color: #f1f1f1;
            border: 1px solid #444;
        }
        .form-control::placeholder {
            color: #aaa;
        }
        .form-label {
            color: #ccc;
        }
    </style>
</head>
<body>

<div class="container min-vh-100 d-flex justify-content-center align-items-center">
    <div class="col-md-6 col-lg-5 p-5 rounded shadow form-container">
        <h2 class="mb-4 text-center text-light">Cadastro de Usuário</h2>

        <%-- Alerta de erro --%>
        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                ${error}
            </div>
        </c:if>
        <form action="usuarios" class="needs-validation" novalidate method="post">
            <input type="hidden" name="action" value="create" />
            <div class="mb-3">
                <label for="name" class="form-label">Nome</label>
                <input type="text" name="name" class="form-control" id="name" placeholder="Digite seu nome" required>
                <div class="invalid-feedback">
                    Por favor, informe seu nome.
                </div>
            </div>
            <div class="mb-3">
                <label for="cpf" class="form-label">CPF</label>
                <input type="text" minlength="14" class="form-control" name="cpf" id="cpf" placeholder="000.000.000-00" required>
                <div class="invalid-feedback">
                    Por favor, informe um cpf válido.
                </div>
            </div>
            <div class="mb-3">
                <label for="email" class="form-label">E-mail</label>
                <input type="email" class="form-control" name="username" id="email" placeholder="nome@exemplo.com" required>
                <div class="invalid-feedback">
                    Por favor, informe seu e-mail.
                </div>
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Senha</label>
                <input minlength="8" type="password" class="form-control" name="password" id="password" placeholder="Digite sua senha" required>
                <div class="invalid-feedback">
                    A senha deve ter no mínimo 8 caracteres.
                </div>
            </div>
            <div class="mb-4">
                <label for="confirmPassword" class="form-label">Confirmação de Senha</label>
                <input type="password" class="form-control" id="confirmPassword" placeholder="Confirme sua senha" required>
                <div id="confirmPasswordFeedback" class="invalid-feedback">
                    A confirmação de senha é obrigatória.
                </div>
            </div>
            <div class="d-grid">
                <button type="submit" class="btn btn-primary">Cadastrar</button>
            </div>
        </form>
    </div>
</div>

<%-- IMask JS --%>
<script src="resources/js/imask.js"></script>
<!-- Bootstrap JS -->
<script src="resources/js/bootstrap.bundle.js"></script>
<script src="resources/js/cadastrar-usuario.js"></script>
</body>
</html>

