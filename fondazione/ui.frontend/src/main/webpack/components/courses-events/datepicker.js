import flatpickr from "flatpickr";
import "flatpickr/dist/flatpickr.min.css";
import "flatpickr/dist/plugins/confirmDate/confirmDate.css";
import { isMobileDevice } from "../../utils/isMobileDevice";

document.addEventListener("DOMContentLoaded", () => {
  const dateOrPeriodInput = document.querySelector("#dateOrPeriod");
  const clearButton = document.querySelector("#clearDateOrPeriod");

  if (!dateOrPeriodInput) {
    console.error("Element with ID 'dateOrPeriod' not found.");
    return;
  }

  const urlParams = new URLSearchParams(window.location.search);

  const getFormattedDate = (date) => {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(
      2,
      "0"
    )}-${String(date.getDate()).padStart(2, "0")}`;
  };

  const updateUrl = (startDate = null, endDate = null) => {
    const currentUrl = new URL(window.location.href);
    const baseUrl = `${currentUrl.origin}${currentUrl.pathname}`;
    const urlParams = new URLSearchParams(currentUrl.search);

    if (startDate && endDate) {
      urlParams.set("dateOrPeriod", `${startDate}-to-${endDate}`);
      if (!isMobileDevice()) {
        clearButton.style.display = "block";
      }
    } else {
      urlParams.delete("dateOrPeriod");
      clearButton.style.display = "none";
    }

    const newUrl = urlParams.toString()
      ? `${baseUrl}?${urlParams.toString()}`
      : baseUrl;
    window.history.pushState({ path: newUrl }, "", newUrl);

    if (!isMobileDevice() && startDate) {
      location.reload();
    }
  };

  const getPreselectedDates = () => {
    const dateOrPeriodParam = urlParams.get("dateOrPeriod");
    if (!dateOrPeriodParam) {
      return [];
    }

    clearButton.style.display = "block";
    const [startDate, endDate] = dateOrPeriodParam.split("-to-");
    return startDate ? [startDate, endDate || startDate] : [];
  };

  const flatpickrInstance = flatpickr(dateOrPeriodInput, {
    mode: "range",
    dateFormat: "Y-m-d",
    altInput: true,
    altFormat: "F j, Y",
    defaultDate: getPreselectedDates(),
    onClose: (selectedDates) => {
      if (selectedDates.length > 0) {
        const startDate = getFormattedDate(selectedDates[0]);
        const endDate = selectedDates[1]
          ? getFormattedDate(selectedDates[1])
          : startDate;
        updateUrl(startDate, endDate);
      } else {
        updateUrl();
      }
    },
  });

  clearButton.addEventListener("click", (event) => {
    event.preventDefault();
    flatpickrInstance.clear();
    updateUrl();
  });
});
