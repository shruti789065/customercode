import $ from "jquery";

const blog = (() => {
  function copyDataFromJson() {
    const tag = document.querySelector(".cmp-bloglist").dataset.category || "";
    const domainName = window.location.hostname;
    const port = window.location.port;
    const protocol = window.location.protocol;
    let graphqlQuery;

    /*
	https://author-p100658-e925061.adobeaemcloud.com/graphql/execute.json/global/blog-filtered;category=menarini-berlinchemie:menarini-berlin-blog-tag/menarini-art 
	*/

    if (!tag) {
      graphqlQuery = `/graphql/execute.json/menarini-berlinchemie/blog-all`;
    } else {
      graphqlQuery = `/graphql/execute.json/menarini-berlinchemie/blog-filtered;category=${tag}`;
    }
    const url =
      domainName === "localhost" && port === "4502"
        ? `${protocol}//${domainName}:${port}${graphqlQuery}`
        : domainName === "localhost"
        ? "https://raw.githubusercontent.com/davide-mariotti/JSON/main/blogFeedMT/blogFeed.json"
        : `${protocol}//${domainName}${graphqlQuery}`;

    fetch(url)
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

          
          if(item.category != null){
            item.category.forEach((singleCat,index) => {
              item.category[index] = singleCat.substr(singleCat.lastIndexOf('/')+1).replaceAll('-'," ");
            });
			const category = document.createElement("div");
          category.setAttribute("class", "category");

            category.innerHTML = item.category.join(", ");
          article
            .querySelector(".cmp-contentfragment__element--category")
            .append(category);
          }

          const title = document.createElement("div");
          title.setAttribute("class", "title");
          title.innerHTML = item.title;
          article
            .querySelector(".cmp-contentfragment__element--title")
            .append(title);

		if(item.date != null){
			const date = document.createElement("div");
			date.setAttribute("class","date");
			date.innerHTML =  item.date;
		  article
            .querySelector(".cmp-contentfragment__element--date")
            .append(date);
		}	
		  
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

          const link = document.createElement("div");
          link.setAttribute("class", "link");
          const linkReadMore = `
					<a class="cmp-link cmp-link--arrow" href="${item.pageLink}" target="_blank">
					  <i class="cmp-link__icon-asset"></i>
					  <span class="cmp-link--text">READ MORE</span>
					</a>`;
          link.innerHTML = linkReadMore;
          article
            .querySelector(".cmp-contentfragment__element--pageLink")
            .append(link);
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
