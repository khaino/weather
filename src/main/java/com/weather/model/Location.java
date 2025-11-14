package com.weather.model;

/**
 * Represents a geographic location with coordinates and address information
 */
public class Location {
    private final double latitude;
    private final double longitude;
    private final String city;
    private final String region;
    private final String country;

    public Location(double latitude, double longitude, String city, String region, String country) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.region = region != null ? region : "";
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public String getFormattedLocation() {
        return city + ", " + region + ", " + country;
    }
}

