package com.demo.retrieve_weather_data_app.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

class OpenWeatherClientTest {

	private MockWebServer server;
	private OpenWeatherClient client;

	@BeforeEach
	void setUp() throws IOException {
		server = new MockWebServer();
		server.start();
		client = new OpenWeatherClient(new OpenWeatherApiProperties("test-api-key", server.url("/").toString()));
	}

	@AfterEach
	void tearDown() throws IOException {
		server.shutdown();
	}

	@Test
	void fetchCurrentWeatherParsesSuccessfulResponse() throws Exception {
		server.enqueue(new MockResponse()
				.setResponseCode(200)
				.setHeader("Content-Type", "application/json")
				.setBody("""
						{
						  "coord": { "lon": 105.85, "lat": 21.03 },
						  "weather": [ { "id": 800, "main": "Clear", "description": "clear sky", "icon": "01d" } ],
						  "main": { "temp": 29.5, "feels_like": 33.2, "humidity": 75, "pressure": 1009 },
						  "visibility": 10000,
						  "name": "Hanoi",
						  "dt": 1730000000
						}
						"""));

		RawWeatherData result = client.fetchCurrentWeather("Hanoi");

		assertThat(result.city()).isEqualTo("Hanoi");
		assertThat(result.temperature()).isEqualTo(29.5);
		assertThat(result.humidity()).isEqualTo(75);
		assertThat(result.description()).isEqualTo("clear sky");
		assertThat(result.observedAt()).isEqualTo(Instant.ofEpochSecond(1730000000));

		RecordedRequest request = server.takeRequest();
		assertThat(request.getPath()).startsWith("/weather?");
		assertThat(request.getPath()).contains("q=Hanoi");
		assertThat(request.getPath()).contains("appid=test-api-key");
		assertThat(request.getPath()).contains("units=metric");
	}

	@Test
	void fetchCurrentWeatherThrowsOnCityNotFound() {
		server.enqueue(new MockResponse()
				.setResponseCode(404)
				.setHeader("Content-Type", "application/json")
				.setBody("{\"cod\":\"404\",\"message\":\"city not found\"}"));

		assertThatThrownBy(() -> client.fetchCurrentWeather("Atlantis"))
				.isInstanceOf(WeatherApiException.class)
				.hasMessageContaining("404")
				.hasMessageContaining("city not found");
	}

	@Test
	void fetchCurrentWeatherThrowsOnServerError() {
		server.enqueue(new MockResponse().setResponseCode(500));

		assertThatThrownBy(() -> client.fetchCurrentWeather("Hanoi"))
				.isInstanceOf(WeatherApiException.class)
				.hasMessageContaining("500");
	}

	@Test
	void fetchCurrentWeatherRejectsBlankCity() {
		assertThatThrownBy(() -> client.fetchCurrentWeather("  "))
				.isInstanceOf(WeatherApiException.class)
				.hasMessageContaining("City must not be blank");
	}
}