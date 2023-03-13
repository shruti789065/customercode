

import jQuery from "jquery";

//caricamento pagina

jQuery(function () {
	// assegnazione tabindex agli elementi selezionabili dall'utente del form
	var tabindex = 1;
	jQuery('#new_form *').each(function () {
		var data_cmp_hook_form_text = jQuery(this).attr('data-cmp-hook-form-text');
		if (this.type == "text" || this.type == "checkbox" || data_cmp_hook_form_text == "input" || this.name == "info" || this.type == "radio" || this.type == "SUBMIT") {
			var input = jQuery(this);
			//al file non diamo il tabindex
			if (this.type != "file") {
				input.attr("tabindex", tabindex);
				tabindex++;
			}
		}
	});
	// assegnazione tabindex al submit dopo il ciclo
	var currentSubmit = jQuery('#new_form').closest('form').find(':submit');
	currentSubmit.attr("tabindex", tabindex);

	//introduzione evento validazione file
	let inputFile = $('#myfile');
	let filesContainer = $('#myFiles');

	inputFile.change(function () {
		let file = inputFile[0].files[0];
		var fileNameExt = file.name.substring(file.name.indexOf('.') + 1);
		var fileExtensionsAllowed = ['pdf', 'doc', 'docx'];
		// Limite: 3 MB (this size is in bytes)
		filesContainer.text(file.name);
		if (file && (file.size < 3 * 1048576)) {
			if (jQuery(this).parent().find('.label_file_too_big').length) {
				jQuery(this).parent().find('.label_file_too_big').remove();
			}
		} else {
			if (jQuery(this).parent().find('.label_file_too_big').length) {
				jQuery(this).parent().find('.label_file_too_big').remove();
			}
			if (jQuery(this).parent().find('.label_file_too_big').length == 0) {
				jQuery(this).parent().append("<p class='label_file_too_big'>File too big: Limit is 3MB</p>");
			}
		}
		//controllo estensione
		if (jQuery.inArray(fileNameExt, fileExtensionsAllowed) !== -1) {
			if (jQuery(this).parent().find('.label_file_extension_not_allowed').length) {
				jQuery(this).parent().find('.label_file_extension_not_allowed').remove();
			}
		} else {
			if (jQuery(this).parent().find('.label_file_extension_not_allowed').length) {
				jQuery(this).parent().find('.label_file_extension_not_allowed').remove();
			}
			if (jQuery(this).parent().find('.label_file_extension_not_allowed').length == 0) {
				jQuery(this).parent().append("<p class='label_file_extension_not_allowed'>File extension not allowed</p>");
			}
		}

	});
});

