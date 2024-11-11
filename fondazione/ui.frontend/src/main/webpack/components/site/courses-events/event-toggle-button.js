document.addEventListener("DOMContentLoaded", () => {
  if (document.getElementById("scheduledEvents")) {
    const scheduledButton = document.getElementById("scheduledEvents");
    const finishedButton = document.getElementById("finishedEvents");

    function updateQueryParams(status) {
      const url = new URL(window.location);
      url.searchParams.set("eventStatus", status);
      window.history.pushState({}, "", url);

      location.reload();
    }

    function setActiveButtonFromURL() {
      const url = new URL(window.location);
      let eventStatus = url.searchParams.get("eventStatus");

      if (!eventStatus) {
        eventStatus = "scheduled";
        url.searchParams.set("eventStatus", eventStatus);
        window.history.replaceState({}, "", url);
      }

      if (eventStatus === "scheduled") {
        scheduledButton.classList.add("active");
        finishedButton.classList.remove("active");
      } else if (eventStatus === "finished") {
        finishedButton.classList.add("active");
        scheduledButton.classList.remove("active");
      }
    }

    scheduledButton.addEventListener("click", () => {
      if (!scheduledButton.classList.contains("active")) {
        scheduledButton.classList.add("active");
        finishedButton.classList.remove("active");
        updateQueryParams("scheduled");
      }
    });

    finishedButton.addEventListener("click", () => {
      if (!finishedButton.classList.contains("active")) {
        finishedButton.classList.add("active");
        scheduledButton.classList.remove("active");
        updateQueryParams("finished");
      }
    });

    setActiveButtonFromURL();
  }
});
