import $ from "jquery";
import { getUrl } from "../../../mastertemplate/site/_util.js";

const blog = (() => {
  function copyDataFromJson() {
    const tag = document.querySelector(".cmp-bloglist").dataset.category || "";
    const linkLanguage = document.querySelector(".cmp-link--translation").value;
    const JSONmock =
      "/etc.clientlibs/menarinimaster/clientlibs/clientlib-site/resources/mock/blogItem.json";
    let graphqlQuery;

    if (!tag) {
      graphqlQuery = `/graphql/execute.json/menarini-berlinchemie/blog-all`;
    } else {
      graphqlQuery = `/graphql/execute.json/menarini-berlinchemie/blog-filtered;category=${tag}`;
    }

    fetch(getUrl(graphqlQuery, JSONmock))
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        const blogElement = document.querySelector(".blog-list");
        if (!blogElement) {
          console.error("Blog element not found");
          return;
        }

        const articles = blogElement.getElementsByTagName("article");
        if (!articles || !articles.length) {
          console.error("No articles found");
          return;
        }

        data.data.blogItemList.items.forEach((item, i) => {
          const article = articles[i];
          if (!article) {
            console.error(`Article not found for item ${i}`);
            return;
          }

          if (item.category != null) {
            item.category.forEach((singleCat, index) => {
              item.category[index] = singleCat
                .substr(singleCat.lastIndexOf("/") + 1)
                .replaceAll("-", " ");
            });
            const category = document.createElement("div");
            category.setAttribute("class", "category");

            category.innerHTML = item.category.join(", ");
            article
              .querySelector(".cmp-contentfragment__element--category")
              .append(category);
          }

          if (item.title != null) {
            const title = document.createElement("div");
            title.setAttribute("class", "title");
            title.innerHTML = item.title;
            article
              .querySelector(".cmp-contentfragment__element--title")
              .append(title);
          }

          if (item.date != null) {
            const date = document.createElement("div");
            date.setAttribute("class", "date");
            date.innerHTML = item.date;
            article
              .querySelector(".cmp-contentfragment__element--date")
              .append(date);
          }

          if (item.imageLink != null) {
            const image = document.createElement("div");
            image.setAttribute("class", "image");
            const imageContent = `
					  <div class="cmp-image" itemscope="">
						<img src="${item.imageLink}">
					  </div>`;
            image.innerHTML = imageContent;
            article
              .querySelector(".cmp-contentfragment__element--imageLink")
              .append(image);
          }

          if (item.pageLink != null) {
            const link = document.createElement("div");
            link.setAttribute("class", "link");
            const linkReadMore = `
						<a class="cmp-link cmp-link--arrow" href="${item.pageLink}" target="_blank">
						<i class="cmp-link__icon-asset"></i>
						<span class="cmp-link--text">${linkLanguage}</span>
						</a>`;
            link.innerHTML = linkReadMore;
            article
              .querySelector(".cmp-contentfragment__element--pageLink")
              .append(link);
          }
        });
      })
      .catch((error) => {
        console.error("Error fetching or integrating blog data:", error);
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
