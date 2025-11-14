package com.weather;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
            boolean useFahrenheit = false;
            List<String> locationArgs = new ArrayList<>();

            // Parse arguments for flags
            for (String arg : args) {
                if (arg.equals("-f") || arg.equals("--fahrenheit")) {
                    useFahrenheit = true;
                } else if (!arg.startsWith("-")) {
                    locationArgs.add(arg);
                }
            }

            if (!locationArgs.isEmpty()) {
                // Get location from command-line argument
                String locationQuery = String.join(" ", locationArgs);
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

            // Get weather with comprehensive data
            String tempUnit = useFahrenheit ? "fahrenheit" : "celsius";
            String weatherUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f" +
                "&daily=temperature_2m_max,temperature_2m_min,weathercode,precipitation_probability_max,wind_speed_10m_max,wind_direction_10m_dominant,uv_index_max,sunrise,sunset" +
                "&hourly=temperature_2m,apparent_temperature,weathercode,precipitation_probability,relative_humidity_2m,wind_speed_10m" +
                "&timezone=auto&temperature_unit=%s",
                lat, lon, tempUnit
            );
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
            HourlyWeather hourly = weatherData.hourly;
            
            String tempSymbol = useFahrenheit ? "°F" : "°C";

            // Print header
            System.out.println("╔═══════════════════════════════════════════════════════╗");
            System.out.println("║           WEATHER FORECAST                            ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("📍 Location: " + city + ", " + region + ", " + country);
            System.out.println();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);
            DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

            // Display today's detailed weather
            double todayLow = daily.temperature_2m_min.get(0);
            double todayHigh = daily.temperature_2m_max.get(0);
            int todayCode = daily.weathercode.get(0);
            String todayCondition = getCondition(todayCode);
            String todayEmoji = getWeatherEmoji(todayCode);
            LocalDate today = LocalDate.parse(daily.time.get(0), dateFormatter);
            String todayFormatted = today.format(fullDateFormatter);
            
            // Sunrise/Sunset
            String sunrise = daily.sunrise.get(0).substring(11, 16); // Extract HH:MM
            String sunset = daily.sunset.get(0).substring(11, 16);
            
            // Today's additional details
            int precipProb = daily.precipitation_probability_max.get(0);
            double windSpeed = daily.wind_speed_10m_max.get(0);
            int windDir = daily.wind_direction_10m_dominant.get(0);
            double uvIndex = daily.uv_index_max.get(0);
            
            System.out.println("🗓️  TODAY (" + todayFormatted + ")");
            System.out.printf("    🌡️  Low: %.0f%s  |  High: %.0f%s%n", todayLow, tempSymbol, todayHigh, tempSymbol);
            System.out.printf("    %s  %s", todayEmoji, todayCondition);
            if (precipProb > 0) {
                System.out.printf(" (💧 %d%% chance)", precipProb);
            }
            System.out.println();
            System.out.printf("    💨 Wind: %.0f km/h %s%n", windSpeed, getWindDirection(windDir));
            System.out.printf("    ☀️  UV Index: %.0f (%s)%n", uvIndex, getUvLevel(uvIndex));
            System.out.printf("    🌅 Sunrise: %s  |  🌇 Sunset: %s%n", formatTime(sunrise), formatTime(sunset));
            System.out.println();
            
            // Hourly forecast for today
            System.out.println("⏰ HOURLY FORECAST (Next 12 hours)");
            System.out.println();
            LocalDateTime now = LocalDateTime.now();
            int hourCount = 0;
            for (int i = 0; i < hourly.time.size() && hourCount < 12; i++) {
                LocalDateTime hourTime = LocalDateTime.parse(hourly.time.get(i), hourFormatter);
                if (hourTime.isAfter(now) || hourTime.isEqual(now)) {
                    double temp = hourly.temperature_2m.get(i);
                    double feelsLike = hourly.apparent_temperature.get(i);
                    int code = hourly.weathercode.get(i);
                    int precip = hourly.precipitation_probability.get(i);
                    int humidity = hourly.relative_humidity_2m.get(i);
                    
                    String timeStr = hourTime.format(timeFormatter);
                    String emoji = getWeatherEmoji(code);
                    
                    System.out.printf("   %8s  %s  %.0f%s (feels %.0f%s)  💧%d%%  💦%d%%%n",
                        timeStr, emoji, temp, tempSymbol, feelsLike, tempSymbol, precip, humidity);
                    hourCount++;
                }
            }
            System.out.println();
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println();
            System.out.println("📅 7-DAY FORECAST");
            System.out.println();

            for (int i = 1; i < 7; i++) {
                double low = daily.temperature_2m_min.get(i);
                double high = daily.temperature_2m_max.get(i);
                int code = daily.weathercode.get(i);
                String condition = getCondition(code);
                String emoji = getWeatherEmoji(code);
                int precip = daily.precipitation_probability_max.get(i);
                
                LocalDate date = LocalDate.parse(daily.time.get(i), dateFormatter);
                String dayOfWeek = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH));
                System.out.println("📆 " + dayOfWeek);
                System.out.printf("    🌡️  %.0f%s - %.0f%s  |  %s  %s", low, tempSymbol, high, tempSymbol, emoji, condition);
                if (precip > 0) {
                    System.out.printf("  (💧 %d%%)", precip);
                }
                System.out.println();
                System.out.println();
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
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class HourlyWeather {
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
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class WeatherResponse {
        public DailyWeather daily;
        public HourlyWeather hourly;
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
    
    private static String getWindDirection(int degrees) {
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                               "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int index = (int) Math.round(((degrees % 360) / 22.5));
        return directions[index % 16];
    }
    
    private static String getUvLevel(double uvIndex) {
        if (uvIndex < 3) return "Low";
        if (uvIndex < 6) return "Moderate";
        if (uvIndex < 8) return "High";
        if (uvIndex < 11) return "Very High";
        return "Extreme";
    }
    
    private static String formatTime(String time24) {
        String[] parts = time24.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        String period = hour >= 12 ? "PM" : "AM";
        int hour12 = hour % 12;
        if (hour12 == 0) hour12 = 12;
        return String.format("%d:%02d %s", hour12, minute, period);
    }
}
