package com.weather.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.model.GeocodingResponse;
import com.weather.model.IpLocationResponse;
import com.weather.model.Location;
import com.weather.model.LocationResult;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Service for resolving locations via IP geolocation or geocoding
 */
public class LocationService extends BaseHttpService {
    
    private static final String IP_LOCATION_URL = "http://ip-api.com/json";
    private static final String GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String GEOCODING_PARAMS = "&count=10&language=en&format=json";

    public LocationService(HttpClient client, ObjectMapper mapper) {
        super(client, mapper);
    }

    /**
     * Gets location based on the user's IP address
     */
    public Location getLocationFromIp() throws Exception {
        IpLocationResponse locData = performGetRequest(
            IP_LOCATION_URL, 
            IpLocationResponse.class, 
            "Failed to get location from IP"
        );
        
        if (!"success".equals(locData.status)) {
            throw new LocationServiceException("Location service error");
        }
        
        return new Location(
            locData.lat,
            locData.lon,
            locData.city,
            locData.regionName,
            locData.country
        );
    }

    /**
     * Geocodes a location query string to get coordinates and location details
     */
    public Location geocodeLocation(String locationQuery) throws Exception {
        String geocodeUrl = GEOCODING_BASE_URL + "?name=" + 
            URLEncoder.encode(locationQuery, StandardCharsets.UTF_8) + 
            GEOCODING_PARAMS;
        
        GeocodingResponse geoData = performGetRequest(
            geocodeUrl, 
            GeocodingResponse.class, 
            "Failed to geocode location: " + locationQuery
        );
        
        List<LocationResult> results = geoData.results;
        
        if (results == null || results.isEmpty()) {
            throw new LocationNotFoundException(
                "Location not found: " + locationQuery + "\n" +
                "Try a different format, e.g., 'Pomona' or 'London'\n" +
                "For US cities with common names, try: 'CityName StateName'"
            );
        }
        
        // Use the first result (best match)
        LocationResult location = results.get(0);
        return new Location(
            location.latitude,
            location.longitude,
            location.name,
            location.admin1,
            location.country
        );
    }

    /**
     * Exception thrown when location service fails
     */
    public static class LocationServiceException extends Exception {
        public LocationServiceException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a location cannot be found
     */
    public static class LocationNotFoundException extends Exception {
        public LocationNotFoundException(String message) {
            super(message);
        }
    }
}

