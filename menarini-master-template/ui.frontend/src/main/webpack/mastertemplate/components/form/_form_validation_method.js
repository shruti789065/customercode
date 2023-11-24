const ERRORS = {
  CLASS: "label_error",
  MESSAGE: {
    file_required: "Please, this field cannot be empty, upload your",
    file_size: "File too big: Limit is 3MB",
    text_field: "Please, this field cannot be empty",
    file_extension: "File extension not allowed",
    select: "Please select at least one option",
    email: "Please enter a valid email address",
    reCAPTCHA: "Please fill in the reCAPTCHA field",
  },
};

function isValidMailFormat(email) {
  const re = /^\w+([-+.'][^\s]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
  return re.test(email);
}

function getAssociatedErrorMessage(element, errorMessage) {
  const _errorMessage =
    element.parentElement.dataset.cmpRequiredMessage || errorMessage;
  return _errorMessage;
}

function appendErrorMessage(el, errorMessage) {
  const parent = $(el).parent();
  const existingError = parent.find("." + ERRORS.CLASS);

  if (!existingError.length) {
    parent.append(`<p class='${ERRORS.CLASS}'>${errorMessage}</p>`);
  }
}

function isElementOrParentRequired(element) {
  return (
    $(element).prop("required") || $(element).parents("[required]").length > 0
  );
}

function removeErrorMessage(el) {
  const parent = $(el).parent();
  const errorElement = parent.find("." + ERRORS.CLASS);

  if (errorElement.length) {
    errorElement.remove();
    $(el).css("border", "3px solid #000");
  }
}

/**
 *
 * TODO
 *
 */

function validateText(element) {
  const value = element.value;
  const errorMessageText = getAssociatedErrorMessage(
    element,
    ERRORS.MESSAGE.text_field
  );

  const isRequired = isElementOrParentRequired(element);

  if (value.length === 0 && isRequired) {
    handleValidationResult(element, false, errorMessageText);
  } else {
    handleValidationResult(element, true);
  }

  if ($(element).attr("type") === "email") {
    validateEmailField(element, value);
  }
}

function validateSelect(element) {
  const valueSelected = element.options.selectedIndex > 0;
  const errorMessageSelect = getAssociatedErrorMessage(
    element,
    ERRORS.MESSAGE.select
  );

  const isRequired = isElementOrParentRequired(element);

  handleValidationResult(
    element,
    valueSelected || !isRequired,
    errorMessageSelect
  );
}

function validateEmailField(element, value) {
  const emailFormat = isValidMailFormat(value);
  const errorMessageMail = getAssociatedErrorMessage(
    element,
    ERRORS.MESSAGE.email
  );
  if (value.length > 0) {
    handleValidationResult(element, emailFormat, errorMessageMail);
  } else {
    removeErrorMessage(element);
  }
}

function validateFileExtension(element, file, errorMessage) {
  const fileExtensionsAllowed = ["pdf", "doc", "docx"];
  const fileNameExt = file.name
    .substring(file.name.lastIndexOf(".") + 1)
    .toLowerCase();
  if (!fileExtensionsAllowed.includes(fileNameExt)) {
    appendErrorMessage(element, errorMessage);
  }
}

function validateFileSize(element, file, errorMessage) {
  if (file.size > 3 * 1024 * 1024) {
    appendErrorMessage(element, errorMessage);
  }
}

function handleValidationResult(element, isValid, errorMessage) {
  const errorClass = ERRORS.CLASS;

  if (!isValid) {
    $(element).css("border", "3px solid #a94442");
    appendErrorMessage(element, errorMessage);
  } else {
    const parent = $(element).parent();
    const errorElement = parent.find("." + errorClass);

    if (errorElement.length) {
      errorElement.remove(); // Rimuovi il messaggio di errore se esiste
    }

    // Aggiungi un nuovo elemento vuoto per mantenere la struttura
    parent.append(`<p class='${errorClass}'></p>`);
    $(element).css("border", ""); // Ripristina il bordo al suo stato originale
  }
}

/**
 *
 * ? Inizio funzioni da esportazione
 */

export function validateInputs(form) {
  let inputsValid = true;

  form.find(":input:not(:hidden,:checkbox)").each(function () {
    console.log("$(this) ", $(this));
    console.log("this ", this);
    if ($(this).is("select")) {
      inputsValid = validateSelect(this) && inputsValid;
    } else if ($(this).attr("type") == "file") {
      inputsValid = validateFile(this) && inputsValid;
    } else {
      inputsValid = validateText(this) && inputsValid;
    }
  });

  return inputsValid;
}

export function validateFile(element, file, fileContainer) {
  const errorMessageFileSize = getAssociatedErrorMessage(
    element,
    ERRORS.MESSAGE.file_size
  );
  const errorMessageFileExtension = getAssociatedErrorMessage(
    element,
    ERRORS.MESSAGE.file_size
  );
  const errorMessageFileRequired = getAssociatedErrorMessage(
    element,
    ERRORS.MESSAGE.file_required
  );
  const isRequired = isElementOrParentRequired(element);

  if (file) {
    validateFileSize(element, file, errorMessageFileSize);
    validateFileExtension(element, file, errorMessageFileExtension);
    fileContainer.text(file.name).append('<i class="cmp-close__icon"></i>');
  }

  if (isRequired) {
    handleValidationResult(element, !isRequired, errorMessageFileRequired);
  }
}

export function validateRadios(form) {
  let radiosValid = true;
  let isRequired = 0;

  form.find(":radio").each(function () {
    const $this = $(this);
    const value = $this.val().toLowerCase();

    if (["si", "yes", "1"].includes(value)) {
      if ($this.attr("required")) {
        isRequired = 1;
        if ($this.is(":checked")) {
          removeErrorMessage($this);
        }
      } else {
        isRequired = 0;
      }
    }

    if (value === "no" && $this.is(":checked")) {
      if (isRequired === 1) {
        radiosValid = false;
        appendErrorMessage(
          $this,
          "label_required",
          $this.closest("fieldset").data("cmp-required-message")
        );
      } else {
        removeErrorMessage($this);
      }
    }
  });
  return radiosValid;
}

export function validateRecaptcha() {
  const recaptchaElement = document.getElementById("g-recaptcha-response");
  const tokenRecaptcha = recaptchaElement.value.trim();
  if (!tokenRecaptcha) {
    appendErrorMessage(recaptchaElement, ERRORS.MESSAGE.reCAPTCHA);
    return false;
  }
  return true;
}
