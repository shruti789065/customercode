document.addEventListener("DOMContentLoaded", function () {
  if (window.location.hostname === "localhost") {
    document.body.classList.add("local-environment");
  }
});