jQuery("button[type='submit']").on("click keypress", function () {
	$('#new_form').on('submit', function (e) {
		//controllo valori input
		var inputsValid = validateInputs();
		//controllo valori radio button
		var radiosValid = validateRadios();
		//controllo valori recaptcha
		//var recaptchaValid=validateRecaptcha();
		var valid = inputsValid && radiosValid;
		//var valid=inputsValid && radiosValid && recaptchaValid;
		if (!valid) {
			e.preventDefault();
		}
	});
});
//restituisce true se recaptcha validato, false altrimenti
function validateRecaptcha() {
	var tokenRecaptcha = document.getElementById('g-recaptcha-response').value;
	if (tokenRecaptcha === null || tokenRecaptcha === "") {
		alert('Please, fill the recaptcha');
		return false;
	}
	return true;
}
//restituisce true se input validati, false altrimenti
function validateInputs() {
	var inputsValid = true;
	jQuery('#new_form *').filter(':input').each(function () {
		//se campo non hidden e non è checkbox vengono eseguiti i controlli
		if (jQuery(this).attr('type') != "hidden" && jQuery(this).attr('type') != "checkbox") {
			/*controllo che sia presente data-cmp-required-message, quindi obbligatorio; 
			se sì vengono eseguiti i controlli*/
			var attr_to_check = jQuery(this).parent().attr('data-cmp-required-message');
			if (typeof attr_to_check !== 'undefined' && attr_to_check !== false) {
				//se è select
				if (jQuery(this).is("select")) {
					var idSelect = jQuery(this).attr("id");
					var valueSelected = jQuery('#' + idSelect).find(":selected").attr("value");
					if (valueSelected === undefined) {
						jQuery(this).css("border", "3px solid #a94442");
						if (jQuery(this).parent().find('.label_required').length == 0) {
							jQuery(this).parent().append("<p class='label_required'>This field is required</p>");
						}
						inputsValid = false;
					}
					else {
						jQuery(this).css("border", "3px solid #000");
						if (jQuery(this).parent().find('.label_required').length) {
							jQuery(this).parent().find('.label_required').remove();
						}
					}
				}
				//se non è select
				else {
					//se non è select
					//se è file
					if (jQuery(this).attr('type') == "file") {
						//se non ha contenuto
						if ((jQuery(this).val().length == 0)) {
							//se non è file si può dare il border
							if (jQuery(this).parent().find('.label_required').length == 0) {
								jQuery(this).parent().append("<p class='label_required'>This field is required</p>");
							}
							inputsValid = false;
						}
						//se ha contenuto viene fatta la validazione del file
						else {
							jQuery(this).css("border", "3px solid #000");
							if (jQuery(this).parent().find('.label_required').length) {
								jQuery(this).parent().find('.label_required').remove();
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
								if (jQuery(this).parent().find('.label_file_too_big').length) {
									jQuery(this).parent().find('.label_file_too_big').remove();
								}
								//se grandezza file nok
							} else {
								if (jQuery(this).parent().find('.label_file_too_big').length) {
									jQuery(this).parent().find('.label_file_too_big').remove();
								}
								if (jQuery(this).parent().find('.label_file_too_big').length == 0) {
									jQuery(this).parent().append("<p class='label_file_too_big'>File too big: Limit is 3MB</p>");
								}
								inputsValid = false;
							}
							//controllo estensione
							//se estensione ok
							if (jQuery.inArray(fileNameExt, fileExtensionsAllowed) !== -1) {
								if (jQuery(this).parent().find('.label_file_extension_not_allowed').length) {
									jQuery(this).parent().find('.label_file_extension_not_allowed').remove();
								}
								//se estensione nok
							} else {
								if (jQuery(this).parent().find('.label_file_extension_not_allowed').length) {
									jQuery(this).parent().find('.label_file_extension_not_allowed').remove();
								}
								if (jQuery(this).parent().find('.label_file_extension_not_allowed').length == 0) {
									jQuery(this).parent().append("<p class='label_file_extension_not_allowed'>File extension not allowed</p>");
								}
								inputsValid = false;
							}
						}
					}
					//se non è file
					else {
						//se non ha contenuto
						if ((jQuery(this).val().length == 0)) {
							jQuery(this).css("border", "3px solid #a94442");
							if (jQuery(this).parent().find('.label_required').length == 0) {
								jQuery(this).parent().append("<p class='label_required'>This field is required</p>");
							}
							inputsValid = false;
						}
						//se ha contenuto
						else {
							jQuery(this).css("border", "3px solid #000");
							if (jQuery(this).parent().find('.label_required').length) {
								jQuery(this).parent().find('.label_required').remove();
							}
						}
					}
				}

				//validazione email
				if (jQuery(this).attr('type') == "email") {
					var email = jQuery(this).val();
					var emailFormat = validateEmail(email);
					//se email valorizzata
					if (email.length > 0) {
						//email valida
						if (emailFormat == true) {
							jQuery(this).css("border", "3px solid #000");
							if (jQuery(this).parent().find('.label_email_not_valid').length) {
								jQuery(this).parent().find('.label_email_not_valid').remove();
								jQuery(this).css("border", "3px solid #000");
							}
						}//fine email valida
						//email non valida e non esiste messaggio di errore e mail ha almeno un carattere;
						//se esiste anche la label "required" la toglie
						else {
							jQuery(this).css("border", "3px solid #a94442");
							if (jQuery(this).parent().find('.label_email_not_valid').length == 0 & jQuery(this).parent().find('.label_required').length == 0) {
								jQuery(this).parent()
									.append("<p class='label_email_not_valid'>Please enter a valid email address</p>");
								if (jQuery(this).parent().find('.label_required').length) {
									jQuery(this).parent().find('.label_required').remove();
								}
							}
							inputsValid = false;
						}//fine email non valida
					}//fine email valorizzata
					//se email non valorizzata e esiste la label di mail non valida, viene rimossa
					else {
						if (jQuery(this).parent().find('.label_email_not_valid').length) {
							jQuery(this).parent().find('.label_email_not_valid').remove();
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
	jQuery('#new_form *').filter(':radio').each(function () {
		//il messaggio di errore viene se c'è un checked sul no e il campo è required; si presuppone
		//che il sì sia sempre primo, allora valorizzo una variabile isRequired
		//siamo sul valore si
		if (jQuery(this).attr('value') == "si" | jQuery(this).attr('value') == "yes" | jQuery(this).attr('value') == "1" | jQuery(this).attr('value') == "si") {
			//valore si e obbligatorio
			if (jQuery(this).attr('required')) {
				isRequired = 1;
				//valore si, obbligatorio e checked
				if (jQuery(this).is(':checked')) {
					if (jQuery(this).parent().siblings('.label_required').length) {
						jQuery(this).parent().siblings('.label_required').remove();
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
		if (jQuery(this).attr('value') == "no" | jQuery(this).attr('value') == "0" | jQuery(this).attr('value') == "false") {
			//valore no e checked
			if (jQuery(this).is(':checked')) {
				//obbligatorio e checked no
				if (isRequired == 1) {
					radiosValid = false;
					if (jQuery(this).parent().siblings('.label_required').length == 0) {
						jQuery(this).parent().after('<p class="label_required">This field is required</p>');
					}
				}
				//non obbligatorio e checked no
				else {
					if (jQuery(this).parent().siblings('.label_required').length) {
						jQuery(this).parent().siblings('.label_required').remove();
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
