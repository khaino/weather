package com.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Model class for weather API response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
    public String timezone;
    public DailyWeather daily;
    public HourlyWeather hourly;
}

