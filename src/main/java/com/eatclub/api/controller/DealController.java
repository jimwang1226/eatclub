package com.eatclub.api.controller;

import com.eatclub.api.dto.DealResponse;
import com.eatclub.api.service.IDealService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deals")
public class DealController {

    private static final Logger log = LoggerFactory.getLogger(DealController.class);

    private final IDealService dealService;

    public DealController(IDealService dealService) {
        this.dealService = dealService;
    }

    @GetMapping
    public DealResponse getDeals(@RequestParam @NotBlank String timeOfDay) {
        log.info("Received request to get deals for time: {}", timeOfDay);
        DealResponse response = dealService.queryActiveDealsByTime(timeOfDay);
        log.info("Returning {} deals for time: {}", response.getDeals().size(), timeOfDay);
        return response;
    }
}
