package com.weather;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            // Get location from IP
            HttpRequest locRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://ip-api.com/json"))
                .build();
            HttpResponse<String> locResponse = client.send(locRequest, HttpResponse.BodyHandlers.ofString());
            if (locResponse.statusCode() != 200) {
                System.err.println("Failed to get location");
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> locData = mapper.readValue(locResponse.body(), Map.class);
            String status = (String) locData.get("status");
            if (!"success".equals(status)) {
                System.err.println("Location service error");
                return;
            }
            double lat = ((Number) locData.get("lat")).doubleValue();
            double lon = ((Number) locData.get("lon")).doubleValue();
            String city = (String) locData.get("city");
            String region = (String) locData.get("regionName");
            String country = (String) locData.get("country");

            System.out.println("Location: " + city + ", " + region + ", " + country);

            // Get weather
            String weatherUrl = String.format("https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&daily=temperature_2m_max,temperature_2m_min,weathercode&timezone=auto&temperature_unit=celsius", lat, lon);
            HttpRequest weatherRequest = HttpRequest.newBuilder()
                .uri(URI.create(weatherUrl))
                .build();
            HttpResponse<String> weatherResponse = client.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
            if (weatherResponse.statusCode() != 200) {
                System.err.println("Failed to get weather");
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> weatherData = mapper.readValue(weatherResponse.body(), Map.class);
            @SuppressWarnings("unchecked")
            Map<String, List<?>> daily = (Map<String, List<?>>) weatherData.get("daily");
            @SuppressWarnings("unchecked")
            List<Number> maxTemps = (List<Number>) daily.get("temperature_2m_max");
            @SuppressWarnings("unchecked")
            List<Number> minTemps = (List<Number>) daily.get("temperature_2m_min");
            @SuppressWarnings("unchecked")
            List<Number> weatherCodes = (List<Number>) daily.get("weathercode");

            for (int i = 0; i < 7; i++) {
                if (i == 0) {
                    System.out.println("Today");
                } else {
                    System.out.println("Day " + i);
                }
                double low = minTemps.get(i).doubleValue();
                double high = maxTemps.get(i).doubleValue();
                int code = weatherCodes.get(i).intValue();
                String condition = getCondition(code);
                System.out.printf("Low: %.0f C%n", low);
                System.out.printf("High: %.0f C%n", high);
                System.out.println(condition);
                if (i < 6) {
                    System.out.println("-----");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
