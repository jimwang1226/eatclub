
package com.eatclub.api.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * WebClient configuration class
 */
@Configuration
public class WebClientConfig {

    /**
     * Create a webclient bean for DAO to fetch data
     * @param properties
     * @return
     */
    @Bean
    public WebClient webClient(WebClientProperties properties) {

        // 1) Create HttpClient (Netty)
        HttpClient httpClient = HttpClient.create();

        // 2) Set up connection timeout
        int connectTimeout = (int) properties.connectTimeout().toMillis();
        httpClient = httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);

        // 3) Set up read timeout
        httpClient = httpClient.responseTimeout(properties.readTimeout());

        // 4) Use the HttpClient to create WebClient
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder().clientConnector(connector).build();
    }
}
