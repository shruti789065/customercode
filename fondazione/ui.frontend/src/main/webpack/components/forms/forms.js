/**
 * @file
 * Basic form validation.
 */

document.addEventListener("DOMContentLoaded", function() {

    const formInputs = document.querySelectorAll(
        'input[required]:not([type="hidden"]), textarea',
      );

    /**
     * Check if a input is empty.
     * @param {string} value
     * @returns boolean
     */
    function isEmpty(value) {
        return value.trim() === "";
    }

    /**
     * Validate the email.
     * @param {string} email
     * @returns boolean
     */
    function isValidEmail(email) {
        const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        return emailPattern.test(email);
    }

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

    /**
     * Handle the checks.
     * @param {DOM Element} input
     * @param {Event} event
     */
    function inputCheck(input, event) {
        input.addEventListener(event, () => {
            let isValid = true;

            const value = input.value;
            const inputType = input.getAttribute("type");
            const errorMessage = input.closest("[data-cmp-required-message]").getAttribute("data-cmp-required-message");

            // Remove existing error messages
            removeErrorMessageElement(input);

            // Check if the field is empty
            if (isEmpty(value)) {
                createErrorMessageElement(input, errorMessage);
                isValid = false;
            } else if (inputType === "email" && !isValidEmail(value)) {
                createErrorMessageElement(input, "Please enter a valid email address");
                isValid = false;
            }
            else if (inputType === "checkbox" && !input.checked) {
                createErrorMessageElement(input, errorMessage);
                isValid = false;
            }
        });
    }

    formInputs.forEach((input) => {
      inputCheck(input, 'focusout');
    });
});
