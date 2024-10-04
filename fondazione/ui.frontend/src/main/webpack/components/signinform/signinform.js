let signInForm = document.querySelector('#signInForm');
let alertBtn = document.querySelector('#alertBtn');

document.addEventListener('DOMContentLoaded', function () {
    let forgotPasswordToggle = document.querySelector('#forgotPasswordLink');
    let signinToggle = document.querySelector('#signinLink');
    let signinToggleReset = document.querySelector('#signinLinkReset');
    let resetPasswordEmailForm = document.querySelector('#resetPasswordEmailForm');
    let resetPasswordEmailSubmit = document.querySelector('#resetPasswordEmailSubmit');
    let resetPasswordForm = document.querySelector('#resetPasswordForm');
    let signupSection = document.querySelector('#signupSection');
    let resetPasswordCodeCta = document.querySelector('#resetPasswordCodeCta');
    let resetPasswordEmail = "";

    // TOGGLE FORMS
    forgotPasswordToggle.addEventListener('click', () => {
        signInForm.classList.remove('d-block');
        signInForm.classList.add('d-none');
        signupSection.classList.remove('d-block');
        signupSection.classList.add('d-none');
        resetPasswordEmailForm.classList.add('d-block');
        resetPasswordEmailForm.classList.remove('d-none');
    });

    signinToggle.addEventListener('click', () => {
        signInForm.classList.add('d-block');
        signInForm.classList.remove('d-none');
        resetPasswordEmailForm.classList.remove('d-block');
        resetPasswordEmailForm.classList.add('d-none');
        signupSection.classList.remove('d-none');
        signupSection.classList.add('d-block');
    })

    signinToggleReset.addEventListener('click', () => {
        signInForm.classList.add('d-block');
        signInForm.classList.remove('d-none');
        resetPasswordForm.classList.remove('d-block');
        resetPasswordForm.classList.add('d-none');
        signupSection.classList.remove('d-none');
        signupSection.classList.add('d-block');
    })

    function backToSigninPage() {
        resetPasswordForm.classList.remove('d-block');
        resetPasswordForm.classList.add('d-none');
        signInForm.classList.add('d-block');
        signInForm.classList.remove('d-none');
        signupSection.classList.remove('d-none');
        signupSection.classList.add('d-block');
    }

    function displayEmailForm() {
        resetPasswordEmailForm.classList.remove('d-block');
        resetPasswordEmailForm.classList.add('d-none');
        resetPasswordForm.classList.remove('d-none');
        resetPasswordForm.classList.add('d-block');
    }

    // DATA VALIDATION
    function validateEmail() {
        let emailField = document.querySelector('#emailFieldResetPassword');
        let errorElement = document.querySelector('#emailErrorString');

        if (emailField && !emailField.value.toString().includes("@")) {
            errorElement.innerHTML = Granite.I18n.get('email_not_valid');
            return false;
        } else if (!emailField || emailField.value === "") {
            errorElement.innerHTML = Granite.I18n.get('mandatory_field');
            return false;
        }
        return true;
    }

    function validateCode(data) {
        let errorElement = document.querySelector('#codeErrorString');
        if (!data.code) {
            errorElement.innerHTML = Granite.I18n.get('mandatory_field');
            return false
        }
        return true;
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

    // ERROR HANDLING
    function displayErrorsAlertEmailForm(errorString) {
        let errorAlert = document.querySelector("#alertMessageEmailForm");
        let alertBoxEmailForm = document.querySelector("#alertBoxEmailForm");
        if (errorAlert && errorString && errorString !== "") {
            if (errorAlert.classList.contains("d-none")) {
                errorAlert.innerHTML = errorString;
                alertBoxEmailForm.classList.remove("d-none");
                alertBoxEmailForm.classList.add("d-block");
                errorAlert.classList.remove("d-none");
                errorAlert.classList.add("d-block");
            }
        }
    }

    function hideErrorsAlertEmailForm() {
        let errorAlert = document.querySelector("#alertMessageEmailForm");
        let alertBoxEmailForm = document.querySelector("#alertBoxEmailForm");
        errorAlert.classList.remove("d-block");
        errorAlert.classList.add("d-none");
        alertBoxEmailForm.classList.remove("d-block");
        alertBoxEmailForm.classList.add("d-none");
    }

    function displayErrorsAlertCodeForm(errorString) {
        let errorAlert = document.querySelector("#alertMessageCodeForm");
        let alertBocCodeForm = document.querySelector("#alertBoxCodeForm");
        if (errorAlert && errorString && errorString !== "") {
            if (errorAlert.classList.contains("d-none")) {
                errorAlert.innerHTML = errorString;
                alertBocCodeForm.classList.remove("d-none");
                alertBocCodeForm.classList.add("d-block");
                errorAlert.classList.remove("d-none");
                errorAlert.classList.add("d-block");
            }
        }
    }

    function hideErrorsAlertCodeForm() {
        let errorAlert = document.querySelector("#alertMessageCodeForm");
        let alertBocCodeForm = document.querySelector("#alertBoxCodeForm");
        errorAlert.classList.remove("d-block");
        errorAlert.classList.add("d-none");
        alertBocCodeForm.classList.remove("d-block");
        alertBocCodeForm.classList.add("d-none");
    }

    function resetErrorMessages() {
        let errorElementPasswordConfirmation = document.querySelector('#passwordConfirmationErrorString');
        let errorElementPassword = document.querySelector('#passwordErrorString');
        let errorElementCode = document.querySelector('#codeErrorString');
        if (errorElementPasswordConfirmation) { errorElementPasswordConfirmation.innerHTML = "" }
        if (errorElementPassword) { errorElementPassword.innerHTML = "" }
        if (errorElementCode) { errorElementCode.innerHTML = "" }
    }

    // FORM SUBMISSION
    if (resetPasswordEmailForm) {
        resetPasswordEmailForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const formData = new FormData(resetPasswordEmailForm);
            let tmpFormData = {
                email: formData.get('emailFieldResetPassword')
            }
            resetPasswordEmail = tmpFormData.email;
            hideErrorsAlertEmailForm();
            const responseCsrf = await fetch("/libs/granite/csrf/token.json");
            const csrfToken = await responseCsrf.json();
            if (validateEmail()) {
                resetPasswordEmailSubmit.disabled = true;
                let loader = document.querySelector("#formEmailLoader");
                loader.classList.remove("d-none");
                loader.classList.add("d-block");
                try {
                    const signIResponse = await fetch("/bin/api/forgetPassword", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json", // Aggiunto Content-Type
                            "CSRF-Token": csrfToken.token,
                        },
                        body: JSON.stringify(tmpFormData)
                    });
                    const responseData = await signIResponse.json();
                    if (!signIResponse.ok) {
                        displayErrorsAlertEmailForm(Granite.I18n.get('error_sanding_code'));
                    } else {
                        displayEmailForm();
                    }
                } catch (error) {
                    console.log("ERROR: ", error);
                } finally {
                    resetPasswordEmailSubmit.disabled = false;
                    loader.classList.remove("d-block");
                    loader.classList.add("d-none");
                }
            }
        })
    }

    if (resetPasswordForm) {
        resetPasswordForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            resetErrorMessages();
            hideErrorsAlertCodeForm();
            const formData = new FormData(resetPasswordForm);
            let tmpFormDataValidation = {
                code: formData.get('codeField'),
                password: formData.get('passwordField'),
                passwordConfirmation: formData.get('passwordConfirmationField')
            }
            let isCodeValid = validateCode(tmpFormDataValidation);
            let isPasswordValid = validatePassword(tmpFormDataValidation);
            let isPasswordConfirmationValid = validatePasswordConfirmation(tmpFormDataValidation);
            let tmpFormData = {
                email: resetPasswordEmail,
                confirmCode: formData.get('codeField'),
                password: formData.get('passwordField'),
            }
            const responseCsrf = await fetch("/libs/granite/csrf/token.json");
            const csrfToken = await responseCsrf.json();
            if (isPasswordValid && isPasswordConfirmationValid && isCodeValid) {
                try {
                    let loaderCode = document.querySelector("#formCodeLoader");
                    loaderCode.classList.remove("d-none");
                    loaderCode.classList.add("d-block");
                    resetPasswordCodeCta.disabled = true;
                    const signIResponse = await fetch("/bin/api/confirmForgetPassword", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            "CSRF-Token": csrfToken.token,
                        },
                        body: JSON.stringify(tmpFormData)
                    });
                    if (!signIResponse.ok) {
                        displayErrorsAlertCodeForm("Wrong code or password");
                    } else {
                        backToSigninPage();
                    }
                } catch (error) {
                    console.log("ERROR: ", error);
                } finally {
                    resetPasswordCodeCta.disabled = false;
                    loaderCode.classList.remove("d-block");
                    loaderCode.classList.add("d-none");
                }
            }
        })
    }
})

