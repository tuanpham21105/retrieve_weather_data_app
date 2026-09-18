package com.demo.retrieve_weather_data_app.external;

import java.time.Instant;

public record RawWeatherData(String city, double temperature, int humidity, String description, Instant observedAt) {
}