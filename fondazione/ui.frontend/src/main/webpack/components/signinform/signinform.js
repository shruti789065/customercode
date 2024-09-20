let signInForm = document.querySelector('#signInForm');
let alertBtn = document.querySelector('#alertBtn');


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