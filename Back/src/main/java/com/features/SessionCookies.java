package com.features;

import java.sql.*;
import java.time.Instant;

public class SessionCookies {

    public static int isCookieValid(String sessionId) throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "READ_ONLY_USER", "123ghfjdfhj7382tvFTYF66");

        PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM SESSION_COOKIES WHERE session_id = ? AND expiration >= NOW();");
        statement.setString(1, sessionId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            return resultSet.getInt("user_id");
        }
        resultSet.close();
        statement.close();
        connection.close();
        return -1;
    }

    public void storeSessionCookie(String sessionId, String userId, Instant expiration) throws SQLException, ClassNotFoundException {
        deleteOutdatedSessionCookie();
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "INSERT_SESSION_COOKIES", "dlcpscbdshj89230328tevdghe");
        PreparedStatement statement = connection.prepareStatement("INSERT INTO SESSION_COOKIES (session_id, user_id, expiration) VALUES (?, ?, ?)");
        statement.setString(1, sessionId);
        statement.setString(2, userId);
        statement.setTimestamp(3, Timestamp.from(expiration));
        statement.executeUpdate();
        statement.close();
        connection.close();
    }

    public void deleteOutdatedSessionCookie() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
        PreparedStatement statement = connection.prepareStatement("DELETE FROM SESSION_COOKIES WHERE expiration < NOW();");
        statement.executeUpdate();
        statement.close();
        connection.close();

         connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
    }

    public boolean deleteSessionCookie(String sessionCookie) {
        try {
            Class.forName("org.h2.Driver");
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
            PreparedStatement statement = connection.prepareStatement("DELETE FROM SESSION_COOKIES WHERE session_id = ? ;");
            statement.setString(1, sessionCookie);
            statement.executeUpdate();
            statement.close();
            connection.close();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
