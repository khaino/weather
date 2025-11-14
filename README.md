# Weather App

A Java application that displays comprehensive weather forecasts for any location worldwide using free APIs.

## Prerequisites

- Java 17 or higher
- Maven 3.x
- Internet connection

## Quick Start

```bash
# Build the project
mvn clean compile

# Run with your current location (detected via IP)
mvn exec:java

# Run with a specific location
mvn exec:java -Dexec.args="London"
mvn exec:java -Dexec.args="Tokyo"
mvn exec:java -Dexec.args="New York"

# Use Fahrenheit
mvn exec:java -Dexec.args="Paris -f"
mvn exec:java -Dexec.args="--fahrenheit"
```

## Testing

```bash
# Run all tests
mvn test

# Run tests with detailed output
mvn test -X

# Run specific test class
mvn test -Dtest=AppTest

# Clean, compile, and test
mvn clean test
```

## Sample Output

```
╔═══════════════════════════════════════════════════════╗
║           WEATHER FORECAST                            ║
╚═══════════════════════════════════════════════════════╝

📍 Location: Tokyo, Tokyo, Japan

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🗓️  TODAY (Friday, November 14, 2025)
    🌡️  Low: 9°C  |  High: 19°C
    ⛅  Partly cloudy (💧 33% chance)
    💨 Wind: 6 km/h N
    ☀️  UV Index: 4 (Moderate)
    🌅 Sunrise: 6:16 AM  |  🌇 Sunset: 4:35 PM

⏰ HOURLY FORECAST (Next 12 hours)

   12:00 AM  🌤️  10°C (feels 9°C)  💧0%  💦83%
    1:00 AM  ☀️  10°C (feels 9°C)  💧0%  💦77%
    2:00 AM  ☀️  10°C (feels 9°C)  💧0%  💦73%
    3:00 AM  ☀️  10°C (feels 9°C)  💧0%  💦73%
    4:00 AM  ☀️  11°C (feels 9°C)  💧0%  💦68%
    5:00 AM  ☀️  10°C (feels 8°C)  💧0%  💦72%
    6:00 AM  ☀️  9°C (feels 8°C)  💧0%  💦79%
    7:00 AM  ☀️  10°C (feels 8°C)  💧0%  💦77%
    8:00 AM  ☀️  11°C (feels 10°C)  💧0%  💦69%
    9:00 AM  ☀️  14°C (feels 12°C)  💧0%  💦61%
   10:00 AM  ☀️  16°C (feels 14°C)  💧0%  💦59%
   11:00 AM  ☀️  17°C (feels 16°C)  💧0%  💦57%

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📅 7-DAY FORECAST

📆 Saturday, November 15
    🌡️  10°C - 17°C  |  🌦️  Drizzle  (💧 33%)

📆 Sunday, November 16
    🌡️  8°C - 17°C  |  🌤️  Mainly clear

📆 Monday, November 17
    🌡️  9°C - 22°C  |  🌤️  Mainly clear

📆 Tuesday, November 18
    🌡️  7°C - 15°C  |  ⛅  Partly cloudy  (💧 10%)

📆 Wednesday, November 19
    🌡️  6°C - 14°C  |  ☁️  Overcast  (💧 6%)

📆 Thursday, November 20
    🌡️  8°C - 16°C  |  ☁️  Overcast  (💧 3%)
```

## Usage Examples

### Multiple Locations
```bash
# Try different cities
mvn exec:java -Dexec.args="Berlin"
mvn exec:java -Dexec.args="Sydney"
mvn exec:java -Dexec.args="San Francisco"
```

### Temperature Units
```bash
# Fahrenheit (short flag)
mvn exec:java -Dexec.args="Tokyo -f"

# Fahrenheit (long flag)
mvn exec:java -Dexec.args="Madrid --fahrenheit"

# Current location in Fahrenheit
mvn exec:java -Dexec.args="-f"
```

### Location Tips

