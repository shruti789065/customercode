/* eslint-disable max-len */
const copyDataFromJsonCompound = () => {
  const domainName = window.location.hostname;
  const port = window.location.port;
  const currentNodePipeline = document.querySelector('.currentNodeClinicalTrials').value;
  let url;

  const loadingSpinner = document.createElement("div");
  loadingSpinner.classList.add("loading-spinner");
  document.body.appendChild(loadingSpinner);

  if (domainName === 'localhost' && port === '4502') {
    url = `http://${domainName}:${port}${currentNodePipeline}.pipeline.json?type=compound`;
  } else if (domainName === 'localhost') {
    url = 'https://raw.githubusercontent.com/davide-mariotti/JSON/main/clinicaltrialST/clinicaltrials.json';
  } else {
    url = `http://${domainName}${currentNodePipeline}.pipeline.json?type=compound`;
  }

  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      localStorage.setItem('clinicalTrials', JSON.stringify(data));
    })
    .catch((error) => {
      console.error("Error copying data to local storage:", error);
      loadingSpinner.remove();
    });
};

function displayDataCompound() {
  const compoundData = JSON.parse(localStorage.getItem("clinicalTrials"));
  const resultsCompound = document.querySelector(".resultsclinicalTrials");
  resultsCompound.innerHTML = "";

  if (compoundData.length === 0) {
    resultsCompound.innerHTML = "No results found.";
    return;
  }

  const template = `
    <div class="headerPipeline">
        <div class="compoundPipeline">Compound</div>
        <div class="indicationPipeline">Indication</div>
        <div class="statusPipeline">Status</div>
        <div class="detailPipeline"></div>
    </div> 
    <div class="bodyPipeline">
        ${compoundData
          .map(
            (compound) => `
        <div class="rowPipeline compounds">
            <div class="compoundPipeline">${compound.compound}<br>
              <div class="popUpPipeline">
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#${compound.compound.replace(/[^a-zA-Z0-9]/g, "")}${compound.indication.replace(/[^a-zA-Z0-9]/g, "")}">Read More</button>
                <div class="modal fade" id="${compound.compound.replace(/[^a-zA-Z0-9]/g, "")}${compound.indication.replace(/[^a-zA-Z0-9]/g, "")}" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">${compound.compound}
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                              ${compound.description}
                            </div>
                        </div>
                    </div>
                </div>
              </div>
            </div>
            <div class="indicationPipeline">${compound["indication"]}</div>
            <div class="statusPipeline">${compound.status}</div>
            <div class="detailPipeline">
                <a href="${compound.clinicaltrials}" target="${compound.targetclinicaltrials}">${compound.labelclinicaltrials}</a>
            </div>
        </div>
        `
          )
          .join("")}
    </div>
    `;
  resultsCompound.innerHTML = template;
}

function addFirstLastClassesCompound() {
  const rows = Array.from(
    document.querySelectorAll(".rowPipeline.compounds")
  ).sort(
    (a, b) => a.getBoundingClientRect().top - b.getBoundingClientRect().top
  );

  const compounds = {};
  rows.forEach((row, i) => {
    const compound = row.querySelector(".compoundPipeline").textContent.trim();

    if (!compounds[compound]) {
      compounds[compound] = {
        first: i,
        last: i,
      };
    } else {
      compounds[compound].last = i;
    }
  });

  for (const compound in compounds) {
    const { first, last } = compounds[compound];
    if (first === last) {
      rows[first].classList.add("first", "last");
    } else {
      rows[first].classList.add("first");
      rows[last].classList.add("last");
    }
  }
}

const init = () => {
  copyDataFromJsonCompound();

  const dataCompound = JSON.parse(localStorage.getItem("clinicalTrials"));
  if (dataCompound && dataCompound.length > 0) {
    displayDataCompound();
    addFirstLastClassesCompound();
  } else {
    const intervalIdCompound = setInterval(() => {
      const dataCompound = JSON.parse(localStorage.getItem("clinicalTrials"));
      if (dataCompound && dataCompound.length > 0) {
        clearInterval(intervalIdCompound);
        displayDataCompound();
        addFirstLastClassesCompound();
      }
    }, 500);
  }
};

document.addEventListener("DOMContentLoaded", function () {
  const clinicalTrials = document.getElementById("clinicalTrials");
  if (clinicalTrials) {
    init();
  } else {
    console.log("noclinicalTrials");
  }
});
