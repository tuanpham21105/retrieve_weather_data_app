package com.demo.retrieve_weather_data_app.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.retrieve_weather_data_app.model.entity.WeatherRecord;

public interface WeatherRecordRepository extends JpaRepository<WeatherRecord, Long> {
}