document.addEventListener("DOMContentLoaded", async function () {
    const loadImageCta = document.getElementById('loadImageCta');
    const fileInputEditImage = document.getElementById('fileInputEditImage');
    const errorMessage = document.getElementById('errorMessage');
    let imgWrapper = document.querySelector('#editImageImgWrapper');
    let ctaSave = document.querySelector('#ctaSaveEditImage');
    let loader = document.querySelector('#editImageLoader');
    let image = document.querySelector('#editImageImg');
    let file = null;
    let errorMessagges = [];

    getUserProfileImage();

    let isUserLoggedIn = false;

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
        let userData = dataResponse;
        let userNameField = document.querySelector('#editImageUserName');
        let userEmailField = document.querySelector('#editImageUserEmail');
        if (userData && userEmailField && userNameField) {
            userEmailField.textContent = userData.updatedUser.email;
            userNameField.textContent = userData.updatedUser.firstname + " " + userData.updatedUser.lastname
        }
    }

    loadImageCta?.addEventListener('click', function () {
        if (fileInputEditImage) {
            fileInputEditImage.click();
        }
    });

    fileInputEditImage?.addEventListener('change', async function (event) {
        let successMessage = document.querySelector('#cmp-editImage__successAlert');
        let errorMessageComponent = document.querySelector('#cmp-editImage__errorsAlert');
        let errorMessageList = document.querySelector('#cmp-editImage__errorsList');
        errorMessagges = [];

        if (successMessage && errorMessageComponent && errorMessageList) {
            successMessage.classList.add('d-none');
            successMessage.classList.remove('d-block');
            errorMessageComponent.classList.add('d-none');
            errorMessageComponent.classList.remove('d-block');
            errorMessageList.innerHTML = "";
        }

        file = event.target.files[0];

        if (file) {
            const chechSize = checkFileSize(file);
            const checkType = checkFileType(file);
            const checkDimension = await validateImageDimensions(file, errorMessage);
            if (chechSize && checkType && checkDimension && errorMessagges.length === 0 && ctaSave) {
                ctaSave.disabled = false;
                showHideLoadedImg(file);
            } else {
                if (errorMessageComponent && errorMessageList) {
                    errorMessageComponent.classList.add('d-block');
                    errorMessageComponent.classList.remove('d-none');
                    errorMessagges.forEach((message) => {
                        let li = document.createElement('li');
                        li.textContent = message;
                        errorMessageList.appendChild(li);
                    })
                }
            }
        }
    });

    if (ctaSave) {
        ctaSave.disabled = true;
        ctaSave.addEventListener('click', () => postUserProfileImage(file));
    }

    function fileToBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            if (reader) {
                reader.onload = () => resolve(reader.result);
                reader.onerror = error => reject(error);
                reader.readAsDataURL(file);
            }
        });
    }

    function checkFileSize(file) {
        if (file.size > 250 * 1024 && fileInputEditImage) { // 250KB                   
            errorMessagges.push(Granite.I18n.get("error_image_size"));
            fileInputEditImage.value = "";
            return false;
        }
        return true;
    }

    function checkFileType(file) {
        const validFormats = ["image/jpeg", "image/png"];
        if (!validFormats.includes(file.type) && fileInputEditImage) {
            errorMessagges.push(Granite.I18n.get("error_image_format"));
            fileInputEditImage.value = "";
            return false;
        }
        return true;
    }

    function validateImageDimensions(file, errorMessage) {
        return new Promise((resolve) => {
            const img = new Image();
            if (img) {
                img.onload = function () {
                    if (img.width < 100 || img.width > 512 || img.height < 100 || img.height > 512) {
                        errorMessagges.push(Granite.I18n.get("error_image_dimension"));
                        resolve(false);
                    } else {
                        resolve(true);
                    }
                };
                img.src = URL.createObjectURL(file);
            }
        });
    }

    function showHideLoadedImg(file) {
        if (imgWrapper && image) {
            image.src = URL.createObjectURL(file);
            imgWrapper.classList.add('d-block');
            imgWrapper.classList.remove('d-none');
        }
    }

    async function getUserProfileImage() {
        const responseCsrf = await fetch("/libs/granite/csrf/token.json");
        const csrfToken = await responseCsrf.json();
        const regResponse = await fetch("/private/api/profile/image", {
            method: "GET",
            headers: {
                "CSRF-Token": csrfToken.token,
            },
        });
        const dataResponse = await regResponse.json();

        if (dataResponse.success === true && image && imgWrapper) {
            image.src = "data:image/jpeg;base64," + dataResponse.imageData;
            imgWrapper.classList.add('d-block');
            imgWrapper.classList.remove('d-none');
        } else {
            console.log("Error:  " + dataResponse.errorMessage);
        }
    }

    async function postUserProfileImage() {
        if (loader && ctaSave) {
            loader.classList.add('d-block');
            loader.classList.remove('d-none');
            ctaSave.disabled = true;
        }
        let storedToken = localStorage.getItem('token');
        let base64String = null;
        let payload = null;
        try {
            base64String = await fileToBase64(file);
            payload = {
                imageData: base64String.split(',')[1]
            };
        } catch (error) {
            console.error(Granite.I18n.get("error_image_conversion"), error);
        }
        if (storedToken && errorMessagges.length === 0) {
            if (localStorage.getItem('remember_me') === "false") {
                let tokenObject = JSON.parse(storedToken);
                token = tokenObject.token;
            } else {
                token = storedToken
            }

            try {
                const responseCsrf = await fetch("/libs/granite/csrf/token.json");
                const csrfToken = await responseCsrf.json();
                const regResponse = await fetch("/private/api/profile/image", {
                    method: "POST",
                    headers: {
                        "CSRF-Token": csrfToken.token,
                        'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify(payload),

                });
                const dataResponse = await regResponse.json();

                if (dataResponse.success === true) {
                    let successMessage = document.querySelector('#cmp-editImage__successAlert');
                    successMessage.classList.add('d-block');
                    successMessage.classList.remove('d-none');
                }
            } catch (error) {
                console.log("Error: ", error);
            } finally {
                if (ctaSave && loader) {
                    ctaSave.disabled = false;
                    loader.classList.add('d-none');
                    loader.classList.remove('d-block');
                }
            }

        }
    }
});