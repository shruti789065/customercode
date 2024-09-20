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

    resetPasswordEmailSubmit.addEventListener('click', () => {
        if (validateEmail()) {
            resetPasswordEmailForm.classList.remove('d-block');
            resetPasswordEmailForm.classList.add('d-none');
            resetPasswordForm.classList.remove('d-none');
            resetPasswordForm.classList.add('d-block');
        }
    })

    resetPasswordForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const formData = new FormData(resetPasswordForm);

        let tmpFormData = {
            code: formData.get('codeField'),
            password: formData.get('passwordField'),
            passwordConfirmation: formData.get('passwordConfirmationField')
        }

        let isCodeValid = validateCode(tmpFormData)
        let isPasswordValid = validatePassword(tmpFormData);
        let isPasswordConfirmationValid = validatePasswordConfirmation(tmpFormData);

        if (isCodeValid && isPasswordValid && isPasswordConfirmationValid) {
            // LOGIC FOR NEW PASSWORD VALIDATION AND REDIRECT GOES HERE
            return
        }

        return

    })

    function validateEmail() {
        let emailField = document.querySelector('#emailFieldResetPassword');
        if (emailField && emailField.value.toString().includes("@")) {
            return true;
        }
        return false
    }

    function validateCode(data) {
        const TMP_CODE = "123" // CHANGE THIS WITH THE CODE GIVEN BACK BY THE BACK END
        let errorElement = document.querySelector('#codeErrorString');
        if (!data.code) {
            errorElement.innerHTML = Granite.I18n.get('mandatory_field');
            return false
        } else if (data.code !== TMP_CODE) {
            errorElement.innerHTML = Granite.I18n.get('code_error');
            return false
        }
        return true;
    }

    function validatePassword(data) {
        let errorElement = document.querySelector('#passwordErrorString');
        if ((data.password && data.password !== data.passwordConfirmation) || (data.password && !data.passwordConfirmation)) {
            errorElement.innerHTML = Granite.I18n.get('password_form_error');
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
        } else if (!data.passwordConfirmation) {
            errorElement.innerHTML = Granite.I18n.get('mandatory_field');
            return false;
        }
        return true;
    }
})


async function sendData(logInData) {
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
    return dataResponse;
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

        if (responseSig.cognitoSignInErrorResponseDto) {
            showAlert(responseSig.cognitoSignInErrorResponseDto.message);
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