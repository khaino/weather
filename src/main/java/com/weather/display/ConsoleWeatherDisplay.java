package com.weather.display;

import com.weather.formatter.WeatherFormatter;
import com.weather.model.Location;
import com.weather.model.WeatherResponse;

/**
 * Displays weather information to the console
 * This class handles I/O operations (System.out)
 */
public class ConsoleWeatherDisplay {
    
    private final WeatherFormatter formatter;

    public ConsoleWeatherDisplay(WeatherFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * Displays weather forecast to console
     */
    public void display(Location location, WeatherResponse weatherData) {
        String formattedWeather = formatter.formatWeather(location, weatherData);
        System.out.print(formattedWeather);
    }
}

