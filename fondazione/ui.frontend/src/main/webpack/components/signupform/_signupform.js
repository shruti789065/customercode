document.addEventListener('DOMContentLoaded', function () {
  const dropdownButtonMultiple = document.querySelector('#dropdownMultiselectMenuButton');
  const dropdownMenuInterests = document.querySelector('#interestList');
  const checkboxes = dropdownMenuInterests.querySelectorAll('input[type="checkbox"]');
  let selectedItemsMultipleSelect = [];

  const dropdownButtonProfession = document.querySelector('#dropdownProfessionMenuButton');
  const dropdownMenuProfession = document.querySelector('#professionList');
  const professionItems = document.querySelectorAll("#professionList li div");
  let selectedProfession = "";

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

    displayButtonBorderBottom(dropdownButtonMultiple, dropdownMenuInterests);
    displayButtonBorderBottom(dropdownButtonProfession, dropdownMenuProfession);

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

  professionItems.forEach(element => {
    element.addEventListener('click', function (event) {
      selectedProfession = element.textContent.trim();
      dropdownMenuProfession.style.display = 'none'
      displayButtonBorderBottom(dropdownButtonProfession, dropdownMenuProfession);
      updateDropdownTextProfession();
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

  function displayButtonBorderBottom(button, menu) {
    if (menu.style.display === 'block') {
      button.classList.add('border-bottom-0');
      button.classList.add('active-dropdown');

    } else {
      button.classList.remove('border-bottom-0');
      button.classList.remove('active-dropdown');
    }
  }


  async function sendData(registrationData) {
    const responseCsrf = await fetch('/libs/granite/csrf/token.json');
    const csrfToken = await responseCsrf.json();
    console.log('CSRF-Token:',csrfToken);
    const regResponse = await fetch("/bin/api/awsSignUp",{
      method:"POST",
      headers: {
        'CSRF-Token':csrfToken.token
      },
      body:JSON.stringify(registrationData)

    });
    const dataResponse = await regResponse.json();
    console.log(JSON.stringify(dataResponse,null,3));
    return dataResponse;

  }


  let form = document.querySelector('#signUpForm');

  if(form) {
    form.addEventListener("submit",  async(event) => {
      event.preventDefault();
      const formData = new FormData(form);

      let tmpFormData = {
        profession: selectedProfession,
        areasOfInterest: selectedItemsMultipleSelect.map(x => x.replaceAll(" ",""))
      };
  
      for (let [key, value] of formData.entries()) {
        tmpFormData = {
          ...tmpFormData,
          [`${key}`]: value
        }
      }

      console.log("FORM DATA: ",tmpFormData);
      let registrationData = {
        "firstName":tmpFormData.firstName,
        "lastName":tmpFormData.lastName,
        "birthDate":tmpFormData.dateOfBirth.replaceAll("-",""),
        "password":tmpFormData.password,
        "email":tmpFormData.email,
        "profession":tmpFormData.profession,
        "phone":tmpFormData.telNumber,
        "country":tmpFormData.country,
        "interests":tmpFormData.areasOfInterest,
        "rolesNames":[],
        "gender":tmpFormData.gender,
        "privacyConsent":tmpFormData.privacy,
        "profilingConsent":tmpFormData.personalDataProcessing,
        "newsletterConsent":tmpFormData.receiveNewsletter
      }
      console.log("REGISTRATION DATA: ",registrationData);
      const responseReg = await sendData(registrationData);
      if(responseReg.cognitoSignUpErrorResponseDto) {
          alert(JSON.stringify(responseReg.cognitoSignUpErrorResponseDto.message));
      } else {
        document.location.href = "/us/welcome";
      }
    });
  }
});


