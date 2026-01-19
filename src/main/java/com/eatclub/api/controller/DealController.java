package com.eatclub.api.controller;

import com.eatclub.api.dto.DealResponse;
import com.eatclub.api.service.IDealService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deals")
public class DealController {

    private final IDealService dealService;

    public DealController(IDealService dealService) {
        this.dealService = dealService;
    }

    @GetMapping
    public DealResponse getDeals(@RequestParam @NotBlank String timeOfDay) {
        return dealService.queryActiveDealsByTime(timeOfDay);
    }
}
