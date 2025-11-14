package com.weather.mapper;

/**
 * Maps UV index values to descriptive levels
 */
public class UvIndexMapper {
    
    public static String getUvLevel(double uvIndex) {
        if (uvIndex < 3) return "Low";
        if (uvIndex < 6) return "Moderate";
        if (uvIndex < 8) return "High";
        if (uvIndex < 11) return "Very High";
        return "Extreme";
    }
}

