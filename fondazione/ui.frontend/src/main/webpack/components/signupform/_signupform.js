document.addEventListener('DOMContentLoaded', function () {
  const dropdownButtonMultiple = document.querySelector('#dropdownMultiselectMenuButton');
  const dropdownMenuInterests = document.querySelector('#interestList');
  const checkboxes = dropdownMenuInterests.querySelectorAll('input[type="checkbox"]');
  let selectedItemsMultipleSelect = [];

  const dropdownButtonProfession = document.querySelector('#dropdownProfessionMenuButton');
  const dropdownMenuProfession = document.querySelector('#professionList');
  const professionItems = document.querySelectorAll("#professionList li div");
  let selectedProfession = "";


  const dropdownButtonCountry = document.querySelector('#dropdownCountryMenuButton');
  const dropdownMenuCountry = document.querySelector('#countryList');
  const countryItems = document.querySelectorAll("#countryList li div");
  let selectedCountry = "";

  let erroeMessagges = [];

  // STYLE FUNCTIONS AND CUSTOM COMPONENT FUNCTIONS
  dropdownButtonMultiple.addEventListener('click', function () {
    dropdownMenuInterests.style.display = dropdownMenuInterests.style.display === 'block' ? 'none' : 'block';
    displayButtonBorderBottom(dropdownButtonMultiple, dropdownMenuInterests);
  });

  document.addEventListener('click', function (event) {
    if (!dropdownButtonMultiple.contains(event.target) && !dropdownMenuInterests.contains(event.target)) {
      dropdownMenuInterests.style.display = 'none';
    }

    if (!dropdownButtonProfession.contains(event.target) && !dropdownMenuProfession.contains(event.target)) {
      dropdownMenuProfession.style.display = 'none';
    }

    if (!dropdownButtonCountry.contains(event.target) && !dropdownMenuCountry.contains(event.target)) {
      dropdownMenuCountry.style.display = 'none';
    }

    displayButtonBorderBottom(dropdownButtonMultiple, dropdownMenuInterests);
    displayButtonBorderBottom(dropdownButtonProfession, dropdownMenuProfession);
    displayButtonBorderBottom(dropdownButtonCountry, dropdownMenuCountry);

  });

  checkboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function () {

      //Add new item to selectedItems
      if (selectedItemsMultipleSelect.length <= 3 && checkbox.checked === true && !selectedItemsMultipleSelect.includes(checkbox.value)) {
        selectedItemsMultipleSelect.push(checkbox.value)
      }

      //Remove item to selectedItems
      if (selectedItemsMultipleSelect.length <= 3 && checkbox.checked === false && selectedItemsMultipleSelect.includes(checkbox.value)) {
        selectedItemsMultipleSelect = selectedItemsMultipleSelect.filter(item => item !== checkbox.value)
      }

      // Disable every not selected checkbox if user select 3 elements
      checkboxes.forEach(element => {
        if (!selectedItemsMultipleSelect.includes(element.value) && selectedItemsMultipleSelect.length === 3) {
          element.disabled = true;
        } else {
          element.disabled = false;
        }
      })
      updateDropdownTextMultiple();
    });
  });

  dropdownButtonProfession.addEventListener('click', function () {
    dropdownMenuProfession.style.display = dropdownMenuProfession.style.display === 'block' ? 'none' : 'block';
    displayButtonBorderBottom(dropdownButtonProfession, dropdownMenuProfession);
  });

  dropdownButtonCountry.addEventListener('click', function () {
    dropdownMenuCountry.style.display = dropdownMenuCountry.style.display === 'block' ? 'none' : 'block';
    displayButtonBorderBottom(dropdownButtonCountry, dropdownMenuCountry);
  });

  professionItems.forEach(element => {
    element.addEventListener('click', function (event) {
      selectedProfession = element.textContent.trim();
      dropdownMenuProfession.style.display = 'none'
      displayButtonBorderBottom(dropdownButtonProfession, dropdownMenuProfession);
      updateDropdownTextProfession();
    })
  })

  countryItems.forEach(element => {
    element.addEventListener('click', function (event) {
      selectedCountry = element.textContent.trim();
      dropdownMenuCountry.style.display = 'none'
      displayButtonBorderBottom(dropdownButtonCountry, dropdownMenuCountry);
      updateDropdownTextCountry();
    })
  })

  function updateDropdownTextMultiple() {
    let innerString = selectedItemsMultipleSelect.length === 0 ? 'Select Area of Interest' : selectedItemsMultipleSelect.join(', ');
    if (innerString !== 'Select Area of Interest') {
      dropdownButtonMultiple.classList.add('dropdown-toggle-filled');
    } else {
      dropdownButtonMultiple.classList.remove('dropdown-toggle-filled');
    }
    dropdownButtonMultiple.textContent = innerString
  }

  function updateDropdownTextProfession() {
    let innerString = selectedProfession === "" ? 'Select Profession' : selectedProfession;
    if (innerString !== 'Select Profession') {
      dropdownButtonProfession.classList.add('dropdown-toggle-filled');
    } else {
      dropdownButtonProfession.classList.remove('dropdown-toggle-filled');
    }
    dropdownButtonProfession.textContent = innerString
  }

  function updateDropdownTextCountry() {
    let innerString = selectedCountry === "" ? 'Select Country' : selectedCountry;
    if (innerString !== 'Select Country') {
      dropdownButtonCountry.classList.add('dropdown-toggle-filled');
    } else {
      dropdownButtonCountry.classList.remove('dropdown-toggle-filled');
    }
    dropdownButtonCountry.textContent = innerString
  }

  function displayButtonBorderBottom(button, menu) {
    if (menu.style.display === 'block') {
      button.classList.add('border-bottom-0');
      button.classList.add('active-dropdown');

    } else {
      button.classList.remove('border-bottom-0');
      button.classList.remove('active-dropdown');
    }
  }




  // FORM DATA VALIDATION FUNCTIONS
  function validateProfession() {
    let errorElement = document.querySelector('#professionErrorString');
    let dropdownButton = document.querySelector('#dropdownProfessionMenuButton');

    if (selectedProfession === "") {
      erroeMessagges.push(
        {
          id: "profession",
          message: "Profession field is mandatory"
        }
      )
      errorElement.innerHTML = "This field is mandatory."
      dropdownButton.classList.add('cmp-signupform__borderRed');
      dropdownButton.classList.remove('cmp-signupform__borderGreen');
    } else {
      dropdownButton.classList.remove('cmp-signupform__borderRed');
      dropdownButton.classList.add('cmp-signupform__borderGreen');
      errorElement.innerHTML = ""
    }
  }

  function validateCountry() {
    let errorElement = document.querySelector('#countryErrorString');
    let dropdownButton = document.querySelector('#dropdownCountryMenuButton');
    if (selectedCountry === "") {
      erroeMessagges.push(
        {
          id: "country",
          message: "Country field is mandatory"
        }
      )
      errorElement.innerHTML = "This field is mandatory."
      dropdownButton.classList.add('cmp-signupform__borderRed');
      dropdownButton.classList.remove('cmp-signupform__borderGreen');
    } else {
      dropdownButton.classList.remove('cmp-signupform__borderRed');
      dropdownButton.classList.add('cmp-signupform__borderGreen');
      errorElement.innerHTML = ""
    }
  }

  function validateInterests(data) {
    let errorElement = document.querySelector('#interestErrorString');
    let dropdownButton = document.querySelector('#dropdownMultiselectMenuButton');

    if (!data.areasOfInterest || data.areasOfInterest.length === 0) {
      erroeMessagges.push(
        {
          id: "areasOfInterest",
          message: "Please select at least 1 area of interest"
        }

      )
      errorElement.innerHTML = "This field is mandatory."
      dropdownButton.classList.add('cmp-signupform__borderRed');
      dropdownButton.classList.remove('cmp-signupform__borderGreen');
    } else {
      dropdownButton.classList.remove('cmp-signupform__borderRed');
      dropdownButton.classList.add('cmp-signupform__borderGreen');
      errorElement.innerHTML = ""
    }
  }

  function validateDataProcessing(data) {
    let errorElement = document.querySelector('#newsletterErrorString');
    if (!data.personalDataProcessing) {
      erroeMessagges.push(
        {
          id: "personalDataProcessing",
          message: "Please select personal data processing preference"
        }
      )
      errorElement.innerHTML = "This field is mandatory."
    } else {
      errorElement.innerHTML = ""
    }
  }

  function validateNewsLetter(data) {
    let errorElement = document.querySelector('#dataProcessingErrorString');
    if (!data.receiveNewsletter) {
      erroeMessagges.push(
        {
          id: "receiveNewsletter",
          message: "Please select if you want to receive newsletter or not"
        }
      )
      errorElement.innerHTML = "This field is mandatory."
    } else {
      errorElement.innerHTML = ""
    }
  }

  function validatePassword(data) {
    let errorElement = document.querySelector('#passwordErrorString');

    if ((data.password && data.password !== data.passwordConfirmation) || (data.password && !data.passwordConfirmation)) {
      errorElement.innerHTML = "Password and password confirmation fields doesn't match";
      erroeMessagges.push(
        {
          id: "password",
          message: "Password and password confirmation fields doesn't match"
        }
      )
    } else if (!data.password) {
      errorElement.innerHTML = "This field is mandatory.";
      erroeMessagges.push(
        {
          id: "password",
          message: "This field is mandatory."
        }
      )
    }
  }

  function validatePasswordConfirmation(data) {
    let errorElement = document.querySelector('#passwordConfirmationErrorString');

    if ((data.passwordConfirmation && data.passwordConfirmation !== data.password) || (data.passwordConfirmation && !data.password)) {
      errorElement.innerHTML = "Password and password confirmation fields doesn't match";
      erroeMessagges.push(
        {
          id: "passwordConfirmation",
          message: "Password and password confirmation fields doesn't match"
        }
      )
    } else if (!data.passwordConfirmation) {
      errorElement.innerHTML = "This field is mandatory.";
      erroeMessagges.push(
        {
          id: "passwordConfirmation",
          message: "This field is mandatory."
        }
      )
    }
  }

  function validateEmail(data) {
    let errorElement = document.querySelector('#emailErrorString');

    if ((data.email && data.email !== data.emailConfirmation) || (data.email && !data.emailConfirmation)) {      
      errorElement.innerHTML = "Email and email confirmation fields doesn't match";
      erroeMessagges.push(
        {
          id: "email",
          message: "Email and email confirmation fields doesn't match."
        }
      )
    } else if (!data.email) {
      errorElement.innerHTML = "This field is mandatory.";
      erroeMessagges.push(
        {
          id: "email",
          message: "This field is mandatory."
        }
      )
    }
  }

  function validateEmailConfirmation(data) {
    let errorElement = document.querySelector('#emailConfirmationErrorString');

    if ((data.emailConfirmation && data.emailConfirmation !== data.email) || (data.emailConfirmation && !data.email)) {      
      errorElement.innerHTML = "Email and email confirmation fields doesn't match";
      erroeMessagges.push(
        {
          id: "emailConfirmation",
          message: "Email and email confirmation fields doesn't match."
        }
      )
    } else if (!data.emailConfirmation) {
      errorElement.innerHTML = "This field is mandatory.";
      erroeMessagges.push(
        {
          id: "emailConfirmation",
          message: "Email confirmation field is mandatory."
        }
      )
    }
  }

  function validateGdpr(data) {
    let errorElement = document.querySelector('#privacyErrorString');
    if (data.privacy === "no" || !data.privacy) {
      erroeMessagges.push(
        {
          id: "privacy",
          message: "Please accept the privacy information notice before sign up"
        }
      )
      errorElement.innerHTML = data.privacy === "no" ? "Please accept the privacy information notice before sign up." : "This field is mandatory." 
    } else {
      errorElement.innerHTML = ""
    }
  }

  // function generateErrorsAlert() {
  //   let errorsAlert = document.querySelector('#cmp-signupform__errorsAlert');
  //   let errorsList = document.querySelector('#cmp-signupform__errorsList');
  //   errorsList.innerHTML = '';

  //   if (erroeMessagges.length > 0) {
  //     errorsAlert.classList.add("d-block");
  //     errorsAlert.classList.remove("d-none");

  //     erroeMessagges.forEach(item => {
  //       const li = document.createElement('li');
  //       li.textContent = item.message;
  //       errorsList.appendChild(li);
  //     });
  //   } else {
  //     errorsAlert.classList.remove("d-block");
  //     errorsAlert.classList.add("d-none");
  //   }
  // }

  // FORM SUBMIT FUNCTIONS
  async function sendData(registrationData) {
    const responseCsrf = await fetch('/libs/granite/csrf/token.json');
    const csrfToken = await responseCsrf.json();
    console.log('CSRF-Token:', csrfToken);
    const regResponse = await fetch("/bin/api/awsSignUp", {
      method: "POST",
      headers: {
        'CSRF-Token': csrfToken.token
      },
      body: JSON.stringify(registrationData)

    });
    const dataResponse = await regResponse.json();
    console.log(JSON.stringify(dataResponse, null, 3));
    return dataResponse;

  }

  let form = document.querySelector('#signUpForm');

  if (form) {
    form.addEventListener("submit", async (event) => {
      erroeMessagges = []; // reset errors array
      event.preventDefault();

      const formData = new FormData(form);

      let tmpFormData = {
        profession: selectedProfession,
        areasOfInterest: selectedItemsMultipleSelect.map(x => x.replaceAll(" ", ""))
      };

      for (let [key, value] of formData.entries()) {
        tmpFormData = {
          ...tmpFormData,
          [`${key}`]: value
        }
      }

      let registrationData = {
        "firstName": tmpFormData.firstName,
        "lastName": tmpFormData.lastName,
        "birthDate": tmpFormData.dateOfBirth.replaceAll("-", ""),
        "password": tmpFormData.password,
        "email": tmpFormData.email,
        "profession": tmpFormData.profession,
        "phone": tmpFormData.telNumber,
        "country": tmpFormData.country,
        "interests": tmpFormData.areasOfInterest,
        "rolesNames": [],
        "gender": tmpFormData.gender,
        "privacyConsent": tmpFormData.privacy,
        "profilingConsent": tmpFormData.personalDataProcessing,
        "newsletterConsent": tmpFormData.receiveNewsletter
      }

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
          alert(JSON.stringify(responseReg.cognitoSignUpErrorResponseDto.message));
        } else {
          document.location.href = "/us/welcome";
        }
      }
    });
  }
});

function setFieldAsNotValid() {
  var forms = document.getElementsByClassName('needs-validation');

  var validation = Array.prototype.filter.call(forms, function (form) {
    form.addEventListener('submit', function (event) {
      if (form.checkValidity() === false) {
        event.preventDefault();
        event.stopPropagation();
      }
      form.classList.add('was-validated');
    }, false);
  });
}

window.addEventListener('load', function () {
  setFieldAsNotValid();
}, false);



