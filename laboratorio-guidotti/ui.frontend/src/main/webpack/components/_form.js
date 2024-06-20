import {
  validateInputs,
  validateRadios,
  validateRecaptcha,
  validateFile,
} from "./_form_validation_method";

import { showOverlayAndLoader } from "../site/_util";

$(function () {
  const $form = $("#new_form");
  const $fileInput = $("#myfile");
  const $filesContainer = $("#myFiles");

  assignTabIndexes($form);
  setupFileInput($fileInput, $filesContainer);
  setupFormSubmit($form);

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
        showOverlayAndLoader($form,true);
      }
    });
  }

  function performAllValidations($form) {
    return (
      validateRadios($form) && validateInputs($form) && validateRecaptcha()
    );
  }
});
