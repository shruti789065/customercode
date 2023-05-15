var existCookiePref = document.querySelector('.cmp-experiencefragment--footer .cmp-list .ot-sdk-show-settings');
if (existCookiePref == undefined || existCookiePref == '' ) {
	document.querySelector('.cmp-experiencefragment--footer .cmp-list').insertAdjacentHTML("beforeend",'<li class="cmp-list__item"><a class="cmp-list__item-link ot-sdk-show-settings"><span class="cmp-list__item-title">Preferenze cookie</span></a></li>')
}

