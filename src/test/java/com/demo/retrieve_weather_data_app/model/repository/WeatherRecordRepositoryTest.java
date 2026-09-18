package com.demo.retrieve_weather_data_app.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.demo.retrieve_weather_data_app.model.entity.WeatherRecord;

@DataJpaTest
class WeatherRecordRepositoryTest {

	@Autowired
	private WeatherRecordRepository repository;

	@Test
	void saveAndFindByIdRoundTripsAllFields() {
		LocalDateTime fetchedAt = LocalDateTime.of(2026, 9, 18, 9, 0);
		WeatherRecord record = WeatherRecord.builder()
				.city("Hanoi")
				.temperature(29.5)
				.humidity(75)
				.description("clear sky")
				.fetchedAt(fetchedAt)
				.build();

		WeatherRecord saved = repository.save(record);

		assertThat(saved.getId()).isNotNull();
		Optional<WeatherRecord> found = repository.findById(saved.getId());
		assertThat(found).isPresent();
		WeatherRecord reloaded = found.get();
		assertThat(reloaded.getCity()).isEqualTo("Hanoi");
		assertThat(reloaded.getTemperature()).isEqualTo(29.5);
		assertThat(reloaded.getHumidity()).isEqualTo(75);
		assertThat(reloaded.getDescription()).isEqualTo("clear sky");
		assertThat(reloaded.getFetchedAt()).isEqualTo(fetchedAt);
	}
}