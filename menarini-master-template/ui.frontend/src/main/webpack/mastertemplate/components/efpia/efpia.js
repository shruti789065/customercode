import { getUrl } from "../../../mastertemplate/site/_util";
/* eslint-disable max-len */

const JSONmock =
  "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/mock/efpia.json";

// Function to execute when the button is clicked
function onSubmitEfpia() {
  // Check if reCAPTCHA is checked
  if (grecaptcha.getResponse() !== "") {
    // Show the success message
    document.getElementById("successMessage").style.display = "block";
    document.getElementById("efpia-button-report").style.display = "block";
    // Hide the reCAPTCHA and button by class name
    var recaptchaElements = document.getElementsByClassName("recaptcha");
    for (var i = 0; i < recaptchaElements.length; i++) {
      recaptchaElements[i].style.display = "none";
    }

    document.getElementById("efpia-button").style.display = "none";
  } else {
    // reCAPTCHA is not checked, display an error or take other actions
    //alert("Please complete the reCAPTCHA.");
    console.log("Please complete the reCAPTCHA");
  }
}

var efpiaButton = document.getElementById("efpia-button");
if (efpiaButton) {
  efpiaButton.addEventListener("click", function (event) {
    event.preventDefault();
    checkEfpia();
  });
}

function openPopup() {
  var modalEfpia = document.getElementById("modalEfpia");
  var closeBtn = document.getElementById("modalEfpiaClose");

  if (closeBtn) {
    closeBtn.addEventListener("click", function () {
      modalEfpia.style.display = "none";
    });
  }

  modalEfpia.style.display = "flex";
}

var efpiaButtonReport = document.getElementById("efpia-button-report");
if (efpiaButtonReport) {
  efpiaButtonReport.addEventListener("click", openPopup);
}

const copyDataFromJsonEfpia = () => {
  const currentNodeEfpia = document.querySelector(".currentNodeEfpia").value;
  const endpoint = `${currentNodeEfpia}.efpia.json`;

  fetch(getUrl(endpoint, JSONmock))
    .then((response) => response.json())
    .then((data) => {
      localStorage.setItem("efpiaData", JSON.stringify(data));
      displayDataEfpia(data);
    })
    .catch((error) => {
      console.error("Error copying data to local storage:", error);
    });
};

function displayDataEfpia(efpiaData) {
  const resultsEfpia = document.querySelector("#modalEfpia");
  resultsEfpia.innerHTML = "";

  const years = Object.keys(efpiaData);

  if (years.length === 0) {
    resultsEfpia.innerHTML = "No results found.";
    return;
  }

  const template = `
    <div id="modal-content">
        <div id="modal-content-text">
            ${years
              .map((year) => {
                const images = efpiaData[year]
                  .map((entry) => `<img src="${entry.url}" alt="${year}">`)
                  .join("");
                return `
                    <div class="year-container">
                        <h3>${year}</h3>
                        <div class="image-container">${images}</div>
                    </div>
                `;
              })
              .join("")}
        </div>
        <span id="modalEfpiaClose">X</span>
    </div>
  `;
  resultsEfpia.innerHTML = template;
}

const initEfpia = () => {
  copyDataFromJsonEfpia();

  const dataEfpia = JSON.parse(localStorage.getItem("efpiaData"));
  if (dataEfpia && dataEfpia.length > 0) {
    displayDataEfpia();
  } else {
    const intervalIdEfpia = setInterval(() => {
      const dataEfpia = JSON.parse(localStorage.getItem("efpiaData"));
      if (dataEfpia && dataEfpia.length > 0) {
        clearInterval(intervalIdEfpia);
        displayDataEfpia();
      }
    }, 500);
  }
};

document.addEventListener("click", function (event) {
  const clickedYearContainer = event.target.closest(".year-container");

  if (clickedYearContainer) {
    // Remove the "active" class from all year containers
    const allYearContainers = document.querySelectorAll(".year-container");
    allYearContainers.forEach((yearContainer) => {
      yearContainer.classList.remove("active");
    });

    // Add the "active" class to the clicked year container
    clickedYearContainer.classList.add("active");
  }
});

function checkEfpia() {
  var cmpEfpia = document.getElementById("cmp-efpia");
  if (cmpEfpia) {
    console.log("efpia");
    onSubmitEfpia();
    initEfpia();
  }
}
