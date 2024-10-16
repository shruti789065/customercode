document.addEventListener("DOMContentLoaded", function () {

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const preSelectedTopics = urlParams?.get('topics').split("-");

  const dropdownButtonMultiple = document.querySelector(
    "#dropdownMultiselectMenuButton"
  );
  const dropdownMenuInterests = document.querySelector("#interestList");
  const checkboxes = dropdownMenuInterests.querySelectorAll(
    'input[type="checkbox"]'
  );
  let selectedItemsMultipleSelect = [];
  let selectedTopicsIds = [];

  //FILL SELECTED TOPICS IF PRESELECTED
  if (preSelectedTopics && preSelectedTopics.length > 0) {
    preSelectedTopics.forEach((topicId) => {
      selectedTopicsIds.push(topicId);
      checkboxes?.forEach((checkbox) => {
        if (checkbox.dataset.topicId === topicId) {
          checkbox.checked = true;
          selectedItemsMultipleSelect.push(checkbox.dataset.topicName);
        }
        updateDropdownTextMultiple();
      })

      if(preSelectedTopics.length === 3) {
        checkboxes.forEach((element) => {
          if (
            !selectedItemsMultipleSelect.includes(element.dataset.topicName) &&
            selectedItemsMultipleSelect.length === 3
          ) {
            element.disabled = true;
          } else {
            element.disabled = false;
          }
        });
      }

    })
  }

  const dropdownButtonProfession = document.querySelector(
    "#dropdownProfessionMenuButton"
  );
  const dropdownMenuProfession = document.querySelector("#professionList");
  const professionItems = document.querySelectorAll("#professionList li div");
  let selectedProfession = "";

  const dropdownButtonCountry = document.querySelector(
    "#dropdownCountryMenuButton"
  );
  const dropdownMenuCountry = document.querySelector("#countryList");
  const countryItems = document.querySelectorAll("#countryList li div");
  let selectedCountry = "";
  let selectedCountryId = "";

  let erroeMessagges = [];

  let formComponent = document.querySelector("#formComponentWrapper");
  let thankyouComponent = document.querySelector("#thankyouComponent");

  if (formComponent && thankyouComponent) {
    formComponent.classList.add("d-block");
    formComponent.classList.remove("d-none");
    thankyouComponent.classList.add("d-none");
    thankyouComponent.classList.remove("d-block");
  }

  // STYLE FUNCTIONS AND CUSTOM COMPONENT FUNCTIONS
  dropdownButtonMultiple.addEventListener("click", function () {
    dropdownMenuInterests.style.display =
      dropdownMenuInterests.style.display === "block" ? "none" : "block";
    displayButtonBorderBottom(dropdownButtonMultiple, dropdownMenuInterests);
  });

  document.addEventListener("click", function (event) {
    if (
      !dropdownButtonMultiple.contains(event.target) &&
      !dropdownMenuInterests.contains(event.target)
    ) {
      dropdownMenuInterests.style.display = "none";
    }

    if (
      !dropdownButtonProfession.contains(event.target) &&
      !dropdownMenuProfession.contains(event.target)
    ) {
      dropdownMenuProfession.style.display = "none";
    }

    if (
      !dropdownButtonCountry.contains(event.target) &&
      !dropdownMenuCountry.contains(event.target)
    ) {
      dropdownMenuCountry.style.display = "none";
    }

    displayButtonBorderBottom(dropdownButtonMultiple, dropdownMenuInterests);
    displayButtonBorderBottom(dropdownButtonProfession, dropdownMenuProfession);
    displayButtonBorderBottom(dropdownButtonCountry, dropdownMenuCountry);
  });

  checkboxes.forEach((checkbox) => {
    checkbox.addEventListener("change", function () {
      //Add new item to selectedItems
      if (
        selectedItemsMultipleSelect.length <= 3 &&
        checkbox.checked === true &&
        !selectedItemsMultipleSelect.includes(checkbox.dataset.topicName)
      ) {
        selectedItemsMultipleSelect.push(checkbox.dataset.topicName);
        selectedTopicsIds.push(checkbox.dataset.topicId);
      }

      //Remove item to selectedItems
      if (
        selectedItemsMultipleSelect.length <= 3 &&
        checkbox.checked === false &&
        selectedItemsMultipleSelect.includes(checkbox.dataset.topicName)
      ) {

        selectedItemsMultipleSelect = selectedItemsMultipleSelect.filter(
          (item) => item !== checkbox.dataset.topicName
        );

        selectedTopicsIds = selectedTopicsIds.filter((item) => {
          item !== checkbox.dataset.topicId
        });
      }

      // Disable every not selected checkbox if user select 3 elements      
      checkboxes.forEach((element) => {
        if (
          !selectedItemsMultipleSelect.includes(element.dataset.topicName) &&
          selectedItemsMultipleSelect.length === 3
        ) {
          element.disabled = true;
        } else {
          element.disabled = false;
        }
      });
      updateDropdownTextMultiple();
    });
  });

  dropdownButtonProfession.addEventListener("click", function () {
    dropdownMenuProfession.style.display =
      dropdownMenuProfession.style.display === "block" ? "none" : "block";
    displayButtonBorderBottom(dropdownButtonProfession, dropdownMenuProfession);
  });

  dropdownButtonCountry.addEventListener("click", function () {
    dropdownMenuCountry.style.display =
      dropdownMenuCountry.style.display === "block" ? "none" : "block";
    displayButtonBorderBottom(dropdownButtonCountry, dropdownMenuCountry);
  });

  professionItems.forEach((element) => {
    element.addEventListener("click", function () {
      selectedProfession = element.textContent.trim();
      dropdownMenuProfession.style.display = "none";
      displayButtonBorderBottom(
        dropdownButtonProfession,
        dropdownMenuProfession
      );
      updateDropdownTextProfession();
    });
  });

  countryItems.forEach((element) => {
    let fiscalCodeInput = document.querySelector("#fiscalCodeInput");
    let fiscalCode = document.querySelector("#fiscalCode");
    element.addEventListener("click", function () {
      let currestSelectedCountryId = element.getAttribute("data-country-id");
      selectedCountryId = element.getAttribute("data-country-id");
      selectedCountry = element.textContent.trim();

      dropdownMenuCountry.style.display = "none";
      displayButtonBorderBottom(dropdownButtonCountry, dropdownMenuCountry);
      updateDropdownTextCountry();

      if (currestSelectedCountryId.toLowerCase() === "1") {
        fiscalCodeInput.classList.remove("d-none");
        fiscalCodeInput.classList.add("d-block");
      } else {
        fiscalCode.value = "";
        fiscalCodeInput.classList.add("d-none");
        fiscalCodeInput.classList.remove("d-block");
      }
    });
  });

  function updateDropdownTextMultiple() {
    let innerString =
      selectedItemsMultipleSelect.length === 0
        ? "Select Area of Interest"
        : selectedItemsMultipleSelect.join(", ");
    if (innerString !== "Select Area of Interest") {
      dropdownButtonMultiple.classList.add("dropdown-toggle-filled");
    } else {
      dropdownButtonMultiple.classList.remove("dropdown-toggle-filled");
    }
    dropdownButtonMultiple.textContent = innerString;
  }

  function updateDropdownTextProfession() {
    let innerString = selectedProfession === "" ? "Select Profession" : selectedProfession;
    let areaOfInterestsElement = document.querySelector("#areaOfInterestsComponent");
    if (selectedProfession === Granite.I18n.get("no_healthcare")) {
      areaOfInterestsElement.classList.add("d-none");
      areaOfInterestsElement.classList.remove("d-block");
      selectedItemsMultipleSelect = [];
      selectedTopicsIds = [];
    } else {
      areaOfInterestsElement.classList.add("d-block");
      areaOfInterestsElement.classList.remove("d-none");
    }
    if (innerString !== "Select Profession") {
      dropdownButtonProfession.classList.add("dropdown-toggle-filled");
    } else {
      dropdownButtonProfession.classList.remove("dropdown-toggle-filled");
    }
    dropdownButtonProfession.textContent = innerString;
  }

  function updateDropdownTextCountry() {
    let innerString =
      selectedCountry === "" ? "Select Country" : selectedCountry;
    if (innerString !== "Select Country") {
      dropdownButtonCountry.classList.add("dropdown-toggle-filled");
    } else {
      dropdownButtonCountry.classList.remove("dropdown-toggle-filled");
    }
    dropdownButtonCountry.textContent = innerString;
  }

  function displayButtonBorderBottom(button, menu) {
    if (menu.style.display === "block") {
      button.classList.add("border-bottom-0");
      button.classList.add("active-dropdown");
    } else {
      button.classList.remove("border-bottom-0");
      button.classList.remove("active-dropdown");
    }
  }

  function displayErrorsAlert(errorString) {
    let errorAlert = document.querySelector("#cmp-signupform__errorsAlert");
    if (errorAlert && errorString && errorString !== "") {
      if (errorAlert.classList.contains("d-none")) {
        errorAlert.innerHTML = errorString.slice(1, -1);
        errorAlert.classList.remove("d-none");
        errorAlert.classList.add("d-block");
      } else {
        errorAlert.classList.remove("d-block");
        errorAlert.classList.add("d-none");
      }
    }
  }

  // FORM DATA VALIDATION FUNCTIONS
  function validateProfession() {
    let errorElement = document.querySelector("#professionErrorString");
    let dropdownButton = document.querySelector(
      "#dropdownProfessionMenuButton"
    );

    if (selectedProfession === "") {
      erroeMessagges.push({
        id: "profession",
        message: Granite.I18n.get("mandatory_field"),
      });
      errorElement.innerHTML = Granite.I18n.get("mandatory_field");
      dropdownButton.classList.add("cmp-signupform__borderRed");
      dropdownButton.classList.remove("cmp-signupform__borderGreen");
    } else {
      dropdownButton.classList.remove("cmp-signupform__borderRed");
      dropdownButton.classList.add("cmp-signupform__borderGreen");
      errorElement.innerHTML = "";
    }
  }

  function validateCountry() {
    let errorElement = document.querySelector("#countryErrorString");
    let dropdownButton = document.querySelector("#dropdownCountryMenuButton");
    if (selectedCountry === "") {
      erroeMessagges.push({
        id: "country",
        message: Granite.I18n.get("mandatory_field"),
      });
      errorElement.innerHTML = Granite.I18n.get("mandatory_field");
      dropdownButton.classList.add("cmp-signupform__borderRed");
      dropdownButton.classList.remove("cmp-signupform__borderGreen");
    } else {
      dropdownButton.classList.remove("cmp-signupform__borderRed");
      dropdownButton.classList.add("cmp-signupform__borderGreen");
      errorElement.innerHTML = "";
    }
  }

  function validateInterests(data) {
    let errorElement = document.querySelector("#interestErrorString");
    let dropdownButton = document.querySelector(
      "#dropdownMultiselectMenuButton"
    );

    if (!data.areasOfInterest || data.areasOfInterest.length === 0) {
      erroeMessagges.push({
        id: "areasOfInterest",
        message: Granite.I18n.get("mandatory_field"),
      });
      errorElement.innerHTML = Granite.I18n.get("mandatory_field");
      dropdownButton.classList.add("cmp-signupform__borderRed");
      dropdownButton.classList.remove("cmp-signupform__borderGreen");
    } else {
      dropdownButton.classList.remove("cmp-signupform__borderRed");
      dropdownButton.classList.add("cmp-signupform__borderGreen");
      errorElement.innerHTML = "";
    }
  }

  function validateDataProcessing(data) {
    let errorElement = document.querySelector("#newsletterErrorString");
    if (!data.personalDataProcessing) {
      erroeMessagges.push({
        id: "personalDataProcessing",
        message: Granite.I18n.get("mandatory_field"),
      });
      errorElement.innerHTML = Granite.I18n.get("mandatory_field");
    } else {
      errorElement.innerHTML = "";
    }
  }

  function validateNewsLetter(data) {
    let errorElement = document.querySelector("#dataProcessingErrorString");
    if (!data.receiveNewsletter) {
      erroeMessagges.push({
        id: "receiveNewsletter",
        message: Granite.I18n.get("mandatory_field"),
      });
      errorElement.innerHTML = Granite.I18n.get("mandatory_field");
    } else {
      errorElement.innerHTML = "";
    }
  }

  function validatePassword(data) {
    let errorElement = document.querySelector("#passwordErrorString");

    if (
      (data.password && data.password !== data.passwordConfirmation) ||
      (data.password && !data.passwordConfirmation)
    ) {
      errorElement.innerHTML = Granite.I18n.get("password_form_error");
      erroeMessagges.push({
        id: "password",
        message: Granite.I18n.get("password_form_error"),
      });
    } else if (!data.password) {
      errorElement.innerHTML = Granite.I18n.get("mandatory_field");
      erroeMessagges.push({
        id: "password",
        message: Granite.I18n.get("mandatory_field"),
      });
    }
  }

  function validatePasswordConfirmation(data) {
    let errorElement = document.querySelector(
      "#passwordConfirmationErrorString"
    );

    if (
      (data.passwordConfirmation &&
        data.passwordConfirmation !== data.password) ||
      (data.passwordConfirmation && !data.password)
    ) {
      errorElement.innerHTML = Granite.I18n.get("password_form_error");
      erroeMessagges.push({
        id: "passwordConfirmation",
        message: Granite.I18n.get("password_form_error"),
      });
    } else if (!data.passwordConfirmation) {
      errorElement.innerHTML = Granite.I18n.get("mandatory_field");
      erroeMessagges.push({
        id: "passwordConfirmation",
        message: Granite.I18n.get("mandatory_field"),
      });
    }
  }

  function validateEmail(data) {
    let errorElement = document.querySelector("#emailErrorString");

    if (
      (data.email && data.email !== data.emailConfirmation) ||
      (data.email && !data.emailConfirmation)
    ) {
      errorElement.innerHTML = Granite.I18n.get("email_form_error");
      erroeMessagges.push({
        id: "email",
        message: (errorElement.innerHTML =
          Granite.I18n.get("email_form_error")),
      });
    } else if (!data.email) {
      errorElement.innerHTML = Granite.I18n.get("mandatory_field");
      erroeMessagges.push({
        id: "email",
        message: Granite.I18n.get("mandatory_field"),
      });
    }
  }

  function validateEmailConfirmation(data) {
    let errorElement = document.querySelector("#emailConfirmationErrorString");

    if (
      (data.emailConfirmation && data.emailConfirmation !== data.email) ||
      (data.emailConfirmation && !data.email)
    ) {
      errorElement.innerHTML = Granite.I18n.get("email_form_error");
      erroeMessagges.push({
        id: "emailConfirmation",
        message: Granite.I18n.get("email_form_error"),
      });
    } else if (!data.emailConfirmation) {
      errorElement.innerHTML = Granite.I18n.get("mandatory_field");
      erroeMessagges.push({
        id: "emailConfirmation",
        message: Granite.I18n.get("mandatory_field"),
      });
    }
  }

  function validateGdpr(data) {
    let errorElement = document.querySelector("#privacyErrorString");
    if (data.privacy === "no" || !data.privacy) {
      erroeMessagges.push({
        id: "privacy",
        message: Granite.I18n.get("accept_privacy"),
      });
      errorElement.innerHTML =
        data.privacy === "no"
          ? Granite.I18n.get("accept_privacy")
          : Granite.I18n.get("mandatory_field");
    } else {
      errorElement.innerHTML = "";
    }
  }

  // FORM SUBMIT FUNCTIONS
  async function sendData(registrationData) {
    let loader = document.querySelector("#signupLoader");
    let ctaSignUp = document.querySelector("#ctaSignup");
    try {
      loader.classList.remove("d-none");
      loader.classList.add("d-block");
      ctaSignUp.disabled = true;
      const responseCsrf = await fetch("/libs/granite/csrf/token.json");
      const csrfToken = await responseCsrf.json();
      const regResponse = await fetch("/bin/api/awsSignUp", {
        method: "POST",
        headers: {
          "CSRF-Token": csrfToken.token,
        },
        body: JSON.stringify(registrationData),
      });
      const dataResponse = await regResponse.json();
      return dataResponse;
    } catch (error) {
      console.log(error);
    } finally {
      loader.classList.add("d-none");
      loader.classList.remove("d-block");
      ctaSignUp.disabled = false;
    }

  }

  let form = document.querySelector("#signUpForm");

  if (form) {
    form.addEventListener("submit", async (event) => {
      erroeMessagges = [];
      event.preventDefault();
      const formData = new FormData(form);
      let tmpFormData = {
        profession: selectedProfession,
        country: selectedCountryId,
        areasOfInterest: selectedTopicsIds
      };

      for (let [key, value] of formData.entries()) {
        tmpFormData = {
          ...tmpFormData,
          [`${key}`]: value,
        };
      }

      let registrationData = {
        firstName: tmpFormData.firstName,
        lastName: tmpFormData.lastName,
        birthDate: tmpFormData.dateOfBirth.replaceAll("-", ""),
        password: tmpFormData.password,
        email: tmpFormData.email,
        profession: tmpFormData.profession,
        phone: tmpFormData.telNumber,
        country: tmpFormData.country,
        taxIdCode: tmpFormData.fiscalCode ? tmpFormData.fiscalCode : null,
        interests: tmpFormData.areasOfInterest,
        rolesNames: [],
        gender: tmpFormData.gender,
        privacyConsent: tmpFormData.privacy === "yes" ? true : false,
        profilingConsent:
          tmpFormData.personalDataProcessing === "yes" ? true : false,
        newsletterConsent:
          tmpFormData.receiveNewsletter === "yes" ? true : false,
      };

      validateProfession(tmpFormData);
      validateCountry(tmpFormData);
      validateInterests(tmpFormData);
      validatePassword(tmpFormData);
      validatePasswordConfirmation(tmpFormData);
      validateEmail(tmpFormData);
      validateGdpr(tmpFormData);
      validateDataProcessing(tmpFormData);
      validateNewsLetter(tmpFormData);
      validateEmailConfirmation(tmpFormData);

      if (erroeMessagges.length === 0) {
        const responseReg = await sendData(registrationData);
        if (responseReg.cognitoSignUpErrorResponseDto) {
          displayErrorsAlert(
            JSON.stringify(responseReg.cognitoSignUpErrorResponseDto.message)
          );
        } else {
          if (formComponent && thankyouComponent) {
            formComponent.classList.add("d-none");
            formComponent.classList.remove("d-block");
            thankyouComponent.classList.add("d-block");
            thankyouComponent.classList.remove("d-none");
          }
        }
      }
    });
  }
});

function setFieldAsNotValid() {
  var forms = document.getElementsByClassName("needs-validation");

  Array.prototype.filter.call(forms, function (form) {
    form.addEventListener(
      "submit",
      function (event) {
        if (form.checkValidity() === false) {
          event.preventDefault();
          event.stopPropagation();
        }
        form.classList.add("was-validated");
      },
      false
    );
  });
}

window.addEventListener(
  "load",
  function () {
    setFieldAsNotValid();
  },
  false
);
