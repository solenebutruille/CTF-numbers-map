package com.util;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Marker {
    private int number;
    private double lat;
    private double lng;
    private String message;
    private int userID;

    @JsonCreator
    public Marker(@JsonProperty("number") int number,
                  @JsonProperty("coordinates") Coordinates coordinates,
                  @JsonProperty("message") String message,
                  @JsonProperty("userID") int userID) {
        this.number = number;
        this.lat = coordinates.getLat();
        this.lng = coordinates.getLng();
        this.message = message;
        this.userID = userID;
    }

    @JsonProperty("number")
    public int getNumber() {
        return number;
    }

    @JsonProperty("coordinates")
    public Coordinates getCoordinates() {
        return new Coordinates(lat, lng);
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("userID")
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
