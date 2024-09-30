import $ from "jquery";

import {
  validateInputs,
  validateRadios,
  validateRecaptcha,
  validateFile,
} from "./_form_validation_method";

import { showOverlayAndLoader } from "../../site/_util";

$(function () {
  const $form = $("#new_form"); //CONTROLLARE CHE IL CONTAINER DEL FORM NON ABBIA UN ID DIVERSO
  const $fileInput = $("#myfile");
  const $filesContainer = $("#myFiles");

  assignTabIndexes($form);
  setupFileInput($fileInput, $filesContainer);
  setupFormSubmit($form);

  function assignTabIndexes($form) {
    let tabIndex = 1;

    $form.find(":input").each(function () {
      const type = this.type;
      const name = this.name;
      const dataset = this.dataset;
      const cmpHookFormText = dataset.cmpHookFormText;

      if (
        ["text", "checkbox", "radio", "submit", "tel"].includes(type) ||
        cmpHookFormText === "input" ||
        name === "info"
      ) {
        if (type !== "file" && type !== "hidden") {
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
        $filesContainer.html(file.name + '<i class="cmp-close__icon"></i>');
      } else {
        $filesContainer.html("No file selected");
      }
      updateFileSpan();
    });

    $filesContainer.on("click", ".cmp-close__icon", function (event) {
      event.stopPropagation();
      $fileInput.val("");
      $filesContainer.text("No file selected");
      updateFileSpan();
    });
  }

  function setupFormSubmit($form) {
    $form.on("submit", function (event) {
      if (!performAllValidations($form)) {
        event.preventDefault();
      } else {
        $(this).find(":submit").prop("disabled", true); // Disabilita il submit
        showOverlayAndLoader($form, true);
      }
    });
  }

  function performAllValidations($form) {
    // Eseguiamo tutte le validazioni indipendentemente l'una dall'altra
    const isInputsValid = validateInputs($form);
    const isRadiosValid = validateRadios($form);
    const isRecaptchaValid = validateRecaptcha();

    // Torniamo true solo se tutte le validazioni sono valide
    return isInputsValid && isRadiosValid && isRecaptchaValid;
  }

  function updateFileSpan() {
    const $filesContainer = $("#myFiles");
    if ($filesContainer.find(".cmp-close__icon").length > 0) {
      $filesContainer.removeClass("empty").addClass("has-icon");
    } else {
      $filesContainer.removeClass("has-icon").addClass("empty");
      if ($filesContainer.text().trim() === "") {
        $filesContainer.text("No file selected...");
      }
    }
  }

  // Chiamare updateFileSpan all'onload
  updateFileSpan();
});
