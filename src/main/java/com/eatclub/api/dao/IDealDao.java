package com.eatclub.api.dao;

import com.eatclub.api.model.domain.Restaurant;

import java.util.List;

public interface IDealDao {
    List<Restaurant> getRestaurants();
}
