/* eslint-disable max-len */
import $ from "jquery";
import jQuery from "jquery";
window.$ = jQuery;

(function () {
  "use strict";

  /**
   * NewsList ListScroll
   *
   * @class ContactForm
   * @classdesc initiating javascript for the ListScroll Component.
   */

  var ListScroll = (function () {
    var CONST = {
      URL: _getJSON(),
    };

    var $newsListContainer, mainContainer, mainChildren, loader;

    /**
     * Initializes the ListScroll
     *
     * @public
     */
    function init() {
      $newsListContainer = document.querySelector(".cmp-news-list");
      if ($newsListContainer != undefined || $newsListContainer != null) {
        $newsListContainer.append(
          Object.assign(document.createElement("div"), { id: "loading" })
        );

        mainContainer = document.querySelector(".cmp-news-list .cmp-list");
        mainChildren = mainContainer.childElementCount;
        loader = document.querySelector("#loading");

        document.addEventListener("DOMContentLoaded", () => {
          let options = {
            root: null,
            rootMargins: "0px",
            threshold: 0.5,
          };
          const observer = new IntersectionObserver(handleIntersect, options);
          observer.observe(document.querySelector("footer"));
        });
      }
    }

    function getData() {
      displayLoading(loader);
      fetch(CONST.URL)
        .then((response) => response.json())
        .then((data) => {
          hideLoading(loader);

          var boxes = Object.keys(data)
            .filter((v) => !v.startsWith("jcr:"))
            .map((e) => data[e]);
          boxes.forEach((item, index) => {
            if (typeof item == "string") {
              return;
            }
            if ("jcr:content" in item && index >= mainChildren) {
              _createElement(item, mainContainer);
            }
          });
        });
    }

    function _createElement(item, mainContainer) {
      let listItem = Object.assign(document.createElement("div"), {
        className: "cmp-list__item",
      });
      let teaserContainer = Object.assign(document.createElement("div"), {
        className: "cmp-teaser",
      });

      let imageTeaserContainer = Object.assign(document.createElement("div"), {
        className: "cmp-teaser__image",
      });
      let imageComponent = Object.assign(document.createElement("div"), {
        className: "cmp-image",
        itemtype: "http://schema.org/ImageObject",
      });
      let image = Object.assign(document.createElement("img"), {
        className: "cmp-image__image",
        itemprop: "contentUrl",
        loading: "lazy",
      });

      let teaserContent = Object.assign(document.createElement("div"), {
        className: "cmp-teaser__content",
      });
      let teaserPretitle = Object.assign(document.createElement("p"), {
        className: "cmp-teaser__pretitle",
        innerHTML: _formatDate(item["jcr:content"]),
      });
      let teaserTitle = Object.assign(document.createElement("h2"), {
        className: "cmp-teaser__title",
        innerHTML: item["jcr:content"]["jcr:title"],
      });
      let teaserDescription = Object.assign(document.createElement("div"), {
        className: "cmp-teaser__description",
        innerHTML: item["jcr:content"]["jcr:description"] || "placeholder",
      });

      let teaserActionContainer = Object.assign(document.createElement("div"), {
        className: "cmp-teaser__action-container",
      });
      let teaserActionLink = Object.assign(document.createElement("a"), {
        className: "cmp-teaser__action-container",
        href: "/content/prova",
        innerHTML: "read more",
      });
      let imageSrc = "";
      if ("cq:featuredimage" in item["jcr:content"]) {
        imageSrc = item["jcr:content"]["cq:featuredimage"].fileReference;
        image.src = imageSrc;
      }

      mainContainer
        .appendChild(listItem)
        .appendChild(teaserContainer)
        .appendChild(imageTeaserContainer)
        .appendChild(imageComponent)
        .appendChild(image);

      teaserContainer.appendChild(teaserContent).appendChild(teaserPretitle);

      teaserContent.appendChild(teaserTitle);
      teaserContent.appendChild(teaserDescription);

      teaserContent.appendChild(teaserActionContainer);
      teaserActionContainer.appendChild(teaserActionLink);
      mainChildren += 1;
    }

    function displayLoading(loader) {
      loader.classList.add("display");
      setTimeout(() => {
        loader.classList.remove("display");
      }, 5000);
    }

    function hideLoading(loader) {
      loader.classList.remove("display");
    }

    function _formatDate(jcrContent) {
      let formattedDate = "";
      if ("cq:lastModified" in jcrContent) {
        formattedDate = jcrContent["cq:lastModified"];
      } else {
        formattedDate = jcrContent["jcr:created"];
      }
      return formattedDate;
    }

    function handleIntersect(entries) {
      if (entries[0].isIntersecting) {
        getData();
      }
    }

    function _getJSON() {
      let origin = window.location.origin;
      let pathName = window.location.pathname;
      return origin + pathName.replace("html", "4.json");
    }

    return {
      init: init,
    };
  })();

  $(function () {
    ListScroll.init();
  });
})($);
