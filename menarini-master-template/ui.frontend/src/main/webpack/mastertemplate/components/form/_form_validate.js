

import $ from "jquery";

$(() => {
	// assign tabindex to user-selectable form elements
	let tabIndex = 1;
	const $form = document.querySelector('form');
	const $submitBtn = $('#new_form').closest('form').find(':submit');
	const $fileInput = $('#myfile');
	const $filesContainer = $('#myFiles');
	const fileExtensionsAllowed = ['pdf', 'doc', 'docx'];
	const $fileInputParent = $fileInput.parent();

	if($form !== null){
	    $('#new_form *').each((i, el) => {
			const { type, name, dataset: { cmpHookFormText } } = el;
			if (type === 'text' || type === 'checkbox' || cmpHookFormText === 'input' ||
				name === 'info' || type === 'radio' || type === 'submit') {
				if (type !== 'file') {
					$(el).prop('tabindex', tabIndex++);
				}
			}
		});

		$form.querySelectorAll("[required]").forEach((item) => {
			if (item.type === 'radio') {
				item.closest('label').setAttribute('for', item.id);
			}
			if (item.id !== '') {
				document.querySelector('label[for=' + item.id + ']').classList.add('required-field');
			}
		});

		$submitBtn.attr('tabindex', tabIndex);

		$fileInput.on('change', function () {
			const file = this.files[0];
			// Verifica che l'utente abbia selezionato un file
			if (!file) {
				return;
			}
			const fileNameExt = file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase();

			// Verifica che la dimensione del file sia inferiore a 3MB
			if (file.size > 3 * 1024 * 1024) {
				$fileInputParent.find('.label_file_too_big').remove();
				$fileInputParent.append('<p class="label-error label_file_too_big">File too big: Limit is 3MB</p>');
			} else {
				$fileInputParent.find('.label_file_too_big').remove();
			}
			// Verifica che l'estensione del file sia consentita
			if (!fileExtensionsAllowed.includes(fileNameExt)) {
				$fileInputParent.find('.label_file_extension_not_allowed').remove();
				$fileInputParent.append('<p class="label-error label_file_extension_not_allowed">File extension not allowed</p>');
			} else {
				$fileInputParent.find('.label_file_extension_not_allowed').remove();
			}
			$filesContainer.text(file.name);

			$filesContainer.append('<i class="cmp-close__icon"></i>');


		});

		$filesContainer.on('click', '.cmp-close__icon', function () {
			// Rimuovi il file selezionato
			$fileInput.val('');
			// Rimuovi il testo del nome del file
			$filesContainer.text('');
			// Rimuovi l'icona X
			$(this).remove();

			if(document.querySelector('.cmp-form-text .label_file_extension_not_allowed') !== null){
				document.querySelector('.cmp-form-text .label_file_extension_not_allowed').remove();
			}
		});


		$form.addEventListener("submit", function (event) {
			event.preventDefault();
			if (allValidation()) {
				$form.submit();
			}
		});
	}
});

function allValidation() {
	// Validazione dei campi di input
	let inputsAreValid = validateInputs();

	// Validazione delle opzioni radio
	let radiosAreValid = validateRadios();

	// Validazione della verifica reCAPTCHA
	let recaptchaIsValid = validateRecaptcha();

	// Restituzione del risultato della validazione
	return inputsAreValid && radiosAreValid && recaptchaIsValid;
}

// return true if recaptcha is validated, false otherwise
function validateRecaptcha() {
	const recaptchaElement = document.getElementById('g-recaptcha-response');
	const tokenRecaptcha = recaptchaElement.value.trim();
	if (!tokenRecaptcha) {
		_appendErrorMessage(recaptchaElement, 'Please fill in the reCAPTCHA field');
		return false;
	}
	return true;
}

