let signInForm = document.querySelector("#signInForm");
let alertBtn = document.querySelector("#alertBtn");

async function sendData(logInData) {
  const responseCsrf = await fetch("/libs/granite/csrf/token.json");
  const csrfToken = await responseCsrf.json();
  console.log("CSRF-Token:", csrfToken);
  const signIResponse = await fetch("/bin/api/awsSignIn", {
    method: "POST",
    headers: {
      "CSRF-Token": csrfToken.token,
    },
    body: JSON.stringify(logInData),
  });
  const dataResponse = await signIResponse.json();
  console.log(JSON.stringify(dataResponse, null, 3));
  return dataResponse;
}

function showAlert(message) {
  let alert = $("#alertBox");
  if (alert) {
    console.log("alert message: ", message);
    $("#alertMessage").html("<strong>" + message + "</strong>");
    alert.show();
  }
}

function updateTokens(signInResponse, rememberCheck) {
  console.log("remember me ?", rememberCheck);
  if (rememberCheck) {
    console.log("local storage");
    localStorage.setItem("token", signInResponse.AuthenticationResult.IdToken);
    localStorage.setItem(
      "refresh_token",
      signInResponse.AuthenticationResult.RefreshToken
    );
  } else {
    console.log("session storage");
    sessionStorage.setItem(
      "token",
      signInResponse.AuthenticationResult.IdToken
    );
  }
}

if (alertBtn) {
  alertBtn.addEventListener("click", () => {
    let alert = $("#alertBox");
    if (alert) {
      alert.hide();
    }
  });
}

if (signInForm) {
  signInForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    let emailForm = document.querySelector("#emailField");
    let passForm = document.querySelector("#passField");
    let rememberCheck = document.querySelector("#rememberCheck");
    console.log("remember: ", rememberCheck.checked);
    let singIdData = {
      email: emailForm.value,
      password: passForm.value,
    };
    const responseSig = await sendData(singIdData);
    console.log("SIGN IN RESPONSE: ", responseSig);
    if (responseSig.cognitoSignInErrorResponseDto) {
      showAlert(responseSig.cognitoSignInErrorResponseDto.message);
    } else {
      updateTokens(responseSig, rememberCheck.checked);
      //document.location.href = "/";
    }
  });
}
