import $ from "jquery";

const LightboxPopup = (() => {
  let lightboxPopup;

  function createOverlayPopup() {
    const lightboxClone = lightboxPopup.cloneNode(true);
    lightboxClone.id = "";
    lightboxClone.classList.add("cmp-lightbox");

    const buttonContainer = document.createElement("div");
    const buttonConfirm = document.createElement("button");

    buttonContainer.classList.add("button", "cmp-button--small");
    buttonConfirm.classList.add("cmp-button", "cmp-form-button");
    buttonConfirm.innerText = "Confirm";
    buttonContainer.appendChild(buttonConfirm);

    buttonConfirm.addEventListener("click", buttonBehaviour);

    const overlay = document.createElement("div");
    overlay.classList.add("overlay-popup");
    overlay.appendChild(lightboxClone);
    lightboxClone.appendChild(buttonContainer);

    document.body.appendChild(overlay);
  }

  function buttonBehaviour() {
    const overlay = document.querySelector(".overlay-popup");
    if (overlay) {
      overlay.remove();
    }
    if (
      lightboxPopup &&
      lightboxPopup.classList.contains("cmp-lightbox--session")
    ) {
      sessionStorage.setItem("disclaimerConfirmed", "accepted");
    }
    if (lightboxPopup) {
      lightboxPopup.style.display = "none"; // Nasconde il popup
      document.body.style.overflow = "auto"; // Ripristina l'overflow del body
    }
  }

  function init() {
    lightboxPopup = document.querySelector(".lightbox-popup");
    if (!lightboxPopup) {
      return;
    }

    createOverlayPopup();

    if (lightboxPopup.classList.contains("cmp-lightbox--session")) {
      const disclaimerAccepted = sessionStorage.getItem("disclaimerConfirmed");
      if (disclaimerAccepted == "accepted") {
        document.querySelector(".overlay-popup").style.display = "none";
      }
    }

    document.body.style.overflow = "hidden";
  }

  return {
    init: init,
  };
})();

$(function () {
  LightboxPopup.init();
});
