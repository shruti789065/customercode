(function () {
	"use strict";
  
	var CardAvatar = (function () {
	  var cardAvatars = document.querySelectorAll(".cmp-card--avatar");
  
	  function init() {
		if (cardAvatars.length > 0) {
		  cardAvatars.forEach(function (cardAvatar) {
			var actionLinks = cardAvatar.querySelectorAll(".cmp-teaser__action-link");
  
			actionLinks.forEach(function (actionLink) {
			  actionLink.addEventListener("click", function (e) {
				e.preventDefault();
				var teaserDescription = actionLink.parentElement.nextElementSibling;
  
				// Toggle della classe "active" sul pulsante
				actionLink.classList.toggle("active");
  
				// Toggle della visualizzazione della descrizione
				if (teaserDescription.style.display === "none" || teaserDescription.style.display === "") {
				  teaserDescription.style.display = "block";
				} else {
				  teaserDescription.style.display = "none";
				}
			  });
			});
		  });
		}
	  }
  
	  return {
		init: init,
	  };
	})();
  
	document.addEventListener("DOMContentLoaded", function () {
	  CardAvatar.init();
	});
  })();
  
