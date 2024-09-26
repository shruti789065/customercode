(function () {
  "use strict";

  const SPACER = {
    ELEMENT: ".cmp-spacer",
    MIN_VIEWPORT: 375, // Converto a numero
    MAX_VIEWPORT: 1920, // Converto a numero
  };

  /**
   * Initializes the Spacer
   *
   * @public
   */
  function init() {
    // Applichiamo la funzione su tutti gli elementi all'inizializzazione
    document.querySelectorAll(SPACER.ELEMENT).forEach(function (element) {
      const dataMin = parseFloat(element.dataset.spacerMin); // Converto a float
      const dataMax = parseFloat(element.dataset.spacerMax); // Converto a float

      setDynamicHeight(element, dataMin, dataMax);

      // Aggiungiamo l'evento resize per ricalcolare l'altezza dinamica
      window.addEventListener("resize", function () {
        setDynamicHeight(element, dataMin, dataMax);
      });
    });
  }

  function setDynamicHeight(element, min, max) {
    let calculatedClamp;

    if (!min && max) {
      // Se manca `min`, usiamo solo `max`
      calculatedClamp = `${max}px`;
    } else if (min && !max) {
      // Se manca `maxn`, usiamo solo `min`
      calculatedClamp = `${min}px`;
    } else if (min && max) {
      // Otteniamo la larghezza attuale della viewport, vincolata tra il minimo e il massimo
      const viewportWidth = Math.max(
        SPACER.MIN_VIEWPORT,
        Math.min(window.innerWidth, SPACER.MAX_VIEWPORT)
      );

      // Calcolo del valore intermedio in base alla larghezza della viewport
      const intermedio =
        min +
        (max - min) *
          ((viewportWidth - SPACER.MIN_VIEWPORT) /
            (SPACER.MAX_VIEWPORT - SPACER.MIN_VIEWPORT));

      // Applichiamo il valore height usando clamp()
      calculatedClamp = `clamp(${min}px, ${intermedio}px, ${max}px)`;
    }

    if (calculatedClamp) {
      element.style.height = calculatedClamp;
    }
  }
  document.addEventListener("DOMContentLoaded", init);
})();
