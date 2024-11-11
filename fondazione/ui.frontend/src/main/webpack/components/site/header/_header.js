let headerIsOpen = false;
let headerMobileIsOpen = false;

document.addEventListener("DOMContentLoaded", () => {
  let subMenuToggle = document.getElementById("subMenuToggle");
  let subMenuToggleMobile = document.getElementById("subMenuToggleMobile");
  let headerMobileHamburger = document.getElementById("hamburger-header");
  let headerMobileClose = document.getElementById("close-header-mobile");
  let back = document.getElementById("back-to-submenu-list");
  let languageNavigationListWrapper = document.getElementById(
    "languageNavigationListWrapper"
  );
  let shareIconHeader = document.getElementById("shareIconHeader");

  if (headerMobileHamburger) {
    headerMobileHamburger.addEventListener("click", toggleHeaderMobile);
  }

  if (subMenuToggle) {
    subMenuToggle.addEventListener("click", toggleSubMenuHeader);
  }

  if (subMenuToggleMobile) {
    subMenuToggleMobile.addEventListener("click", toggleSubMenuHeaderMobile);
  }

  if (headerMobileClose) {
    headerMobileClose.addEventListener("click", closeHeader);
  }

  if (back) {
    back.addEventListener("click", backToSubmenuList);
  }

  if (languageNavigationListWrapper) {
    languageNavigationListWrapper.addEventListener("click", toggleLanguageMenu);
  }

  if (shareIconHeader) {
    shareIconHeader.addEventListener("click", toggleShareMenu);
  }
});

window.addEventListener("scroll", () => {
  setBottomHeaderStyle();
  setBottomHeaderMobileStyle();
});

function toggleSubMenuHeader() {
  headerIsOpen = !headerIsOpen;

  let headerSubMenu = document.getElementById("headerSubMenu");
  let headerLinkIconX = document.getElementById("iconSubMenuToggle");

  if (headerSubMenu.classList.contains("d-none")) {
    headerLinkIconX.classList.remove("d-none");
    headerLinkIconX.classList.add("d-block");
    headerSubMenu.classList.remove("d-none");
    headerSubMenu.classList.add("d-block");
  } else {
    headerLinkIconX.classList.remove("d-block");
    headerLinkIconX.classList.add("d-none");
    headerSubMenu.classList.remove("d-block");
    headerSubMenu.classList.add("d-none");
  }

  // Set bottom header style
  if (
    bottomHeader.classList.contains("bottom_header_not_scrolled") &&
    window.scrollY === 0
  ) {
    bottomHeader.classList.remove("bottom_header_not_scrolled");
    bottomHeader.classList.add("bottom_header_scrolled");
  } else if (
    !bottomHeader.classList.contains("bottom_header_not_scrolled") &&
    window.scrollY === 0
  ) {
    bottomHeader.classList.add("bottom_header_not_scrolled");
    bottomHeader.classList.remove("bottom_header_scrolled");
  }

  closeTopHeaderDropdowns();
}

function toggleSubMenuHeaderMobile() {
  headerMobileIsOpen = !headerMobileIsOpen;
  let subMenuListMobile = document.getElementById("sub-header-list-mobile");
  let subMenuInitiatives = document.getElementById("sub-header-initiatives");

  subMenuListMobile.classList.remove("d-block");
  subMenuListMobile.classList.add("d-none");

  subMenuInitiatives.classList.remove("d-none");
  subMenuInitiatives.classList.add("d-block");

  closeTopHeaderDropdowns();
}

function toggleHeaderMobile() {
  headerMobileIsOpen = true;
  let headerMobile = document.getElementById("bottomHeaderMobile");
  let headerMobileHamburger = document.getElementById("hamburger-header");
  let headerMobileClose = document.getElementById("close-header-mobile");
  let subMenuListMobile = document.getElementById("sub-header-list-mobile");

  if (headerMobile) {
    headerMobile.classList.remove("bottom_header_mobile_closed");
    headerMobile.classList.add("bottom_header_mobile_open");
  }

  if (headerMobileHamburger) {
    headerMobileHamburger.classList.add("d-none");
  }

  if (headerMobileClose.classList.contains("d-none")) {
    headerMobileClose.classList.remove("d-none");
    headerMobileClose.classList.add("d-block");
  }

  if (subMenuListMobile.classList.contains("d-none")) {
    subMenuListMobile.classList.remove("d-none");
    subMenuListMobile.classList.add("d-block");
  }
  closeTopHeaderDropdowns();
}

