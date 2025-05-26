<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="navbar navbar-expand-lg navbar-dark bg-dark px-4">
    <a class="navbar-brand d-flex align-items-center text-light" href="#">
        <img src="../resources/img/logo.svg" alt="Logo" width="40" height="40" class="me-2">
        Fintech
    </a>

    <button class="btn text-light ms-3 d-none d-lg-inline-flex" id="sidebarToggle" type="button">
        <i class="bi bi-layout-sidebar-inset fs-5"></i>
    </button>

    <button class="btn text-light ms-auto d-lg-none" type="button" data-bs-toggle="offcanvas" data-bs-target="#sidebar" aria-controls="sidebar">
        <i class="bi bi-list fs-4"></i>
    </button>

    <div class="dropdown ms-auto d-none d-lg-flex align-items-center text-light">
        <a href="#" class="d-flex align-items-center text-light text-decoration-none dropdown-toggle" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
            <i class="bi bi-person-circle fs-4 me-2"></i>
            <span><c:out value="${sessionScope.loggedUser.name}" default="Usuário" /></span>
        </a>
        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
            <li><a class="dropdown-item" href="login">Sair</a></li>
        </ul>
    </div>
</header>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const toggleButton = document.getElementById("sidebarToggle");
        toggleButton.addEventListener("click", () => {
            document.body.classList.toggle("sidebar-expanded");
        });
    });
</script>