✅ **Best Practice**: Use just the city name
- Works well: `London`, `Tokyo`, `Paris`, `Sydney`
- Multi-word cities: `New York`, `Los Angeles`, `San Francisco`

❌ **Avoid**: Adding country names
- Bad: `Paris France`
- Good: `Paris`

💡 **For Common Names**: Add state for US cities
- `Portland Oregon`
- `Portland Maine`

## Features

### Weather Data
- 🌡️ **Temperature**: High/low with feels-like
- ⏰ **Hourly Forecast**: Next 12 hours with details
- 📅 **7-Day Forecast**: Extended outlook
- 💧 **Precipitation**: Probability and type
- 💨 **Wind**: Speed and direction
- ☀️ **UV Index**: With safety levels
- 🌅 **Sunrise/Sunset**: Daily times
- 💦 **Humidity**: Relative humidity

### Output Format
Beautiful formatted console display with:
- Unicode box drawing
- Weather emojis (☀️ ☁️ 🌧️ ⛈️ 🌨️)
- Color-coded information
- Easy-to-read layout

## Architecture

Clean, modular design following SOLID principles:

### Package Structure
```
com.weather/
├── App.java                       # Main entry point
├── cli/
│   └── CommandLineParser.java    # Argument parsing
├── display/
│   └── ConsoleWeatherDisplay.java # Console output
├── formatter/
│   ├── WeatherFormatter.java      # Weather formatting
│   └── TimeFormatter.java         # Time utilities
├── mapper/
│   ├── WeatherCodeMapper.java     # Weather codes → text
│   ├── WindDirectionMapper.java   # Degrees → directions
│   └── UvIndexMapper.java         # UV values → levels
├── model/
│   ├── Location.java              # Location data
│   ├── WeatherResponse.java       # Weather data
│   └── ... (other models)
└── service/
    ├── BaseHttpService.java       # HTTP base class
    ├── LocationService.java       # Location resolution
    └── WeatherService.java        # Weather API
```

### Design Benefits

✅ **Testable** - Components tested independently  
✅ **Maintainable** - Clear separation of concerns  
✅ **Readable** - Small, focused classes  
✅ **Extensible** - Easy to add new features  
✅ **Type-safe** - Custom exceptions for errors

## How It Works

1. **Location Resolution**
   - Default: IP-based geolocation (ip-api.com)
   - Specified: Geocoding API (Open-Meteo)

2. **Weather Data**
   - Free API: Open-Meteo forecast API
   - No API key required
   - Comprehensive hourly and daily data

3. **Display**
   - Formatted console output
   - Unicode characters and emojis
   - Supports both °C and °F

## Development

### Building
```bash
# Compile only
mvn compile

# Clean and compile
mvn clean compile

# Package as JAR
mvn package
```

### Testing
```bash
# Run all tests
mvn test

# Run with coverage (if configured)
mvn clean test

# Skip tests during build
mvn install -DskipTests
```

### Running in IDE
- Import as Maven project
- Run `com.weather.App` class
- Configure run arguments in IDE settings

## Dependencies

- **Jackson** (2.17.2) - JSON parsing
- **Java HTTP Client** - Built-in HTTP support
- **JUnit** (4.11) - Testing

## API Information

### Location Services
- **IP Geolocation**: [ip-api.com](http://ip-api.com)
- **Geocoding**: [Open-Meteo Geocoding](https://open-meteo.com/en/docs/geocoding-api)

### Weather Data
- **Weather API**: [Open-Meteo Forecast](https://open-meteo.com/en/docs)
- **Free**: No API key required
- **Rate limits**: Reasonable usage limits apply

## Notes

- Free APIs have rate limits
- Location accuracy depends on IP for default mode
- VPN/proxy may affect IP-based location
- Network errors are displayed to stderr

## Troubleshooting

```bash
# Check Maven version
mvn --version

# Verify Java version
java -version

# Clean build artifacts
mvn clean

# Debug mode
mvn exec:java -X -Dexec.args="London"
```

## License

This project uses free public APIs. Check respective API terms for usage limits.
