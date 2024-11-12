document.addEventListener("DOMContentLoaded", function () {
  const maxSelectable = 3;
  const topicButtons = document.querySelectorAll(".topics__list--item--button");
  const submitButton = document.querySelector(".topics-container .cmp-button");

  /**
   * Helper function to count active buttons.
   * @returns {DOM element} The active buttons count.
   */
  function getActiveCount() {
    return document.querySelectorAll(".topics__list--item--button.active").length;
  }

  // Add event listener to each topic button
  topicButtons.forEach(button => {
    button.addEventListener("click", function () {
      const isActive = button.classList.contains("active");

      // Toggle active class based on current state
      if (isActive) {
        button.classList.remove("active");
      } else if (getActiveCount() < maxSelectable) {
        button.classList.add("active");
      }

      // Update disabled state of non-active buttons
      const activeCount = getActiveCount();
      topicButtons.forEach(btn => {
        btn.disabled = !isActive && activeCount >= maxSelectable && !btn.classList.contains("active");
      });
    });
  });

  if (submitButton) {
    submitButton.addEventListener("click", function (e) {
      e.preventDefault();

      // Gather values of active buttons.
      const selectedValues = Array.from(document.querySelectorAll(".topics__list--item--button.active"))
        .map(button => button.value);

      const topicsString = selectedValues.join("-");
      let currentHref = submitButton.getAttribute("href");
      let newHref = "";

      // There might be a case with no url, although this is editorial issue,
      // but for any case.
      if (currentHref) {
        newHref = currentHref.split("?")[0];
      }

      if (topicsString) {
        newHref += `?topics=${encodeURIComponent(topicsString)}`;
      }

      submitButton.setAttribute("href", newHref);
      window.location.href = newHref;
    });
  }
});
