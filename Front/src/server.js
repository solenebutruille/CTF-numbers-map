const express = require('express');
const fetch = require('isomorphic-fetch');
const FormData = require('form-data');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();
var port = process.env.PORT;
const backEndURL = process.env.BACKEND_URL;

if (!port) {
  port = 80;
}

if (!backEndURL) {
  console.error('BACKEND_URL environment variable is not set. Please set it before running the application.');
  process.exit(1); // Exit the application with an error code (non-zero) to indicate failure
}

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Simulating user authentication
let isLoggedIn = false;
let userID = null;

// Middleware function to check if the user is logged in
function requireLogin(req, res, next) {
  if (isLoggedIn) {
    next(); 
  } else {
    res.status(401).send('Unauthorized');
  }
}

app.use(bodyParser.text({ type: 'application/xml' }));

app.post('/login', async (req, res) => {

  const response = await fetch(`${backEndURL}/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/xml'
    },
    body: req.body
  }).catch(error => console.error(error));

  const responseData = await response.json();
  const responseHeaders = await response.headers.get("set-cookie")

  if (responseData.authenticated) {
    isLoggedIn = true
    userID = responseData.userID 
  }
  res.setHeader("set-cookie", responseHeaders)
  res.json(responseData)
});

app.post('/loadPurchaseHistory', async (req, res) => {

  const { substring } = req.body;
  const formData = new FormData();
  const rawHeaders = req.rawHeaders;
  const sessionCookieHeader = rawHeaders.find(header => header.includes('sessionCookie'));
  formData.append('substring', substring)

  const response = await fetch(`${backEndURL}/searchFileForUserID`, {
    method: 'POST',
    headers: {
      'Cookie': sessionCookieHeader
    },
    body: formData
  });

  const responseData = await response.json();
  res.json(responseData)
});

app.post('/addMarker', async (req, res) => {

  const { number, coordinates, message } = req.body;
  const updatedBody = { number, coordinates, message, userID };
  const rawHeaders = req.rawHeaders;
  const sessionCookieHeader = rawHeaders.find(header => header.includes('sessionCookie'));

  const response = await fetch(`${backEndURL}/addMarker`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Cookie': sessionCookieHeader
    },
    body: JSON.stringify(updatedBody)
  });

  const responseData = await response.text();
  res.send(responseData)
});

app.post('/logout', async (req, res) => {
    isLoggedIn = false
    userID = null
    const rawHeaders = req.rawHeaders;
    const sessionCookieHeader = rawHeaders.find(header => header.includes('sessionCookie'));

    await fetch(`${backEndURL}/logout`, {
      method: 'POST',
      headers: {
        'Cookie': sessionCookieHeader
      },
    });
});

app.get('/markers', async (req, res) => {
  const response = await fetch(`${backEndURL}/markers`, {method: 'GET'}).catch(error => console.error(error));

  const responseData = await response.json();
  res.json(responseData)
});

app.get('/getNextAvailableNumber', async (req, res) => {
  const response = await fetch(`${backEndURL}/getNextAvailableNumber`, {method: 'GET'}).catch(error => console.error(error));

  const responseData = await response.json();
  const responseHeaders = await response.headers.get("set-cookie")

  res.setHeader("set-cookie", responseHeaders)
  res.json(responseData)
});

// Define the route for downloading the file
app.get('/invoice/:fileName', async (req, res) => {
  const fileName = req.params.fileName;
  const response = await fetch(`${backEndURL}/invoice/${fileName}`, {
    method: 'GET'
  });
  const fileContent = await response.text();
  res.setHeader('Content-Type', 'text/plain');
  res.setHeader('Content-Disposition', `attachment; filename="${fileName}"`);
  res.send(fileContent);
});

app.get('/', (req, res) => {
  res.sendFile('index.html', { root: __dirname });
});

app.get('/styles.css', (req, res) => {
  res.sendFile('styles.css', { root: __dirname });
});

app.get('/login-form.html', (req, res) => {
  res.sendFile('/forms/login-form.html', { root: __dirname });
});

app.get('/login.js', (req, res) => {
  res.sendFile('/forms/login.js', { root: __dirname });
});

app.get('/purchase-history.css', (req, res) => {
  res.sendFile('/purchase-history/purchase-history.css', { root: __dirname });
});

app.get('/purchase-history.html', requireLogin, (req, res) => {
  res.sendFile('/purchase-history/purchase-history.html', { root: __dirname });
});

app.get('/purchase-history.js', (req, res) => {
  res.sendFile('/purchase-history/purchase-history.js', { root: __dirname });
});

app.get('/purchase-history/purchase-history', (req, res) => {
  res.sendFile('/purchase-history/purchase-history.js', { root: __dirname });
});

app.get('/map.js', (req, res) => {
  res.sendFile('map.js', { root: __dirname });
});

app.get('/buy-number-form.js', (req, res) => {
  res.sendFile('/forms/buy-number-form.js', { root: __dirname });
});

app.get('/buy-number-form.html', (req, res) => {
  res.sendFile('/forms/buy-number-form.html', { root: __dirname });
});

app.get('/forms.css', (req, res) => {
  res.sendFile('/forms/forms.css', { root: __dirname });
});

app.get('/logo.png', (req, res) => {
  res.sendFile('/logo.png', { root: __dirname });
});

app.get('/font/Inter-Regular.otf', (req, res) => {
  res.sendFile('/font/Inter-Regular.otf', { root: __dirname });
});

app.listen(port, () => {
  console.log(`Now listening on port ${port}`);
});
