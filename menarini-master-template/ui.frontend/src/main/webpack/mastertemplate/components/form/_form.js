import {
  validateInputs,
  validateRadios,
  validateRecaptcha,
  validateFile,
} from "./_form_validation_method";

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
      if (this.type === "radio" || this.type === "file") {
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
      if (!file) {
        return;
      }
      validateFile(this, file, $filesContainer);
    });

    $filesContainer.on("click", ".cmp-close__icon", function () {
      $fileInput.val("");
      $filesContainer.text("");
      $(this).remove();
    });
  }

  function setupFormSubmit($form) {
    $form.on("submit", function (event) {
      event.preventDefault();
      if (performAllValidations($form)) {
        $form.trigger("submit");
      }
    });
  }

  function performAllValidations($form) {
    let validInput = validateInputs($form);
    let validRadio = validateRadios($form);
    let validCaptcha = validateRecaptcha();

    if (validRadio && validInput && validCaptcha) {
      return true;
    }
  }
});
