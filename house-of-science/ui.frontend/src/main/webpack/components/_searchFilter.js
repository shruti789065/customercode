/* eslint-disable max-len */
import _ from "lodash";

const copyDataFromJson = () => {
  const domainName = window.location.hostname;
  const port = window.location.port;
  const protocol = window.location.protocol;
  const currentNodePipeline = document.querySelector('.currentNodeSearchFilter').value;
  let url;

  const loadingSpinner = document.createElement("div");
  loadingSpinner.classList.add("loading-spinner");
  document.body.appendChild(loadingSpinner);

  if (domainName === 'localhost' && port === '4502') {
    url = `${protocol}//${domainName}:${port}${currentNodePipeline}.searchFilter.json`;
  } else if (domainName === 'localhost') {
    url = 'https://raw.githubusercontent.com/davide-mariotti/JSON/main/searchHOS/searchFilter.json';
  } else {
    url = `${protocol}//${domainName}${currentNodePipeline}.searchFilter.json`;
  }

  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      localStorage.setItem('searchFilter', JSON.stringify(data));
    })
    .catch((error) => {
      console.error("Error copying data to local storage:", error);
      loadingSpinner.remove();
    });
};

// La funzione 'getData' recupera i dati precedentemente salvati nella memoria locale dell'utente tramite 'localStorage'
// e li restituisce come oggetto JSON, utilizzando 'JSON.parse' per parsare il valore salvato.
const getData = () => {
  return JSON.parse(localStorage.getItem("searchFilter"));
};

//La funzione changeTypologyColor cambia il colore di sfondo e di testo degli elementi HTML
//che hanno la classe "typology", a seconda del loro contenuto testuale.
const changeTypologyColor = () => {
  var elements = document.getElementsByClassName("typology");
  for (var i = 0; i < elements.length; i++) {
    if (elements[i].textContent === "Articles") {
      elements[i].style.backgroundColor = "red";
    }
    if (elements[i].textContent === "Report") {
      elements[i].style.backgroundColor = "green";
    }
    if (elements[i].textContent === "Perspective") {
      elements[i].style.backgroundColor = "yellow";
      elements[i].style.color = "black";
    }
    if (elements[i].textContent === "Correspondence") {
      elements[i].style.backgroundColor = "gray";
    }
    if (elements[i].textContent === "News") {
      elements[i].style.backgroundColor = "blue";
    }
    if (elements[i].textContent === "Original Investigation") {
      elements[i].style.backgroundColor = "black";
    }
    if (elements[i].textContent === "New Result") {
      elements[i].style.backgroundColor = "brown";
    }
    if (elements[i].textContent === "Editorial") {
      elements[i].style.backgroundColor = "orange";
    }
    if (elements[i].textContent === "Comment") {
      elements[i].style.backgroundColor = "purple";
    }
    if (elements[i].textContent === "Viewpoint") {
      elements[i].style.backgroundColor = "aqua";
    }
    if (elements[i].textContent === "Case Report") {
      elements[i].style.backgroundColor = "burlywood";
    } else if (elements[i].textContent === "Letter") {
      elements[i].style.backgroundColor = "cadetblue";
    }
  }
};

//Questa funzione controlla se ci sono filtri vuoti e rimuove gli elementi del DOM corrispondenti.
//Viene creato un array di classi di elementi del DOM che si desidera controllare
//Per ogni classe, viene selezionato un array di elementi e per ogni elemento viene controllato se
//il testo contenuto è vuoto. Se è vuoto, l'elemento viene rimosso dal DOM.
//In seguito, vengono controllati gli elementi con classe ".card-body .Link".
//Per ogni elemento, se la lunghezza dell'attributo "href" è uguale a 42, l'elemento viene rimosso dal DOM.
const checkFilterEmpty = () => {
  var classes = [
    ".card-body .Topic",
    ".card-body .Author",
    ".card-body .Source",
    ".card-body .Year",
    ".card-body .Tag",
    ".card-body .description",
  ];
  for (var i = 0; i < classes.length; i++) {
    var elements = document.querySelectorAll(classes[i]);
    for (var j = 0; j < elements.length; j++) {
      if (elements[j].textContent.trim().length <= 0) {
        elements[j].remove();
      }
    }
  }
  var links = document.querySelectorAll(".card-body .Link");
  for (var k = 0; k < links.length; k++) {
    if (links[k].href.length === 42) {
      links[k].remove();
      //console.log('link rimosso');
    } else {
      //console.log('link ok');
    }
  }
};

