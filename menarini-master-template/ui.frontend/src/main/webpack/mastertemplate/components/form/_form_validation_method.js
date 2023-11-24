const ERRORS = {
  CLASS: "label-error label_required",
  MESSAGE: {
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
  $(el).parent().append(`<p class='${ERRORS.CLASS}'>${errorMessage}</p>`);
}

function isElementOrParentRequired(element) {
  return (
    $(element).prop("required") || $(element).parents("[required]").length > 0
  );
}

function validateFile(element) {
  const inputFile = $(element);
  const file = inputFile[0].files[0];
  const errorMessageFile = getAssociatedErrorMessage(
    element,
    ERRORS.MESSAGE.file_size
  );

  if (file && file.size < 3 * 1024 * 1024) {
    handleValidationResult(element, true);
  } else {
    handleValidationResult(element, false, errorMessageFile);
  }

  if (file) {
    const fileNameExt = file.name
      .substring(file.name.lastIndexOf(".") + 1)
      .toLowerCase();
    validateFileExtension(element, fileNameExt);
  }
}
/**
 *
 * TODO Controllare come mai inserisce la label di errore su tutti i campi
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

function handleValidationResult(element, isValid, errorMessage) {
  if (!isValid) {
    $(element).css("border", "3px solid #a94442");
    if (
      !$(element)
        .parent()
        .find("." + ERRORS.CLASS).length
    ) {
      $(element)
        .parent()
        .append(`<p class='${ERRORS.CLASS}'>${errorMessage}</p>`);
    }
  } else {
    removeErrorMessage(element);
  }
}

/**
 *
 * ? Inizio funzioni da esportazione
 */
export function validateFileExtension(element, fileNameExt) {
  const fileExtensionsAllowed = ["pdf", "doc", "docx"];
  const errorMessageFileExtension = getAssociatedErrorMessage(
    element,
    ERRORS.MESSAGE.file_extension
  );
  if (!fileExtensionsAllowed.includes(fileNameExt)) {
    appendErrorMessage(element, errorMessageFileExtension);
  }
}

export function validateFileSize(element, file) {
  const errorMessageFileSize = getAssociatedErrorMessage(
    element,
    ERRORS.MESSAGE.file_size
  );
  if (file.size > 3 * 1024 * 1024) {
    appendErrorMessage(element, errorMessageFileSize);
  }
}

export function removeErrorMessage(el) {
  const parent = $(el).parent();
  const errorElement = parent.find("." + ERRORS.CLASS);

  if (errorElement.length) {
    errorElement.remove();
    $(el).css("border", "3px solid #000");
  }
}

export function validateInputs(form) {
  let inputsValid = true;

  form.find(":input:not(:hidden,:checkbox)").each(function () {
    if ($(this).is("select")) {
      inputsValid = validateSelect(this) && inputsValid;
    } else if ($(this).attr("type") === "file") {
      inputsValid = validateFile(this) && inputsValid;
    } else {
      inputsValid = validateText(this) && inputsValid;
    }
  });

  return inputsValid;
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
