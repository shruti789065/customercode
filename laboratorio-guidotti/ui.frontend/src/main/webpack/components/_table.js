document.addEventListener("DOMContentLoaded", function () {
    function checkTables() {
        var cmpTables = document.querySelectorAll(".cmp-table");
        cmpTables.forEach(cmpTable => {
            if (cmpTable) {
                console.log("Tabella trovata");
                initTable(cmpTable);
            } else {
                console.log("Tabella non trovata");
            }
        });
    }

    function initTable(table) {
        var columnCount = getColumnCount(table);
        if (columnCount) {
            console.log("La tabella ha " + columnCount + " colonne");
            adjustForMobile(table, columnCount);
        } else {
            console.log("Numero di colonne non identificato");
        }
    }

    function getColumnCount(table) {
        if (table.classList.contains("cmp-table--2-column")) {
            return 2;
        } else if (table.classList.contains("cmp-table--3-column")) {
            return 3;
        } else if (table.classList.contains("cmp-table--4-column")) {
            return 4;
        } else if (table.classList.contains("cmp-table--5-column")) {
            return 5;
        }
        return null;
    }

    function adjustForMobile(table, columnCount) {
        var headers = Array.from(table.querySelectorAll(".cmp-table--row-header .cmp-title__text")).map(header => header.textContent);
        var rows = table.querySelectorAll(".cmp-table--row");

        rows.forEach(row => {
            if (!row.classList.contains("cmp-table--row-header")) {
                var cells = row.querySelectorAll(".cmp-table--cell");
                cells.forEach((cell, index) => {
                    var existingHeader = cell.querySelector(".cmp-mobile-header");
                    if (existingHeader) {
                        cell.removeChild(existingHeader);
                    }
                    if (index < columnCount && window.innerWidth <= 768) {
                        var headerText = headers[index];
                        var headerDiv = document.createElement("div");
                        headerDiv.classList.add("cmp-mobile-header");
                        headerDiv.textContent = headerText;
                        cell.insertBefore(headerDiv, cell.firstChild);
                    }
                });
            } else {
                row.style.display = window.innerWidth <= 768 ? "none" : "";
            }
        });
    }

    checkTables();
    window.addEventListener("resize", checkTables);
});
