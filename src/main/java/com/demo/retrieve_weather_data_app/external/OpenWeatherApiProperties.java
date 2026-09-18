package com.demo.retrieve_weather_data_app.external;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openweathermap.api")
public record OpenWeatherApiProperties(String key, String baseUrl) {
}