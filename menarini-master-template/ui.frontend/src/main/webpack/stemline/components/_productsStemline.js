/* eslint-disable max-len */
/* ---Scommentare quando vogliamo rendere dinamico l'url del json---
const copyDataFromJson = () => {
    const domainName = window.location.hostname;
    const currentNodeProducts = document.querySelector('.currentNodeProducts').value;
    const url = `http://${domainName}${currentNodeProducts}.products.json`;

    const loadingSpinner = document.createElement("div");
    loadingSpinner.classList.add("loading-spinner");
    document.body.appendChild(loadingSpinner);
  
    fetch(url)
      .then((response) => response.json())
      .then((data) => {
        localStorage.setItem('productsStemline', JSON.stringify(data));
        loadingSpinner.remove();
      })
      .catch((error) => {
        console.error("Error copying data to local storage:", error);
        loadingSpinner.remove();
      });
  };*/

function copyDataFromJson() {
  const url = `https://raw.githubusercontent.com/davide-mariotti/productsStemline/main/productsStemline.json`;

  const loadingSpinner = document.createElement("div");
  loadingSpinner.classList.add("loading-spinner");
  document.body.appendChild(loadingSpinner);

  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      localStorage.setItem("productsStemline", JSON.stringify(data));
      loadingSpinner.remove();
    })
    .catch((error) => {
      console.error("Error copying data to local storage:", error);
      loadingSpinner.remove();
    });
}

function displayDataproductsStemline() {
  const productsStemlineSelect = document.getElementById("productsStemlineSelect");
  const productsStemlineResults = document.getElementById("productsStemlineResults");
  const data = JSON.parse(localStorage.getItem("productsStemline"));
  const countries = new Set(data.map((item) => item.country));

  // create select element
  const select = document.createElement("select");
  select.classList.add("selectStemline", "form-select");
  select.addEventListener("change", () => {
    const selectedCountry = select.value;
    if (selectedCountry) {
      // show only products from selected country
      productElements.forEach((element) => {
        if (element.dataset.country === selectedCountry) {
          element.style.display = "block";
        } else {
          element.style.display = "none";
        }
      });
    } else {
      // show no products when no country selected
      productElements.forEach((element) => {
        element.style.display = "none";
      });
    }
  });

  // add placeholder option
  const placeholderOption = document.createElement("option");
  placeholderOption.text = "Select a country";
  placeholderOption.value = "";
  placeholderOption.selected = true;
  placeholderOption.disabled = true;
  select.appendChild(placeholderOption);

  // add options for each country
  countries.forEach((country) => {
    const option = document.createElement("option");
    option.classList.add("country");
    option.classList.add(country);
    option.text = country;
    option.value = country;
    select.appendChild(option);
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
  productsStemlineSelect.appendChild(select);
  productElements.forEach((element) => {
    productsStemlineResults.appendChild(element);
  });

  // hide all products on first load
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

window.onload = function () {
  const productsStemline = document.getElementById("productsStemline");
  if (productsStemline) {
    init();
  } else {
    console.log("noProductsStemline");
  }
};
