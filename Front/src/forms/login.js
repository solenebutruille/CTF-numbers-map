import { loadPurchaseHistory } from "../purchase-history/purchase-history"
import {unblurMap, blurMap} from "../map.js"


const loginButton = document.getElementById('login-button');
const logoutButton = document.getElementById('logout-button');
const purchaseButton = document.getElementById('history-button');
const buyButton = document.querySelector('#buy-number');
var loginForm;

// Load the login form HTML from the separate file
fetch('login-form.html')
    .then(response => response.text())
    .then(html => {
        // Insert the login form HTML into the container
        document.getElementById('login-form-container').innerHTML = html;
        loginForm = document.getElementById('login-form');
        const errorMessage = document.getElementById('errorMessage');

        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const formData = new FormData(loginForm);
            const xmlString = '<form-data>' + Array.from(formData.entries())
                .map(([name, value]) => `<${name}>${value}</${name}>`).join('') + '</form-data>';

          /*  const response = await fetch('/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/xml'
                },
                body: xmlString
            }); */
            const response = await fetch('/login', {
                method: 'POST',
                headers: {
                'Content-Type': 'application/xml'
                },
                body: xmlString
            });

            const responseData = await response.json();
            
             

            if (responseData.authenticated) {
                showLoggedInButtons();
                loadPurchaseHistory();
                loginForm.classList.remove('show');
                loginForm.style.display = "none";
                unblurMap();
            } else {
                errorMessage.innerHTML = responseData.message;
            }
        });
    });

loginButton.addEventListener('click', function () {
    loginForm.classList.add('show');
    document.getElementById("login-form").style.display = "block";
    blurMap();
});

logoutButton.addEventListener('click', async function () {
    showLoggedOutButtons();
    await fetch('/logout', {
        method: 'POST'
    });
});

function showLoggedInButtons() {
    loginButton.style.display = 'none';
    logoutButton.style.display = 'inline-block';
    purchaseButton.style.display = 'inline-block';
    buyButton.style.display = 'inline-block';
}

function showLoggedOutButtons() {
    loginButton.style.display = 'block';
    logoutButton.style.display = 'none';
    purchaseButton.style.display = 'none';
    buyButton.style.display = 'none';
}
