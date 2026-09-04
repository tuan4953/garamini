document.addEventListener("DOMContentLoaded", function () {
    const licensePlateInput = document.getElementById("licensePlate");
    if (licensePlateInput) {
        licensePlateInput.addEventListener("input", function () {
            this.value = this.value.toUpperCase();
        });
    }
});