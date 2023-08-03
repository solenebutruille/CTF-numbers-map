import {initMap, getZoomAndCenter, unblurMap, blurMap} from "../map.js"
const buyButton = document.querySelector('#buy-number');

var buyNumberForm;

// Load the login form HTML from the separate file
fetch('buy-number-form.html')
    .then(response => response.text())
    .then(html => {
        // Insert the login form HTML into the container
        document.getElementById('buy-number-form-container').innerHTML = html;
        buyNumberForm = document.getElementById('buy-number-form');
        buyNumberForm.addEventListener('submit', async (event) => {
            event.preventDefault(); // Prevent form from submitting normally

            const id = buyNumberForm.elements.id.value;
            const latitude = buyNumberForm.elements.latitude.value;
            const longitude = buyNumberForm.elements.longitude.value;
            const message = buyNumberForm.elements.message.value;

            // Create a new marker object from the input values
            const newMarker = { number: parseInt(id), coordinates: { lat: parseFloat(latitude), lng: parseFloat(longitude) }, message: message };

            // Send a POST request to the server to add the new marker
          fetch('/addMarker', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newMarker)
            })
                .then(response => {
                    return response.text()
                })
                .then(data => {
                    if (data === "") {
                        var zoom;
                        var center;
                        [zoom, center] = getZoomAndCenter();
                        initMap(zoom, [center.lng, center.lat]);
                        buyNumberForm.style.display = "none";
                        unblurMap();
                    } else {
                        const errorMessageSpace = document.getElementById("error-message-space");
                        errorMessageSpace.innerHTML = data;
                    }
                })
                .catch(error => {
                    const errorMessageSpace = document.getElementById("error-message-space");
                        errorMessageSpace.innerHTML = "error";
                });
        });
    });

buyButton.addEventListener('click', async function () {
    const response = await fetch("/getNextAvailableNumber");
  const id = document.getElementById("nextMarkerId");
  id.value = await response.text();
    buyNumberForm.style.display = "block";
    blurMap();
});