async function sendData(logInData) {
    let submitBtn = document.querySelector("#submitBtnSignin");
    let loader = document.querySelector("#signinLoader");
    submitBtn.disabled = true;
    resetLocalStorage();
    try {
        loader.classList.remove("d-none");
        loader.classList.add("d-block");
        const responseCsrf = await fetch('/libs/granite/csrf/token.json');
        const csrfToken = await responseCsrf.json();
        const signIResponse = await fetch("/bin/api/awsSignIn", {
            method: "POST",
            headers: {
                'CSRF-Token': csrfToken.token
            },
            body: JSON.stringify(logInData)
        });
        const dataResponse = await signIResponse.json();
        if (dataResponse.AuthenticationResult?.AccessToken) {
            localStorage.setItem("accessToken", dataResponse.AuthenticationResult?.AccessToken);
        }
        return dataResponse;
    } catch (error) {
        console.log("ERROR: ", error);
    } finally {
        submitBtn.disabled = false;
        loader.classList.remove("d-block");
        loader.classList.add("d-none");
    }
}

function resetLocalStorage() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("token");
    localStorage.removeItem("refresh_token");
    localStorage.removeItem("remember_me");
}

function showAlert(message) {
    let alert = $("#alertBox");
    if (alert) {
        $("#alertMessage").html('<div>' + message + '</div>');
        alert.show();
    }

}

