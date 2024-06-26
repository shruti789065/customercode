/* eslint-disable max-len */

import $ from "jquery";

// Funzione per ottenere il dominio del sito
function getDomain() {
  const hostname = window.location.hostname;
  return hostname === "localhost" ? hostname : hostname.replace('www.', '');
}

// Funzione per impostare un cookie
function setCookie(name, value, days) {
  let expires = "";
  if (days) {
    const date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    expires = "; expires=" + date.toUTCString();
  }
  const domain = getDomain();
  const domainString = domain === "localhost" ? "" : `; domain=${domain}`;
  document.cookie = `${name}=${value || ""}${expires}; path=/${domainString}`;
  console.log(`Cookie set: ${name}=${value}; expires=${expires}; path=/${domainString}`);
}

// Funzione per ottenere un cookie
function getCookie(name) {
  const nameEQ = name + "=";
  const ca = document.cookie.split(';');
  for (let i = 0; i < ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) === ' ') c = c.substring(1, c.length);
    if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
  }
  return null;
}

// Funzione per il comportamento del pulsante di conferma
function buttonBehaviour() {
  const domain = getDomain();
  const popup = document.querySelector(".cmp-popup");

  if (popup.classList.contains("cmp-popup--session")) {
    sessionStorage.setItem(`${domain}-Popup`, "accepted");
  } else if (popup.classList.contains("cmp-popup--cookie")) {
    setCookie(`${domain}-Popup`, "accepted", 30); // Cookie valido per 30 giorni
  }

  popup.classList.remove('show');
  document.documentElement.classList.remove('no-scroll');
}

// Funzione di inizializzazione
function init() {
  const popup = document.querySelector(".cmp-popup");
  if (!popup) {
    console.log("Popup element not found");
    return;
  }

  const popupConfirmButton = document.getElementById("popupConfirm");
  if (!popupConfirmButton) {
    console.log("Popup confirm button not found");
    return;
  }

  const domain = getDomain();
  popupConfirmButton.addEventListener("click", buttonBehaviour);

  if (popup.classList.contains("cmp-popup--persistent")) {
    popup.classList.add('show');
    document.documentElement.classList.add('no-scroll');
  } else if (popup.classList.contains("cmp-popup--session")) {
    const disclaimerAccepted = sessionStorage.getItem(`${domain}-Popup`);
    if (!disclaimerAccepted) {
      popup.classList.add('show');
      document.documentElement.classList.add('no-scroll');
    }
  } else if (popup.classList.contains("cmp-popup--cookie")) {
    const disclaimerAccepted = getCookie(`${domain}-Popup`);
    if (!disclaimerAccepted) {
      popup.classList.add('show');
      document.documentElement.classList.add('no-scroll');
    }
  }
}

$(function () {
  init();
});
