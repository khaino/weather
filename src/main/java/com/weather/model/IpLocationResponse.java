package com.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Model class for IP-based location API response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpLocationResponse {
    public String status;
    public double lat;
    public double lon;
    public String city;
    public String regionName;
    public String country;
}

