/* eslint-disable max-len */
const copyDataFromJsonCompound = () => {
    //const domainName = window.location.hostname;
    //const currentNodePipeline = document.querySelector('.currentNodePipeline').value;
    //const url = `http://${domainName}${currentNodePipeline}.pipeline.json?type=compound`;
    const url = `https://raw.githubusercontent.com/davide-mariotti/JSON/main/clinicaltrialST/clinicaltrials.json`;
  
    fetch(url)
      .then((response) => response.json())
      .then((data) => {
        localStorage.setItem('clinicalTrials', JSON.stringify(data));
      })
      .catch((error) => console.error(error));
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
        <div class="mechanismOfActionPipeline">Mechanism Of Action</div>
        <div class="indicationPipeline">Indication</div>
        <div class="developmentStagePipeline">Development Stage</div>
        <div class="popUpPipeline"></div>
    </div> 
    <div class="bodyPipeline">
        ${compoundData
          .map(
            (compound) => `
        <div class="rowPipeline compounds">
            <div class="compoundPipeline">${compound.compound}</div>
            <div class="mechanismOfActionPipeline">${
              compound.mechanismofaction
            }</div>
            <div class="indicationPipeline">${compound["indication"]}</div>
            <div class="clinicalTrialsPipeline">
                <a href="${compound.clinicaltrials}" target="${
              compound.targetclinicaltrials
            }">${compound.labelclinicaltrials}</a>
            </div>
            <div class="popUpPipeline">
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#${compound.compound.replace(/[^a-zA-Z0-9]/g, "")}${compound.indication.replace(/[^a-zA-Z0-9]/g, "")}">Read More</button>
                <div class="modal fade" id="${compound.compound.replace(/[^a-zA-Z0-9]/g, "")}${compound.indication.replace(/[^a-zA-Z0-9]/g, "")}" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">${compound.compound}
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                ${compound.readmore}
                            </div>
                        </div>
                    </div>
                </div>
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
    const rows = document.querySelectorAll(".rowPipeline.compounds");
    const compounds = {};
    rows.forEach((row, i) => {
        const compound = row.querySelector(".compoundPipeline").textContent.trim();
  
        if (!compounds[compound]) {
            compounds[compound] = {
                first: i,
                last: i
            };
        } else {
            compounds[compound].last = i;
        }
    });
    for (const compound in compounds) {
        const {
            first,
            last
        } = compounds[compound];
        if (first === last) {
            rows[first].classList.add("first", "last");
        } else {
            rows[first].classList.add("first");
            rows[last].classList.add("last");
        }
    }
  }
 
  function addLastClassToLastTrue() {
    const dsBodies = document.querySelectorAll(".dsBody");
    dsBodies.forEach((dsBody) => {
        const trueDivs = dsBody.querySelectorAll(".true");
        const lastTrueDiv = trueDivs[trueDivs.length - 1];
        lastTrueDiv.classList.add("last");
    });
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
    
      addLastClassToLastTrue();
    };    
  
  document.addEventListener("DOMContentLoaded", function() {
    const clinicalTrials = document.getElementById("clinicalTrials");
    if (clinicalTrials) {
        init();
    } else {
        console.log("noclinicalTrials");
    }
  });
  