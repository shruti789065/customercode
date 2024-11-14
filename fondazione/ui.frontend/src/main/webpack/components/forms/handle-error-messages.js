/**
 * Create error message element.
 * @param {DOM Element} input
 * @param {string} message
 */
function createErrorMessageElement(input, message) {
    let errorMessageElement = input.nextElementSibling;

    // Check if the next sibling is an error message element
    if (!errorMessageElement || !errorMessageElement.classList.contains("error-message")) {
        // Create a new error message element
        errorMessageElement = document.createElement("div");
        errorMessageElement.classList.add("error-message");
        input.insertAdjacentElement("afterend", errorMessageElement);
    }

    // Set the error message text
    errorMessageElement.textContent = message;
    errorMessageElement.style.display = "block";
}

/**
 * Remove the error message.
 * @param {DOM Element} input
 */
function removeErrorMessageElement(input) {
    let errorMessageElement = input.nextElementSibling;
    if (errorMessageElement && errorMessageElement.classList.contains("error-message")) {
        errorMessageElement.style.display = "none";
    }
}

export {createErrorMessageElement, removeErrorMessageElement};
