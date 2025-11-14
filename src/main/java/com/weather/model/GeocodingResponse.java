package com.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Model class for geocoding API response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResponse {
    public List<LocationResult> results;
}

