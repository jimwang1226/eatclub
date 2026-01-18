package com.eatclub.api.dao.impl;

import com.eatclub.api.config.WebClientProperties;
import com.eatclub.api.model.domain.Restaurant;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DealDaoImpl
 * Tests the data fetching and caching functionality
 */
class DealDaoImplTest {

    private MockWebServer mockWebServer;
    private DealDaoImpl dealDao;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        
        WebClientProperties properties = new WebClientProperties(
                baseUrl,
                Duration.ofSeconds(5),
                Duration.ofSeconds(2),
                Duration.ofSeconds(4)
        );

        WebClient webClient = WebClient.builder().build();
        dealDao = new DealDaoImpl(webClient, properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    /**
     * Integration test - calls the real EatClub API
     * Remove @Disabled to run this test manually
     */
    @Test
    @DisplayName("[Integration] Should fetch real data from EatClub API")
    @Disabled("Integration test - enable manually to test real API call")
    void testRealApiCall_FetchFromEatClubApi() {
        // Arrange - Use real API URL
        String realApiUrl = "https://eccdn.com.au/misc/challengedata.json";
        WebClientProperties properties = new WebClientProperties(
                realApiUrl,
                Duration.ofSeconds(5),
                Duration.ofSeconds(5),
                Duration.ofSeconds(10)
        );

        WebClient webClient = WebClient.builder().build();
        DealDaoImpl realDao = new DealDaoImpl(webClient, properties);

        // Act - Call the real API
        realDao.refreshSnapshot();

        // Assert - Verify we got real data
        List<Restaurant> restaurants = realDao.getRestaurants();
        
        System.out.println("========== Real API Call Result ==========");
        System.out.println("Total restaurants fetched: " + restaurants.size());
        
        assertNotNull(restaurants, "Restaurants list should not be null");
        assertFalse(restaurants.isEmpty(), "Restaurants list should not be empty");
        
        // Print first few restaurants for verification
        int displayCount = Math.min(5, restaurants.size());
        System.out.println("\nFirst " + displayCount + " restaurants:");
        for (int i = 0; i < displayCount; i++) {
            Restaurant r = restaurants.get(i);
            System.out.println("  [" + (i + 1) + "] " + r.getName());
            System.out.println("      ID: " + r.getObjectId());
            System.out.println("      Address: " + r.getAddress1() + ", " + r.getSuburb());
            System.out.println("      Hours: " + r.getOpen() + " - " + r.getClose());
            if (r.getDeals() != null && !r.getDeals().isEmpty()) {
                System.out.println("      Deals: " + r.getDeals().size() + " available");
                r.getDeals().forEach(deal -> 
                    System.out.println("        - Discount: " + deal.getDiscount() + "%, ID: " + deal.getObjectId())
                );
            }
            System.out.println();
        }
        System.out.println("==========================================");

        // Verify data structure
        Restaurant firstRestaurant = restaurants.get(0);
        assertNotNull(firstRestaurant.getObjectId(), "Restaurant should have objectId");
        assertNotNull(firstRestaurant.getName(), "Restaurant should have name");
    }
}