//Questa è una funzione che filtra un array di oggetti data in base ad un set di filtri passato come parametro filters.
//Utilizza la libreria Lodash che fornisce diverse funzionalità per la manipolazione di array e oggetti.
//La funzione restituisce un nuovo array di oggetti che soddisfano tutti i filtri specificati.
//Ogni oggetto viene passato alla funzione di filtro come parametro item.
//I filtri sono definiti come oggetti con chiave-valore che corrispondono alle proprietà dell'oggetto item.
//Se un filtro non è specificato (cioè è vuoto), viene considerato come soddisfatto.
const filterData = (data, filters) => {
  return _.filter(data, (item) => {
    return (
      (_.isEmpty(filters.topic) || _.includes(filters.topic, item.topic)) &&
      (_.isEmpty(filters.author) || _.includes(filters.author, item.author)) &&
      (_.isEmpty(filters.source) || _.includes(filters.source, item.source)) &&
      (_.isEmpty(filters.year) || _.includes(filters.year, item.year)) &&
      (_.isEmpty(filters.typology) ||
        _.includes(filters.typology, item.typology)) &&
      (_.isEmpty(filters.Tag) || _.includes(filters.Tag, item.Tag))
    );
  });
};

//La funzione displayData si occupa di visualizzare i dati che sono stati filtrati tramite la funzione filterData.
//In particolare, la funzione prende in input un array di oggetti e genera l'HTML che rappresenta le card che mostrano le informazioni di ogni oggetto.
//Nella prima parte della funzione viene creato un'HTML vuoto che verrà popolato nel ciclo forEach con un blocco di codice HTML per ogni oggetto.
//Nel blocco di codice HTML sono presenti alcune variabili che vengono utilizzate per impostare gli ID dei tag div che rappresentano le card,
//utili per gestire la visualizzazione in caso di click sul pulsante di espansione/crollo della card.
//Infine, viene impostato l'HTML nel tag div con ID "results" e vengono chiamate le funzioni changeTypologyColor e checkFilterEmpty
//per impostare i colori delle card e rimuovere eventuali elementi vuoti.
const displayData = (filteredData) => {
  let html = "";
  filteredData.forEach((item) => {
    html += `
    
	<div class="cardSFR">
		<div class="card-header" id="heading-${item.typology.replace(
      /\s+/g,
      ""
    )}-${item.date.replace(/\//g, "-")}">
			<button class="btn btn-link collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse-${item.typology.replace(
        /\s+/g,
        ""
      )}-${item.date.replace(
      /\//g,
      "-"
    )}" aria-expanded="true" aria-controls="collapse-${item.typology.replace(
      /\s+/g,
      ""
    )}-${item.date.replace(/\//g, "-")}">
				<span class="typology">${item.typology}</span>
				<span class="description">${item.description}</span>
			</button>
			<span class="date">${item.date}</span>
		</div>
		<div id="collapse-${item.typology.replace(/\s+/g, "")}-${item.date.replace(
      /\//g,
      "-"
    )}" class="collapse" aria-labelledby="heading-${item.typology.replace(
      /\s+/g,
      ""
    )}-${item.date.replace(/\//g, "-")}" data-bs-parent="#results">
			<div class="card-body">
				<div class="meta-data">
					<div class="meta-data-item Topic">TOPIC: ${item.topic}</div>
					<div class="meta-data-item Author">AUTHORS: ${item.author}</div>
					<div class="meta-data-item Source">SOURCE: ${item.source}</div>
					<div class="meta-data-item Tag">${item.tag}</div>
				</div>
				<div class="description">
          ABSTRACT:
					<br>
            ${item.description}
          
					</div>
					<div class="containerLink">
						<a class="Link" href="${item.link}" target="_blank">Read more</a>
					</div>
				</div>
			</div>
		</div>
  `;
  });
  document.getElementById("results").innerHTML = html;
  changeTypologyColor();
  checkFilterEmpty();
};

