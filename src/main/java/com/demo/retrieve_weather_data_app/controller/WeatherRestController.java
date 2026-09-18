package com.demo.retrieve_weather_data_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.retrieve_weather_data_app.controller.dto.OrderRequest;
import com.demo.retrieve_weather_data_app.controller.dto.RecurringOrderRequest;
import com.demo.retrieve_weather_data_app.controller.dto.ScheduleOrderRequest;
import com.demo.retrieve_weather_data_app.service.WeatherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WeatherRestController {
    private final WeatherService weatherService;

    @PostMapping("/order")
    public ResponseEntity<String> postOrder(@RequestBody OrderRequest request) {
        weatherService.createOrder(request);
        return ResponseEntity.ok().body("Create order success");
    }

    @PostMapping("/order/daily")
    public ResponseEntity<String> postScheduledOrder(@RequestBody ScheduleOrderRequest request) {
        weatherService.createScheduledOrder(request);
        return ResponseEntity.ok().body("Create daily order success");
    }

    @PostMapping("/order/recurring")
    public ResponseEntity<String> postRecurringOrder(@RequestBody RecurringOrderRequest request) {
        weatherService.createRecurringOrder(request);
        return ResponseEntity.ok().body("Create recurring order success");
    }
}
