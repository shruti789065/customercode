window.addEventListener("load", function () {
  document.body.style.display = "none";
  // eslint-disable-next-line @typescript-eslint/no-unused-expressions
  document.body.offsetHeight; // Accede all'altezza per forzare il ricalcolo
  document.body.style.display = "";
});
