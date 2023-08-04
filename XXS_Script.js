// File to run in order to simulate an admin clicks on links
// To run script, run : $ node XXS_Script.js
const puppeteer = require('puppeteer');
const appUrl = "test-qpplicqtion-ctf.azurewebsites.net";
const flag = "CTF{admin_flag}";

(async () => {
  try {

    const sleep = (milliseconds) => new Promise((resolve) => setTimeout(resolve, milliseconds));
    //Good for debug purpuses, opens the browser and lets you look 
    //const browser = await puppeteer.launch({ headless: true }); // Set headless: true for an invisible browser
    const browser = await puppeteer.launch({ headless: "new" }); // Set headless: true for an invisible browser
    const page = await browser.newPage();
    await page.goto(`http://${appUrl}`);
    const cookie = {
      name: 'sessionCookie',
      value: flag,
      domain: appUrl, // Replace with the correct domain of your website
      path: '/', // Replace with the path where the cookie should be sent
      expires: Date.now() + 86400000, // Replace with the expiration date in milliseconds (e.g., 24 hours from now)
    };
    await page.setCookie(cookie);
    const timeout = 1000;
    //Zoom out to maximum so that all the numbers are visible
    for (let i = 0; i < 4; i++) {
      await page.mouse.wheel({ deltaY: 500 }); // You can adjust the deltaY value for different zoom levels
    }

    while(true){
      var response = await fetch(`http://${appUrl}/getNextAvailableNumber`, {method: 'GET'}).catch(error => console.error(error));
      var nextAvailableNumber = await response.text();
      for (var i = 0; i < nextAvailableNumber; i++){
        try {
          const str = `div ::-p-text(${i})`;
          const element = await page.waitForSelector(str);
          await element.click();
          console.log("successful click on number " + i);
          //good for debug
          //await sleep(timeout);
        }catch{
          console.log("number" + i + "does not exist");
        }
      }
      await page.reload();
      //Zoom out to maximum so that all the numbers are visible
      for (let i = 0; i < 4; i++) {
        await page.mouse.wheel({ deltaY: 500 });
      }
  }
    
    await browser.close();
  } catch (err) {
    console.error('An error occurred:', err);
  }
})();


