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
Execute the main class using Maven (recommended, simpler):
```
mvn exec:java
```

Alternatively, use the manual classpath (if preferred):
```
java -cp "target/classes:$(mvn -q dependency:build-classpath | grep -o '/.*')" com.weather.App
```

Or, if using an IDE (e.g., IntelliJ, Eclipse, VS Code with Java extensions):
- Open the project as a Maven project.
- Run the `com.weather.App` class directly (or `mvn exec:java` via terminal).

### Expected Output
The app prints location and daily forecast based on your public IP:
```
Location: [City], [State/Region], [Country]
Today
Low: [temp] C
High: [temp] C
[Condition e.g., Rain]
-----
Day 1
Low: [temp] C
High: [temp] C
[Condition]
-----
... (up to Day 6)
```

## How It Works
- **Location**: Uses free ip-api.com to geolocate via IP (no key needed).
- **Weather**: Queries Open-Meteo forecast API for lat/lon, fetching daily max/min temps and weather codes (mapped to descriptions like "Clear sky", "Rain").
- Temps in Celsius; conditions simplified from WMO codes.

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
