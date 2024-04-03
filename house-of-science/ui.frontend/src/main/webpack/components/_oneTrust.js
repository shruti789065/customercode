import jQuery from "jquery";

// eslint-disable-next-line @typescript-eslint/no-unused-vars
jQuery(function ($) {
    var existCookiePref = document.querySelector('#footer-links #flex-container .cmp-list .ot-sdk-show-settings');
    if (existCookiePref == undefined || existCookiePref == '' ) {
        document.querySelector('#footer-links #flex-container .cmp-list').insertAdjacentHTML("beforeend",'<li class="cmp-list__item"><a class="cmp-list__item-link ot-sdk-show-settings"><span class="cmp-list__item-title">Preferenze cookie</span></a></li>')
    }
});



