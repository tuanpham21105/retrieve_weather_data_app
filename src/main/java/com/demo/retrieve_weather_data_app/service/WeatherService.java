package com.demo.retrieve_weather_data_app.service;

import java.time.Duration;
import java.time.ZoneOffset;

import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.springframework.stereotype.Service;

import com.demo.retrieve_weather_data_app.controller.dto.OrderRequest;
import com.demo.retrieve_weather_data_app.controller.dto.RecurringOrderRequest;
import com.demo.retrieve_weather_data_app.controller.dto.ScheduleOrderRequest;
import com.demo.retrieve_weather_data_app.external.OpenWeatherClient;
import com.demo.retrieve_weather_data_app.external.RawWeatherData;
import com.demo.retrieve_weather_data_app.model.entity.WeatherRecord;
import com.demo.retrieve_weather_data_app.model.repository.WeatherRecordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {
    private final JobScheduler jobScheduler;
	private final OpenWeatherClient openWeatherClient;
	private final WeatherRecordRepository weatherRecordRepository;

    public void createOrder(OrderRequest request) {
        String city = request.city();
        jobScheduler.enqueue(() -> fetchWeather(city));
    }

    public void createScheduledOrder(ScheduleOrderRequest request) {
        String recurringJobId = "daily" + request.city() + request.time().toString().replace(":", "");
        jobScheduler.scheduleRecurrently(
                recurringJobId,
                Cron.daily(request.time().getHour(), request.time().getMinute()),
                () -> fetchWeather(request.city()));
    }

    public void createRecurringOrder(RecurringOrderRequest request) {
        String recurringJobId = "recurring" + request.city() + request.intervalHours();
        jobScheduler.scheduleRecurrently(
                recurringJobId,
                Duration.ofHours(request.intervalHours()),
                () -> fetchWeather(request.city()));
    }

	public void fetchWeather(String city) {
        log.info("Start fetching weather data for city {}", city);

        RawWeatherData data = openWeatherClient.fetchCurrentWeather(city);

        WeatherRecord record = WeatherRecord.builder()
                .city(data.city())
                .temperature(data.temperature())
                .humidity(data.humidity())
                .description(data.description())
                .fetchedAt(data.observedAt().atZone(ZoneOffset.UTC).toLocalDateTime())
                .build();
        WeatherRecord saved = weatherRecordRepository.save(record);

        log.info(
            "Persisted weather data for city '{}': record id={}, temp={}, humidity={}, description={}", 
            city, saved.getId(), saved.getTemperature(), saved.getHumidity(), saved.getDescription()
        );
	}
}
