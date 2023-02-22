/* eslint-disable max-len */
import $ from 'jquery';
import jQuery from 'jquery';
window.$ = jQuery;

(function () {

	"use strict";

	/**
	 * ContactForm Component
	 *
	 * @class ContactForm
	 * @classdesc initiating javascript for the ContactForm Component.
	 */
	var ContactForm = function () {

		var CONST = {
			BUTTON: '.cmp-form-button',
			SELECT: '.cmp-form-options__field--drop-down'
		};

		var $contactForm, $formButton, $formSelect, _formButtonHTML, _allRadioButton,radioValidation;

		/**
		 * Initializes the ContactForm
		 *
		 * @public
		 */
		function init() {
			$contactForm = document.querySelector('form.cmp-form');
			if ($contactForm == undefined || $contactForm == '') {return;}

			$formButton = document.querySelector(CONST.BUTTON);
			$formSelect = document.querySelector(CONST.SELECT);
			_formButtonHTML = $formButton.innerHTML;
			_allRadioButton = $contactForm.querySelectorAll('input[type=radio]');
			radioValidation = false;

			if($formSelect){
				$formSelect.addEventListener('change', (event) => {
					/* if (getComputedStyle($formButton).visibility === 'hidden') {
						$formButton.style.visibility = 'visible';
					} */
					$formButton.innerHTML = _formButtonHTML + ` ${event.target.value}`;
				});
			}

			$contactForm.querySelectorAll("[required]").forEach((item) => {
				if (item.type === 'radio') {
					item.closest('label').setAttribute('for', item.id);
				}
				if (item.id !== '') {
					document.querySelector('label[for=' + item.id + ']').classList.add('required-field');
				}
			});

			document.querySelector("form").addEventListener("invalid", (event) => {
				event.preventDefault();
				event.target.style.borderColor = '#dc3545';
				let parentMessage = event.target.closest('[data-cmp-required-message]').dataset.cmpRequiredMessage || ' field required ';
				let parentTarget;
				if(event.target.type === 'radio'){
					if (event.target.required) {parentTarget = event.target.parentNode.parentElement;}
				}else{
					parentTarget = event.target.parentElement;
				}
				if (event.target.nextElementSibling !== null && event.target.nextElementSibling.classList.value == 'text-danger') {return;}
				_appendErrorMessage(parentTarget, parentMessage);
			},true);

			/* $contactForm.querySelectorAll("input[type=radio][required]").forEach((item) => {
				console.log({item});
				item.addEventListener('invalid',(e) => {
					console.log({e});
				});
			}); */

			$contactForm.addEventListener("submit", (ev) => {
				_checkRadioValidity();
				console.log({radioValidation});
				if (!radioValidation){
					ev.preventDefault();
				} 
			});

			let inputFile = $('#myfile');
			let filesContainer = $('#myFiles');
			
			inputFile.change(function () {
				let file = inputFile[0].files[0];
				var fileNameExt = file.name.substring(file.name.indexOf('.')+1);
				var fileExtension = ['pdf', 'doc', 'docx'];
				// 2 MB (this size is in bytes)
				if(file && (file.size < 3 *1048576)){
					filesContainer.text(file.name);
				}else{
					alert('File non ammesso; dimensioni : ' + file.size);
				}
				
			});

		}


		function _appendErrorMessage(el, str) {
			var span = document.createElement('span');
			span.innerHTML = str;
			span.classList.add('text-danger');
			span.setAttribute("role", "alert");
			el.append(span);
		}
		function _checkRadioValidity(){
			let count = 0;
			document.querySelectorAll('.cmp-form-options--radio .text-danger').forEach((item) => {
				item.remove();
			});
			document.querySelectorAll('input[type=radio][required]').forEach((radio) => {
				if(radio.checked && count <= 0){
					radioValidation = true;
				}else{
					radioValidation = false;
					count += 1;
				} 
				if(!radio.checked && radio.required){
					if (radio.nextElementSibling !== null && radio.closest('fieldset').querySelector('.text-danger') != undefined) {return;}
					_appendErrorMessage(radio.parentNode.parentElement,radio.closest('[data-cmp-required-message]').dataset.cmpRequiredMessage);
				} 
			});
		}

		
		return {
			init: init
		};

	}();

	$(function () {
		ContactForm.init();
	});

}($));
