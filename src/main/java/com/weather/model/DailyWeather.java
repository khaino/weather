package com.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Model class for daily weather data
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyWeather {
    public List<String> time;
    
    @JsonProperty("temperature_2m_max")
    public List<Double> temperature_2m_max;
    
    @JsonProperty("temperature_2m_min")
    public List<Double> temperature_2m_min;
    
    public List<Integer> weathercode;
    
    @JsonProperty("precipitation_probability_max")
    public List<Integer> precipitation_probability_max;
    
    @JsonProperty("wind_speed_10m_max")
    public List<Double> wind_speed_10m_max;
    
    @JsonProperty("wind_direction_10m_dominant")
    public List<Integer> wind_direction_10m_dominant;
    
    @JsonProperty("uv_index_max")
    public List<Double> uv_index_max;
    
    public List<String> sunrise;
    public List<String> sunset;
}

