package com.weather.mapper;

/**
 * Maps weather codes to human-readable conditions and emojis
 */
public class WeatherCodeMapper {
    
    public static String getCondition(int code) {
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

    public static String getWeatherEmoji(int code) {
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

