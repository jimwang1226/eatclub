package com.eatclub.api.service.impl;

import com.eatclub.api.dao.IDealDao;
import com.eatclub.api.dto.DealDto;
import com.eatclub.api.dto.DealResponse;
import com.eatclub.api.model.domain.Deal;
import com.eatclub.api.model.domain.Restaurant;
import com.eatclub.api.service.IDealService;
import com.eatclub.api.util.DealUtils;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.eatclub.api.util.TimeUtils.parseTime;

/**
 * Service for querying deals.
 */
@Service
public class DealServiceImpl implements IDealService {

    private final IDealDao dealDao;

    public DealServiceImpl(IDealDao dealDao) {
        this.dealDao = dealDao;
    }

    /**
     * Query the active deals by time.
     * @param time is the query time
     * @return DealResponse
     */
    @Override
    public DealResponse queryActiveDealsByTime(String time) {

        // 1) Time format check, and parse to local time.
        LocalTime queryTime = parseTime(time);

        // 2) Get restaurants from the cache.
        List<Restaurant> restaurants = dealDao.getRestaurants();
        List<DealDto> result = new ArrayList<>();

        // 3) Go through every deal in each restaurant.
        for (Restaurant restaurant : restaurants) {
            if (restaurant == null || restaurant.getDeals() == null) continue;

            // 3.1) Go through deals.
            for (int j = 0; j < restaurant.getDeals().size(); j++) {
                Deal deal = restaurant.getDeals().get(j);
                if (deal == null) continue;

                // 3.2) Using the query time, restaurant info, deal info to check the deal is active or not
                if (!DealUtils.isActiveDealContainsTime(queryTime, restaurant, deal)) continue;

                // 3.3) Transfer to DTO and add to result.
                result.add(toDealDto(restaurant, deal));
            }
        }

        // 4) Wrap in DealResponse and return.
        return new DealResponse(result);
    }

    private DealDto toDealDto(Restaurant restaurant, Deal deal) {
        return new DealDto(
                setEmptyStringIfNull(restaurant.getObjectId()),     //restaurantObjectId
                setEmptyStringIfNull(restaurant.getName()),         //restaurantName
                setEmptyStringIfNull(restaurant.getAddress1()),     //restaurantAddress1
                setEmptyStringIfNull(restaurant.getSuburb()),       //restaurantSuburb
                setEmptyStringIfNull(restaurant.getOpen()),         //restaurantOpen
                setEmptyStringIfNull(restaurant.getClose()),        //restaurantClose
                setEmptyStringIfNull(deal.getObjectId()),           //dealObjectId
                setEmptyStringIfNull(deal.getDiscount()),           //discount
                setEmptyStringIfNull(deal.getDineIn()),             //dineIn
                setEmptyStringIfNull(deal.getLightning()),          //lightning
                setEmptyStringIfNull(deal.getQtyLeft())             //qtyLeft
        );
    }

    /**
     * Set an empty string if the String is null
     */
    private String setEmptyStringIfNull(String string) {
        return string == null ? "" : string;
    }
}