function closeHeader() {
  headerMobileIsOpen = false;
  setBottomHeaderMobileStyle();

  let headerMobile = document.getElementById("bottomHeaderMobile");
  let headerMobileHamburger = document.getElementById("hamburger-header");
  let headerMobileClose = document.getElementById("close-header-mobile");
  let subMenuListMobile = document.getElementById("sub-header-list-mobile");
  let subMenuInitiatives = document.getElementById("sub-header-initiatives");

  if (headerMobile) {
    headerMobile.classList.add("bottom_header_mobile_closed");
    headerMobile.classList.remove("bottom_header_mobile_open");
  }

  if (headerMobileHamburger.classList.contains("d-none")) {
    headerMobileHamburger.classList.add("d-block");
    headerMobileHamburger.classList.remove("d-none");

    if (headerMobileClose.classList.contains("d-block")) {
      headerMobileClose.classList.add("d-none");
      headerMobileClose.classList.remove("d-block");
    }
  }

  if (subMenuListMobile.classList.contains("d-block")) {
    subMenuListMobile.classList.remove("d-block");
    subMenuListMobile.classList.add("d-none");
  }

  if (subMenuInitiatives.classList.contains("d-block")) {
    subMenuInitiatives.classList.remove("d-block");
    subMenuInitiatives.classList.add("d-none");
  }
  closeTopHeaderDropdowns();
}

function backToSubmenuList() {
  let subMenuListMobile = document.getElementById("sub-header-list-mobile");
  let subMenuInitiatives = document.getElementById("sub-header-initiatives");

  if (subMenuListMobile.classList.contains("d-none")) {
    subMenuListMobile.classList.remove("d-none");
    subMenuListMobile.classList.add("d-block");
  }

  if (subMenuInitiatives.classList.contains("d-block")) {
    subMenuInitiatives.classList.remove("d-block");
    subMenuInitiatives.classList.add("d-none");
  }
  closeTopHeaderDropdowns();
}

function setBottomHeaderStyle() {
  let bottomHeader = document.getElementById("bottomHeader");
  if (bottomHeader && window.scrollY > 0 && !headerIsOpen) {
    if (bottomHeader.classList.contains("bottom_header_not_scrolled")) {
      bottomHeader.classList.remove("bottom_header_not_scrolled");
    }
    bottomHeader.classList.add("bottom_header_scrolled");
  } else if (
    bottomHeader &&
    window.scrollY === 0 &&
    bottomHeader.classList.contains("bottom_header_scrolled") &&
    !headerIsOpen
  ) {
    bottomHeader.classList.remove("bottom_header_scrolled");
    bottomHeader.classList.add("bottom_header_not_scrolled");
  }
}

function setBottomHeaderMobileStyle() {
  let bottomHeaderMobile = document.getElementById("bottomHeaderMobile");

  if (bottomHeaderMobile && window.scrollY > 0 && !headerMobileIsOpen) {
    if (
      bottomHeaderMobile.classList.contains("bottom_header_mobile_not_scrolled")
    ) {
      bottomHeaderMobile.classList.remove("bottom_header_mobile_not_scrolled");
    }
    bottomHeaderMobile.classList.add("bottom_header_mobile_scrolled");
  } else if (
    bottomHeaderMobile &&
    window.scrollY === 0 &&
    bottomHeaderMobile.classList.contains("bottom_header_mobile_scrolled") &&
    !headerMobileIsOpen
  ) {
    bottomHeaderMobile.classList.remove("bottom_header_mobile_scrolled");
    bottomHeaderMobile.classList.add("bottom_header_mobile_not_scrolled");
  }
}

function toggleLanguageMenu() {
  let list = document.getElementById("languageNavigationList");
  let shareSocialList = document.getElementById("shareSocialList");
  let languageNavigationIcon = document.getElementById(
    "languageNavigationIcon"
  );

  if (list && list.classList.contains("d-none")) {
    list.classList.remove("d-none");
    list.classList.add("d-block");
    languageNavigationIcon.classList.add("rotate_icon");
  } else if (list && list.classList.contains("d-block")) {
    list.classList.add("d-none");
    list.classList.remove("d-block");
    languageNavigationIcon.classList.remove("rotate_icon");
  }

  if (shareSocialList && shareSocialList.classList.contains("d-block")) {
    shareSocialList.classList.add("d-none");
    shareSocialList.classList.remove("d-block");
  }
}

function toggleShareMenu() {
  let shareSocialList = document.getElementById("shareSocialList");
  let list = document.getElementById("languageNavigationList");
  let languageNavigationIcon = document.getElementById(
    "languageNavigationIcon"
  );

  if (shareSocialList && shareSocialList.classList.contains("d-none")) {
    shareSocialList.classList.remove("d-none");
    shareSocialList.classList.add("d-block");
  } else if (shareSocialList && shareSocialList.classList.contains("d-block")) {
    shareSocialList.classList.add("d-none");
    shareSocialList.classList.remove("d-block");
  }

  if (list && list.classList.contains("d-block")) {
    list.classList.add("d-none");
    list.classList.remove("d-block");
    languageNavigationIcon.classList.remove("rotate_icon");
  }
}

function closeTopHeaderDropdowns() {
  let shareSocialList = document.getElementById("shareSocialList");
  let list = document.getElementById("languageNavigationList");
  let languageNavigationIcon = document.getElementById(
    "languageNavigationIcon"
  );

  if (list && list.classList.contains("d-block")) {
    list.classList.add("d-none");
    list.classList.remove("d-block");
    languageNavigationIcon.classList.remove("rotate_icon");
  }

  if (shareSocialList && shareSocialList.classList.contains("d-block")) {
    shareSocialList.classList.add("d-none");
    shareSocialList.classList.remove("d-block");
  }
}