//restituisce true se input validati, false altrimenti
function validateInputs() {
	var inputsValid = true;
	$('#new_form *').filter(':input').each(function () {
		//se campo non hidden e non è checkbox vengono eseguiti i controlli
		if ($(this).attr('type') != "hidden" && $(this).attr('type') != "checkbox") {
			/*controllo che sia presente data-cmp-required-message, quindi obbligatorio; 
			se sì vengono eseguiti i controlli*/
			var attr_to_check = $(this).parent().attr('data-cmp-required-message');
			if (typeof attr_to_check !== 'undefined' && attr_to_check !== false) {
				//se è select
				if ($(this).is("select")) {
					var idSelect = $(this).attr("id");
					var valueSelected = $('#' + idSelect).find(":selected").attr("value");
					if (valueSelected === undefined) {
						$(this).css("border", "3px solid #a94442");
						if ($(this).parent().find('.label_required').length == 0) {
							$(this).parent().append("<p class='label-error label_required'>This field is required</p>");
						}
						inputsValid = false;
					}
					else {
						$(this).css("border", "3px solid #000");
						if ($(this).parent().find('.label_required').length) {
							$(this).parent().find('.label_required').remove();
						}
					}
				}
				//se non è select
				else {
					//se non è select
					//se è file
					if ($(this).attr('type') == "file") {
						//se non ha contenuto
						if (($(this).val().length == 0)) {
							//se non è file si può dare il border
							if ($(this).parent().find('.label_required').length == 0) {
								$(this).parent().append("<p class='label-error label_required'>This field is required</p>");
							}
							inputsValid = false;
						}
						//se ha contenuto viene fatta la validazione del file
						else {
							$(this).css("border", "3px solid #000");
							if ($(this).parent().find('.label_required').length) {
								$(this).parent().find('.label_required').remove();
							}
							let inputFile = $('#myfile');
							let filesContainer = $('#myFiles');

							//inputFile.change(function () {
							let file = inputFile[0].files[0];
							var fileNameExt = file.name.substring(file.name.indexOf('.') + 1);
							var fileExtensionsAllowed = ['pdf', 'doc', 'docx'];
							// Limite: 3 MB (this size is in bytes)
							//se grandezza file ok
							filesContainer.text(file.name);
							if (file && (file.size < 3 * 1048576)) {
								if ($(this).parent().find('.label_file_too_big').length) {
									$(this).parent().find('.label_file_too_big').remove();
								}
								//se grandezza file nok
							} else {
								if ($(this).parent().find('.label_file_too_big').length) {
									$(this).parent().find('.label_file_too_big').remove();
								}
								if ($(this).parent().find('.label_file_too_big').length == 0) {
									$(this).parent().append("<p class='label-error label_file_too_big'>File too big: Limit is 3MB</p>");
								}
								inputsValid = false;
							}
							//controllo estensione
							//se estensione ok
							if ($.inArray(fileNameExt, fileExtensionsAllowed) !== -1) {
								if ($(this).parent().find('.label_file_extension_not_allowed').length) {
									$(this).parent().find('.label_file_extension_not_allowed').remove();
								}
								//se estensione nok
							} else {
								if ($(this).parent().find('.label_file_extension_not_allowed').length) {
									$(this).parent().find('.label_file_extension_not_allowed').remove();
								}
								if ($(this).parent().find('.label_file_extension_not_allowed').length == 0) {
									$(this).parent().append("<p class='label-error label_file_extension_not_allowed'>File extension not allowed</p>");
								}
								inputsValid = false;
							}
						}
					}
					//se non è file
					else {
						//se non ha contenuto
						if (($(this).val().length == 0)) {
							$(this).css("border", "3px solid #a94442");
							if ($(this).parent().find('.label_required').length == 0) {
								$(this).parent().append("<p class='label-error label_required'>This field is required</p>");
							}
							inputsValid = false;
						}
						//se ha contenuto
						else {
							$(this).css("border", "3px solid #000");
							if ($(this).parent().find('.label_required').length) {
								$(this).parent().find('.label_required').remove();
							}
						}
					}
				}

				//validazione email
				if ($(this).attr('type') == "email") {
					var email = $(this).val();
					var emailFormat = validateEmail(email);
					//se email valorizzata
					if (email.length > 0) {
						//email valida
						if (emailFormat == true) {
							$(this).css("border", "3px solid #000");
							if ($(this).parent().find('.label_email_not_valid').length) {
								$(this).parent().find('.label_email_not_valid').remove();
								$(this).css("border", "3px solid #000");
							}
						}//fine email valida
						//email non valida e non esiste messaggio di errore e mail ha almeno un carattere;
						//se esiste anche la label "required" la toglie
						else {
							$(this).css("border", "3px solid #a94442");
							if ($(this).parent().find('.label_email_not_valid').length == 0 & $(this).parent().find('.label_required').length == 0) {
								$(this).parent()
									.append("<p class='label-error label_email_not_valid'>Please enter a valid email address</p>");
								if ($(this).parent().find('.label_required').length) {
									$(this).parent().find('.label_required').remove();
								}
							}
							inputsValid = false;
						}//fine email non valida
					}//fine email valorizzata
					//se email non valorizzata e esiste la label di mail non valida, viene rimossa
					else {
						if ($(this).parent().find('.label_email_not_valid').length) {
							$(this).parent().find('.label_email_not_valid').remove();
						}
					}
				}//fine validazione email
			} //fine if campo obbligatorio
		}//fine if controllo che campo non sia hidden
	});//fine loop sugli input
	return inputsValid;
}
//restituisce true se radio validati, false altrimenti
function validateRadios() {
	const $radios = $('#new_form *').filter(':radio');
	let radiosValid = true;
	let isRequired = 0;
	$radios.each(function () {
		switch ($(this).val()) {
			case 'si':
			case 'yes':
			case '1':
				if ($(this).data('required')) {
					isRequired = 1;
					if ($(this).is(':checked')) {
						$(this).siblings('.label_required').remove();
					}
				} else {
					isRequired = 0;
				}
				break;
			case 'no':
			case '0':
			case 'false':
				if ($(this).is(':checked')) {
					if (isRequired == 1) {
						radiosValid = false;
						if ($(this).siblings('.label_required').length == 0) {
							$(this).after('<p class="label-error label_required">This field is required</p>');
						}
					} else {
						$(this).siblings('.label_required').remove();
					}
				}
				break;
		}
	});
	return radiosValid;
}

//restituisce true se formato email valido, false altrimenti
function validateEmail(email) {
	var re = /^\w+([-+.'][^\s]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	var emailFormat = re.test(email);
	return emailFormat;
}

function _appendErrorMessage(el, str) {
	var span = document.createElement('span');
	span.innerHTML = str;
	span.classList.add('text-danger');
	span.setAttribute("role", "alert");
	el.append(span);
}

