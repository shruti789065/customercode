import $ from "jquery";
import "../tabsmenu/_tabs-menu";
import { _isDesktop } from "../../site/_util.js";

const NavbarMenu = (() => {
  let currentContainerIndex = -1;
  const containerElements = document.querySelectorAll(
    ".cmp-navbar-menu--container"
  );
  const navbarSection = document.getElementById("navbarMenuSection");

  const navbarMenuCloseButton = document.getElementById("navbarMenuClose");

  function setNavbarBackgroundImage(dataLogoValue) {
    navbarSection.style.backgroundImage = `
		linear-gradient(
		  to bottom,
		  #143F59,
		  #143F59,
		  rgb(20 63 89 / 0%)
		),
		url(${dataLogoValue})
	  `;
  }

  function init() {
    const mobileBtn = document.querySelector(".cmp-button--mobile");
    const navbarItemAnchors = document.querySelectorAll(
      ".cmp-navbar-menu--item"
    );

    if (navbarItemAnchors != null) {
      navbarItemAnchors.forEach((element, index) => {
        const associatedInput = element.querySelector('input[type="hidden"]');
        if (associatedInput) {
          let dataLogoValue = associatedInput.getAttribute("data-logo");
          if (dataLogoValue !== null && navbarSection) {
            element.addEventListener("click", () => {
              navbarSection.style.display = "none";
              toggleNavbarMenu(element, index);
              setNavbarBackgroundImage(dataLogoValue);
            });
          }
        }
      });
    }

    if (navbarMenuCloseButton != null) {
      navbarMenuCloseButton.addEventListener("click", () =>
        toggleNavbarMenu(null, -1)
      );
    }

    const observer = new MutationObserver((mutationsList) => {
      for (const mutation of mutationsList) {
        if (
          mutation.target.classList.contains(
            "cmp-button--mobile__toggler_close"
          )
        ) {
          const menuCloseButton = document.querySelector(
            ".cmp-button--mobile__toggler_close"
          );
          menuCloseButton.addEventListener("click", () => {
            if (navbarSection !== null) {
              navbarSection.style.left = "100%";
              containerElements.forEach((container) => {
                container.style.left = "100%";
              });
            }
          });
        }
      }
    });

    if (mobileBtn != null) {
      observer.observe(mobileBtn, {
        attributes: true,
        attributeFilter: ["class"],
      });
    }
  }

  function toggleNavbarMenu(clickedElement, dataIndex) {
    if (navbarSection !== null) {
      if (dataIndex !== currentContainerIndex) {
        currentContainerIndex = dataIndex;

        const body = document.body;

        if (_isDesktop()) {
          const displayStyle = navbarSection.style.display;
          navbarSection.style.display =
            displayStyle === "none" ? "block" : "none";
          dataIndex = dataIndex;
        } else {
          const leftPosition = navbarSection.style.left;
          navbarSection.style.display = "block";
          navbarSection.style.left = leftPosition === "0px" ? "100%" : "0px";
          dataIndex = dataIndex - containerElements.length;
        }

        containerElements.forEach((container, index) => {
          container.style.display = index === dataIndex ? "block" : "none";
        });

        const dataLogoValue = clickedElement
          ? clickedElement.getAttribute("data-logo")
          : null;
        if (dataLogoValue !== null) {
          setNavbarBackgroundImage(dataLogoValue);
        }

        // Aggiungi gestione dell'overflow del body
        body.style.overflow =
          navbarSection.style.display === "none" ? "auto" : "hidden";
      }
    }
  }

  return {
    init: init,
    toggleNavbarMenu: toggleNavbarMenu,
  };
})();

$(function () {
  NavbarMenu.init();
});
