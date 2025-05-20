// Máscara CPF
const cpfElement = document.getElementById("cpf");
IMask(cpfElement, {
    mask: "000.000.000-00"
});

(function () {
    const form = document.querySelector(".needs-validation");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirmPassword");
    const confirmFeedback = document.getElementById("confirmPasswordFeedback");

    form.addEventListener("submit", function (event) {
        // Reset feedback
        confirmPassword.setCustomValidity("");

        if (password.value !== confirmPassword.value) {
            confirmPassword.setCustomValidity("As senhas devem ser compatíveis");
            confirmFeedback.textContent = "As senhas devem ser compatíveis.";
        }

        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        }

        form.classList.add("was-validated");
    });
})();