function updateTokens(signInResponse, rememberCheck) {
    localStorage.setItem("remember_me", rememberCheck);
    if (rememberCheck) {
        localStorage.setItem("token", signInResponse.AuthenticationResult.IdToken);
        localStorage.setItem("refresh_token", signInResponse.AuthenticationResult.RefreshToken);
    } else {
        const ttl = signInResponse.AuthenticationResult.ExpiresIn;
        const item = {
            token: signInResponse.AuthenticationResult.IdToken,
            expiry: ttl,
        };
        localStorage.setItem("token", JSON.stringify(item));
    }
}

if (alertBtn) {
    alertBtn.addEventListener("click", (event) => {
        let alert = $("#alertBox");
        if (alert) {
            alert.hide();
        }
    })
}

if (signInForm) {
    signInForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        let emailForm = document.querySelector("#emailField");
        let passForm = document.querySelector("#passField");
        let rememberCheck = document.querySelector("#rememberCheck");

        let singIdData = {
            email: emailForm.value,
            password: passForm.value,
        }
        const responseSig = await sendData(singIdData);

        if (responseSig.cognitoSignInErrorResponseDto?.message) {
            showAlert(responseSig.cognitoSignInErrorResponseDto?.message !== undefined ? responseSig.cognitoSignInErrorResponseDto.message : "Error");
        } else {
            updateTokens(responseSig, rememberCheck.checked);
            const redirectUrl = sessionStorage.getItem('redirectUrl');
            document.location.href = redirectUrl;
        }
    });
}

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