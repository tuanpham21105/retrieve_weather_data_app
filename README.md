# Retrieve Weather Data App

A small Spring Boot application built to **learn [JobRunr](https://www.jobrunr.io/)** — a Java library for background job processing.

The app fetches weather data from the [OpenWeather API](https://openweathermap.org/api) and persists it to a relational database. The key idea: instead of calling the external API synchronously during a REST request, requests simply **enqueue background jobs** with JobRunr. The jobs run asynchronously on a background job server, fetch the weather, and save the result.

## JobRunr Concepts Covered

| Concept | Where it's shown |
| --- | --- |
| **Fire-and-forget jobs** (enqueue) | `WeatherService.createOrder()` |
| **Recurring jobs — cron schedule** | `WeatherService.createScheduledOrder()` |
| **Recurring jobs — interval schedule** | `WeatherService.createRecurringOrder()` |
| **Job scheduling via lambda** | `jobScheduler.enqueue(() -> fetchWeather(city))` |
| **Storage auto-detection** | JobRunr uses the app's datasource (H2 / PostgreSQL) |
| **Dashboard** | JobRunr web dashboard on port 8000 |

## Tech Stack

- Java 21
- Spring Boot 4.1.1 (WebMVC + WebFlux/WebClient + Data JPA)
- JobRunr 8.7.0 (`jobrunr-spring-boot-4-starter`)
- H2 (in-memory) / PostgreSQL
- Lombok
- springdoc-openapi (Swagger UI)

## How It Works

The flow follows a layered architecture:

```
REST Controller → WeatherService → JobScheduler (JobRunr)
                                       │
                                       └─▶ fetchWeather(city) ─▶ OpenWeatherClient (WebClient) ─▶ OpenWeather API
                                                        └─▶ WeatherRecordRepository ─▶ Database
```

When you `POST` an order, the controller returns immediately (`200 OK`). The actual work happens inside the JobRunr background job server:

1. JobRunr picks up the enqueued/recurring job.
2. `WeatherService.fetchWeather(city)` calls `OpenWeatherClient.fetchCurrentWeather(city)`.
3. The raw response is mapped to a `WeatherRecord` entity and saved via `WeatherRecordRepository`.
4. The record is logged with its id, temperature, humidity and description.

This makes the external API call non-blocking and retriable, and gives full visibility into every job through the JobRunr dashboard.

### Job scheduling — `WeatherService`

- **One-off**: `jobScheduler.enqueue(() -> fetchWeather(city))` → runs once ASAP.
- **Daily**: `jobScheduler.scheduleRecurrently(id, Cron.daily(hour, minute), () -> fetchWeather(city))`.
- **Interval**: `jobScheduler.scheduleRecurrently(id, Duration.ofHours(n), () -> fetchWeather(city))`.

Recurring job IDs are derived from city + time / interval so that scheduling the same city twice does not duplicate the job.

## Prerequisites

- JDK 21
- Maven (or use the provided `./mvnw` wrapper)
- An OpenWeather API key (free tier works): <https://openweathermap.org/api>
- (Optional) A local PostgreSQL instance, e.g. `demo_weather_app` on port 5432

## Configuration

All configuration lives in `src/main/resources/application.properties`:

| Property | Default | Description |
| --- | --- | --- |
| `OPENWEATHER_API_KEY` | `your-api-key-here` | OpenWeather API key (env var or edit the file) |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/demo_weather_app` | Database connection |
| `db_username` / `db_password` | `devuser` / `devpassword` | Database credentials (env vars override) |
| `jobrunr.dashboard.enabled` | `true` | Enable JobRunr dashboard |
| `jobrunr.background-job-server.enabled` | `true` | Enable the job worker |
| `jobrunr.dashboard.port` | `8000` | Dashboard port |

> JobRunr stores its job data in the same database as the app (auto-detected). With `spring.jpa.hibernate.ddl-auto=update`, the `weather_record` table is created automatically.

## Running the App

```bash
# uses the default profile (PostgreSQL by default in application.properties)
export OPENWEATHER_API_KEY=your-real-key
./mvnw spring-boot:run
```

Or run with PostgreSQL via the dev profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

If you prefer in-memory H2, point the datasource at `jdbc:h2:mem:weatherdb` (the H2 console dependency is already included).

## Usage — REST Endpoints

All endpoints respond immediately; the real work happens in the background.

**1. Fetch weather once**

```bash
curl -X POST http://localhost:8080/order \
  -H "Content-Type: application/json" \
  -d '{"city": "Hanoi"}'
```

**2. Fetch weather daily at a fixed time**

```bash
curl -X POST http://localhost:8080/order/daily \
  -H "Content-Type: application/json" \
  -d '{"city": "Hanoi", "time": "08:00"}'
```

**3. Fetch weather every N hours**

```bash
curl -X POST http://localhost:8080/order/recurring \
  -H "Content-Type: application/json" \
  -d '{"city": "Hanoi", "intervalHours": 6}'
```

### Useful URLs

| URL | What it is |
| --- | --- |
| `http://localhost:8080/swagger-ui.html` | Swagger/OpenAPI UI |
| `http://localhost:8000` | JobRunr dashboard — watch jobs run, retry, and inspect their logs |

## Project Structure

```
src/main/java/com/demo/retrieve_weather_data_app/
├── RetrieveWeatherDataAppApplication.java   # Main entry point
├── controller/
│   ├── WeatherRestController.java           # REST endpoints
│   └── dto/                                 # Request bodies (records)
├── service/
│   └── WeatherService.java                  # Business logic + JobRunr scheduling
├── external/
│   ├── OpenWeatherClient.java               # WebClient HTTP client for OpenWeather
│   ├── OpenWeatherResponse.java             # API response record
│   ├── RawWeatherData.java                  # Parsed DTO
│   ├── WeatherApiException.java
│   └── OpenWeatherApiProperties.java        # @ConfigurationProperties
└── model/
    ├── entity/WeatherRecord.java            # JPA entity
    └── repository/WeatherRecordRepository.java
```

## Tests

```bash
./mvnw test
```

> The full test suite (MockWebServer-based client tests, repository tests) was removed in a later commit; only the Spring context smoke test remains. MockWebServer is still on the classpath for future tests.

## Learning Resources

- [JobRunr documentation](https://www.jobrunr.io/en/documentation/)
- [JobRunr Spring Boot starter](https://www.jobrunr.io/en/documentation/prologue/spring-boot-starter/)