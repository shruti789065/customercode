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

export function _getJsonProperty(jsonData, parameter) {
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

export function _generateUniqueValue(name, key) {
  // Rimuovi spazi e converti il nome e la chiave in minuscolo
  const cleanName = name.toLowerCase().replace(/\s+/g, "-");
  const cleanKey = key.toLowerCase().replace(/\s+/g, "-");

  // Estrai le prime tre lettere dal nome e dalla chiave
  const shortName = cleanName.substring(0, 4);
  const shortKey = cleanKey.substring(0, 4);

  // Combina le parti per creare il valore univoco
  const uniqueValue = `${shortName}-${shortKey}`;

  return uniqueValue;
}

export function showOverlayAndLoader(item, needOverlay) {
  if (needOverlay) {
    const overlay = document.createElement("div");
    overlay.classList.add("overlay");
    item.append(overlay);
  }
  const loader = document.createElement("div");
  loader.classList.add("loader");
  item.append(loader);
  item.addClass("loading");
}

export function hideOverlayAndLoader(item) {
  item.find(".loader").remove();
  const overlay = item.find(".overlay");
  if (overlay.length > 0) {
    overlay.remove();
  }
  item.removeClass("loading");
}

export function getUrl(endpoint, JSONmock) {
  const domainName = window.location.hostname;
  const port = window.location.port;
  const protocol = window.location.protocol;
  return domainName === "localhost" && port === "4502"
    ? `${protocol}//${domainName}:${port}${endpoint}`
    : domainName === "localhost"
    ? JSONmock
    : `${protocol}//${domainName}${endpoint}`;
}
