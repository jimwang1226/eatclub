package com.eatclub.api.controller;

import com.eatclub.api.dto.DealDto;
import com.eatclub.api.dto.DealResponse;
import com.eatclub.api.service.IDealService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for DealController
 */
@WebMvcTest(DealController.class)
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDealService dealService;

    @Test
    @DisplayName("正向案例：传入有效 timeOfDay 参数，返回 deals 列表")
    void getDeals_WithValidTimeOfDay_ReturnsDeals() throws Exception {
        // Given
        DealDto deal1 = new DealDto();
        deal1.setRestaurantName("Restaurant A");
        deal1.setDiscount("20%");

        DealDto deal2 = new DealDto();
        deal2.setRestaurantName("Restaurant B");
        deal2.setDiscount("30%");

        DealResponse response = new DealResponse(List.of(deal1, deal2));
        given(dealService.queryActiveDealsByTime("12:30")).willReturn(response);

        // When & Then
        mockMvc.perform(get("/deals").param("timeOfDay", "12:30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deals").isArray())
                .andExpect(jsonPath("$.deals.length()").value(2))
                .andExpect(jsonPath("$.deals[0].restaurantName").value("Restaurant A"))
                .andExpect(jsonPath("$.deals[1].discount").value("30%"));

        then(dealService).should().queryActiveDealsByTime("12:30");
    }

    @Test
    @DisplayName("反向案例：缺少 timeOfDay 参数，返回 400 错误")
    void getDeals_WithoutTimeOfDay_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/deals"))
                .andExpect(status().isBadRequest());

        then(dealService).should(never()).queryActiveDealsByTime(anyString());
    }
}
