import {
  validateInputs,
  validateRadios,
  validateRecaptcha,
  validateFileSize,
  validateFileExtension,
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
        ["text", "checkbox", "radio", "submit"].includes(type) ||
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

  //Function per upload file component
  function setupFileInput($fileInput, $filesContainer) {
    $fileInput.on("change", function () {
      const file = this.files[0];

      if (!file) return;

      const fileNameExt = file.name
        .substring(file.name.lastIndexOf(".") + 1)
        .toLowerCase();

      removeErrorMessage(".label_file_too_big");
      removeErrorMessage(".label_file_extension_not_allowed");

      validateFileSize(file);
      validateFileExtension(fileNameExt);

      $filesContainer.text(file.name).append('<i class="cmp-close__icon"></i>');
    });

    $filesContainer.on("click", ".cmp-close__icon", function () {
      $fileInput.val("");
      $filesContainer.text("");
      $(this).remove();
      removeErrorMessage(".label_file_extension_not_allowed");
    });
  }

  function setupFormSubmit($form) {
    $form.on("submit", function (event) {
      event.preventDefault();
      if (performAllValidations($form)) {
        this.dispatchEvent(new Event("submit", { bubbles: true }));
      }
    });
  }

  function performAllValidations($form) {
    return (
      //validateInputs($form) && validateRadios($form) && validateRecaptcha()
	  //validateInputs($form) && validateRadios($form)
	  //validateRadios($form)
	  validateInputs($form) 
    );
  }
});
