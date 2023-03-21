

import $ from "jquery";

$(() => {
	// assign tabindex to user-selectable form elements
	let tabIndex = 1;
	const $form = document.querySelector('form');
	const $submitBtn = $('#new_form').closest('form').find(':submit');
	const $fileInput = $('#myfile');
	const $filesContainer = $('#myFiles');



	$('#new_form *').each((i, el) => {
		const { type, name, dataset: { cmpHookFormText } } = el;
		if (type === 'text' || type === 'checkbox' || cmpHookFormText === 'input' || name === 'info' || type === 'radio' || type === 'submit') {
			if (type !== 'file') {
				$(el).attr('tabindex', tabIndex++);
			}
		}
	});

	// assign tabindex to submit button after loop
	//$form.attr('novalidate', 'novalidate');
	$submitBtn.attr('tabindex', tabIndex);

	// introduce file validation event

	$fileInput.change(() => {
		const file = $fileInput[0].files[0];
		const fileNameExt = file.name.substring(file.name.indexOf('.') + 1);
		const fileExtensionsAllowed = ['pdf', 'doc', 'docx'];
		// Limit: 3 MB (this size is in bytes)
		$filesContainer.text(file.name);
		if (file && file.size < 3 * 1048576) {
			$fileInput.parent().find('.label_file_too_big').remove();
		} else {
			$fileInput.parent().find('.label_file_too_big').remove();
			$fileInput.parent().append('<p class="label_file_too_big">File too big: Limit is 3MB</p>');
		}
		// check extension
		if (fileExtensionsAllowed.includes(fileNameExt)) {
			$fileInput.parent().find('.label_file_extension_not_allowed').remove();
		} else {
			$fileInput.parent().find('.label_file_extension_not_allowed').remove();
			$fileInput.parent().append('<p class="label_file_extension_not_allowed">File extension not allowed</p>');
		}
	});
	
	$form.addEventListener("submit", function(event) {
		event.preventDefault();
		if (allValidation()) {
			$form.submit();
		}
	  });

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
		alert('Please fill in the reCAPTCHA field');
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
							$(this).parent().append("<p class='label_required'>This field is required</p>");
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
								$(this).parent().append("<p class='label_required'>This field is required</p>");
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
									$(this).parent().append("<p class='label_file_too_big'>File too big: Limit is 3MB</p>");
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
									$(this).parent().append("<p class='label_file_extension_not_allowed'>File extension not allowed</p>");
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
								$(this).parent().append("<p class='label_required'>This field is required</p>");
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
									.append("<p class='label_email_not_valid'>Please enter a valid email address</p>");
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
	var radiosValid = true;
	var isRequired = 0;
	$('#new_form *').filter(':radio').each(function () {
		//il messaggio di errore viene se c'è un checked sul no e il campo è required; si presuppone
		//che il sì sia sempre primo, allora valorizzo una variabile isRequired
		//siamo sul valore si
		if ($(this).attr('value') == "si" | $(this).attr('value') == "yes" | $(this).attr('value') == "1" | $(this).attr('value') == "si") {
			//valore si e obbligatorio
			if ($(this).attr('required')) {
				isRequired = 1;
				//valore si, obbligatorio e checked
				if ($(this).is(':checked')) {
					if ($(this).parent().siblings('.label_required').length) {
						$(this).parent().siblings('.label_required').remove();
					}
				}
				//valore si, obbligatorio e non checked
				else {
				}
			}
			//valore no, non obbligatorio
			else {
				isRequired = 0;
			}
		}
		//siamo sul valore no
		if ($(this).attr('value') == "no" | $(this).attr('value') == "0" | $(this).attr('value') == "false") {
			//valore no e checked
			if ($(this).is(':checked')) {
				//obbligatorio e checked no
				if (isRequired == 1) {
					radiosValid = false;
					if ($(this).parent().siblings('.label_required').length == 0) {
						$(this).parent().after('<p class="label_required">This field is required</p>');
					}
				}
				//non obbligatorio e checked no
				else {
					if ($(this).parent().siblings('.label_required').length) {
						$(this).parent().siblings('.label_required').remove();
					}
				}
			}
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
