window.addEventListener("load", function () {
  document.body.style.display = "none";
  document.body.offsetHeight; // Accede all'altezza per forzare il ricalcolo
  document.body.style.display = "";
});
