package com.eatclub.api.service.impl;

import com.eatclub.api.dao.IDealDao;
import com.eatclub.api.dto.DealDto;
import com.eatclub.api.dto.DealResponse;
import com.eatclub.api.model.domain.Deal;
import com.eatclub.api.model.domain.Restaurant;
import com.eatclub.api.service.IDealService;
import com.eatclub.api.util.TimeWindow;
import io.micrometer.common.util.StringUtils;
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
                if (!isActiveDeal(queryTime, restaurant, deal)) continue;

                // 3.3) Transfer to DTO and add to result.
                result.add(toDealDto(restaurant, deal));
            }
        }

        // 4) Wrap in DealResponse and return.
        return new DealResponse(result);
    }

    /**
     * Active window logic:
     * restaurantWindow = [restaurant.open, restaurant.close)
     * dealWindow priority:
     *   - deal.open/close
     *   - deal.start/end
     *   - fallback restaurant.open/close
     * effectiveWindow = intersection(restaurantWindow, dealWindow)
     */
    private boolean isActiveDeal(LocalTime time, Restaurant restaurant, Deal deal) {
        // 1) Generate the TimeWindow object of the restaurant.
        LocalTime restaurantOpen = parseTime(restaurant.getOpen());
        LocalTime restaurantClose = parseTime(restaurant.getClose());
        TimeWindow restaurantWindow = new TimeWindow(restaurantOpen, restaurantClose);

        /* 2) Calculate the deal's start time.
            Because the API might return 'open' field or 'start' field that indicates the start time,
            use the latest one.
         */
        LocalTime dealOpen = StringUtils.isNotBlank(deal.getOpen()) ? parseTime(deal.getOpen()) : null;
        LocalTime dealStart = StringUtils.isNotBlank(deal.getStart()) ? parseTime(deal.getStart()) : null;
        LocalTime dealStartResult;
        if (dealOpen == null && dealStart != null) {
            dealStartResult = dealStart;
        } else if (dealOpen != null && dealStart == null) {
            dealStartResult = dealOpen;
        } else if (dealOpen != null) {
            dealStartResult = dealOpen.isAfter(dealStart) ? dealOpen : dealStart;
        } else {
            dealStartResult = restaurantOpen;
        }

        /* 3) Calculate the deal's end time.
            Because the API might return 'close' field or 'end' field that indicates the end time,
            use the earliest one.
         */
        LocalTime dealClose = StringUtils.isNotBlank(deal.getClose()) ? parseTime(deal.getClose()) : null;
        LocalTime dealEnd = StringUtils.isNotBlank(deal.getEnd()) ? parseTime(deal.getEnd()) : null;
        LocalTime dealEndResult;
        if (dealClose == null && dealEnd != null) {
            dealEndResult = dealEnd;
        } else if (dealClose != null && dealEnd == null) {
            dealEndResult = dealClose;
        } else if (dealClose != null) {
            dealEndResult = dealClose.isBefore(dealEnd) ? dealClose : dealEnd;
        } else {
            dealEndResult = restaurantClose;
        }

        // 4) Create the deal's TimeWindow.
        TimeWindow dealWindow = new TimeWindow(dealStartResult, dealEndResult);

        /* 5) Judge the deal is active or not
          Get intersection between restaurant's open window and deal's open window,
          and check the time contains in the window or not.
        */
        TimeWindow effective = TimeWindow.windowIntersection(restaurantWindow, dealWindow);
        boolean isInWindow = effective != null && effective.isInWindow(time);
        boolean isValidQuantity = safeTransInt(deal.getQtyLeft()) > 0;
        return isInWindow && isValidQuantity;
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
     * Safely transfer a string to an int value, if failed, return 0.
     * @param string a string can trans to int
     * @return an integer
     */
    private int safeTransInt(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (Exception e) {
            return 0;
        }
    }

    /**
     * Set an empty string if the String is null
     */
    private String setEmptyStringIfNull(String string) {
        return string == null ? "" : string;
    }
}
