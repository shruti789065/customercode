import $ from "jquery";

$(document).ready(function () {
  const maxSelectable = 3;

  $(".topics__list--item--button").on("click", function () {
    if ($(this).hasClass("active")) {
      $(this).removeClass("active");
    } else {
      let activeCount = $(".topics__list--item--button.active").length;

      if (activeCount < maxSelectable) {
        $(this).addClass("active");
      }
    }

    let activeCount = $(".topics__list--item--button.active").length;

    if (activeCount >= maxSelectable) {
      $(".topics__list--item--button:not(.active)").prop("disabled", true);
    } else {
      $(".topics__list--item--button").prop("disabled", false);
    }
  });

  $(".cmp-button").on("click", function (e) {
    e.preventDefault();

    let selectedValues = $(".topics__list--item--button.active")
      .map(function () {
        return $(this).val();
      })
      .get();

    let topicsString = selectedValues.join("_");
    let currentHref = $(this).attr("href");
    let newHref = currentHref.split("?")[0];

    if (topicsString) {
      newHref += `?topics=${encodeURIComponent(topicsString)}`;
    }

    $(this).attr("href", newHref);

    window.location.href = newHref;
  });
});
