package com.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Model class for hourly weather data
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HourlyWeather {
    public List<String> time;
    
    @JsonProperty("temperature_2m")
    public List<Double> temperature_2m;
    
    @JsonProperty("apparent_temperature")
    public List<Double> apparent_temperature;
    
    public List<Integer> weathercode;
    
    @JsonProperty("precipitation_probability")
    public List<Integer> precipitation_probability;
    
    @JsonProperty("relative_humidity_2m")
    public List<Integer> relative_humidity_2m;
    
    @JsonProperty("wind_speed_10m")
    public List<Double> wind_speed_10m;
}

