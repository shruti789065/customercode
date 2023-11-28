const ERRORS = {
  CLASS: "label_error",
  MESSAGE: {
    file_required: "Please, this field cannot be empty, upload your file",
    file_size: "File too big: Limit is 3MB",
    text_field: "Please, this field cannot be empty",
    file_extension: "File extension not allowed",
    select: "Please select at least one option",
    email: "Please enter a valid email address",
    email_format: "Email not valid",
    phone: "Please enter a phone number",
    phone_format: "Please enter a valid phone number format",
    radio: "Please, you must accept this option",
    reCAPTCHA: "Please fill in the reCAPTCHA field",
  },
};

function isValidMailFormat(email) {
  const re = /^\w+([-+.'][^\s]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
  return re.test(email);
}

function isValidPhoneNumber(phoneNumber) {
  const phoneRegex = /^(\+(?:[0-9] ?){6,14}[0-9]|(?:[0-9] ?){6,14}[0-9])$/;

  if (phoneRegex.test(phoneNumber)) {
    return true;
  } else {
    return false;
  }
}

function getAssociatedErrorMessage(element, errorMessage) {
  const _errorMessage =
    element.parentElement.dataset.cmpRequiredMessage || errorMessage;
  return _errorMessage;
}

function appendErrorMessage(el, errorMessage) {
  const $el = $(el);
  const parent = $el.parent();
  const existingError = parent.find("." + ERRORS.CLASS);

  if (!existingError.length || existingError.text() !== errorMessage) {
    existingError.remove(); // Rimuovi il messaggio di errore esistente se presente

    if ($el.is(":radio") || $el.is(":checkbox")) {
      // Se l'elemento è un radio button o una casella di controllo,
      // appendi l'errore al parent del parent
      parent.parent().append(`<p class='${ERRORS.CLASS}'>${errorMessage}</p>`);
    } else {
      parent.append(`<p class='${ERRORS.CLASS}'>${errorMessage}</p>`);
    }
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
  if ($(element).attr("type") === "tel") {
    validatePhoneField(element, value);
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
  const errorMessageMailFormat = ERRORS.MESSAGE.email_format;

  if (value.length > 0) {
    emailFormat
      ? removeErrorMessage(element)
      : handleValidationResult(element, emailFormat, errorMessageMailFormat);
  } else {
    handleValidationResult(element, emailFormat, errorMessageMail);
  }
}

function validatePhoneField(element, value) {
  const phoneFormat = isValidPhoneNumber(value);
  const errorMessagePhone = getAssociatedErrorMessage(
    element,
    ERRORS.MESSAGE.phone
  );
  const errorMessagePhoneFormat = ERRORS.MESSAGE.phone_format;
  if (value.length > 0) {
    phoneFormat
      ? removeErrorMessage(element)
      : handleValidationResult(element, phoneFormat, errorMessagePhoneFormat);
  } else {
    handleValidationResult(element, phoneFormat, errorMessagePhone);
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
  const $element = $(element);
  const parent = $element.parent();
  const errorElement = parent.find("." + errorClass);

  if (!isValid) {
    $element.css("border", "3px solid #a94442");
    appendErrorMessage(element, errorMessage);
  } else {
    if ($element.is(":radio") || $element.is(":checkbox")) {
      // Se l'elemento è un radio button
      // rimuovi l'errore dal parent del parent
      parent
        .parent()
        .find("." + errorClass)
        .remove();
    } else if (errorElement.length) {
      // Se l'elemento non è un radio button,
      // rimuovi l'errore dal parent
      errorElement.remove();
    }

    // Aggiungi un nuovo elemento vuoto per mantenere la struttura
    parent.append(`<p class='${errorClass} no_error_label'></p>`);
    $element.css("border", ""); // Ripristina il bordo al suo stato originale
  }
}

/**
 *
 * ? Inizio funzioni da esportazione
 */

export function validateInputs(form) {
  let inputsValid = true;

  form.find(":input:not(:hidden,:checkbox)").each(function () {
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
    ERRORS.MESSAGE.file_extension
  );
  const errorMessageFileRequired = getAssociatedErrorMessage(
    element,
    ERRORS.MESSAGE.file_required
  );
  const isRequired = isElementOrParentRequired(element);

  const myFilesInput = document.getElementById("myfile");
  const isMyFilesEmpty = myFilesInput && myFilesInput.value.trim() === "";

  if (isMyFilesEmpty && isRequired) {
    // Se il campo myFiles è vuoto e il campo è richiesto, mostra il messaggio di errore
    handleValidationResult(element, false, errorMessageFileRequired);
  } else if (file) {
    // Se un file è stato caricato, rimuovi il messaggio di errore per il file richiesto
    handleValidationResult(element, true, errorMessageFileRequired);

    // Esegui le opportune validazioni e carica il file
    validateFileSize(element, file, errorMessageFileSize);
    validateFileExtension(element, file, errorMessageFileExtension);
    fileContainer.text(file.name).append('<i class="cmp-close__icon"></i>');
  }
}

export function validateRadios(form) {
  let radioValid = true;

  form.find(":radio").each(function () {
    const $this = $(this);
    const value = $this.val().toLowerCase();

    if (["si", "yes", "1"].includes(value)) {
      if ($this.attr("required") && !$this.is(":checked")) {
        radioValid = false;
        handleValidationResult(
          $this,
          false,
          getAssociatedErrorMessage(this, ERRORS.MESSAGE.radio)
        );
      } else {
        handleValidationResult($this, true, "");
      }
    }

    /* if (value === "no" && $this.is(":checked")) {
      radioValid = false;
      handleValidationResult(
        $this,
        false,
        $this.closest("fieldset").data("cmp-required-message")
      );
    } */
  });

  return radioValid;
}

export function validateRecaptcha() {
  const recaptchaElement = document.getElementById("g-recaptcha-response");
  const tokenRecaptcha = recaptchaElement.value.trim();
  if (!tokenRecaptcha) {
    appendErrorMessage(recaptchaElement, ERRORS.MESSAGE.reCAPTCHA);
    return false;
  } else {
    removeErrorMessage(recaptchaElement);
    return true;
  }
}
