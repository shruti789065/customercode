// core version + navigation, pagination modules:
import Swiper from "swiper";
import { Navigation, Pagination, Thumbs } from "swiper/modules";
// import Swiper and modules styles
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";
import "swiper/css/thumbs";

if (document.querySelector("body").dataset.wcmMode != 'EDIT') {
  document.querySelectorAll(".swiper").forEach(function (s) {
    let next = s.parentElement.querySelector(".swiper-button-next"),
      prev = s.parentElement.querySelector(".swiper-button-prev"),
      pag = s.parentElement.querySelector(".swiper-pagination"),
      slidesNumber = s.parentElement.querySelector(".slides-number").value,
      slideMode = s.parentElement.querySelector(".slide-mode").value,
      tabletSlidesNumber = slidesNumber == 1 ? 1 : 2,
      desktopSlidesNumber = slideMode == "gallery" ? 1 : slidesNumber,
      $thumbs = null;

    if (slideMode == "gallery") {
      let ts = s.parentElement.parentElement.parentElement.querySelector(
        ".swiper-container .swiper-thumbs-slider"
      );

      //setup preview markup and then initialize
      s.querySelectorAll(".swiper-slide").forEach(function (sl) {
        const newElement = document.createElement("div");
        newElement.classList.add("swiper-slide");
        let slSrc = sl.querySelector(".cmp-image__image").getAttribute("src");
        let slideHTML = `<img src='${slSrc}' width="70"/>`;
        newElement.innerHTML = slideHTML;
        ts.querySelector(".swiper-wrapper").appendChild(newElement);
      });

      $thumbs = new Swiper(ts, {
        spaceBetween: 5,
        slidesPerView: 8,
        breakpoints: {
          768: {
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

    let paginationActive = slideMode != "gallery",
      navigationActive = paginationActive;

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
        enabled: false,
      },
      breakpoints: {
        768: {
          slidesPerView: tabletSlidesNumber,
          spaceBetween: 50,
          navigation: {
            enabled: false,
          },
        },
        1200: {
          slidesPerView: desktopSlidesNumber,
          spaceBetween: 50,
          navigation: {
            enabled: false,
          },
        },
        1550: {
          slidesPerView: desktopSlidesNumber,
          spaceBetween: 50,
          navigation: {
            enabled: navigationActive,
          },
        },
      },
      // configure Swiper to use modules
      modules: [Navigation, Pagination, Thumbs],
      thumbs: {
        swiper: $thumbs,
        autoScrollOffset: 1,
      },
    });
  });
}

// init Swiper:
/*
const swiper = new Swiper('.swiper', {
  slidesPerView: 1,
  spaceBetween: 10,
  pagination: {
    el: ".swiper-pagination",
    dynamicBullets: true,
    enabled: true, //false
    clickable: true,
  },
  navigation: {
    nextEl: ".swiper-button-next",
    prevEl: ".swiper-button-prev",
    enabled: false,   
  }, 
  breakpoints: {
    768: {
      slidesPerView: 2,
      spaceBetween: 50,
      navigation: {
        enabled: false,   
      }
    },
    1200: {
      slidesPerView: 3, //parametro sperview da html > vedi logo js
      spaceBetween: 50,
      navigation: {
        enabled: false,   
      }
    },
    1550: {
      slidesPerView: 4, //parametro sperview da html > vedi logo js
      spaceBetween: 50,
      navigation: {
        enabled: true,   
      }
    }
  }, 
  // configure Swiper to use modules
  modules: [Navigation, Pagination],
});
*/
