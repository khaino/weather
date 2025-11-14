package com.weather;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Weather App - Get weather forecast for any location
 */
public class App 
{
    public static void main( String[] args ) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            double lat, lon;
            String city, region, country;

            if (args.length > 0) {
                // Get location from command-line argument
                String locationQuery = String.join(" ", args);
                String geocodeUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + 
                    java.net.URLEncoder.encode(locationQuery, "UTF-8") + "&count=10&language=en&format=json";
                
                HttpRequest geoRequest = HttpRequest.newBuilder()
                    .uri(URI.create(geocodeUrl))
                    .build();
                HttpResponse<String> geoResponse = client.send(geoRequest, HttpResponse.BodyHandlers.ofString());
                
                if (geoResponse.statusCode() != 200) {
                    System.err.println("Failed to geocode location: " + locationQuery);
                    return;
                }
                
                GeocodingResponse geoData = mapper.readValue(geoResponse.body(), GeocodingResponse.class);
                List<LocationResult> results = geoData.results;
                
                if (results == null || results.isEmpty()) {
                    System.err.println("Location not found: " + locationQuery);
                    System.err.println("Try a different format, e.g., 'Pomona' or 'London'");
                    System.err.println("For US cities with common names, try: 'CityName StateName'");
                    return;
                }
                
                // Use the first result (best match)
                LocationResult location = results.get(0);
                lat = location.latitude;
                lon = location.longitude;
                city = location.name;
                region = location.admin1 != null ? location.admin1 : "";
                country = location.country;
            } else {
                // Get location from IP (default behavior)
                HttpRequest locRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://ip-api.com/json"))
                    .build();
                HttpResponse<String> locResponse = client.send(locRequest, HttpResponse.BodyHandlers.ofString());
                if (locResponse.statusCode() != 200) {
                    System.err.println("Failed to get location");
                    return;
                }
                IpLocationResponse locData = mapper.readValue(locResponse.body(), IpLocationResponse.class);
                if (!"success".equals(locData.status)) {
                    System.err.println("Location service error");
                    return;
                }
                lat = locData.lat;
                lon = locData.lon;
                city = locData.city;
                region = locData.regionName;
                country = locData.country;
            }

            // Get weather
            String weatherUrl = String.format("https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&daily=temperature_2m_max,temperature_2m_min,weathercode&timezone=auto&temperature_unit=celsius", lat, lon);
            HttpRequest weatherRequest = HttpRequest.newBuilder()
                .uri(URI.create(weatherUrl))
                .build();
            HttpResponse<String> weatherResponse = client.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
            if (weatherResponse.statusCode() != 200) {
                System.err.println("Failed to get weather: " + weatherResponse.statusCode());
                System.err.println("Response: " + weatherResponse.body());
                return;
            }
            WeatherResponse weatherData = mapper.readValue(weatherResponse.body(), WeatherResponse.class);
            DailyWeather daily = weatherData.daily;
            List<Double> maxTemps = daily.temperature_2m_max;
            List<Double> minTemps = daily.temperature_2m_min;
            List<Integer> weatherCodes = daily.weathercode;
            List<String> dates = daily.time;

            // Print header
            System.out.println("╔═══════════════════════════════════════════════════════╗");
            System.out.println("║           WEATHER FORECAST                            ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("📍 Location: " + city + ", " + region + ", " + country);
            System.out.println();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);

            for (int i = 0; i < 7; i++) {
                double low = minTemps.get(i);
                double high = maxTemps.get(i);
                int code = weatherCodes.get(i);
                String condition = getCondition(code);
                String emoji = getWeatherEmoji(code);
                
                LocalDate date = LocalDate.parse(dates.get(i), inputFormatter);
                String formattedDate = date.format(outputFormatter);

                if (i == 0) {
                    System.out.println("🗓️  TODAY (" + formattedDate + ")");
                    System.out.printf("    🌡️  Low:  %.0f°C  |  High: %.0f°C%n", low, high);
                    System.out.println("    " + emoji + "  Condition: " + condition);
                    System.out.println();
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    System.out.println();
                    System.out.println("📅 NEXT 7 DAYS");
                    System.out.println();
                } else {
                    String dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH));
                    System.out.println("📆 " + dayOfWeek);
                    System.out.printf("    🌡️  %.0f°C - %.0f°C  |  %s  %s%n", low, high, emoji, condition);
                    System.out.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Model classes for API responses
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class LocationResult {
        public String name;
        public double latitude;
        public double longitude;
        public String admin1;
        public String country;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class GeocodingResponse {
        public List<LocationResult> results;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class DailyWeather {
        public List<String> time;
        
        @JsonProperty("temperature_2m_max")
        public List<Double> temperature_2m_max;
        
        @JsonProperty("temperature_2m_min")
        public List<Double> temperature_2m_min;
        
        public List<Integer> weathercode;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class WeatherResponse {
        public DailyWeather daily;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class IpLocationResponse {
        public String status;
        public double lat;
        public double lon;
        public String city;
        public String regionName;
        public String country;
    }

    private static String getCondition(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1 -> "Mainly clear";
            case 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45 -> "Fog";
            case 48 -> "Depositing rime fog";
            case 51, 53, 55 -> "Drizzle";
            case 61, 63, 65 -> "Rain";
            case 71, 73, 75 -> "Snow";
            case 77 -> "Snow grains";
            case 80, 81, 82 -> "Showers";
            case 85, 86 -> "Snow showers";
            case 95, 96, 99 -> "Thunderstorm";
            default -> "Unknown (" + code + ")";
        };
    }

    private static String getWeatherEmoji(int code) {
        return switch (code) {
            case 0 -> "☀️";           // Clear sky
            case 1 -> "🌤️";          // Mainly clear
            case 2 -> "⛅";          // Partly cloudy
            case 3 -> "☁️";          // Overcast
            case 45, 48 -> "🌫️";    // Fog
            case 51, 53, 55 -> "🌦️"; // Drizzle
            case 61, 63, 65 -> "🌧️"; // Rain
            case 71, 73, 75 -> "🌨️"; // Snow
            case 77 -> "🌨️";         // Snow grains
            case 80, 81, 82 -> "🌦️"; // Showers
            case 85, 86 -> "🌨️";     // Snow showers
            case 95, 96, 99 -> "⛈️";  // Thunderstorm
            default -> "🌡️";         // Unknown
        };
    }
}
