/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable max-len */
import $, {
	event
} from "jquery";
import {
	showOverlayAndLoader,
	hideOverlayAndLoader
} from "../../site/_util";

$(function () {
	const SearchMedicine = {
		url: "",
		searchResults: null,
		query: "",
		input: null,
		searchButton: null,
		resultsContainer: null,
		currentPageSearch: "",
		errorMsg: null,
		farmacovigilanzaForm: null,
		resultTitle: null,

		init() {
			this.initializeVariables();
			this.setupEventListeners();
		},

		initializeVariables() {
			// current page path
			this.currentPageSearch = $(".currentPageSearchMedicine").val();
			// input value
			this.input = $("#search-medicine-input");
			//result and error msg container
			this.resultsContainer = document.querySelector(".farmacovigilanzaResult");
			this.errorMsg = document.querySelector(".farmacovigilanzaError");
			this.resultTitle = document.querySelector(".resultTitle");
			// search Button
			this.searchButton = $("#search-medicine-button");

			//core form container con id
			this.farmacovigilanzaForm = document.querySelector("#farmacovigilanzaForm");
		},

		callPIMAPI(query, domainName, port, protocol, currentPageSearch) {
			const url =
				domainName === "localhost" ? `${protocol}//${domainName}:${port}${currentPageSearch}.searchMedicineResult.json?medicineName=${query}` :
				`${protocol}//${domainName}${currentPageSearch}.searchMedicineResult.json?medicineName=${query}`;

			fetch(url)
				.then((response) => response.json())
				.then((data) => {
					// ERROR
					if (data === null) {
						this.errorMsg.style.display = "grid";
						return;
					}
					if (data.name.length === 0) {
						this.errorMsg.style.display = "grid";
						return;
					}
					this.resultTitle.style.display = "block";
					this.resultsContainer.style.display = "block";
					if(this.farmacovigilanzaForm){
						this.farmacovigilanzaForm.style.display = "block";
					}
					

					let radioBtn = "";
					radioBtn = data.name.map(
							(result, index) => ` <input type="radio" name="product" value="${result}" ${index === 0 ? 'checked': ''}>
							  <label for="${result}">${result}</label><br>`
						)
						.join("");
					this.resultsContainer.innerHTML = radioBtn;
					this.radioBtnAction();
				})
				.catch((error) => {
					console.error("Error copying data to local storage:", error);
				});
		},

		setupEventListeners() {
			this.searchButton.on("click", () => {
				this.performSearch();
			});

			this.input.on("keydown", (event) => {
				if (event.keyCode === 13) {
					this.performSearch();
				}
			});
		},

		performSearch() {
			this.query = this.input.val().toLowerCase().trim();
			if(this.resultsContainer && this.errorMsg && this.resultTitle){
				this.resultsContainer.innerHTML = "";
				this.resultsContainer.style.display = "none";
				this.errorMsg.style.display = "none";
				this.resultTitle.style.display = "none";
			}
			if(this.farmacovigilanzaForm){
				this.farmacovigilanzaForm.style.display = "none";
			}
			if (this.query != "") {
				this.callPIMAPI(
					this.query,
					window.location.hostname,
					window.location.port,
					window.location.protocol,
					this.currentPageSearch
				);
			}

		},

		radioBtnAction() {
			let radioBtns = document.querySelectorAll(".farmacovigilanzaResult input[type='radio']");
			if (radioBtns) {
				radioBtns.forEach(radio => {
					const label = radio.nextElementSibling;
					radio.addEventListener("change", event => {
						const myTarget = event.target;
						if (myTarget.checked) {
							myTarget.setAttribute('checked', 'checked');
							radioBtns.forEach(otherRadio => {
								if (otherRadio !== myTarget) {
									otherRadio.removeAttribute('checked');
								}
							});
						}
					});
					label.addEventListener("click", () => {
						radio.checked = true; // Check the associated radio button when label is clicked
						radio.dispatchEvent(new Event("change")); // Dispatch change event to ensure consistency with change event handler
					});
				});
			}
		},

	};
	SearchMedicine.init();
});
/* eslint-disable max-len */

