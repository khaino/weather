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
```
mvn exec:java -Dexec.args="London"
mvn exec:java -Dexec.args="Pomona"
mvn exec:java -Dexec.args="Tokyo"
mvn exec:java -Dexec.args="Paris France"
```

**Note:** The app uses Open-Meteo's geocoding API, so just use the city name (with optional country). Common formats that work:
- Simple: `London`, `Tokyo`, `Paris`
- With country: `Paris France`, `London UK`
- US cities: Most work with just the city name (e.g., `Pomona`, `Miami`)

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
The app displays a beautiful formatted forecast with emojis:
```
╔═══════════════════════════════════════════════════════╗
║           WEATHER FORECAST                            ║
╚═══════════════════════════════════════════════════════╝

📍 Location: London, England, United Kingdom

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🗓️  TODAY (Friday, November 14, 2025)
    🌡️  Low:  13°C  |  High: 16°C
    🌦️  Condition: Showers

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📅 NEXT 7 DAYS

📆 Saturday, November 15
    🌡️  11°C - 15°C  |  ☁️  Overcast

📆 Sunday, November 16
    🌡️  7°C - 13°C  |  🌧️  Rain
... (continues for 7 days)
```

## How It Works
- **Location Detection**:
  - **Default (no arguments)**: Uses ip-api.com to geolocate via your public IP.
  - **With arguments**: Uses Open-Meteo's free geocoding API to convert location names to coordinates.
- **Weather Data**: Queries Open-Meteo forecast API for lat/lon, fetching daily max/min temps and weather codes.
- **Display**: Beautiful formatted output with Unicode box drawing and weather emojis (☀️ ☁️ 🌧️ ⛈️).
- Temps in Celsius; conditions mapped from WMO weather codes.

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
