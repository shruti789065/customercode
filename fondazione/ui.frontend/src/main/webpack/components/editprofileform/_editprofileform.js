// import moment from 'moment';
// console.log("Edit Profile Form Component Loaded");

document.addEventListener('DOMContentLoaded', function () {
    
    let selectedTab = "";
    let successAlert = document.querySelector("#cmp-editprofileform__successAlert");
    let errorAlert = document.querySelector("#cmp-editprofileform__errorsAlert");
    let userProfileTab = document.querySelector('#userProfileTab');
    let passwordTab = document.querySelector('#passwordTab');
    let userProfileComponent = document.querySelector('#userProfileComponent');
    let loaderUserData = document.querySelector('#loaderUserDataComponent');
    let passwordComponent = document.querySelector('#passwordComponent');
    let goToLoginButton = document.querySelector('#cmp-editprofileform__modal a');
    userProfileTab.addEventListener('click', () => toggleTab('userProfileTab'));
    passwordTab.addEventListener('click', () => toggleTab('passwordTab'));

    const dropdownButtonMultiple = document.querySelector("#dropdownMultiselectMenuButtonUserProfile");
    const dropdownMenuInterests = document.querySelector("#interestListUserProfile");
    const checkboxes = dropdownMenuInterests.querySelectorAll('input[type="checkbox"]');
    let selectedItemsMultipleSelect = [];
    let selectedTopicsIds = [];

    const dropdownButtonProfession = document.querySelector("#dropdownProfessionMenuButtonUserProfile");
    const dropdownMenuProfession = document.querySelector("#professionListUserProfile");
    const professionItems = document.querySelectorAll("#professionListUserProfile li div");
    let selectedProfession = "";

    const dropdownButtonCountry = document.querySelector("#dropdownCountryMenuButtonUserProfile");
    const dropdownMenuCountry = document.querySelector("#countryListUserProfile");
    const countryItems = document.querySelectorAll("#countryListUserProfile li div");
    let selectedCountry = "";
    let selectedCountryId = "";

    goToLoginButton?.addEventListener('click', () => {
        hideRdirectModal();
    });

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
    let taxIdCode = document.querySelector('#taxIdCode');
    let fiscalCode = document.querySelector('#fiscalCodeInput');
    let isUserLoggedIn = false;



    // FILL FORM WITH USER DATA
    async function fillForm() {
        userProfileComponent.classList.add('d-none');
        userProfileComponent.classList.remove('d-block');
        loaderUserData.classList.add('d-block');
        loaderUserData.classList.remove('d-none');
        try {
            const responseCsrf = await fetch("/libs/granite/csrf/token.json");
            const csrfToken = await responseCsrf.json();
            const regResponse = await fetch("/private/api/isSignIn", {
                method: "GET",
                headers: {
                    "CSRF-Token": csrfToken.token,
                },
            });
            if (regResponse.status === 200) {
                isUserLoggedIn = true;
            }
        } catch (error) {
            console.log("Error: ", error);
        }

        if (isUserLoggedIn) {
            const responseCsrf = await fetch("/libs/granite/csrf/token.json");
            const csrfToken = await responseCsrf.json();
            const regResponse = await fetch("/private/api/user", {
                method: "GET",
                headers: {
                    "CSRF-Token": csrfToken.token,
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
                if (linkedinProfile && dataResponse.updatedUser.linkedinProfile && dataResponse.updatedUser.linkedinProfile !== "") {
                    linkedinProfile.value = dataResponse.updatedUser.linkedinProfile;
                }
                if (taxIdCode && dataResponse.updatedUser.taxIdCode && dataResponse.updatedUser.taxIdCode !== "") {
                    taxIdCode.value = dataResponse.updatedUser.taxIdCode;
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
                    const formattedDate = moment(dataResponse.updatedUser.createdOn).format("L");
                    userProfileCreationDate.textContent = formattedDate;
                }
                if (userProfileLastUpdatedOn && dataResponse.updatedUser.lastUpdatedOn !== "") {
                    const formattedDate = moment(dataResponse.updatedUser.lastUpdatedOn).format("L");
                    userProfileLastUpdatedOn.textContent = formattedDate;
                }
                if (country && dataResponse.updatedUser.country !== "") {
                    let fiscalCodeInput = document.querySelector("#taxIdCode");
                    let fiscalCodeTitle = document.querySelector("#fiscalCodeUserProfileTitle");
                    countryItems.forEach((element) => {
                        if (dataResponse.updatedUser.country === element.getAttribute("data-country-id")) {
                            selectedCountry = element.textContent.trim();
                            selectedCountryId = element.getAttribute("data-country-id");
                        }
                    });
                    if (selectedCountryId === "1") {
                        fiscalCodeInput.classList.remove("d-none");
                        fiscalCodeInput.classList.add("d-block");
                        fiscalCodeTitle.classList.add("d-block");
                        fiscalCodeTitle.classList.remove("d-none");
                    } else {
                        fiscalCodeInput.classList.add("d-none");
                        fiscalCodeInput.classList.remove("d-block");
                        fiscalCodeTitle.classList.add("d-none");
                        fiscalCodeTitle.classList.remove("d-block");
                    }
                    updateDropdownTextCountry()
                }
                if (profession && dataResponse.updatedUser.occupation !== "") {
                    selectedProfession = dataResponse.updatedUser.occupation;

                    if (selectedProfession === Granite.I18n.get("no_healthcare")) {
                        selectedItemsMultipleSelect = [];
                        selectedTopicsIds = [];
                        updateDropdownTextMultiple();
                        checkboxes.forEach(checkbox => {
                            if (checkbox.checked) {
                                checkbox.checked = false;
                                checkbox.disabled = false;
                            }
                        });
                    }


                    updateDropdownTextProfession()
                }
                if (areaOfIntersts && dataResponse?.updatedUser?.registeredUserTopics?.length > 0) {
                    dataResponse.updatedUser.registeredUserTopics.forEach(topic => {
                        checkboxes.forEach(checkbox => {
                            if (checkbox.dataset.topicId === topic.topic.id) {
                                checkbox.checked = true;
                                checkbox.disabled = false;
                                selectedItemsMultipleSelect.push(checkbox.dataset.topicName)
                                selectedTopicsIds.push(topic.topic.id);
                            }
                        })

                        checkboxes.forEach((element) => {
                            if (element.checked === false && dataResponse.updatedUser.dataResponse?.updatedUser?.registeredUserTopics?.length >= 3) {
                                element.disabled = true;
                            }
                        })
                    })
                    updateDropdownTextMultiple();
                }
                if (privacyYes && dataResponse.updatedUser.personalDataProcessingConsent === "1") {
                    privacyYes.checked = true;
                }
                if (privacyNo && dataResponse.updatedUser.personalDataProcessingConsent === "0") {
                    privacyNo.checked = true;
                }
                if (dataProcessingYes && dataResponse.updatedUser.profilingConsent === "1") {
                    dataProcessingYes.checked = true;
                }
                if (dataProcessingNo && dataResponse.updatedUser.profilingConsent === "0") {
                    dataProcessingNo.checked = true;
                }
                if (newsletterYes && dataResponse.updatedUser.newsletterSubscription === "1") {
                    newsletterYes.checked = true;
                }
                if (newsletterNo && dataResponse.updatedUser.newsletterSubscription === "0") {
                    newsletterNo.checked = true;
                }
            }
            userProfileComponent.classList.add('d-block');
            userProfileComponent.classList.remove('d-none');
            loaderUserData.classList.add('d-none');
            loaderUserData.classList.remove('d-block');
        }

    }

    fillForm();

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
                selectedTopicsIds.forEach((element, index) => {
                    if (element === checkbox.dataset.topicId) {
                        selectedTopicsIds.splice(index, 1);
                    }
                })
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
        let fiscalCodeInput = document.querySelector("#taxIdCode");
        let fiscalCodeErrorString = document.querySelector("#fiscalCodeErrorString");
        element.addEventListener("click", function () {
            let currestSelectedCountryId = element.getAttribute("data-country-id");
            let userProfileFiscalCodeTitle = document.querySelector("#fiscalCodeUserProfileTitle");
            selectedCountry = element.textContent.trim();

            dropdownMenuCountry.classList.add("d-none");
            dropdownMenuCountry.classList.remove("d-block");
            selectedCountryId = element.getAttribute("data-country-id");
            displayButtonBorderBottom(dropdownButtonCountry, dropdownMenuCountry);
            updateDropdownTextCountry();

            if (currestSelectedCountryId.toLowerCase() === "1") {
                fiscalCodeInput.classList.remove("d-none");
                fiscalCodeInput.classList.add("d-block");
                fiscalCodeErrorString.classList.add("d-block");
                fiscalCodeErrorString.classList.remove("d-none");
                userProfileFiscalCodeTitle.classList.add("d-block");
                userProfileFiscalCodeTitle.classList.remove("d-none");
            } else {
                fiscalCodeInput.classList.add("d-none");
                fiscalCodeInput.classList.remove("d-block");
                userProfileFiscalCodeTitle.classList.add("d-none");
                userProfileFiscalCodeTitle.classList.remove("d-block");
                fiscalCodeErrorString.classList.add("d-none");
                fiscalCodeErrorString.classList.remove("d-block");
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
        let areaOfInterestsElement = document.querySelector("#areaOfInterestsComponentUserProfile");
        if (selectedProfession === Granite.I18n.get("no_healthcare")) {
            areaOfInterestsElement.classList.add("d-none");
            areaOfInterestsElement.classList.remove("d-block");
            selectedItemsMultipleSelect = [];
            selectedTopicsIds = [];
            updateDropdownTextMultiple();
            checkboxes.forEach(checkbox => {
                if (checkbox.checked) {
                    checkbox.checked = false;
                    checkbox.disabled = false;
                }
            });
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

    function hideErrorAlertChangePassword() {
        let element = document.querySelector("#errorAlertChangePassword");
        element.classList.remove("d-block");
        element.classList.add("d-none");
    }

    function displaySuccessAlertChangePassword() {
        let element = document.querySelector("#successAlertChangePassword");
        element.classList.remove("d-none");
        element.classList.add("d-block");
    }

    function hideSuccessAlertChangePassword() {
        let element = document.querySelector("#successAlertChangePassword");
        element.classList.remove("d-block");
        element.classList.add("d-none");
    }

    function displayErrorAlertChangePassword() {
        let element = document.querySelector("#errorAlertChangePassword");
        element.classList.remove("d-none");
        element.classList.add("d-block");
    }

    function showRedirectModal() {
        let modal = document.querySelector('#cmp-editprofileform__modal .modal');
        let overlay = document.querySelector("#cmp-editprofileform__overlay");
        modal.classList.add("d-block");
        modal.classList.remove("d-none");
        overlay.classList.add("d-block");
        overlay.classList.remove("d-none");
    }

    function hideRdirectModal() {
        let modal = document.querySelector('#cmp-editprofileform__modal .modal');
        let overlay = document.querySelector("#cmp-editprofileform__overlay");
        modal.classList.add("d-none");
        modal.classList.remove("d-block");
        overlay.classList.add("d-none");
        overlay.classList.remove("d-block");
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

        if ((!data.areasOfInterest || data.areasOfInterest.length === 0) && selectedProfession !== Granite.I18n.get("no_healthcare")) {
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
        let errorElement = document.querySelector('#passwordErrorString');
        if ((data.password && data.password !== data.passwordConfirmation) || (data.password && !data.passwordConfirmation)) {
            errorElement.innerHTML = Granite.I18n.get('password_form_error');
            return false;
        } else if (data.password && data.password.length < 8) {
            errorElement.innerHTML = Granite.I18n.get('password_length_error');
            return false;
        } else if (data.password && !containsNumber(data.password)) {
            errorElement.innerHTML = Granite.I18n.get('password_number_error');
            return false;
        } else if (data.password && !containsSpecialCharacter(data.password)) {
            errorElement.innerHTML = Granite.I18n.get('password_special_character_error');
            return false;
        } else if (data.password && !containsUppercase(data.password)) {
            errorElement.innerHTML = Granite.I18n.get('password_uppercase_error');
            return false;
        } else if (data.password && !containsLowercase(data.password)) {
            errorElement.innerHTML = Granite.I18n.get('password_lowercase_error');
            return false;
        } else if (!data.password) {
            errorElement.innerHTML = Granite.I18n.get('mandatory_field');
            return false
        }
        return true
    }

    function validatePasswordConfirmation(data) {
        let errorElement = document.querySelector('#passwordConfirmationErrorString');
        if ((data.passwordConfirmation && data.passwordConfirmation !== data.password) || (data.passwordConfirmation && !data.password)) {
            errorElement.innerHTML = Granite.I18n.get('password_form_error');
            return false
        } else if (data.passwordConfirmation && data.passwordConfirmation.length < 8) {
            errorElement.innerHTML = Granite.I18n.get('password_length_error');
            return false;
        } else if (data.passwordConfirmation && !containsNumber(data.passwordConfirmation)) {
            errorElement.innerHTML = Granite.I18n.get('password_number_error');
            return false;
        } else if (data.passwordConfirmation && !containsSpecialCharacter(data.passwordConfirmation)) {
            errorElement.innerHTML = Granite.I18n.get('password_special_character_error');
            return false;
        } else if (data.passwordConfirmation && !containsUppercase(data.passwordConfirmation)) {
            errorElement.innerHTML = Granite.I18n.get('password_uppercase_error');
            return false;
        } else if (data.passwordConfirmation && !containsLowercase(data.passwordConfirmation)) {
            errorElement.innerHTML = Granite.I18n.get('password_lowercase_error');
            return false;
        } else if (!data.passwordConfirmation) {
            errorElement.innerHTML = Granite.I18n.get('mandatory_field');
            return false;
        }
        return true;
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
        validateFiscalCode(tmpFormData)
    }

    function containsNumber(str) {
        const regex = /\d/;
        return regex.test(str);
    }

    function containsSpecialCharacter(str) {
        const regex = /[^a-zA-Z0-9]/;
        return regex.test(str);
    }

    function containsUppercase(str) {
        const regex = /[A-Z]/;
        return regex.test(str);
    }

    function containsLowercase(str) {
        const regex = /[a-z]/;
        return regex.test(str);
    }

    function resetErrorMessagesPasswordForm() {
        let passwordError = document.querySelector("#passwordErrorString");
        let passwordConfirmationError = document.querySelector("#passwordConfirmationErrorString");
        passwordError.innerHTML = "";
        passwordConfirmationError.innerHTML = "";
    }

    function validateFiscalCode(data) {
        let errorElement = document.querySelector("#fiscalCodeErrorString");
        if (selectedCountry.toLowerCase() === "1" && (!data.taxIdCode || data.taxIdCode === "")) {
            errorElement.classList.add("d-block");
            errorElement.classList.remove("d-none");
            erroeMessagges.push({
                id: "taxIdCode",
                message: Granite.I18n.get("mandatory_field"),
            });
            errorElement.innerHTML = Granite.I18n.get("mandatory_field");
        } else {
            errorElement.classList.remove("d-block");
            errorElement.classList.add("d-none");
            errorElement.innerHTML = "";
        }
    }

    // SUBMIT FORM USER PROFILE
    async function sendDataUserProfile(registrationData) {
        let ctaSave = document.querySelector("#ctaSaveUserProfile");
        let loader = document.querySelector("#userProfileLoader");
        try {
            ctaSave.disabled = true;
            loader.classList.remove("d-none");
            loader.classList.add("d-block");
            const responseCsrf = await fetch("/libs/granite/csrf/token.json");
            const csrfToken = await responseCsrf.json();
            const regResponse = await fetch("/private/api/user", {
                method: "POST",
                headers: {
                    "CSRF-Token": csrfToken.token,
                },
                body: JSON.stringify(registrationData),
            });
            const dataResponse = await regResponse.json();
            try {
                const checkUserLogged = await fetch("/private/api/isSignIn", {
                    method: "GET",
                    headers: {
                        "CSRF-Token": csrfToken.token,
                    },
                });
                if (checkUserLogged.status !== 200) {
                    isUserLoggedIn = true;
                    showRedirectModal();
                }
            } catch (error) {
                console.log("Error: ", error);
            }
            return dataResponse;
        } catch (error) {
            console.log(error);
        } finally {
            ctaSave.disabled = false;
            loader.classList.add("d-none");
            loader.classList.remove("d-block");
        }
    }

    async function sendDataPassword(newPasswordData) {
        let ctaSave = document.querySelector("#ctaSavePassword");
        let loader = document.querySelector("#passwordLoader");
        ctaSave.disabled = true;
        try {
            loader.classList.remove("d-none");
            loader.classList.add("d-block");
            const responseCsrf = await fetch("/libs/granite/csrf/token.json");
            const csrfToken = await responseCsrf.json();
            const regResponse = await fetch("/private/api/resetPassword", {
                method: "POST",
                headers: {
                    "CSRF-Token": csrfToken.token,
                },
                body: JSON.stringify(newPasswordData),
            });
            const dataResponse = await regResponse.json();

            if (dataResponse.success === true) {
                displaySuccessAlertChangePassword();
                hideErrorAlertChangePassword();
            } else {
                displayErrorAlertChangePassword();
                hideSuccessAlertChangePassword();
            }
            return dataResponse;
        } catch (error) {
            console.error('Error:', error);
        } finally {
            ctaSave.disabled = false;
            loader.classList.add("d-none");
            loader.classList.remove("d-block");
        }
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
                country: selectedCountryId,
                areasOfInterest: selectedProfession !== Granite.I18n.get("no_healthcare") ? selectedTopicsIds : []
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
                taxIdCode: tmpFormData.taxIdCode && selectedCountryId === "1" ? tmpFormData.taxIdCode : "",
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
            hideErrorAlertChangePassword();
            hideSuccessAlertChangePassword();
            resetErrorMessagesPasswordForm();
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
                PreviousPassword: tmpFormData.currentPassword,
                ProposedPassword: tmpFormData.password,
            };

            let isPasswordValid = validatePassword(tmpFormData);
            let isPasswordConfirmationValid = validatePasswordConfirmation(tmpFormData);

            if (isPasswordValid && isPasswordConfirmationValid) {
                sendDataPassword(newPasswordData);
            }
        });
    }
})