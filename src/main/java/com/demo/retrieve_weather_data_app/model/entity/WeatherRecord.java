package com.demo.retrieve_weather_data_app.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "weather_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String city;

	private double temperature;

	private int humidity;

	private String description;

	@Column(name = "fetched_at", nullable = false, updatable = false)
	private LocalDateTime fetchedAt;

}