document.addEventListener('DOMContentLoaded', function () {
    let selectedTab = "userProfileTab";
    let userProfileTab = document.querySelector('#userProfileTab');
    let passwordTab = document.querySelector('#passwordTab');

    let userProfileComponent = document.querySelector('#userProfileComponent');
    let passwordComponent = document.querySelector('#passwordComponent');

    userProfileTab.addEventListener('click', () => toggleTab('userProfileTab'));
    passwordTab.addEventListener('click', () => toggleTab('passwordTab'));

    function toggleTab(tabName) {

        if(tabName === "userProfileTab") {
            selectedTab = "userProfileTab";
            passwordTab.classList.remove('active');
            userProfileTab.classList.add('active');
            userProfileComponent.classList.add('d-block');
            userProfileComponent.classList.remove('d-none');
            passwordComponent.classList.remove('d-block');
            passwordComponent.classList.add('d-none');
        }

        if(tabName === "passwordTab") {
            selectedTab = "passwordTab";
            userProfileTab.classList.remove('active');
            passwordTab.classList.add('active');
            userProfileComponent.classList.add('d-none');
            userProfileComponent.classList.remove('d-block');
            passwordComponent.classList.remove('d-none');
            passwordComponent.classList.add('d-block');
        }
    }
})