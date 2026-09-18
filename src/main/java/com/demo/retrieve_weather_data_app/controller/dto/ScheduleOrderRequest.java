package com.demo.retrieve_weather_data_app.controller.dto;

import java.time.LocalTime;

public record ScheduleOrderRequest(String city, LocalTime time) {
}