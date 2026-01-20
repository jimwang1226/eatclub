package com.eatclub.api.controller;

import com.eatclub.api.dao.IDealDao;
import com.eatclub.api.model.domain.Deal;
import com.eatclub.api.model.domain.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * DealController tests
 * Mock the DAO (IDealDao), the request flows through:
 * Controller -> Service -> DAO (mocked)
 * Not depend on real data.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDealDao dealDao;

    private Restaurant createMockRestaurant(String objectId, String name, String address,
                                            String suburb, String open, String close,
                                            List<Deal> deals) {
        Restaurant restaurant = new Restaurant();
        restaurant.setObjectId(objectId);
        restaurant.setName(name);
        restaurant.setAddress1(address);
        restaurant.setSuburb(suburb);
        restaurant.setOpen(open);
        restaurant.setClose(close);
        restaurant.setDeals(deals);
        return restaurant;
    }

    private Deal createMockDeal(String objectId, String discount, String dineIn,
                                String lightning, String qtyLeft,
                                String open, String close, String start, String end) {
        Deal deal = new Deal();
        deal.setObjectId(objectId);
        deal.setDiscount(discount);
        deal.setDineIn(dineIn);
        deal.setLightning(lightning);
        deal.setQtyLeft(qtyLeft);
        deal.setOpen(open);
        deal.setClose(close);
        deal.setStart(start);
        deal.setEnd(end);
        return deal;
    }

    @Test
    @DisplayName("Returns active deal when time falls inside the deal window")
    void getDeals_whenTimeWithinDealWindow_shouldReturnActiveDeal() throws Exception {
        // Given: a deal that's available from 11:00am to 2:00pm, with qty left
        Deal activeDeal = createMockDeal(
                "deal-001",
                "30",
                "true",
                "false",
                "5",
                "11:00am",
                "2:00pm",
                null,
                null
        );

        Restaurant restaurant = createMockRestaurant(
                "rest-001",
                "Golden Dragon Restaurant",
                "123 Main Street",
                "Melbourne CBD",
                "10:00am",
                "10:00pm",
                List.of(activeDeal)
        );

        when(dealDao.getRestaurants()).thenReturn(List.of(restaurant));

        // When: querying at 12:00pm (inside the window)
        // Then: one deal returned with the expected fields
        mockMvc.perform(get("/deals")
                        .param("timeOfDay", "12:00pm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals", hasSize(1)))
                .andExpect(jsonPath("$.deals[0].restaurantObjectId", is("rest-001")))
                .andExpect(jsonPath("$.deals[0].restaurantName", is("Golden Dragon Restaurant")))
                .andExpect(jsonPath("$.deals[0].restaurantAddress1", is("123 Main Street")))
                .andExpect(jsonPath("$.deals[0].restaurantSuburb", is("Melbourne CBD")))
                .andExpect(jsonPath("$.deals[0].restaurantOpen", is("10:00am")))
                .andExpect(jsonPath("$.deals[0].restaurantClose", is("10:00pm")))
                .andExpect(jsonPath("$.deals[0].dealObjectId", is("deal-001")))
                .andExpect(jsonPath("$.deals[0].discount", is("30")))
                .andExpect(jsonPath("$.deals[0].dineIn", is("true")))
                .andExpect(jsonPath("$.deals[0].lightning", is("false")))
                .andExpect(jsonPath("$.deals[0].qtyLeft", is("5")));
    }

    @Test
    @DisplayName("Returns empty list when time is outside the deal window")
    void getDeals_whenTimeOutsideDealWindow_shouldReturnEmptyDeals() throws Exception {
        // Given: a deal that's only valid from 11:00am to 2:00pm
        Deal deal = createMockDeal(
                "deal-002",
                "25",
                "true",
                "false",
                "10",
                "11:00am",
                "2:00pm",
                null,
                null
        );

        Restaurant restaurant = createMockRestaurant(
                "rest-002",
                "Sakura Sushi",
                "456 High Street",
                "Fitzroy",
                "10:00am",
                "10:00pm",
                List.of(deal)
        );

        when(dealDao.getRestaurants()).thenReturn(List.of(restaurant));

        // When: querying at 18:00 (6:00pm), which is outside the window
        // Then: nothing should be returned
        mockMvc.perform(get("/deals")
                        .param("timeOfDay", "18:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals", hasSize(0)));
    }

    @Test
    @DisplayName("Returns empty list when DAO returns no restaurants")
    void getDeals_whenDaoReturnsEmpty_shouldReturnEmptyDeals() throws Exception {
        when(dealDao.getRestaurants()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/deals")
                        .param("timeOfDay", "12:00pm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals", hasSize(0)));
    }

    @Test
    @DisplayName("Filters out sold-out deals even if the time matches")
    void getDeals_whenDealSoldOut_shouldNotReturnDeal() throws Exception {
        Deal soldOutDeal = createMockDeal(
                "deal-003",
                "40",
                "true",
                "false",
                "0",
                "10:00am",
                "8:00pm",
                null,
                null
        );

        Restaurant restaurant = createMockRestaurant(
                "rest-003",
                "Pasta Palace",
                "789 Queen Street",
                "Richmond",
                "9:00am",
                "11:00pm",
                List.of(soldOutDeal)
        );

        when(dealDao.getRestaurants()).thenReturn(List.of(restaurant));

        mockMvc.perform(get("/deals")
                        .param("timeOfDay", "12:00pm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals", hasSize(0)));
    }

    @Test
    @DisplayName("Invalid time format -> INTERNAL_ERROR")
    void getDeals_whenInvalidTimeFormat_shouldReturnError() throws Exception {
        // DAO shouldn't really matter here; request should fail during parsing/validation
        when(dealDao.getRestaurants()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/deals")
                        .param("timeOfDay", "123456"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", is("INTERNAL_ERROR")));
    }
}
