/* eslint-disable max-len */
import $ from 'jquery';

(function () {
	"use strict";

	var LogoMenu = function () {
		var $menuLogo,$menuLogoAuthor,$header,$siteIdentier,$logoPath;
		
		
		/**
		 * Initializes the LogoMenu
		*
		* @public
		*/
		function init() {
			const url = window.location.href;
			const urlArray = url.split("/"); // Dividi l'URL in un array
			const contentIndex = urlArray.indexOf("content"); // Trova l'indice dell'array contenente la parola "content"
			if (contentIndex !== -1 && contentIndex < urlArray.length - 1) { // Controlla se l'indice esiste e c'è un elemento successivo
				$siteIdentier = urlArray[contentIndex + 1]; // Prendi l'elemento successivo come il pezzo desiderato
				console.log($siteIdentier);
			}

			$menuLogo = document.querySelector('.cmp-image--logo .cmp-image__image');
			//$menuLogoAuthor = document.querySelector('.aem-AuthorLayer-Edit .cmp-image--logo .cmp-image__image');
			$header = document.querySelector('header.cmp-experiencefragment--header');
			//$logoPath = `/content/dam/logo/${$siteIdentier}/`;
			$logoPath = document.getElementById('logoFolder').value;

			/*if (typeof Granite !== 'undefined' && Granite.author && Granite.author.EditorFrame) {
				// sei in ambiente Author
				$menuLogo.src = $logoPath + '/logo-pos.svg';
				//console.log('Sono in Edit Mode!');
			  } else {
				// non sei in ambiente Author
				//console.log('Non sono in Edit Mode');
				$menuLogo.src = $logoPath + '/logo-neg.svg';
			  }

			  function disableImageSrc() {
                // selezioniamo la prima immagine presente nella pagina


                // controlliamo se l'immagine è stata già disabilitata
                if ($menuLogo.getAttribute('data-disabled') === 'true') {
                  return; // se sì, usciamo dalla funzione
                }

                // salviamo il valore dell'attributo "src" in un nuovo attributo personalizzato
                $menuLogo.setAttribute('data-src', $menuLogo.getAttribute('src'));

                // svuotiamo l'attributo "src" dell'immagine
                $menuLogo.setAttribute('src', '');

                // aggiungiamo un attributo personalizzato per tenere traccia della disabilitazione
                $menuLogo.setAttribute('data-disabled', 'true');

                // aggiungiamo un listener per ripristinare il valore dell'attributo "src" al primo scroll della pagina
                window.addEventListener('scroll', function restoreImageSrc() {
                  $menuLogo.setAttribute('src', $menuLogo.getAttribute('data-src'));
                  $menuLogo.removeAttribute('data-src');
                  $menuLogo.removeAttribute('data-disabled');
                  window.removeEventListener('scroll', restoreImageSrc);
                }, { once: true });
              }
			*/

			if ($header !== null) {
				const observer = new MutationObserver(function (mutationsList, observer) {
					// per ogni mutazione nel mutationsList
					for (let mutation of mutationsList) {
						// se la mutazione riguarda la classe "scrolled-page" dell'elemento header
						if (mutation.type === 'attributes' && mutation.attributeName === 'class' && $header.classList.contains('scrolled-page')) {
							// cambiamo dinamicamente l'attributo src dell'immagine del logo
							$menuLogo.src = $logoPath + '/logo-pos.svg';
						} else {
							$menuLogo.src = $logoPath + '/logo-neg.svg';
						}
					}
				});

				// configuriamo l'observer per osservare le modifiche della classe "scrolled-page" dell'elemento header
				observer.observe($header, { attributes: true, attributeFilter: ['class'] });
			}
		}
		return {
			init: init
		};
	}();

	$(function () {
		LogoMenu.init();

	});

}($));
