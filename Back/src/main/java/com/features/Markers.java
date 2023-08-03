package com.features;

import com.util.Coordinates;
import com.util.Marker;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Markers {

    Invoices invoices;

    public Markers(){
        invoices = new Invoices();
    }


    /* Solution XSS
      message = Jsoup.clean(message, Whitelist.none());
     */
    public String addMarker(Marker marker) throws SQLException, ClassNotFoundException {
        double latitude = marker.getCoordinates().getLat();
        double longitude = marker.getCoordinates().getLng();
        String message = marker.getMessage();
        int markerID = marker.getNumber();

        if(markerID < 0 || markerID != getNextAvailableMarker()){
            return "error with the number's ID";
        }

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return "latitude should be in [-90; 90] and longitude should be in [-180; 180]";
        }
        if (message.length() > 150){
            return "message is limited to 150 characters. Message provided : " +message;
        }
        try {
            message = message.replace("alert", "Sorry I remove this alert to avoid that it pops on everyone's screen, this is not the XSS solution !");
            message = message.replace("popup", "Sorry I remove this popup to avoid that it pops on everyone's screen, this is not the XSS solution !");

            Class.forName("org.h2.Driver");
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "INSERT_MARKER_USER", "fhjd837bcvGG3");
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO MARKERS (ID, LATITUDE, LONGITUDE, MESSAGE, BUYER) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, marker.getNumber());
            preparedStatement.setDouble(2, latitude);
            preparedStatement.setDouble(3, longitude);
            preparedStatement.setString(4, message);
            preparedStatement.setInt(5, marker.getUserID());
            int rowsInserted = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            if (rowsInserted > 0) {
                invoices.createInvoice(marker.getNumber(), String.valueOf(marker.getUserID()));
                return "";
            } else {
                return "an error occurred, please try again later.";
            }
        } catch (ClassNotFoundException | SQLException | IOException e) {
            return "an error occurred, please try again later.";
        }
    }

    public List<Integer> getMarkersFromUserID(int userID) throws SQLException, ClassNotFoundException {
        List<Integer> markersID = new ArrayList<>();
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "READ_ONLY_USER", "123ghfjdfhj7382tvFTYF66");
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID FROM MARKERS WHERE BUYER = ?");
        preparedStatement.setInt(1, userID);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            markersID.add(resultSet.getInt("ID"));
        }
        preparedStatement.close();
        connection.close();
        return markersID;
    }

    public int getNextAvailableMarker() throws ClassNotFoundException, SQLException {
        int maxNumber = 0;
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "READ_ONLY_USER", "123ghfjdfhj7382tvFTYF66");
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT MAX(ID) FROM MARKERS;");
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            maxNumber = resultSet.getInt(1);
        }
        preparedStatement.close();
        connection.close();
        return maxNumber + 1;
    }

    public List<Marker> getMarkers() {
        List<Marker> markers = new ArrayList<>();
        try {
            Class.forName("org.h2.Driver");
            Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "READ_ONLY_USER", "123ghfjdfhj7382tvFTYF66");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM MARKERS");
            while (resultSet.next()) {
                Coordinates coordinates = new Coordinates(resultSet.getFloat("LATITUDE"), resultSet.getFloat("LONGITUDE"));
                markers.add(new Marker(resultSet.getInt("ID"), coordinates, resultSet.getString("MESSAGE"), resultSet.getInt("BUYER")));
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return markers;
        } catch (SQLException e) {
            e.printStackTrace();
            return markers;
        }
        return markers;
    }

}
