package com.weather.formatter;

import com.weather.model.DailyWeather;
import com.weather.model.HourlyWeather;
import com.weather.model.Location;
import com.weather.model.WeatherResponse;
import com.weather.mapper.UvIndexMapper;
import com.weather.mapper.WeatherCodeMapper;
import com.weather.mapper.WindDirectionMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formats weather information into string output
 * This class is pure formatting logic - no I/O operations
 */
public class WeatherFormatter {
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FULL_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter HOUR_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter COMPACT_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("ha", Locale.ENGLISH);
    private static final DateTimeFormatter DAY_OF_WEEK_FORMATTER = 
        DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH);
    
    private final boolean useFahrenheit;
    private final String tempSymbol;

    public WeatherFormatter(boolean useFahrenheit) {
        this.useFahrenheit = useFahrenheit;
        this.tempSymbol = useFahrenheit ? "°F" : "°C";
    }

    /**
     * Formats complete weather forecast as a string
     */
    public String formatWeather(Location location, WeatherResponse weatherData) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(formatHeader(location));
        sb.append(formatTodayWeather(weatherData.daily, weatherData.timezone));
        sb.append(formatHourlyForecast(weatherData.hourly, weatherData.timezone));
        sb.append(formatWeeklyForecast(weatherData.daily));
        
        return sb.toString();
    }

    private String formatHeader(Location location) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════╗\n");
        sb.append("║           WEATHER FORECAST                            ║\n");
        sb.append("╚═══════════════════════════════════════════════════════╝\n");
        sb.append("\n");
        sb.append("📍 Location: ").append(location.getFormattedLocation()).append("\n");
        sb.append("\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("\n");
        return sb.toString();
    }

    private String formatTodayWeather(DailyWeather daily, String timezone) {
        StringBuilder sb = new StringBuilder();
        
        double todayLow = daily.temperature_2m_min.get(0);
        double todayHigh = daily.temperature_2m_max.get(0);
        int todayCode = daily.weathercode.get(0);
        String todayCondition = WeatherCodeMapper.getCondition(todayCode);
        String todayEmoji = WeatherCodeMapper.getWeatherEmoji(todayCode);
        
        LocalDate today = LocalDate.parse(daily.time.get(0), DATE_FORMATTER);
        String todayFormatted = today.format(FULL_DATE_FORMATTER);
        
        // Get current time in location's timezone
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        String currentTime = now.format(TIME_FORMATTER);
        
        // Sunrise/Sunset
        String sunrise = daily.sunrise.get(0).substring(11, 16); // Extract HH:MM
        String sunset = daily.sunset.get(0).substring(11, 16);
        
        // Today's additional details
        int precipProb = daily.precipitation_probability_max.get(0);
        double windSpeed = daily.wind_speed_10m_max.get(0);
        int windDir = daily.wind_direction_10m_dominant.get(0);
        double uvIndex = daily.uv_index_max.get(0);
        
        sb.append("🗓️  TODAY (").append(todayFormatted).append(" - ").append(currentTime).append(")\n");
        sb.append(String.format("    🌡️  Low: %.0f%s  |  High: %.0f%s%n", 
            todayLow, tempSymbol, todayHigh, tempSymbol));
        sb.append(String.format("    %s  %s", todayEmoji, todayCondition));
        if (precipProb > 0) {
            sb.append(String.format(" (💧 %d%% chance)", precipProb));
        }
        sb.append("\n");
        sb.append(String.format("    💨 Wind: %.0f km/h %s%n", 
            windSpeed, WindDirectionMapper.getWindDirection(windDir)));
        sb.append(String.format("    ☀️  UV Index: %.0f (%s)%n", 
            uvIndex, UvIndexMapper.getUvLevel(uvIndex)));
        sb.append(String.format("    🌅 Sunrise: %s  |  🌇 Sunset: %s%n", 
            TimeFormatter.format24To12Hour(sunrise), 
            TimeFormatter.format24To12Hour(sunset)));
        sb.append("\n");
        
        return sb.toString();
    }

    private String formatHourlyForecast(HourlyWeather hourly, String timezone) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("⏰ HOURLY FORECAST (Next 12 hours)\n");
        sb.append("\n");
        
        // Get current time in the location's timezone
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        
        // Collect data for next 12 hours
        String[] times = new String[12];
        String[] emojis = new String[12];
        String[] temps = new String[12];
        String[] feelsLikes = new String[12];
        String[] precips = new String[12];
        String[] humidities = new String[12];
        
        int hourCount = 0;
        for (int i = 0; i < hourly.time.size() && hourCount < 12; i++) {
            LocalDateTime localHourTime = LocalDateTime.parse(hourly.time.get(i), HOUR_FORMATTER);
            ZonedDateTime hourTime = localHourTime.atZone(zoneId);
            
            if (hourTime.isAfter(now) || hourTime.isEqual(now)) {
                times[hourCount] = hourTime.format(COMPACT_TIME_FORMATTER);
                emojis[hourCount] = WeatherCodeMapper.getWeatherEmoji(hourly.weathercode.get(i));
                temps[hourCount] = String.format("%.0f%s", hourly.temperature_2m.get(i), tempSymbol);
                feelsLikes[hourCount] = String.format("%.0f%s", hourly.apparent_temperature.get(i), tempSymbol);
                precips[hourCount] = String.format("%d%%", hourly.precipitation_probability.get(i));
                humidities[hourCount] = String.format("%d%%", hourly.relative_humidity_2m.get(i));
                hourCount++;
            }
        }
        
        // Build horizontal display with fixed column width
        int colWidth = 7;
        
        // Time row
        sb.append("Time:   ");
        for (int i = 0; i < hourCount; i++) {
            sb.append(String.format("%" + colWidth + "s", times[i]));
        }
        sb.append("\n");
        
        // Weather emoji row
        sb.append("Weather:");
        for (int i = 0; i < hourCount; i++) {
            sb.append(String.format("%" + colWidth + "s", emojis[i]));
        }
        sb.append("\n");
        
        // Temperature row
        sb.append("Temp:   ");
        for (int i = 0; i < hourCount; i++) {
            sb.append(String.format("%" + colWidth + "s", temps[i]));
        }
        sb.append("\n");
        
        // Feels like row
        sb.append("Feels:  ");
        for (int i = 0; i < hourCount; i++) {
            sb.append(String.format("%" + colWidth + "s", feelsLikes[i]));
        }
        sb.append("\n");
        
        // Precipitation row
        sb.append("Precip: ");
        for (int i = 0; i < hourCount; i++) {
            sb.append(String.format("%" + colWidth + "s", precips[i]));
        }
        sb.append("\n");
        
        // Humidity row
        sb.append("Humid:  ");
        for (int i = 0; i < hourCount; i++) {
            sb.append(String.format("%" + colWidth + "s", humidities[i]));
        }
        sb.append("\n");
        
        sb.append("\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("\n");
        
        return sb.toString();
    }

    private String formatWeeklyForecast(DailyWeather daily) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("📅 7-DAY FORECAST\n");
        sb.append("\n");

        for (int i = 1; i < 7; i++) {
            double low = daily.temperature_2m_min.get(i);
            double high = daily.temperature_2m_max.get(i);
            int code = daily.weathercode.get(i);
            String condition = WeatherCodeMapper.getCondition(code);
            String emoji = WeatherCodeMapper.getWeatherEmoji(code);
            int precip = daily.precipitation_probability_max.get(i);
            
            LocalDate date = LocalDate.parse(daily.time.get(i), DATE_FORMATTER);
            String dayOfWeek = date.format(DAY_OF_WEEK_FORMATTER);
            
            sb.append("📆 ").append(dayOfWeek).append("\n");
            sb.append(String.format("    🌡️  %.0f%s - %.0f%s  |  %s  %s", 
                low, tempSymbol, high, tempSymbol, emoji, condition));
            if (precip > 0) {
                sb.append(String.format("  (💧 %d%%)", precip));
            }
            sb.append("\n");
            sb.append("\n");
        }
        
        return sb.toString();
    }
}
