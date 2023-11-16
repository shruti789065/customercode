export function _isDesktop() {
  if (window.innerWidth < 1200) {
    return false;
  } else {
    return true;
  }
}

export function _prependHtml(el, str) {
  var div = document.createElement("div");
  div.innerHTML = str;
  while (div.children.length > 0) {
    el.prepend(div.children[0]);
  }
}

export function _findSiblingsWithClass(element, className) {
  const siblings = [];
  let sibling = element.parentNode.firstChild;
  while (sibling) {
    if (
      sibling.nodeType === 1 &&
      sibling !== element &&
      sibling.classList.contains(className)
    ) {
      siblings.push(sibling);
    }
    sibling = sibling.nextSibling;
  }
  return siblings;
}

export function getJsonProperty(jsonData, parameter) {
	const items = [];
	for (const key in jsonData) {
	  if (jsonData.hasOwnProperty(key)) {
		const fragment = jsonData[key];
		if (fragment.hasOwnProperty(parameter)) {
			items.push(fragment[parameter]);
		}
	  }
	}

	return items;
  }
