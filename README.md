# Weather App

A simple Java Maven project that fetches current location via IP geolocation and displays weather forecast (high/low temps and conditions) for today and the next 6 days using Open-Meteo API.

## Prerequisites
- Java 17 or higher
- Maven 3.x
- Internet connection (for API calls)

## Building
1. Clone or navigate to the project directory:
   ```
   cd weather-app
   ```
2. Build the project (downloads dependencies and compiles):
   ```
   mvn clean compile
   ```

## Running

### Get weather for your current location (via IP):
```
mvn exec:java
```

### Get weather for a specific location:
```bash
mvn exec:java -Dexec.args="London"
mvn exec:java -Dexec.args="Pomona"
mvn exec:java -Dexec.args="Tokyo"
mvn exec:java -Dexec.args="Paris"
mvn exec:java -Dexec.args="New York"
mvn exec:java -Dexec.args="Los Angeles"
```

### Use Fahrenheit instead of Celsius:
```bash
mvn exec:java -Dexec.args="--fahrenheit"
mvn exec:java -Dexec.args="London -f"
mvn exec:java -Dexec.args="New York --fahrenheit"
```

**Location Tips:** The app uses Open-Meteo's geocoding API. **Best practice: Use just the city name**.
- ✅ Works well: `London`, `Tokyo`, `Paris`, `Miami`, `Sydney`
- ✅ Multi-word cities: `New York`, `Los Angeles`, `San Francisco`
- ❌ Avoid adding country names: "Paris France" won't work, just use `Paris`
- 💡 For common US city names, you can add the state: `Portland Oregon` or `Portland Maine`

### Alternative methods:

Using manual classpath:
```
java -cp "target/classes:$(mvn -q dependency:build-classpath | grep -o '/.*')" com.weather.App
java -cp "target/classes:$(mvn -q dependency:build-classpath | grep -o '/.*')" com.weather.App "London"
```

Or, if using an IDE (e.g., IntelliJ, Eclipse, VS Code with Java extensions):
- Open the project as a Maven project.
- Run the `com.weather.App` class directly.
- To pass arguments, configure run arguments in your IDE (e.g., "London" or "Pomona").

### Expected Output
The app displays a beautiful formatted forecast with comprehensive weather details:

```
╔═══════════════════════════════════════════════════════╗
║           WEATHER FORECAST                            ║
╚═══════════════════════════════════════════════════════╝

📍 Location: London, England, United Kingdom

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🗓️  TODAY (Friday, November 14, 2025)
    🌡️  Low: 56°F  |  High: 61°F
    🌦️  Showers (💧 100% chance)
    💨 Wind: 12 km/h ESE
    ☀️  UV Index: 1 (Low)
    🌅 Sunrise: 7:17 AM  |  🌇 Sunset: 4:12 PM

⏰ HOURLY FORECAST (Next 12 hours)

   12:00 AM  🌧️  56°F (feels 55°F)  💧73%  💦92%
    1:00 AM  🌧️  56°F (feels 54°F)  💧85%  💦93%
    2:00 AM  🌧️  56°F (feels 54°F)  💧95%  💦93%
    ... (continues for 12 hours)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📅 7-DAY FORECAST

📆 Saturday, November 15
    🌡️  53°F - 59°F  |  🌧️  Rain  (💧 43%)

📆 Sunday, November 16
    🌡️  45°F - 55°F  |  🌧️  Rain  (💧 13%)
    ... (continues for 7 days)
```

## Features

✨ **Comprehensive Weather Information:**
- 🌡️ **Temperature Units**: Choose between Celsius (default) or Fahrenheit
- ⏰ **Hourly Forecast**: Next 12 hours with temperature, feels-like, precipitation chance, and humidity
- 📅 **7-Day Forecast**: Extended forecast with daily highs/lows and precipitation probability
- 💨 **Wind Data**: Speed and direction
- ☀️ **UV Index**: With safety level (Low/Moderate/High/Very High/Extreme)
- 🌅 **Sunrise/Sunset**: Daily sun times
- 💧 **Precipitation**: Probability percentages for rain/snow
- 💦 **Humidity**: Relative humidity levels
- 🎨 **Beautiful Display**: Unicode box drawing and weather emojis

## How It Works
- **Location Detection**:
  - **Default (no arguments)**: Uses ip-api.com to geolocate via your public IP.
  - **With arguments**: Uses Open-Meteo's free geocoding API to convert location names to coordinates.
- **Weather Data**: Queries Open-Meteo forecast API (completely free, no API key required) for comprehensive hourly and daily weather data.
- **Display**: Beautiful formatted console output with Unicode box drawing and weather emojis (☀️ ☁️ 🌧️ ⛈️).
- **Temperature Units**: Supports both Celsius (default) and Fahrenheit via `-f` or `--fahrenheit` flag.

## Customization
- Hardcode location: Edit `src/main/java/com/weather/App.java`, replace IP fetch with fixed lat/lon.
- More days/details: Modify Open-Meteo URL params (e.g., add `precipitation_probability`); extend parsing.
- Tests: Run `mvn test` (basic JUnit included).

## Dependencies
- Jackson for JSON parsing.
- Built-in `java.net.http` for HTTP.

## Notes
- Free APIs have rate limits; for production, consider API keys or caching.
- Location may differ from VPN/proxy IPs.
- Errors (e.g., network issues) print to stderr.

For issues, check Maven logs or add logging.
