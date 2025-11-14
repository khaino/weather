package com.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.cli.CommandLineParser;
import com.weather.display.ConsoleWeatherDisplay;
import com.weather.formatter.WeatherFormatter;
import com.weather.model.Location;
import com.weather.model.WeatherResponse;
import com.weather.service.LocationService;
import com.weather.service.WeatherService;

import java.net.http.HttpClient;

/**
 * Weather App - Get weather forecast for any location
 * 
 * This application fetches and displays weather forecasts using:
 * - IP-based geolocation or geocoding for location resolution
 * - Open-Meteo API for weather data
 * 
 * Usage:
 *   java -jar weather-app.jar [location] [-f|--fahrenheit]
 * 
 * Examples:
 *   java -jar weather-app.jar                    # Use current location from IP
 *   java -jar weather-app.jar London             # Weather for London
 *   java -jar weather-app.jar "New York" -f      # Weather for New York in Fahrenheit
 */
public class App {
    
    public static void main(String[] args) {
        try {
            // Initialize dependencies
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            
            // Parse command-line arguments
            CommandLineParser parser = new CommandLineParser(args);
            
            // Initialize services
            LocationService locationService = new LocationService(client, mapper);
            WeatherService weatherService = new WeatherService(client, mapper);
            WeatherFormatter formatter = new WeatherFormatter(parser.isUseFahrenheit());
            ConsoleWeatherDisplay display = new ConsoleWeatherDisplay(formatter);
            
            // Get location
            Location location = getLocation(locationService, parser);
            
            // Fetch weather data
            WeatherResponse weatherData = weatherService.getWeather(location, parser.isUseFahrenheit());
            
            // Display weather forecast
            display.display(location, weatherData);
            
        } catch (LocationService.LocationNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (LocationService.LocationServiceException e) {
            System.err.println("Location error: " + e.getMessage());
            System.exit(1);
        } catch (WeatherService.WeatherServiceException e) {
            System.err.println("Weather error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Resolves location based on command-line arguments or IP address
     */
    private static Location getLocation(LocationService locationService, CommandLineParser parser) 
            throws Exception {
        if (parser.hasLocationArgs()) {
            return locationService.geocodeLocation(parser.getLocationQuery());
        } else {
            return locationService.getLocationFromIp();
        }
    }
}
