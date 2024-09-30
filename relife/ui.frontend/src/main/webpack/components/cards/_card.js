function toggleShowMoreButtons() {
  document
    .querySelectorAll(".cmp-button__show-more")
    .forEach((btn) => btn.remove());

  if (window.innerWidth <= 992) {
    const cards = document.querySelectorAll(".cmp-card--product");

    if (cards.length > 0) {
      cards.forEach((card) => {
        const content = card.querySelector(".cmp-teaser__content");

        if (content) {
          const showMoreBtn = document.createElement("button");
          showMoreBtn.textContent = "Show More";
          showMoreBtn.className = "cmp-button__show-more";

          showMoreBtn.addEventListener("click", function () {
            if (content.classList.contains("expanded")) {
              content.classList.remove("expanded");
              showMoreBtn.textContent = "Show More";
              this.classList.remove("content-expanded");
            } else {
              content.classList.add("expanded");
              this.classList.add("content-expanded");
              showMoreBtn.textContent = "Show Less";
            }
          });

          content.parentNode.insertBefore(showMoreBtn, content.nextSibling);
        }
      });
    }
  }
}

document.addEventListener("DOMContentLoaded", toggleShowMoreButtons);

window.addEventListener("resize", toggleShowMoreButtons);
