export function validateEmail(email) {
	const re = /^\w+([-+.'][^\s]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	return re.test(email);
}

export function appendErrorMessage(el, errorClass, errorMessage) {
	$(el).parent().append(`<p class='label-error ${errorClass}'>${errorMessage}</p>`);
}

export function removeErrorMessage(el, errorClass) {
	if ($(el).parent().find('.' + errorClass).length) {
		$(el).parent().find('.' + errorClass).remove();
	}
}

export function validateRecaptcha() {
  const recaptchaElement = document.getElementById("g-recaptcha-response");
  const tokenRecaptcha = recaptchaElement.value.trim();
  if (!tokenRecaptcha) {
    _formUtils._appendErrorMessage(
      recaptchaElement,
      "Please fill in the reCAPTCHA field"
    );
    return false;
  }
  return true;
}
