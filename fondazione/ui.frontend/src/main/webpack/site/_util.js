/**
 * Checks if the current window width is considered desktop size.
 * @returns {boolean} - True if the window width is greater than 1200px, otherwise false.
 */

// @todo: check how to remove moment from here.
// it should be used in editprofileform.js
import moment from 'moment';
window.moment = moment;

export function _isDesktop() {
  if (window.innerWidth <= 1200) {
    return false;
  } else {
    return true;
  }
}

/**
 * Prepends HTML content to a specified element.
 * @param {Element} el - The element to which the HTML string will be prepended.
 * @param {string} str - The HTML string to prepend.
 */
export function _prependHtml(el, str) {
  var div = document.createElement("div");
  div.innerHTML = str;
  while (div.children.length > 0) {
    el.prepend(div.children[0]);
  }
}

/**
 * Displays an overlay and a loading spinner on a specified item.
 * @param {Element} item - The element on which to display the overlay and loader.
 * @param {boolean} needOverlay - Specifies whether an overlay should be shown.
 */
export function showOverlayAndLoader(item, needOverlay) {
  if (needOverlay) {
    const overlay = document.createElement("div");
    overlay.classList.add("overlay");
    item.append(overlay);
  }
  const loader = document.createElement("div");
  loader.classList.add("loader");
  item.append(loader);
  item.addClass("loading");
}

/**
 * Hides the overlay and loading spinner from a specified item.
 * @param {Element} item - The element from which to remove the overlay and loader.
 */
export function hideOverlayAndLoader(item) {
  item.find(".loader").remove();
  const overlay = item.find(".overlay");
  if (overlay.length > 0) {
    overlay.remove();
  }
  item.removeClass("loading");
}
