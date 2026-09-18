package com.demo.retrieve_weather_data_app.external;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
		"jobrunr.dashboard.enabled=false",
		"jobrunr.background-job-server.enabled=false"
})
@ActiveProfiles("dev")
class OpenWeatherClientLiveTest {

	@Autowired
	private OpenWeatherClient client;

	@Test
	void fetchCurrentWeatherAgainstRealApi() {
		Assumptions.assumeTrue("true".equalsIgnoreCase(System.getenv("OPENWEATHER_LIVE_TESTS")),
				"skipped: set OPENWEATHER_LIVE_TESTS=true to run against the live OpenWeather API");

		RawWeatherData result = client.fetchCurrentWeather("Hanoi");

		System.out.println("LIVE OpenWeather response: " + result);
		assertThat(result.city()).isEqualTo("Hanoi");
		assertThat(result.temperature()).isFinite();
		assertThat(result.humidity()).isBetween(0, 100);
		assertThat(result.description()).isNotBlank();
		assertThat(result.observedAt()).isNotNull();
	}
}