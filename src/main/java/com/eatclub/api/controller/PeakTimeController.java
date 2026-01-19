package com.eatclub.api.controller;

import com.eatclub.api.dto.PeakTimeResponse;
import com.eatclub.api.service.IPeakTimeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/peaktime")
public class PeakTimeController {

    private final IPeakTimeService peakTimeService;

    public PeakTimeController( IPeakTimeService peakTimeService) {
        this.peakTimeService = peakTimeService;
    }

    @GetMapping
    public PeakTimeResponse getPeakTime() {
        return peakTimeService.calculatePeakTime();
    }
}