/**
 * THe raw data received from the deal fetch API
 */
package com.eatclub.api.model.raw;

import com.eatclub.api.model.domain.Restaurant;

import java.util.List;

public class DealDataResponse {

    private List<Restaurant> restaurants;

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }
}
