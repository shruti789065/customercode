 //
import $ from "jquery";

const blog = (() => {
  let blog;
  let blogJSON;
  let domainName;
  let port;
  let protocol;

  function copyDataFromJson() {

    domainName = window.location.hostname;
    port = window.location.port;
    protocol = window.location.protocol;


    const lang = document.documentElement.getAttribute('lang');
  
    const url =
      domainName === "localhost" && port === "4502"
        ? `${protocol}//${domainName}:${port}/graphql/execute.json/global/locale;locale=${lang}`
        : domainName === "localhost"
        ? "https://raw.githubusercontent.com/davide-mariotti/JSON/main/blogFeedMT/blogFeed.json"
        : `${protocol}//${domainName}/graphql/execute.json/global/locale;locale=${lang}`;
  
    fetch(url)
      .then((response) => response.json())
      .then((data) => {

        let blogElement = document.querySelector(".blog-list");
          
        var articles = blogElement.getElementsByTagName("article");

        for(var i = 0; i<articles.length; i++){


          /*data.items.forEach((item) => {

            console.log(item.category);
            
          });*/
          

          //FILL CATEGORY
          var category = document.createElement("div");
          category.setAttribute("class","category");
          category.innerHTML = data.items[i].category;
          articles[i].getElementsByClassName("cmp-contentfragment__element--category")[0].append(category);
          //articles[i].getElementsByClassName("cmp-contentfragment__element--category")[0].getElementsByClassName("cmp-contentfragment__element-title")[0].innerHTML = data.items[i].category;
          

          //FILL TITLE
          var title = document.createElement("div");
          title.setAttribute("class","title");
          title.innerHTML = data.items[i].title;
          articles[i].getElementsByClassName("cmp-contentfragment__element--title")[0].append(title);
          
          //FILL IMMAGINE
          var image = document.createElement("div");
          image.setAttribute("class","image");
          var imageContent= `
          <div class="cmp-image" itemscope="" >
              <img src="`+data.items[i].image+`">
          </div>`;
          image.innerHTML= imageContent;
          articles[i].getElementsByClassName("cmp-contentfragment__element--imageLink")[0].append(image);
          
          //FILL LINK
          var link = document.createElement("div");
          link.setAttribute("class", "link");
          var linkReadMore = `
						<a class="cmp-link cmp-link--arrow" href="` + data.items[i].link +`" target="_blank">
								<i class="cmp-link__icon-asset"></i>
								<span class="cmp-link--text">READ MORE</span>
						</a>`;
            link.innerHTML = linkReadMore;
          articles[i].getElementsByClassName("cmp-contentfragment__element--pageLink")[0].append(link);
           
          console.log(articles[i]);
        }

       

  
        
  
      })
      .catch((error) => {
        console.error("Error copying data to local storage:", error);
      });
  
  }


  function init() {
    blog= document.querySelector(".blog-list");
    if (!blog) {
      return;
    }

    copyDataFromJson();

  }

  return {
    init: init,
  };
})();

$(function () {
  blog.init();
});
