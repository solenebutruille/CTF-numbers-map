// File to run in order to simulate an admin clicks on links

const puppeteer = require('puppeteer');

(async () => {
  try {

    const sleep = (milliseconds) => new Promise((resolve) => setTimeout(resolve, milliseconds));

    const response = await fetch(`http://localhost:8080/getNextAvailableNumber`, {method: 'GET'}).catch(error => console.error(error));
    const nextAvailableNumber = await response.text();

    const browser = await puppeteer.launch({ headless: false }); // Set headless: true for an invisible browser
    const page = await browser.newPage();
    await page.goto('http://localhost:8080');
    // Add a new cookie to the page
    const cookie = {
      name: 'sessionCookie',
      value: 'CTF{FLAG}',
      domain: 'localhost', 
      path: '/', 
      expires: Date.now() + 86400000, /
    };

    await page.setCookie(cookie);

    const timeout = 5000;
    for (var i = 0; i < nextAvailableNumber; i++){
      try {
        const str = `div ::-p-text(${i})`;
        const element = await page.waitForSelector(str);
        await element.click();
        console.log("successful click on number " + i);
          console.log("Start");
          await sleep(5000);
          console.log("After 5 seconds");
      }catch{
        console.log("number" + i + "does not exist");
      }
      
    }
    
    await browser.close();
  } catch (err) {
    console.error('An error occurred:', err);
  }
})();

