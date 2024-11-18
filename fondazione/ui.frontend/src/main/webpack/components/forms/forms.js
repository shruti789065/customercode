/**
 * @file
 * Basic form validation.
 */

import { createErrorMessageElement, removeErrorMessageElement } from './handle-error-messages';
import "./dropdown";

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
     * Check if the email matches.
     * @param {Element} email
     * @param {Element} confirmEmail
     * @returns boolean
     */
    function isEmailMatch(email, confirmEmail) {
      const isMatch = email.value === confirmEmail.value;
      if (!isMatch) {
        removeErrorMessageElement(confirmEmail);

        const errorMessage = confirmEmail.closest("[data-cmp-required-message]").getAttribute("data-cmp-constraint-message");
        createErrorMessageElement(confirmEmail, errorMessage);
      }

      return isMatch;
    }

    /**
     * Handle the checks.
     * @param {DOM Element} input
     * @param {Event} event
     */
    function inputCheck(input, event) {
        input.addEventListener(event, () => {
            const value = input.value;
            const inputType = input.getAttribute("type");
            const errorMessage = input.closest("[data-cmp-required-message]").getAttribute("data-cmp-required-message");
            const emailConfirmForm = input.closest('#js-email-confirm-form');

            // Remove existing error messages
            removeErrorMessageElement(input);

            // Check if the field is empty
            if (isEmpty(value)) {
                createErrorMessageElement(input, errorMessage);
            } else if (inputType === "email" && !isValidEmail(value)) {
                createErrorMessageElement(input, "Please enter a valid email address");
            }
            else if (inputType === "checkbox" && !input.checked) {
                createErrorMessageElement(input, errorMessage);
            }


            if (emailConfirmForm && input.getAttribute('name') === 'email_confirm') {
              const emailInput = emailConfirmForm.querySelector('input[type="email"]');
              isEmailMatch(emailInput, input);
            }
        });
    }

    formInputs.forEach((input) => {
      inputCheck(input, 'focusout');
    });

  const emailConfirmForm = document.querySelector('#js-email-confirm-form');

  if (emailConfirmForm) {
    emailConfirmForm.addEventListener('submit', (event) => {
      const emailInput = emailConfirmForm.querySelector('input[type="email"]');
      const confirmEmail = emailConfirmForm.querySelectorAll('input[type="email"]')[1];

      if (!isEmailMatch(emailInput, confirmEmail)) {
        event.preventDefault();
      }
    });
  }
});
