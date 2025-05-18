function toggleNewAddressForm() {
    const addressDropdown = document.getElementById("address");
    const newAddressForm = document.getElementById("newAddressForm");

    newAddressForm.style.display = addressDropdown.value === "new" ? "block" : "none";
}

function validateFields() {
    const addressDropdown = document.getElementById("address");
    const cardName = document.getElementById("cardName").value.trim();
    const cardNumber = document.getElementById("cardNumber").value.trim();
    const expiryDate = document.getElementById("expiryDate").value.trim();
    const cvv = document.getElementById("cvv").value.trim();
    const checkoutButton = document.getElementById("checkoutButton");

    let addressSelected = addressDropdown.value && addressDropdown.value !== "new";

    if (addressDropdown.value === "new") {
        const street = document.getElementById("street").value.trim();
        const houseNumber = document.getElementById("houseNumber").value.trim();
        const postalCode = document.getElementById("postalCode").value.trim();
        const town = document.getElementById("town").value.trim();

        addressSelected = street && houseNumber && postalCode && town;
    }
    if (addressSelected && cardName && cardNumber && expiryDate && cvv) {
        checkoutButton.classList.remove("disabled");
    } else {
        checkoutButton.classList.add("disabled");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("address").addEventListener("change", function () {
        toggleNewAddressForm();
        validateFields();
    });
    document.querySelectorAll("#cardName, #cardNumber, #expiryDate, #cvv")
        .forEach(field => {
            field.addEventListener("input", validateFields);
        });
    validateFields();
});

function handleAddressChange() {
    const dropdown = document.getElementById("address");
    const hiddenInput = document.getElementById("selectedAddressDTOid");
    hiddenInput.value = dropdown.value;
    toggleNewAddressForm();
}




