package com.demo.retrieve_weather_data_app.external;

public class WeatherApiException extends RuntimeException {

	public WeatherApiException(String message) {
		super(message);
	}

	public WeatherApiException(String message, Throwable cause) {
		super(message, cause);
	}
}