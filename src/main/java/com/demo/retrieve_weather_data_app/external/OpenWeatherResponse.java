package com.demo.retrieve_weather_data_app.external;

import java.util.List;

public record OpenWeatherResponse(Main main, List<Weather> weather, String name, long dt) {

	public record Main(double temp, int humidity) {
	}

	public record Weather(String description) {
	}
}