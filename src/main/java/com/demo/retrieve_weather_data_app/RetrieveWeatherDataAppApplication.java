package com.demo.retrieve_weather_data_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RetrieveWeatherDataAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetrieveWeatherDataAppApplication.class, args);
	}

}