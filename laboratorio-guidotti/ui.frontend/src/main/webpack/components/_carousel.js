// Import core Swiper functionality along with necessary modules
import Swiper from "swiper";
import { Navigation, Pagination, Thumbs } from "swiper/modules";

// Import Swiper and its modules' styles
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";
import "swiper/css/thumbs";

// Check if the body dataset attribute 'wcmMode' is not 'EDIT'
if (document.querySelector("body").dataset.wcmMode != 'EDIT') {
  // Iterate over each element with class 'swiper'
  document.querySelectorAll(".swiper").forEach(function (s) {
    // Get the relevant navigation and pagination elements for the current Swiper instance
    let next = s.parentElement.querySelector(".swiper-button-next"),
      prev = s.parentElement.querySelector(".swiper-button-prev"),
      pag = s.parentElement.querySelector(".swiper-pagination"),
      slidesNumber = s.parentElement.querySelector(".slides-number").value,
      slideMode = s.parentElement.querySelector(".slide-mode").value,
      //tabletSlidesNumber = slidesNumber == 1 ? 1 : 2,
      desktopSlidesNumber = slideMode == "gallery" ? 1 : slidesNumber,
      $thumbs = null;

    // If the slide mode is 'gallery', initialize the thumbnail preview
    if (slideMode == "gallery") {
      let ts = s.parentElement.parentElement.parentElement.querySelector(
        ".swiper-container .swiper-thumbs-slider"
      );

      // Set up preview thumbnails by creating new Swiper slides
      s.querySelectorAll(".swiper-slide").forEach(function (sl) {
        const newElement = document.createElement("div");
        newElement.classList.add("swiper-slide");
        let slSrc = sl.querySelector(".cmp-image__image").getAttribute("src");
        let slideHTML = `<img src='${slSrc}' width="70"/>`;
        newElement.innerHTML = slideHTML;
        ts.querySelector(".swiper-wrapper").appendChild(newElement);
      });

      // Initialize the thumbnail Swiper instance
      $thumbs = new Swiper(ts, {
        spaceBetween: 5,
        slidesPerView: 8,
        breakpoints: {
          992: {
            slidesPerView: 10,
          },
          1200: {
            slidesPerView: 12,
          },
        },
        freeMode: true,
        watchSlidesProgress: true,
      });
    }

    // Determine if pagination and navigation should be active
    let paginationActive = slideMode != "gallery",
      navigationActive = paginationActive;

    // Initialize the main Swiper instance
    new Swiper(s, {
      slidesPerView: 1,
      spaceBetween: 10,
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
        992: {
          slidesPerView: 2,
          spaceBetween: 50,
          navigation: {
            enabled: true,
          },
        },
        1200: {
          slidesPerView: desktopSlidesNumber,
          spaceBetween: 50,
          navigation: {
            enabled: true,
          },
        },
        1440: {
          slidesPerView: desktopSlidesNumber,
          spaceBetween: 50,
          navigation: {
            enabled: navigationActive,
          },
        },
      },
      // Configure Swiper to use imported modules
      modules: [Navigation, Pagination, Thumbs],
      thumbs: {
        swiper: $thumbs,
        autoScrollOffset: 1,
      },
    });
  });
}
