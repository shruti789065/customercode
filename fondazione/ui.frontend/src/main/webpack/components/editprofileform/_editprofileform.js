import moment from 'moment';

document.addEventListener('DOMContentLoaded', function () {
    let selectedTab = "";
    let successAlert = document.querySelector("#cmp-editprofileform__successAlert");
    let errorAlert = document.querySelector("#cmp-editprofileform__errorsAlert");
    let userProfileTab = document.querySelector('#userProfileTab');
    let passwordTab = document.querySelector('#passwordTab');
    let userProfileComponent = document.querySelector('#userProfileComponent');
    let passwordComponent = document.querySelector('#passwordComponent');
    userProfileTab.addEventListener('click', () => toggleTab('userProfileTab'));
    passwordTab.addEventListener('click', () => toggleTab('passwordTab'));

    const dropdownButtonMultiple = document.querySelector("#dropdownMultiselectMenuButtonUserProfile");
    const dropdownMenuInterests = document.querySelector("#interestListUserProfile");
    const checkboxes = dropdownMenuInterests.querySelectorAll('input[type="checkbox"]');
    let selectedItemsMultipleSelect = [];

    const dropdownButtonProfession = document.querySelector("#dropdownProfessionMenuButtonUserProfile");
    const dropdownMenuProfession = document.querySelector("#professionListUserProfile");
    const professionItems = document.querySelectorAll("#professionListUserProfile li div");
    let selectedProfession = "";

    const dropdownButtonCountry = document.querySelector("#dropdownCountryMenuButtonUserProfile");
    const dropdownMenuCountry = document.querySelector("#countryListUserProfile");
    const countryItems = document.querySelectorAll("#countryListUserProfile li div");
    let selectedCountry = "";

    let erroeMessagges = [];

    // USER PROFILE FORM FIELDS
    let userProfileCreationDate = document.querySelector('#userProfileCreationDate');
    let userProfileLastUpdatedOn = document.querySelector('#userProfileLastUpdatedOn');
    let firstName = document.querySelector('#firstName');
    let lastName = document.querySelector('#lastName');
    let linkedinProfile = document.querySelector('#linkedinProfile');
    let profession = document.querySelector('#professionListUserProfile');
    let gender = document.querySelector('#gender');
    let dateOfBirth = document.querySelector('#dateOfBirth');
    let telephone = document.querySelector('#telephone');
    let country = document.querySelector('#countryListUserProfile');
    let emailAddress = document.querySelector('#userProfileEmailAddress');
    let areaOfIntersts = document.querySelector('#dropdownMultiselectMenuButtonUserProfile');
    let privacyYes = document.querySelector('#privacyYes');
    let dataProcessingYes = document.querySelector('#dataProcessingYes');
    let dataProcessingNo = document.querySelector('#dataProcessingNo');
    let newsletterYes = document.querySelector('#newsletterYes');
    let newsletterNo = document.querySelector('#newsletterNo');

    // SET TOKEN AND FILL FORM WITH USER DATA
    async function setToken() {
        const token = localStorage.getItem('token') !== null ? localStorage.getItem('token') : sessionStorage.getItem('token');
        console.log("TOKEN: ", token);
        
        const responseCsrf = await fetch("/libs/granite/csrf/token.json");
        const csrfToken = await responseCsrf.json();
        const regResponse = await fetch("/private/api/user", {
            method: "GET",
            headers: {
                "CSRF-Token": csrfToken.token,
                'Authorization': 'Bearer ' + token
            },
        });
        const dataResponse = await regResponse.json();

        if (dataResponse.success === true) {
            if (firstName && dataResponse.updatedUser.firstname !== "") {
                firstName.value = dataResponse.updatedUser.firstname;
            }
            if (lastName && dataResponse.updatedUser.lastname !== "") {
                lastName.value = dataResponse.updatedUser.lastname;
            }
            if (linkedinProfile && dataResponse.updatedUser.linkedinProfile !== "") {
                linkedinProfile.value = dataResponse.updatedUser.linkedinProfile;
            }
            if (gender && dataResponse.updatedUser.gender !== "") {
                gender.value = dataResponse.updatedUser.gender;
            }
            if (telephone && dataResponse.updatedUser.phone !== "") {
                telephone.value = dataResponse.updatedUser.phone;
            }
            if (dateOfBirth && dataResponse.updatedUser.birthDate !== "") {
                moment.locale(dataResponse.updatedUser.country.toLowerCase());
                const backendDate = dataResponse.updatedUser.birthDate;
                const formattedDate = moment(backendDate, "MMM D, YYYY").format("YYYY-MM-DD");
                dateOfBirth.value = formattedDate;
            }
            if (emailAddress && dataResponse.updatedUser.email !== "") {
                emailAddress.textContent = dataResponse.updatedUser.email
            }
            if (userProfileCreationDate && dataResponse.updatedUser.createdOn !== "") {
                const formattedDate = moment(dataResponse.updatedUser.createdOn, "MMM D, YYYY, h:mm:ss A").format("DD/MM/YYYY");
                userProfileCreationDate.textContent = formattedDate;
            }
            if (userProfileLastUpdatedOn && dataResponse.updatedUser.lastUpdatedOn !== "") {
                const formattedDate = moment(dataResponse.updatedUser.lastUpdatedOn, "MMM D, YYYY, h:mm:ss A").format("DD/MM/YYYY");
                userProfileLastUpdatedOn.textContent = formattedDate;
            }
            if (country && dataResponse.updatedUser.country !== "") {
                let fiscalCodeInput = document.querySelector("#fiscalCodeInput");
                selectedCountry = dataResponse.updatedUser.country;
                if (selectedCountry.toLowerCase() === "it") {
                    fiscalCodeInput.classList.remove("d-none");
                    fiscalCodeInput.classList.add("d-block");
                } else {
                    fiscalCodeInput.classList.add("d-none");
                    fiscalCodeInput.classList.remove("d-block");
                }
                updateDropdownTextCountry()
            }
            if (profession && dataResponse.updatedUser.occupation !== "") {
                selectedProfession = dataResponse.updatedUser.occupation;
                updateDropdownTextProfession()
            }
            if (areaOfIntersts && dataResponse.updatedUser.registeredUserTopics.length > 0) {
                const valuesArray = Array.from(checkboxes).map(checkbox => checkbox.value);
                dataResponse.updatedUser.registeredUserTopics.forEach(topic => {
                    selectedItemsMultipleSelect.push(valuesArray[topic.topic.id - 1])
                    checkboxes[topic.topic.id - 1].checked = true;
                });
                updateDropdownTextMultiple();
            }
            if (privacyYes && dataResponse.updatedUser.profilingConsent === "1") {
                privacyYes.checked = true;
            }
            if (privacyNo && dataResponse.updatedUser.profilingConsent === "0") {
                privacyNo.checked = true;
            }
            if (dataProcessingYes && dataResponse.updatedUser.personalDataProcessingConsent === "1") {
                dataProcessingYes.checked = true;
            }
            if (dataProcessingNo && dataResponse.updatedUser.personalDataProcessingConsent === "0") {
                dataProcessingNo.checked = true;
            }
            if (newsletterYes && dataResponse.updatedUser.newsletterSubscription === "1") {
                newsletterYes.checked = true;
            }
            if (newsletterNo && dataResponse.updatedUser.newsletterSubscription === "0") {
                newsletterNo.checked = true;
            }
        }
    }

    setToken();

    // STYLE FUNCTIONS AND CUSTOM COMPONENT FUNCTIONS
    function toggleTab(tabName) {
        if (tabName === "userProfileTab") {
            selectedTab = "userProfileTab";
            passwordTab.classList.remove('active');
            userProfileTab.classList.add('active');
            userProfileComponent.classList.add('d-block');
            userProfileComponent.classList.remove('d-none');
            passwordComponent.classList.remove('d-block');
            passwordComponent.classList.add('d-none');
        }
        if (tabName === "passwordTab") {
            selectedTab = "passwordTab";
            userProfileTab.classList.remove('active');
            passwordTab.classList.add('active');
            userProfileComponent.classList.add('d-none');
            userProfileComponent.classList.remove('d-block');
            passwordComponent.classList.remove('d-none');
            passwordComponent.classList.add('d-block');
        }
    }

    dropdownButtonMultiple.addEventListener('click', () => {
        if (dropdownMenuInterests.classList.contains('d-block')) {
            dropdownMenuInterests.classList.add('d-none');
            dropdownMenuInterests.classList.remove('d-block');
        } else {
            dropdownMenuInterests.classList.remove('d-none');
            dropdownMenuInterests.classList.add('d-block');
        }
        displayButtonBorderBottom(dropdownButtonMultiple, dropdownMenuInterests);
    });

    document.addEventListener("click", function (event) {
        if (
            !dropdownButtonMultiple.contains(event.target) &&
            !dropdownMenuInterests.contains(event.target)
        ) {
            dropdownMenuInterests.classList.add('d-none');
            dropdownMenuInterests.classList.remove('d-block');
        }

        if (
            !dropdownButtonProfession.contains(event.target) &&
            !dropdownMenuProfession.contains(event.target)
        ) {
            dropdownMenuProfession.classList.add('d-none');
            dropdownMenuProfession.classList.remove('d-block');
        }

        if (
            !dropdownButtonCountry.contains(event.target) &&
            !dropdownMenuCountry.contains(event.target)
        ) {
            dropdownMenuCountry.classList.add('d-none');
            dropdownMenuCountry.classList.remove('d-block');
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
                !selectedItemsMultipleSelect.includes(checkbox.value)
            ) {
                selectedItemsMultipleSelect.push(checkbox.value);
            }

            //Remove item to selectedItems
            if (
                selectedItemsMultipleSelect.length <= 3 &&
                checkbox.checked === false &&
                selectedItemsMultipleSelect.includes(checkbox.value)
            ) {
                selectedItemsMultipleSelect = selectedItemsMultipleSelect.filter(
                    (item) => item !== checkbox.value
                );
            }

            // Disable every not selected checkbox if user select 3 elements
            checkboxes.forEach((element) => {
                if (
                    !selectedItemsMultipleSelect.includes(element.value) &&
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

    dropdownButtonProfession.addEventListener('click', () => {
        if (dropdownMenuProfession.classList.contains('d-block')) {
            dropdownMenuProfession.classList.add('d-none');
            dropdownMenuProfession.classList.remove('d-block');
        } else {
            dropdownMenuProfession.classList.remove('d-none');
            dropdownMenuProfession.classList.add('d-block');
        }
        displayButtonBorderBottom(dropdownButtonProfession, dropdownMenuProfession);
    });

    dropdownButtonCountry.addEventListener('click', () => {
        if (dropdownMenuCountry.classList.contains('d-block')) {
            dropdownMenuCountry.classList.add('d-none');
            dropdownMenuCountry.classList.remove('d-block');
        } else {
            dropdownMenuCountry.classList.remove('d-none');
            dropdownMenuCountry.classList.add('d-block');
        }
        displayButtonBorderBottom(dropdownButtonCountry, dropdownMenuCountry);
    });

    professionItems.forEach((element) => {
        element.addEventListener("click", function () {
            selectedProfession = element.textContent.trim();
            dropdownMenuProfession.classList.add('d-none');
            dropdownMenuProfession.classList.remove('d-block');
            displayButtonBorderBottom(
                dropdownButtonProfession,
                dropdownMenuProfession
            );
            updateDropdownTextProfession();
        });
    });

    countryItems.forEach((element) => {
        let fiscalCodeInput = document.querySelector("#fiscalCodeInput");
        element.addEventListener("click", function () {
            selectedCountry = element.textContent.trim();
            dropdownMenuCountry.classList.add("d-none");
            dropdownMenuCountry.classList.remove("d-block");
            displayButtonBorderBottom(dropdownButtonCountry, dropdownMenuCountry);
            updateDropdownTextCountry();

            if (selectedCountry.toLowerCase() === "it") {
                fiscalCodeInput.classList.remove("d-none");
                fiscalCodeInput.classList.add("d-block");
            } else {
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
        let innerString =
            selectedProfession === "" ? "Select Profession" : selectedProfession;
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
        if (menu.classList.contains('d-block')) {
            button.classList.add("border-bottom-0");
            button.classList.add("active-dropdown");
        } else {
            button.classList.remove("border-bottom-0");
            button.classList.remove("active-dropdown");
        }
    }

    function displayErrorsAlert(errorString) {
        if (errorAlert && errorString && errorString !== "") {
            if (errorAlert.classList.contains("d-none") && errorString) {
                errorAlert.innerHTML = errorString.slice(1, -1);
                errorAlert.classList.remove("d-none");
                errorAlert.classList.add("d-block");
            } else {
                errorAlert.classList.remove("d-block");
                errorAlert.classList.add("d-none");
            }
        }
    }

    function displaySuccessAlert() {
        if (successAlert) {
            if (successAlert.classList.contains("d-none")) {
                successAlert.classList.remove("d-none");
                successAlert.classList.add("d-block");
            } else {
                successAlert.classList.add("d-none");
                successAlert.classList.none("d-block");
            }
        }
    }

    // FORM DATA VALIDATION FUNCTIONS
    function validateProfession() {
        let errorElement = document.querySelector("#professionErrorString");
        let dropdownButton = document.querySelector("#dropdownProfessionMenuButtonUserProfile");

        if (selectedProfession === "") {
            erroeMessagges.push({
                id: "profession",
                message: Granite.I18n.get("mandatory_field"),
            });
            errorElement.innerHTML = Granite.I18n.get("mandatory_field");
            dropdownButton.classList.add("cmp-userprofileform__borderRed");
            dropdownButton.classList.remove("cmp-userprofileform__borderGreen");
        } else {
            dropdownButton.classList.remove("cmp-userprofileform__borderRed");
            dropdownButton.classList.add("cmp-userprofileform__borderGreen");
            errorElement.innerHTML = "";
        }
    }

    function validateCountry() {
        let errorElement = document.querySelector("#countryErrorString");
        let dropdownButton = document.querySelector("#dropdownCountryMenuButtonUserProfile");
        if (selectedCountry === "") {
            erroeMessagges.push({
                id: "country",
                message: Granite.I18n.get("mandatory_field"),
            });
            errorElement.innerHTML = Granite.I18n.get("mandatory_field");
            dropdownButton.classList.add("cmp-userprofileform__borderRed");
            dropdownButton.classList.remove("cmp-userprofileform__borderGreen");
        } else {
            dropdownButton.classList.remove("cmp-userprofileform__borderRed");
            dropdownButton.classList.add("cmp-userprofileform__borderGreen");
            errorElement.innerHTML = "";
        }
    }

    function validateInterests(data) {
        let errorElement = document.querySelector("#interestErrorString");
        let dropdownButton = document.querySelector(
            "#dropdownMultiselectMenuButtonUserProfile"
        );

        if (!data.areasOfInterest || data.areasOfInterest.length === 0) {
            erroeMessagges.push({
                id: "areasOfInterest",
                message: Granite.I18n.get("mandatory_field"),
            });
            errorElement.innerHTML = Granite.I18n.get("mandatory_field");
            dropdownButton.classList.add("cmp-userprofileform__borderRed");
            dropdownButton.classList.remove("cmp-userprofileform__borderGreen");
        } else {
            dropdownButton.classList.remove("cmp-userprofileform__borderRed");
            dropdownButton.classList.add("cmp-userprofileform__borderGreen");
            errorElement.innerHTML = "";
        }
    }

    function validatePassword(data) {
        let errorElement = document.querySelector("#newPasswordErrorString");

        if (
            (data.newPassword && data.newPassword !== data.newPasswordConfirmation) ||
            (data.newPassword && !data.newPasswordConfirmation)
        ) {
            errorElement.innerHTML = Granite.I18n.get("password_form_error");
            erroeMessagges.push({
                id: "newPassword",
                message: Granite.I18n.get("password_form_error"),
            });
        } else if (!data.newPassword) {
            errorElement.innerHTML = Granite.I18n.get("mandatory_field");
            erroeMessagges.push({
                id: "newPassword",
                message: Granite.I18n.get("mandatory_field"),
            });
        }
    }

    function validatePasswordConfirmation(data) {
        let errorElement = document.querySelector("#newPasswordConfirmationErrorString");
        if (
            (data.newPasswordConfirmation &&
                data.newPasswordConfirmation !== data.newPassword) ||
            (data.newPasswordConfirmation && !data.newPassword)
        ) {
            errorElement.innerHTML = Granite.I18n.get("password_form_error");
            erroeMessagges.push({
                id: "newPasswordConfirmation",
                message: Granite.I18n.get("password_form_error"),
            });
        } else if (!data.newPasswordConfirmation) {
            errorElement.innerHTML = Granite.I18n.get("mandatory_field");
            erroeMessagges.push({
                id: "newPasswordConfirmation",
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
                data.privacy && data.privacy === "no"
                    ? Granite.I18n.get("accept_privacy_edit_profile")
                    : Granite.I18n.get("mandatory_field");
        } else {
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

    function editUserDataValidation(tmpFormData) {
        validateProfession(tmpFormData);
        validateCountry(tmpFormData);
        validateInterests(tmpFormData);
        validateGdpr(tmpFormData);
        validateDataProcessing(tmpFormData);
        validateNewsLetter(tmpFormData);
    }

    // SUBMIT FORM USER PROFILE
    async function sendDataUserProfile(registrationData) {
        const token = localStorage.getItem('token') !== null ? localStorage.getItem('token') : sessionStorage.getItem('token');
        const responseCsrf = await fetch("/libs/granite/csrf/token.json");
        const csrfToken = await responseCsrf.json();
        const regResponse = await fetch("/private/api/user", {
            method: "POST",
            headers: {
                "CSRF-Token": csrfToken.token,
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(registrationData),
        });
        const dataResponse = await regResponse.json();
        return dataResponse;
    }

    let formUserProfile = document.querySelector("#userProfileForm");
    if (formUserProfile) {
        formUserProfile.addEventListener("submit", async (event) => {

            errorAlert.classList.remove('d-block');
            errorAlert.classList.add('d-none');
            successAlert.classList.remove('d-block');
            successAlert.classList.add('d-none');

            erroeMessagges = [];
            event.preventDefault();
            const formData = new FormData(formUserProfile);
            let tmpFormData = {
                profession: selectedProfession,
                country: selectedCountry,
                areasOfInterest: selectedItemsMultipleSelect.map((x) =>
                    x.replaceAll(" ", "")
                ),
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
                profession: tmpFormData.profession,
                phone: tmpFormData.telNumber,
                country: tmpFormData.country,
                taxIdCode: tmpFormData.fiscalCode ? tmpFormData.fiscalCode : null,
                interests: tmpFormData.areasOfInterest,
                gender: tmpFormData.gender,
                privacyConsent: tmpFormData.privacy === "yes" ? true : false,
                profilingConsent:
                    tmpFormData.personalDataProcessing === "yes" ? true : false,
                newsletterConsent:
                    tmpFormData.receiveNewsletter === "yes" ? true : false,
                linkedinProfile: tmpFormData.linkedinProfile ? tmpFormData.linkedinProfile : null
            };

            editUserDataValidation(tmpFormData);

            if (erroeMessagges.length === 0) {
                const responseReg = await sendDataUserProfile(registrationData);
                if (responseReg.cognitoSignUpErrorResponseDto) {
                    if (!errorAlert.classList.includes('d-block')) {
                        displayErrorsAlert(JSON.stringify(responseReg.cognitoSignUpErrorResponseDto.message));
                    }
                } else {
                    if (successAlert.classList.contains('d-none')) {
                        displaySuccessAlert();
                    }
                }
            }
        });
    }

    // SUBMIT FORM CHANGE PASSWORD
    let formPassword = document.querySelector("#passwordForm");
    if (formPassword) {
        formPassword.addEventListener("submit", async (event) => {
            erroeMessagges = [];
            event.preventDefault();
            const formData = new FormData(formPassword);
            let tmpFormData = {};

            for (let [key, value] of formData.entries()) {
                tmpFormData = {
                    ...tmpFormData,
                    [`${key}`]: value,
                };
            }

            let newPasswordData = {
                currentPassword: tmpFormData.currentPassword,
                newPassword: tmpFormData.newPassword,
                newPasswordConfirmation: tmpFormData.newPasswordConfirmation
            };

            validatePassword(tmpFormData);
            validatePasswordConfirmation(tmpFormData);

            if (erroeMessagges.length === 0) {
                const responseReg = await sendDataUserProfile(newPasswordData);
                if (responseReg.cognitoSignUpErrorResponseDto) {
                    displayErrorsAlert(JSON.stringify(responseReg.cognitoSignUpErrorResponseDto.message));
                }
            }
        });
    }
})