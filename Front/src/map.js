var mapboxgl = window.mapboxgl;
var map;
mapboxgl.accessToken = 'pk.eyJ1IjoiamVzc2ljYWJlbGxldmlsbGUiLCJhIjoiY2xmNXFyNGtzMG83aDN0cjAyc2x0OHh2biJ9.2h7LEINNyiT_DXReBlpk7g';

const mapItem =  document.getElementById("map");

initMap(2, [0,0]);

export function initMap(zoom, center) {
    map = new mapboxgl.Map({
        container: 'map',
        style: 'mapbox://styles/mapbox/dark-v10',
        center: center,
        zoom: zoom,
        interactive: true 
    });

    loadMarkers()

    map.on('zoom', function () {
        var maxZoom = 2; // Set the maximum zoom level here
        if (map.getZoom() <= maxZoom) {
            document.getElementById('buttons').classList.add('visible');
        } else {
            document.getElementById('buttons').classList.remove('visible');
        }
    });

    map.on('click', function(e) {
        var loginForm = document.getElementById('login-form');
        loginForm.classList.remove('show');
        loginForm.style.display = "none";
        var buyNumberForm = document.getElementById('buy-number-form');
        buyNumberForm.classList.remove('show');
        buyNumberForm.style.display = "none";
        var purchaseHistory = document.getElementById('purchase-history');
        purchaseHistory.classList.remove('show');
        purchaseHistory.style.display = "none";
        unblurMap();
    });

    map.on('contextmenu', function (e) {
        var lngLat = e.lngLat;
        alert('Longitude: ' + lngLat.lng + ', Latitude: ' + lngLat.lat);
      });
      
}

export function getZoomAndCenter(){
    return [map.getZoom(), map.getCenter()];
}

export function loadMarkers() {
    fetch('/markers')
        .then(response => response.json())
        .then(data => iterateMarkers(map, data))
        .catch(error => console.error(error));
}

export function blurMap(){
    mapItem.classList.add('blur');
}

export function unblurMap(){
    mapItem.classList.remove('blur');
}

function iterateMarkers(map, markers) {
    for (const marker of markers) {
        addMarker(map, marker.number, marker.coordinates, marker.message)
    }
}

function addMarker(map, number, coordinates, message) {
    // Define the marker icon
    var el = document.createElement('div');
    el.className = 'marker';
    el.textContent = number;

    // Add the marker to the map
    var marker = new mapboxgl.Marker({
        element: el
    })
        .setLngLat([coordinates.lng, coordinates.lat])
        .addTo(map);

    marker.setPopup(new mapboxgl.Popup({ closeOnClick: true })
        .setHTML(getMarkerPopUp(message))
        .setMaxWidth("300px")
    );
}

function getMarkerPopUp(message){
  //  if (message) message = message.replace(/[^a-z0-9]/gi, '');
  //  if (message === "") return "No message for this number"
    //else 
    return message
}