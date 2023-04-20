

(function () {
	"use strict";
	const cardContainers = document.querySelector('.cardHoverContainer');
	if (cardContainers !== null) {
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

