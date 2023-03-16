/* eslint-disable max-len */
const copyDataFromJsonCompound = () => {
  const domainName = window.location.hostname;
  const port = window.location.port;
  const currentNodePipeline = document.querySelector('.currentNodePipeline').value;
  let url;

  if (domainName === 'localhost' && port === '4502') {
    url = `http://${domainName}:${port}${currentNodePipeline}.pipeline.json?type=compound`;
  } else if (domainName === 'localhost') {
    url = 'https://raw.githubusercontent.com/davide-mariotti/JSON/main/pipelineST/compound.json';
  } else {
    url = `http://${domainName}${currentNodePipeline}.pipeline.json?type=compound`;
  }

  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      localStorage.setItem('compoundData', JSON.stringify(data));
    })
    .catch((error) => console.error(error));
};

const copyDataFromJsonIndication = () => {
  const domainName = window.location.hostname;
  const port = window.location.port;
  const currentNodePipeline = document.querySelector('.currentNodePipeline').value;
  let url;

  if (domainName === 'localhost' && port === '4502') {
    url = `http://${domainName}:${port}${currentNodePipeline}.pipeline.json?type=indication`;
  } else if (domainName === 'localhost') {
    url = 'https://raw.githubusercontent.com/davide-mariotti/JSON/main/pipelineST/indication.json';
  } else {
    url = `http://${domainName}${currentNodePipeline}.pipeline.json?type=indication`;
  }

  fetch(url)
    .then((response) => response.json())
    .then((data) => {
      localStorage.setItem('indicationData', JSON.stringify(data));
    })
    .catch((error) => console.error(error));
};

