import $ from "jquery";

const blog = (() => {
  function copyDataFromJson() {
    const lang = document.documentElement.getAttribute("lang");
    const domainName = window.location.hostname;
    const port = window.location.port;
    const protocol = window.location.protocol;

    const url =
      domainName === "localhost" && port === "4502"
        ? `${protocol}//${domainName}:${port}/graphql/execute.json/global/locale;locale=${lang}`
        : domainName === "localhost"
        ? "https://raw.githubusercontent.com/davide-mariotti/JSON/main/blogFeedMT/blogFeed.json"
        : `${protocol}//${domainName}/graphql/execute.json/global/locale;locale=${lang}`;

    fetch(url)
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        const blogElement = document.querySelector(".blog-list");
        if (!blogElement) {return;}

        const articles = blogElement.getElementsByTagName("article");
        for (const [i, article] of Object.entries(articles)) {
          const item = data.items[i];
          if (!item) {break;}

          const category = document.createElement("div");
          category.setAttribute("class", "category");
          category.innerHTML = item.category;
          article
            .getElementsByClassName("cmp-contentfragment__element--category")[0]
            .append(category);

          const title = document.createElement("div");
          title.setAttribute("class", "title");
          title.innerHTML = item.title;
          article
            .getElementsByClassName("cmp-contentfragment__element--title")[0]
            .append(title);

          const image = document.createElement("div");
          image.setAttribute("class", "image");
          const imageContent = `
            <div class="cmp-image" itemscope="">
              <img src="${item.image}">
            </div>`;
          image.innerHTML = imageContent;
          article
            .getElementsByClassName(
              "cmp-contentfragment__element--imageLink"
            )[0]
            .append(image);

          const link = document.createElement("div");
          link.setAttribute("class", "link");
          const linkReadMore = `
            <a class="cmp-link cmp-link--arrow" href="${item.link}" target="_blank">
              <i class="cmp-link__icon-asset"></i>
              <span class="cmp-link--text">READ MORE</span>
            </a>`;
          link.innerHTML = linkReadMore;
          article
            .getElementsByClassName("cmp-contentfragment__element--pageLink")[0]
            .append(link);
        }
      })
      .catch((error) => {
        console.error("Error copying data from JSON:", error);
      });
  }

  function init() {
    const blog = document.querySelector(".blog-list");
    if (blog) {
      copyDataFromJson();
    }
  }

  return {
    init: init,
  };
})();

$(function () {
  blog.init();
});
