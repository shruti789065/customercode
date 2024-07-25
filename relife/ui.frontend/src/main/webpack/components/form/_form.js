import {
  validateInputs,
  validateRadios,
  validateRecaptcha,
  validateFile,
} from "./_form_validation_method";

import $ from "jquery";

import { showOverlayAndLoader } from "../../site/_util";

$(function () {
  const $form = $("#new_form");
  const $fileInput = $("#myfile");
  const $filesContainer = $("#myFiles");

  assignTabIndexes($form);
  setupFileInput($fileInput, $filesContainer);
  setupFormSubmit($form);
  setupInputFocusHandler();
  setupDropdownHandler();
  setupCheckboxHandler();

  function assignTabIndexes($form) {
    let tabIndex = 1;

    $form.find(":input").each(function () {
      const { type, name, dataset } = this;
      const cmpHookFormText = dataset.cmpHookFormText;

      if (
        ["text", "checkbox", "radio", "submit", "tel"].includes(type) ||
        cmpHookFormText === "input" ||
        name === "info"
      ) {
        if (type !== "file") {
          $(this).prop("tabindex", tabIndex++);
        }
      }
    });

    $form.find("[required]").each(function () {
      if (this.type && (this.type === "radio" || this.type === "file")) {
        $(this).closest("label").attr("for", this.id);
      }
      if (this.id !== "") {
        $(`label[for=${this.id}]`).addClass("required-field");
      }
    });

    $form.find(":submit").attr("tabindex", tabIndex);
  }

  function setupFileInput($fileInput, $filesContainer) {
    $fileInput.on("change", function () {
      const file = this.files[0];
      if (file) {
        validateFile(this, file, $filesContainer);
      }
    });

    $filesContainer.on("click", ".cmp-close__icon", function (event) {
      event.stopPropagation();
      $fileInput.val("");
      $filesContainer.text("");
      $(this).remove();
    });
  }

  function setupFormSubmit($form) {
    $form.on("submit", function (event) {
      if (!performAllValidations($form)) {
        event.preventDefault();
      } else {
        showOverlayAndLoader($form, true);
      }
    });
  }

  function performAllValidations($form) {
    return (
      validateRadios($form) && validateInputs($form) && validateRecaptcha()
    );
  }

  function setupInputFocusHandler() {
    const $inputElements = $('.cmp-form-text__text');
    
    $inputElements.each(function () {
      const $inputElement = $(this);
      const $parentElement = $inputElement.closest('.cmp-form-text');
      
      $inputElement.on('focus', function() {
        $parentElement.addClass('textAdded');
      });

      $inputElement.on('blur', function() {
        if ($inputElement.val().trim() === '') {
          $parentElement.removeClass('textAdded');
        }
      });
    });
  }

  function setupDropdownHandler() {
    const $dropdownElements = $('.cmp-form-options__field--drop-down');
    
    $dropdownElements.each(function () {
      const $dropdownElement = $(this);
      const $parentElement = $dropdownElement.closest('.cmp-form-options');
      
      $dropdownElement.on('click', function() {
        $parentElement.toggleClass('dropdownOpen');
      });

      $dropdownElement.on('change', function() {
        $parentElement.removeClass('dropdownOpen');
      });

      $(document).on('click', function(event) {
        if (!$dropdownElement.is(event.target) && !$dropdownElement.has(event.target).length) {
          $parentElement.removeClass('dropdownOpen');
        }
      });
    });
  }

  function setupCheckboxHandler() {
    const $checkboxElements = $('.cmp-form-options__field--checkbox');
    
    $checkboxElements.each(function () {
      const $checkboxElement = $(this);
      const $parentElement = $checkboxElement.closest('.cmp-form-options');
      
      $checkboxElement.on('change', function() {
        if ($checkboxElement.is(':checked')) {
          $parentElement.addClass('checkboxTrue');
        } else {
          $parentElement.removeClass('checkboxTrue');
        }
      });
    });
  }
});
