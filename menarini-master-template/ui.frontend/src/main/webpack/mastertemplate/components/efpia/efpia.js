/* eslint-disable max-len */


// Function to execute when the button is clicked
function onSubmitEfpia() {
    // Check if reCAPTCHA is checked
    if (grecaptcha.getResponse() !== "") {
        // Show the success message
        document.getElementById("successMessage").style.display = "block";
        // Hide the reCAPTCHA and button
        document.getElementById("recaptcha").style.display = "none";
        document.getElementById("efpia-button").style.display = "none";
    } else {
        // reCAPTCHA is not checked, display an error or take other actions
        //alert("Please complete the reCAPTCHA.");
        console.log('Please complete the reCAPTCHA');
    }
}

var efpiaButton = document.getElementById("efpia-button");
if (efpiaButton) {
    efpiaButton.addEventListener("click", checkEfpia);
}

function openPopup() {
    var modalEfpia = document.getElementById("modalEfpia");

    var closeBtn = document.getElementById("modalEfpiaClose");
    
    closeBtn.addEventListener("click", function() {
        modalEfpia.style.display = "none";
    });

    modalEfpia.style.display = "flex";
}

var efpiaButtonReport = document.getElementById("efpia-button-report");
if (efpiaButtonReport) {
    efpiaButtonReport.addEventListener("click", openPopup);
}



const copyDataFromJsonEfpia = () => {
    const domainName = window.location.hostname;
    const port = window.location.port;
    const protocol = window.location.protocol;
    const currentNodeEfpia = document.querySelector(".currentNodeEfpia").value;
    let url;
  
    if (domainName === "localhost" && port === "4502") {
      url = `${protocol}//${domainName}:${port}${currentNodeEfpia}.efpia.json`;
    } else if (domainName === "localhost") {
      url ="https://raw.githubusercontent.com/davide-mariotti/JSON/main/efpiaMT/efpia.json";
    } else {
      url = `${protocol}//${domainName}${currentNodeEfpia}.efpia.json`;
    }
  
    fetch(url)
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
    const resultsEfpia = document.querySelector(".modal-content-text");
    resultsEfpia.innerHTML = "";
    
    const years = Object.keys(efpiaData);

    if (years.length === 0) {
        resultsEfpia.innerHTML = "No results found.";
        return;
    }
 
    const template = years.map((year) => {
        const images = efpiaData[year].map((entry) => `<img src="${entry.url}" alt="${year}">`).join("");
        return `
            <div class="year-container">
                <h3>${year}</h3>
                <div class="image-container">${images}</div>
            </div>
        `;
    }).join("");

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
        console.log('efpia');
        onSubmitEfpia();
        initEfpia();
    }
}