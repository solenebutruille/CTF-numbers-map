import { blurMap } from "../map.js"

export function loadPurchaseHistory() {
    fetch('purchase-history.html')
        .then(response => {
            if (response.status === 401) {
                return ""
            }
            return response.text()
        })
        .then(html => {
            if (html !== "") {
                // Insert the login form HTML into the container
                document.getElementById('purchase-history').innerHTML = html;

                const historyButton = document.getElementById('history-button');
                const purchaseHistoryPopup = document.getElementById('purchase-history-popup');
                const purchaseHistoryDiv = document.getElementById('purchase-history');
                const searchInput = document.getElementById("purchase-history-search");
                const purchaseHistoryList = document.getElementById("purchase-history-list");

                historyButton.addEventListener('click', async () => {
                    await loadMarkers(purchaseHistoryList, "''");
                    purchaseHistoryPopup.style.display = "block";
                    purchaseHistoryPopup.classList.add('show');
                    purchaseHistoryDiv.style.display = "block";
                    purchaseHistoryDiv.classList.add('show');
                    blurMap();
                });
                
                let timeoutId;

                searchInput.addEventListener("input", () => {
                    // Clear the timeout if it exists
                    clearTimeout(timeoutId);

                    // Set a new timeout to debounce the event
                    timeoutId = setTimeout(async () => {
                        // Get the search query from the input
                        const searchQuery = searchInput.value;
                        if (searchQuery === "") await loadMarkers(purchaseHistoryList, "''");
                        else await loadMarkers(purchaseHistoryList, searchQuery);
                    }, 500); // Debounce for 500 milliseconds
                });

            }

        });
}

function clearList(list){
    list.innerHTML = "";
}

function addItemsToList(fileNames, purchaseHistoryList){
    fileNames.forEach((fileName) => {
        const listItem = document.createElement('li');
        const link = document.createElement('a');
        link.style.color = "white";
        link.setAttribute('href', `/invoice/${fileName}`);
        link.innerText = `${fileName}`;
        listItem.appendChild(link);
        purchaseHistoryList.appendChild(listItem);
    });
}

async function loadMarkers(purchaseHistoryList, searchQuery){
    const response = await fetch('/loadPurchaseHistory', {
        method: 'POST',
        body: JSON.stringify({ substring: searchQuery }),
        headers: {
            'Content-Type': 'application/json'
        }
    })
    const markersID = await response.json();
    clearList(purchaseHistoryList);
    addItemsToList(markersID, purchaseHistoryList);
}