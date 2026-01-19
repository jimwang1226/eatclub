package com.eatclub.api.dao.impl;

import com.alibaba.fastjson2.JSON;
import com.eatclub.api.config.WebClientProperties;
import com.eatclub.api.dao.IDealDao;
import com.eatclub.api.model.domain.Restaurant;
import com.eatclub.api.model.raw.DealDataResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DealDao
 * Keep refresh the Deal results
 */
@Repository
public class DealDaoImpl implements IDealDao {

    private static final Logger log = LoggerFactory.getLogger(DealDaoImpl.class);

    private final WebClient webClient;
    private final WebClientProperties properties;

    private final AtomicReference<List<Restaurant>> result =
            new AtomicReference<>(Collections.emptyList());

    public DealDaoImpl(WebClient webClient, WebClientProperties props) {
        this.webClient = webClient;
        this.properties = props;
    }

    @PostConstruct
    public void init() {
        log.info("DealDaoImpl initialized, starting first data refresh...");
        refreshSnapshot();
    }

    /**
     * Refresh cache periodically, and keep the last result if it fails
     */
    @Scheduled(fixedDelayString = "${webclient.data.refresh-interval}")
    public void refreshSnapshot() {
        log.debug("Starting scheduled refresh from URL: {}", properties.url());
        try {
            // 1) Fetch remote JSON
            String body = webClient.get()
                    .uri(properties.url())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (body == null || body.isBlank()) {
                log.warn("Received empty response from API, setting result to empty list");
                result.set(Collections.emptyList());
                return;
            }

            log.debug("Received response body length: {} characters", body.length());

            // 2) Parse JSON to DealDataResponse (fastjson2)
            DealDataResponse resp = JSON.parseObject(body, DealDataResponse.class);

            // 3) Store an in-memory snapshot (AtomicReference)
            if (resp == null || resp.getRestaurants() == null) {
                log.warn("Parsed response is null or has no restaurants, setting result to empty list");
                result.set(Collections.emptyList());
                return;
            }

            // To avoid accidental external modification
            List<Restaurant> restaurants = Collections.unmodifiableList(resp.getRestaurants());
            result.set(restaurants);
            log.info("Successfully refreshed data, loaded {} restaurants", restaurants.size());

        } catch (Exception e) {
            // Keep last good snapshot
            log.error("Failed to refresh data from API, keeping last snapshot. Error: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = result.get();
        log.debug("getRestaurants() called, returning {} restaurants", restaurants.size());
        return restaurants;
    }
}
