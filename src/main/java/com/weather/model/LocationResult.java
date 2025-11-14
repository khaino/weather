package com.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Model class for geocoding API result
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResult {
    public String name;
    public double latitude;
    public double longitude;
    public String admin1;
    public String country;
}

