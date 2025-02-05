import { getUrl } from "../../mastertemplate/site/_util";
/* eslint-disable max-len */
const copyDataFromJson = () => {
	const JSONmock =
	"/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/mock/productsStemline.json";
  const currentNodePipeline = document.querySelector(
    ".currentNodeProducts"
  ).value;
  const endpoint = `${currentNodePipeline}.products.json`;

  fetch(getUrl(endpoint, JSONmock))
    .then((response) => response.json())
    .then((data) => {
      localStorage.setItem("productsStemline", JSON.stringify(data));
    })
    .catch((error) => {
      console.error("Error copying data to local storage:", error);
    });
};

function displayDataproductsStemline() {
  const productsStemlineSelect = document.getElementById(
    "productsStemlineSelect"
  );
  const productsStemlineResults = document.getElementById(
    "productsStemlineResults"
  );
  const data = JSON.parse(localStorage.getItem("productsStemline"));
  const countries = new Set(data.map((item) => item.country));

  // create custom select element
  const selectContainer = document.createElement("div");
  selectContainer.classList.add("select-container");

  const selectLabel = document.createElement("div");
  selectLabel.classList.add("select-label");
  selectLabel.textContent = "Select a country";

  const selectArrow = document.createElement("div");
  selectArrow.classList.add("select-arrow");

  const optionsContainer = document.createElement("div");
  optionsContainer.classList.add("options-container");
  //optionsContainer.style.display = "none";

  countries.forEach((country) => {
    const option = document.createElement("div");
    option.classList.add("option");
    option.classList.add(country.replace(/[^a-zA-Z0-9-_]/g, '').replace(/\s+/g, "-"));
    option.textContent = country;
    option.addEventListener("click", () => {
      selectLabel.textContent = country;
      //optionsContainer.style.display = "none";
      if (country) {
        // show only products from selected country
        productElements.forEach((element) => {
          if (element.dataset.country === country) {
            element.style.display = "block";
          } else {
            element.style.display = "none";
          }
        });
      } else {
        // show all products when no country selected
        productElements.forEach((element) => {
          element.style.display = "block";
        });
      }
    });
    optionsContainer.appendChild(option);
  });

  selectContainer.appendChild(selectLabel);
  selectContainer.appendChild(selectArrow);
  selectContainer.appendChild(optionsContainer);
  selectContainer.addEventListener("click", () => {
    optionsContainer.classList.toggle("show");
  });

  // add event listener to window object to hide options-container when clicking outside of select-container
  window.addEventListener("click", (event) => {
    if (!selectContainer.contains(event.target)) {
      optionsContainer.classList.remove("show");
    }
  });

  // create product elements
  const productElements = data.map((item) => {
    const product = document.createElement("div");
    product.classList.add("product");
    product.dataset.country = item.country;

    const image = document.createElement("img");
    image.classList.add("imageProduct");
    image.src = item.image;
    product.appendChild(image);

    const name = document.createElement("div");
    name.classList.add("nameProduct");
    name.textContent = item.name;
    product.appendChild(name);

    const website = document.createElement("a");
    website.classList.add("websiteProduct");
    website.href = item.website;
    website.target = item.targetwebsite;
    website.textContent = item.labelwebsite;
    product.appendChild(website);

    return product;
  });

  // append select and product elements to container
  productsStemlineSelect.innerHTML = "";
  productsStemlineSelect.appendChild(selectContainer);
  productElements.forEach((element) => {
    productsStemlineResults.appendChild(element);
  });

  // show all products on first load
  productElements.forEach((element) => {
    element.style.display = "none";
  });

}

const init = () => {
  copyDataFromJson();
  const data = JSON.parse(localStorage.getItem("productsStemline"));
  if (data && data.length > 0) {
    displayDataproductsStemline();
  } else {
    const intervalId = setInterval(() => {
      const data = JSON.parse(localStorage.getItem("productsStemline"));
      if (data && data.length > 0) {
        clearInterval(intervalId);
        displayDataproductsStemline();
      }
    }, 500);
  }
};







function showDisclaimer(link, translations) {
  $('#disclaimer h2').text(translations.title);
  $('#disclaimer p').text(translations.message);
  $('#disclaimer').show();

  $('#confirm-btn').off('click');

  $('#confirm-btn').click(function() {
    window.open(link.href, '_blank');

    $('#disclaimer').hide();

    return false;
  });

  $('#cancel-btn').off('click');

  $('#cancel-btn').click(function() {
    $('#disclaimer').hide();

    return false;
  });
}

function addDisclaimer(language) {
  //console.log('check link page');

  const translations = {
    'en': {
      title: 'YOU ARE NOW LEAVING THIS MENARINI STEMLINE WEBSITE',
      message: 'You are about to leave this website for another external website. \nMenarini Stemline has no responsibility for the content of such other sites and is not liable for any damages or injury arising from that content. Any links to other sites are provided merely as a convenience to the users of this website.',
      confirmText: 'Confirm',
      cancelText: 'Cancel'
    },
    'de': {
      title: 'Disclaimer',
      message: 'Germania',
      confirmText: 'Bestätigen',
      cancelText: 'Abbrechen'
    }
  };

  const links = document.querySelectorAll('a.websiteProduct');

  const disclaimerHTML = `
    <div id="disclaimer" style="display:none;">
      <div class="popupDisclaimer">
        <h2>${translations[language].title}</h2>
        <p>${translations[language].message}</p>
        <button id="confirm-btn" class="primary">${translations[language].confirmText}</button>
        <button id="cancel-btn" class="secondary">${translations[language].cancelText}</button>
        </div>        
    </div>
  `;

  $('body').prepend(disclaimerHTML);

  for (let i = 0; i < links.length; i++) {
    const link = links[i];

    if (link.hostname !== window.location.hostname /*&& !$(link).closest('.side_menu').length && !$(link).closest('.ot-sdk-show-settings').length*/) {
      $(link).click(function(e) {
        e.preventDefault();

        showDisclaimer(link, translations[language]);
      });
    }
  }
}

function onLoad() {
  const language = document.documentElement.lang;

  addDisclaimer(language);
}

window.onload = function () {
  const productsStemline = document.getElementById("productsStemline");
  if (productsStemline) {
    init();
    onLoad();
  } else {
    //console.log("noProductsStemline");
  }
};