//Questa funzione si occupa di recuperare i filtri selezionati dall'utente nell'interfaccia grafica.
//In particolare, inizializza l'oggetto filters con tutte le chiavi necessarie e tutti i valori vuoti
//in modo che possano essere popolati in seguito con i filtri selezionati.
//Successivamente, rimuove la classe filter-selected da tutti gli elementi con classe filter-selected
//che sono i checkbox selezionati in precedenza dall'utente.
//Quindi, per ogni gruppo di checkbox (topic, author, source, year, typology e tag) selezionati dall'utente
//viene creata una matrice di valori selezionati e viene assegnata alla corrispondente chiave dell'oggetto filters.
//Infine, l'oggetto filters viene restituito, contenente tutti i filtri selezionati dall'utente.
const getFilters = () => {
  const filters = {
    topic: [],
    author: [],
    source: [],
    year: [],
    typology: [],
    tag: [],
  };
  document
    .querySelectorAll(".filter-selected")
    .forEach((el) => el.classList.remove("filter-selected"));
  [...document.querySelectorAll("input[type='checkbox']:checked")].forEach(
    (el) => el.parentNode.classList.add("filter-selected")
  );
  filters.topic = [
    ...document.querySelectorAll(
      "#topic-filter input[type='checkbox']:checked"
    ),
  ].map((x) => x.value);
  filters.author = [
    ...document.querySelectorAll(
      "#author-filter input[type='checkbox']:checked"
    ),
  ].map((x) => x.value);
  filters.source = [
    ...document.querySelectorAll(
      "#source-filter input[type='checkbox']:checked"
    ),
  ].map((x) => x.value);
  filters.year = [
    ...document.querySelectorAll("#year-filter input[type='checkbox']:checked"),
  ].map((x) => x.value);
  filters.typology = [
    ...document.querySelectorAll(
      "#typology-filter input[type='checkbox']:checked"
    ),
  ].map((x) => x.value);
  filters.tag = [
    ...document.querySelectorAll("#tag-filter input[type='checkbox']:checked"),
  ].map((x) => x.value);
  return filters;
};

//La funzione richiama le funzioni filterData(), getData(), getFilters(), displayData(), changeTypologyColor() e checkFilterEmpty()
//in sequenza per filtrare i dati e visualizzarli nella pagina web.
//In particolare, la funzione getData() recupera tutti i dati dai tag <script> presenti nella pagina web
//getFilters() raccoglie i filtri selezionati dall'utente
//filterData() utilizza i dati e i filtri per restituire i dati filtrati.
//Successivamente, la funzione displayData() visualizza i dati filtrati nella pagina web
//changeTypologyColor() cambia il colore della tipologia in base a una mappatura predefinita
//checkFilterEmpty() controlla se ci sono filtri attivi e aggiunge la classe .active a un elemento HTML appropriato.
//Infine, la funzione searchFilt() viene eseguita quando l'utente esegue una ricerca nella pagina web.
function searchFilt() {
  const filteredData = filterData(getData(), getFilters());
  displayData(filteredData);
  changeTypologyColor();
  checkFilterEmpty();
}

