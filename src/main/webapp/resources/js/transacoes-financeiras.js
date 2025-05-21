const deleteModal = document.getElementById("confirmDeleteModal");
deleteModal.addEventListener("show.bs.modal", function (event) {
    const button = event.relatedTarget;
    const id = button.getAttribute("data-id");
    const type = button.getAttribute("data-type");

    console.log(button);

    document.getElementById("confirmDeleteId").value = id;
    document.getElementById("confirmDeleteType").value = type;
});