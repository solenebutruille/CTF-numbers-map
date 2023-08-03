package com.features;

import com.util.AuthResponse;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class Login {

    SessionCookies sessionCookies;

    public Login() {
        sessionCookies = new SessionCookies();
    }

    public HashMap checkCredentials(String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "READ_ONLY_USER", "123ghfjdfhj7382tvFTYF66");
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, USERNAME FROM USERS where " +
                                                                        "username = '" +username+ "'" +
                                                                        " and password = '" +password +"'");
        ResultSet resultSet = preparedStatement.executeQuery();
        HashMap res = new HashMap<>();
        if (resultSet.next()) {
            res.put("ID", resultSet.getString(1));
            res.put("USERNAME", resultSet.getString(2));
        }
        preparedStatement.close();
        connection.close();
        return res;
    }

    public ResponseEntity<Object> login(String xml, HttpServletResponse response) {
        String userName = "";
        try {
            SAXBuilder builder = new SAXBuilder();
            InputStream stream = new ByteArrayInputStream(xml.getBytes());
            Document document = builder.build(stream);
            Element root = document.getRootElement();

            String password = root.getChild("password").getText();
            String username = root.getChild("username").getText();
            HashMap<String, String> credentials = checkCredentials(username, password);
            String userID;
            if (!credentials.isEmpty()) {
                userID = credentials.get("ID");
                userName = credentials.get("USERNAME");
                userName = Jsoup.clean(userName, Whitelist.none());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Error for username " + username, false, -1));
            }

            if (userID.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Error for username " + username, false, -1));
            }

            // Create a session cookie with a unique ID and set the expiration time
            String sessionId = UUID.randomUUID().toString();
            int expirationTimeInMinutes = 30;
            Instant expiration = Instant.now().plus(Duration.ofMinutes(expirationTimeInMinutes));
            Cookie sessionCookie = new Cookie("sessionCookie", sessionId);
            sessionCookie.setSecure(true);
            sessionCookie.setMaxAge(expirationTimeInMinutes * 60);
            sessionCookies.storeSessionCookie(sessionId, userID, expiration);
            response.addCookie(sessionCookie);
            return ResponseEntity.ok(new AuthResponse("Username: " + userName, true, Integer.parseInt(userID)));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Error for username " + userName, false, -1));
        }
    }

    public boolean logout(String sessionCookie){
        return sessionCookies.deleteSessionCookie(sessionCookie);
    }
}
