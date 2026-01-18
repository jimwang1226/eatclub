package com.eatclub.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "webclient.data")
public record WebClientProperties(
        String url,
        Duration refreshInterval,
        Duration connectTimeout,
        Duration readTimeout
) {}
