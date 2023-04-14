export function _isDesktop() {
	if (window.innerWidth < 1200) {
		return false;
	} else {
		return true;
	}
}

export function _prependHtml(el, str) {
	var div = document.createElement('div');
	div.innerHTML = str;
	while (div.children.length > 0) {
		el.prepend(div.children[0]);
	}
}