function displayDataCompound() {
  const compoundData = JSON.parse(localStorage.getItem("compoundData"));
  const resultsCompound = document.querySelector(".resultsCompound");
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
      <div class="clinicalTrialsPipeline">Clinical Trials</div>
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
          <div class="developmentStagePipeline">
              <div class="developmentStageSteps">
                <div class="dsHeader">
                  <div class="label P ${compound.enablestage1}">P</div>
                  <div class="label Ia ${compound.enablestage2}">Ia</div>
                  <div class="label Ib ${compound.enablestage3}">Ib</div>
                  <div class="label II ${compound.enablestage4}">II</div>
                  <div class="label III ${compound.enablestage5}">III</div>
                  <div class="label F ${compound.enablestage6}">F</div>
                  <div class="label A ${compound.enablestage7}">A</div>
                </div>
                <div class="dsBody">
                  <div class="flow P ${compound.enablestage1}"></div>
                  <div class="flow Ia ${compound.enablestage2}"></div>
                  <div class="flow Ib ${compound.enablestage3}"></div>
                  <div class="flow II ${compound.enablestage4}"></div>
                  <div class="flow III ${compound.enablestage5}"></div>
                  <div class="flow F ${compound.enablestage6}"></div>
                  <div class="flow A ${compound.enablestage7}"></div>
                </div>
              </div>
          </div>
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

function displayDataIndication() {
  const indicationData = JSON.parse(localStorage.getItem("indicationData"));
  const resultsIndication = document.querySelector(".resultsIndication");
  resultsIndication.innerHTML = "";

  if (indicationData.length === 0) {
      resultsIndication.innerHTML = "No results found.";
      return;
  }

  const template = `
  <div class="headerPipeline">
      <div class="indicationPipeline">Indication</div>
      <div class="compoundPipeline">Compound</div>
      <div class="mechanismOfActionPipeline">Mechanism Of Action</div>
      <div class="developmentStagePipeline">Development Stage</div>
      <div class="clinicalTrialsPipeline">Clinical Trials</div>
      <div class="popUpPipeline"></div>
  </div> 
  <div class="bodyPipeline">
      ${indicationData
        .map(
          (indication) => `
      <div class="rowPipeline indications">
          <div class="indicationPipeline">${indication["indication"]}</div>
          <div class="compoundPipeline">${indication.compound}</div>
          <div class="mechanismOfActionPipeline">${
            indication.mechanismofaction
          }</div>
          <div class="developmentStagePipeline">
              <div class="developmentStageSteps">
                <div class="dsHeader">
                  <div class="label P ${indication.enablestage1}">P</div>
                  <div class="label Ia ${indication.enablestage2}">Ia</div>
                  <div class="label Ib ${indication.enablestage3}">Ib</div>
                  <div class="label II ${indication.enablestage4}">II</div>
                  <div class="label III ${indication.enablestage5}">III</div>
                  <div class="label F ${indication.enablestage6}">F</div>
                  <div class="label A ${indication.enablestage7}">A</div>
                </div>
                <div class="dsBody">
                  <div class="flow P ${indication.enablestage1}"></div>
                  <div class="flow Ia ${indication.enablestage2}"></div>
                  <div class="flow Ib ${indication.enablestage3}"></div>
                  <div class="flow II ${indication.enablestage4}"></div>
                  <div class="flow III ${indication.enablestage5}"></div>
                  <div class="flow F ${indication.enablestage6}"></div>
                  <div class="flow A ${indication.enablestage7}"></div>
                </div>
              </div>
          </div>
          <div class="clinicalTrialsPipeline">
              <a href="${indication.clinicaltrials}" target="${indication.targetclinicaltrials}">${indication.labelclinicaltrials}</a>
          </div>
          <div class="popUpPipeline">
              <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#${indication.indication.replace(/[^a-zA-Z0-9]/g, "")}${indication.compound.replace(/[^a-zA-Z0-9]/g, "")}">Read More</button>
              <div class="modal fade" id="${indication.indication.replace(/[^a-zA-Z0-9]/g, "")}${indication.compound.replace(/[^a-zA-Z0-9]/g, "")}" tabindex="-1" aria-hidden="true">
                  <div class="modal-dialog modal-dialog-centered">
                      <div class="modal-content">
                          <div class="modal-header">${indication.indication}
                              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                          </div>
                          <div class="modal-body">
                              ${indication.readmore}
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
  resultsIndication.innerHTML = template;
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

function addFirstLastClassesIndication() {
  const rows = document.querySelectorAll(".rowPipeline.indications");
  const indications = {};
  rows.forEach((row, i) => {
      const indication = row
          .querySelector(".indicationPipeline")
          .textContent.trim();

      if (!indications[indication]) {
          indications[indication] = {
              first: i,
              last: i
          };
      } else {
          indications[indication].last = i;
      }
  });
  for (const indication in indications) {
      const {
          first,
          last
      } = indications[indication];
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

function toggleResults() {
  const compoundBtn = document.querySelector(".compound-btn");
  const indicationBtn = document.querySelector(".indication-btn");
  const compoundResults = document.querySelector(".resultsCompound");
  const indicationResults = document.querySelector(".resultsIndication");

  compoundBtn.addEventListener("click", () => {
      compoundResults.classList.add("show");
      indicationResults.classList.remove("show");
      compoundBtn.classList.add("active");
      indicationBtn.classList.remove("active");
  });

  indicationBtn.addEventListener("click", () => {
      compoundResults.classList.remove("show");
      indicationResults.classList.add("show");
      compoundBtn.classList.remove("active");
      indicationBtn.classList.add("active");
  });
}

const init = () => {
    toggleResults();
    copyDataFromJsonCompound();
    copyDataFromJsonIndication();
  
    const dataCompound = JSON.parse(localStorage.getItem("compoundData"));
    if (dataCompound && dataCompound.length > 0) {
      displayDataCompound();
      addFirstLastClassesCompound();
    } else {
      const intervalIdCompound = setInterval(() => {
        const dataCompound = JSON.parse(localStorage.getItem("compoundData"));
        if (dataCompound && dataCompound.length > 0) {
          clearInterval(intervalIdCompound);
          displayDataCompound();
          addFirstLastClassesCompound();
        }
      }, 500);
    }  
    
    const dataIndication = JSON.parse(localStorage.getItem("indicationData"));
    if (dataIndication && dataIndication.length > 0) {
      displayDataIndication();
      addFirstLastClassesIndication();
    } else {
      const intervalIdIndication = setInterval(() => {
        const dataIndication = JSON.parse(localStorage.getItem("indicationData"));
        if (dataIndication && dataIndication.length > 0) {
          clearInterval(intervalIdIndication);
          displayDataIndication();
          addFirstLastClassesIndication();
        }
      }, 500);
    }
  
    addLastClassToLastTrue();
  };
  

document.addEventListener("DOMContentLoaded", function() {
  const pipeline = document.getElementById("pipeline");
  if (pipeline) {
      init();
  } else {
      console.log("noPipeline");
  }
});
