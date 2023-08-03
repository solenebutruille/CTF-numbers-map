package com.util;

public class AuthResponse {
    private String message;
    private boolean authenticated;
    private int userID;

    public AuthResponse(String message, boolean authenticated, int userID) {
        this.message = message;
        this.authenticated = authenticated;
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public int getUserID() {
        return userID;
    }
}