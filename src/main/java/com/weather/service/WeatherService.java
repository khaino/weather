package com.weather.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.model.Location;
import com.weather.model.WeatherResponse;

import java.net.http.HttpClient;

/**
 * Service for fetching weather data from Open-Meteo API
 */
public class WeatherService extends BaseHttpService {
    
    private static final String WEATHER_BASE_URL = "https://api.open-meteo.com/v1/forecast";

    public WeatherService(HttpClient client, ObjectMapper mapper) {
        super(client, mapper);
    }

    /**
     * Fetches comprehensive weather data for the given location
     */
    public WeatherResponse getWeather(Location location, boolean useFahrenheit) throws Exception {
        String tempUnit = useFahrenheit ? "fahrenheit" : "celsius";
        
        String weatherUrl = String.format(
            "%s?latitude=%f&longitude=%f" +
            "&daily=temperature_2m_max,temperature_2m_min,weathercode,precipitation_probability_max," +
            "wind_speed_10m_max,wind_direction_10m_dominant,uv_index_max,sunrise,sunset" +
            "&hourly=temperature_2m,apparent_temperature,weathercode,precipitation_probability," +
            "relative_humidity_2m,wind_speed_10m" +
            "&timezone=auto&temperature_unit=%s",
            WEATHER_BASE_URL,
            location.getLatitude(),
            location.getLongitude(),
            tempUnit
        );
        
        return performGetRequest(weatherUrl, WeatherResponse.class, "Failed to get weather");
    }

    /**
     * Exception thrown when weather service fails
     */
    public static class WeatherServiceException extends Exception {
        public WeatherServiceException(String message) {
            super(message);
        }
    }
}

