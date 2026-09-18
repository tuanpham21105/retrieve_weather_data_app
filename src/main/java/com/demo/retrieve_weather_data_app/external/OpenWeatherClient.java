package com.demo.retrieve_weather_data_app.external;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class OpenWeatherClient {

	private static final String UNITS = "metric";

	private final WebClient webClient;
	private final String apiKey;
	private final Duration timeout;

	@Autowired
	public OpenWeatherClient(OpenWeatherApiProperties properties) {
		this(properties, Duration.ofSeconds(10));
	}

	public OpenWeatherClient(OpenWeatherApiProperties properties, Duration timeout) {
		this.apiKey = properties.key();
		this.timeout = timeout;
		this.webClient = WebClient.create(properties.baseUrl());
	}

	public RawWeatherData fetchCurrentWeather(String city) {
		if (city == null || city.isBlank()) {
			throw new WeatherApiException("City must not be blank");
		}
		try {
			OpenWeatherResponse response = webClient.get()
					.uri(uriBuilder -> uriBuilder.path("/weather")
							.queryParam("q", city)
							.queryParam("appid", apiKey)
							.queryParam("units", UNITS)
							.build())
					.retrieve()
					.onStatus(HttpStatusCode::isError,
							clientResponse -> clientResponse.bodyToMono(String.class)
									.defaultIfEmpty("")
									.flatMap(body -> Mono.error(new WeatherApiException(
											"OpenWeather API error: HTTP " + clientResponse.statusCode().value()
													+ " - " + body))))
					.bodyToMono(OpenWeatherResponse.class)
					.block(timeout);
			return new RawWeatherData(city, response.main().temp(), response.main().humidity(),
					descriptionOf(response.weather()), Instant.ofEpochSecond(response.dt()));
		} catch (WeatherApiException e) {
			throw e;
		} catch (Exception e) {
			throw new WeatherApiException("Failed to fetch weather data for city '" + city + "': " + e.getMessage(),
					e);
		}
	}

	private String descriptionOf(List<OpenWeatherResponse.Weather> weather) {
		return weather == null || weather.isEmpty() ? null : weather.get(0).description();
	}
}