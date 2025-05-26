document.addEventListener("DOMContentLoaded", () => {
    const deleteModal = document.getElementById("confirmDeleteModal");
    deleteModal.addEventListener("show.bs.modal", function (event) {
        const button = event.relatedTarget;
        const id = button.getAttribute("data-id");
        const type = button.getAttribute("data-type");

        document.getElementById("confirmDeleteId").value = id;
        document.getElementById("confirmDeleteType").value = type;
    });

    const fabMenuButton = document.getElementById("fabMenuButton");
    fabMenuButton.addEventListener("click", () => {
        const menu = document.getElementById("fabMenu");
        menu.classList.toggle("d-none");
    });
});