function displayDataHOS() {
/********************************************************/
  let data = getData();
  const uniqueTopics = _.uniqBy(data, "topic");
  let options = "";
  uniqueTopics.forEach((topic) => {
    options += `
		<label>
			<input type="checkbox" value="${topic.topic}" onchange="searchFilt()">${topic.topic}
			</label>`;
  });
  document.getElementById("topic-filter").innerHTML = options;
  const uniqueAuthors = _.uniqBy(data, "author");
  options = "";
  uniqueAuthors.forEach((author) => {
    options += `
			<label>
				<input type="checkbox" value="${author.author}" onchange="searchFilt()">${author.author}
				</label>`;
  });
  document.getElementById("author-filter").innerHTML = options;
  const uniqueSources = _.uniqBy(data, "source");
  options = "";
  uniqueSources.forEach((source) => {
    options += `
				<label>
					<input type="checkbox" value="${source.source}" onchange="searchFilt()">${source.source}
					</label>`;
  });
  document.getElementById("source-filter").innerHTML = options;
  const uniqueYears = _.uniqBy(data, "year");
  options = "";
  uniqueYears.forEach((year) => {
    options += `
					<label>
						<input type="checkbox" value="${year.year}" onchange="searchFilt()">${year.year}
						</label>`;
  });
  document.getElementById("year-filter").innerHTML = options;
  const uniqueTypologys = _.uniqBy(data, "typology");
  options = "";
  uniqueTypologys.forEach((typology) => {
    options += `
						<label>
							<input type="checkbox" value="${typology.typology}" onchange="searchFilt()">${typology.typology}
							</label>`;
  });
  document.getElementById("typology-filter").innerHTML = options;
  const uniqueTags = _.uniqBy(data, "tag");
  options = "";
  uniqueTags.forEach((tag) => {
    options += `
							<label>
								<input type="checkbox" value="${tag.tag}" onchange="searchFilt()">${tag.tag}
								</label>`;
  });
  document.getElementById("tag-filter").innerHTML = options;
  displayData(data);
  /**************************************************/
}

//La funzione init inizializza la pagina e popola le checkbox per la selezione dei filtri.
//1 Richiama la funzione copyDataFromJson per copiare i dati dal file JSON in una variabile locale.
//2 Ottiene i dati filtrati dalla funzione getData.
//3 Usa la libreria Lodash per trovare tutti i valori unici per ogni attributo, come "topic", "author", "source", ecc.
//4 Per ogni valore unico trovato, crea una checkbox corrispondente con un label che mostra il valore e un attributo value che contiene il valore stesso.
//5 Aggiunge un evento onchange alla checkbox che richiama la funzione searchFilt.
//6 Aggiunge le checkbox create per ogni attributo nei rispettivi div nella pagina HTML.
//7 Richiama la funzione displayData per visualizzare tutti i dati non filtrati inizialmente.
//In generale, la funzione init serve a preparare la pagina per l'utilizzo dei filtri e del sistema di visualizzazione dei dati.
const init = () => {
  copyDataFromJson();

  const dataSearchFilter = JSON.parse(localStorage.getItem("searchFilter"));
  if (dataSearchFilter && dataSearchFilter.length > 0) {
    
    displayDataHOS();

  } else {
    const intervalIdSearchFilter = setInterval(() => {
      const dataSearchFilter = JSON.parse(localStorage.getItem("searchFilter"));
      if (dataSearchFilter && dataSearchFilter.length > 0) {
        clearInterval(intervalIdSearchFilter);
        
        displayDataHOS();

      }
    }, 500);
  }
};

//Esporta le funzioni searchFilt e init in modo che siano accessibili all'interno del contesto globale della finestra.
//In questo modo, possono essere chiamate da qualsiasi parte del codice.
//Il codice si occupa di chiamare la funzione init quando la pagina web viene caricata completamente.
//Se esiste un elemento con l'id results, allora viene chiamata la funzione init. Altrimenti, viene stampato un messaggio di errore in console.
window.searchFilt = searchFilt;
document.addEventListener("DOMContentLoaded", function () {
  const results = document.getElementById("results");
  if (results) {
    init();
  } else {
    console.log("noSearchFilter");
  }
});
