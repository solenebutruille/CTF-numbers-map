package com.bfm.controller;

import com.features.Invoices;
import com.features.Login;
import com.features.Markers;
import com.util.Marker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.features.SessionCookies.isCookieValid;
import static com.util.InitAplication.initApplication;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class HelloController {

    Invoices invoices = new Invoices();
    Login login = new Login();
    Markers markers = new Markers();

    /*
    <!DOCTYPE replace [<!ENTITY ent SYSTEM "file:./flag.flg"> ]>
    <userInfo>
     <username>&ent;</username>
     <password>&ent;</password>
    </userInfo> */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody String xml, HttpServletResponse response) {
        return login.login(xml, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Boolean> logout(@CookieValue(value = "sessionCookie", defaultValue = "wrongSessionCookie") String sessionCookie) {
        if (login.logout(sessionCookie)) return ResponseEntity.ok(true);
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }

    @GetMapping("/markers")
    public List<Marker> getMarkers() {
        return markers.getMarkers();
    }

    // <iframe src="javascript:alert(document.cookie);"></iframe>
    @PostMapping("/addMarker")
    public ResponseEntity<String> addMarker(@RequestBody Marker marker, @CookieValue(value = "sessionCookie", defaultValue = "wrongSessionCookie") String sessionCookie) throws SQLException, ClassNotFoundException {
        try {
            int userID = isCookieValid(sessionCookie);
            if(isCookieValid(sessionCookie) == -1){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            } else {
                marker.setUserID(userID);
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
        return ResponseEntity.ok(markers.addMarker(marker));
    }

    @GetMapping(value = "/invoice/{markerId}")
    public void downloadFile(@PathVariable("markerId") String markerId, HttpServletResponse response) throws IOException, SQLException, ClassNotFoundException {
        invoices.downloadInvoice(markerId, response);
    }

    @GetMapping("/getNextAvailableNumber")
    public int getNextAvailableNumber() throws ClassNotFoundException, SQLException {
        return markers.getNextAvailableMarker();
    }

    @PostMapping("/searchFileForUserID")
    public ResponseEntity<List<String>> searchFileForUserID(@RequestParam String substring, @CookieValue(value = "sessionCookie", defaultValue = "defaultValue") String sessionCookie) {
        int userID;
        try {
            userID = isCookieValid(sessionCookie);
            if(isCookieValid(sessionCookie) == -1){
                return (ResponseEntity<List<String>>) ResponseEntity.status(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e){
            return (ResponseEntity<List<String>>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(invoices.searchInvoiceFromUserID(userID, substring));
    }

    @PostMapping("/resetDatabase/{password}")
    public boolean resetDatabase(@PathVariable("password") String password){
        String resetPassword = System.getenv("RESET_PASSWORD");
        if (resetPassword == null){
            resetPassword = "THIS_IS_THE_RESET_PASSWORD";
        }
        if (password.equals(resetPassword)) {
            try {
                initApplication();
                return true;
            } catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
