package com.eatclub.api.controller;

import com.eatclub.api.dto.PeakTimeResponse;
import com.eatclub.api.service.IPeakTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/peaktime")
public class PeakTimeController {

    private static final Logger log = LoggerFactory.getLogger(PeakTimeController.class);

    private final IPeakTimeService peakTimeService;

    public PeakTimeController( IPeakTimeService peakTimeService) {
        this.peakTimeService = peakTimeService;
    }

    @GetMapping
    public PeakTimeResponse getPeakTime() {
        log.info("Received request to calculate peak time");
        PeakTimeResponse response = peakTimeService.calculatePeakTime();
        log.info("Returning peak time: {} - {}", response.getPeakTimeStart(), response.getPeakTimeEnd());
        return response;
    }
}