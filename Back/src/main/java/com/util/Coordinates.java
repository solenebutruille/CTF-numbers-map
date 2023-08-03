package com.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Coordinates {
    private double lat;
    private double lng;

    @JsonCreator
    public Coordinates(@JsonProperty("lat") double lat,
                  @JsonProperty("lng") double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @JsonProperty("lat")
    public double getLat() {
        return lat;
    }

    @JsonProperty("lng")
    public double getLng() {
        return lng;
    }
}
