package com.weather.mapper;

/**
 * Converts wind direction in degrees to compass direction
 */
public class WindDirectionMapper {
    
    private static final String[] DIRECTIONS = {
        "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
        "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
    };
    
    public static String getWindDirection(int degrees) {
        int index = (int) Math.round(((degrees % 360) / 22.5));
        return DIRECTIONS[index % 16];
    }
}

