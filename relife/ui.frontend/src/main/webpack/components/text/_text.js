let buttons = document.querySelectorAll(".cmp-button__show-more");
buttons.forEach(el => el.addEventListener("click", function() {
  let textParagraph = el.parentElement.firstElementChild;
  textParagraph.classList.toggle("cmp-text__paragraph--visible");
  el.classList.toggle("cmp-button__show-more--active");
  el.innerHTML = el.innerHTML == "Show more" ? "Show less" : "Show more";
}));
