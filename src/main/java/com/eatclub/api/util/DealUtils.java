package com.eatclub.api.util;

import com.eatclub.api.model.domain.Deal;
import com.eatclub.api.model.domain.Restaurant;
import io.micrometer.common.util.StringUtils;

import java.time.LocalTime;

import static com.eatclub.api.util.TimeUtils.parseTime;

public class DealUtils {

    /**
     * Active window logic:
     * restaurantWindow = [restaurant.open, restaurant.close)
     * dealWindow = [latest(deal.open, deal.start), earlist(deal.close,deal.end))
     * effectiveWindow = intersection(restaurantWindow, dealWindow)
     */
    public static boolean isActiveDealContainsTime(LocalTime time, Restaurant restaurant, Deal deal) {
        //Get the active deal's time window.
        TimeWindow effective = getActiveWindowOfDeal(restaurant, deal);
        if (effective == null) return false;
        return effective.isInWindow(time);
    }

    /**
     * Get the active time window from restaurantWindow and deal.
     * The deal's quantity must > 0, otherwise is inactive deal.
     * @return if the deal is active, return the TimeWindow, or return null;
     */
    public static TimeWindow getActiveWindowOfDeal(Restaurant restaurant, Deal deal) {
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
        if (safeTransInt(deal.getQtyLeft()) > 0) {
            return effective;
        } else {
            return null;
        }
    }

    /**
     * Safely transfer a string to an int value, if failed, return 0.
     * @param string a string can trans to int
     * @return an integer
     */
    public static int safeTransInt(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (Exception e) {
            return 0;
        }
    }
}
