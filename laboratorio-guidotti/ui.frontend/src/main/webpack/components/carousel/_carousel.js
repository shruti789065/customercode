// Import core Swiper functionality along with necessary modules
import Swiper from "swiper";
import { Navigation, Pagination, Thumbs } from "swiper/modules";

// Import Swiper and its modules' styles
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";
import "swiper/css/thumbs";

// Check if the body dataset attribute 'wcmMode' is not 'EDIT'
if (document.querySelector("body").dataset.wcmMode !== "EDIT") {
  document.querySelectorAll(".swiper").forEach(function (s) {
    const parentElement = s.parentElement;

    // Verifica che gli elementi esistano prima di accedervi
    const next = parentElement.querySelector(".swiper-button-next"),
      prev = parentElement.querySelector(".swiper-button-prev"),
      pag = parentElement.querySelector(".swiper-pagination"),
      slidesNumberElem = parentElement.querySelector(".slides-number"),
      slideModeElem = parentElement.querySelector(".slide-mode");

    // Se 'slidesNumber' o 'slideMode' non sono presenti, saltare l'inizializzazione
    if (!slidesNumberElem || !slideModeElem) {return;}

    const slidesNumber = parseInt(slidesNumberElem.value, 10),
      slideMode = slideModeElem.value;

    let desktopSlidesNumber = slidesNumber;
    let $thumbs = null;

    // If the slide mode is 'gallery', initialize the thumbnail preview
    if (slideMode === "gallery") {
      const ts = parentElement.parentElement.parentElement.querySelector(
        ".swiper-container .swiper-thumbs-slider"
      );
      if (ts) {
        s.querySelectorAll(".swiper-slide").forEach(function (sl) {
          const newElement = document.createElement("div");
          newElement.classList.add("swiper-slide");
          const slSrc = sl
            .querySelector(".cmp-image__image")
            ?.getAttribute("src");

          if (slSrc) {
            newElement.innerHTML = `<img src='${slSrc}'/>`;
            ts.querySelector(".swiper-wrapper").appendChild(newElement);
          }
        });

        // Initialize the thumbnail Swiper instance
        $thumbs = new Swiper(ts, {
          spaceBetween: 5,
          slidesPerView: 4,
          breakpoints: {
            480: { slidesPerView: 6 },
            992: { slidesPerView: 8 },
            1200: { slidesPerView: 10 },
          },
          freeMode: true,
          watchSlidesProgress: true,
        });

        // In 'gallery' mode, force slidesPerView to 1
        desktopSlidesNumber = 1;
      }
    }

    // Determine if pagination and navigation should be active
    const paginationActive = slideMode !== "gallery",
      navigationActive = paginationActive;

    // Initialize the main Swiper instance
    new Swiper(s, {
      slidesPerView: 1,
      spaceBetween: 20,
      pagination: {
        el: pag,
        dynamicBullets: true,
        enabled: paginationActive,
        clickable: true,
      },
      navigation: {
        nextEl: next,
        prevEl: prev,
        enabled: true,
      },
      breakpoints: {
        480: {
          slidesPerView:
            desktopSlidesNumber === 1 ? 1 : desktopSlidesNumber - 1,
          spaceBetween: 25,
        },
        992: {
          slidesPerView: desktopSlidesNumber,
        },
        1200: {
          slidesPerView: desktopSlidesNumber,
        },
        1440: {
          slidesPerView: desktopSlidesNumber,
          navigation: { enabled: navigationActive },
        },
      },
      modules: [Navigation, Pagination, Thumbs],
      thumbs: {
        swiper: $thumbs,
        autoScrollOffset: 1,
      },
    });
  });
}
