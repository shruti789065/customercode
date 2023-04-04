/* eslint-disable max-len */
const copyDataFromJson = () => {
  const domainName = window.location.hostname;
  const port = window.location.port;
  const protocol = window.location.protocol;
  const currentNodePipeline = document.querySelector(
    ".currentNodeProducts"
  ).value;
  let url;

  if (domainName === "localhost" && port === "4502") {
    url = `${protocol}//${domainName}:${port}${currentNodePipeline}.products.json`;
  } else if (domainName === "localhost") {
    url =
      "https://raw.githubusercontent.com/davide-mariotti/JSON/main/productsST/productsStemline.json";
  } else {
    url = `${protocol}//${domainName}${currentNodePipeline}.products.json`;
  }

  fetch(url)
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

window.onload = function () {
  const productsStemline = document.getElementById("productsStemline");
  if (productsStemline) {
    init();
  } else {
    console.log("noProductsStemline");
  }
};
