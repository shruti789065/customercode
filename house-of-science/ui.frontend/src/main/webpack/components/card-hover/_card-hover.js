

(function () {
	"use strict";
	const cardContainers = document.querySelector('.cardHoverContainer');
	if (cardContainers !== null) {

		const windowSizeMobile = window.matchMedia("(max-width: 768px)");
		const windowSizeDesktop = window.matchMedia("(min-width: 1200px)");
		if(windowSizeMobile.matches){
			document.querySelector('.card-hover-container').closest('.cmp-container--standard_width').style.margin="0";
			document.querySelector('.card-hover-container').closest('.cmp-container--standard_width').style.maxWidth="100%";
		}
		windowSizeMobile.onchange = (e) => {
			if(e.matches){
				document.querySelector('.card-hover-container').closest('.cmp-container--standard_width').style.margin="0";
				document.querySelector('.card-hover-container').closest('.cmp-container--standard_width').style.maxWidth="100%";
			} else {
				document.querySelector('.card-hover-container').closest('.cmp-container--standard_width').style.margin="0 8.33%";
				document.querySelector('.card-hover-container').closest('.cmp-container--standard_width').style.maxWidth="84%";
			}
		};
		windowSizeDesktop.onchange = (e) => {
			if(e.matches){
				document.querySelector('.card-hover-container').closest('.cmp-container--standard_width').style.margin="0 auto";
			} else {
				document.querySelector('.card-hover-container').closest('.cmp-container--standard_width').style.margin="0 8.33%";
			}
		};

		const cards = cardContainers.querySelectorAll('.cardHover');

		cards.forEach(card => {
			card.addEventListener('mouseenter', () => {
				cards.forEach(otherCard => {
					if (otherCard !== card) {
						otherCard.classList.add('cmp-card__reduced');
					}
				});
			});

			card.addEventListener('mouseleave', () => {
				cards.forEach(otherCard => {
					if (otherCard !== card) {
						otherCard.classList.remove('cmp-card__reduced');
					}
				});
			});
		});

	}
}());

