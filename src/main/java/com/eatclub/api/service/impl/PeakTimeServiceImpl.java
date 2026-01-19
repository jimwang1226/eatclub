package com.eatclub.api.service.impl;

import com.eatclub.api.dao.IDealDao;
import com.eatclub.api.dto.PeakTimeResponse;
import com.eatclub.api.model.domain.Deal;
import com.eatclub.api.model.domain.Restaurant;
import com.eatclub.api.service.IPeakTimeService;
import com.eatclub.api.util.DealUtils;
import com.eatclub.api.util.TimeUtils;
import com.eatclub.api.util.TimeWindow;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for calculate the peak time
 * (the time period which has the most active deals).
 */
@Service
public class PeakTimeServiceImpl implements IPeakTimeService {

    private final IDealDao dealDao;

    public PeakTimeServiceImpl(IDealDao dealDao) {
        this.dealDao = dealDao;
    }

    @Override
    public PeakTimeResponse calculatePeakTime() {
        // 1) Get all the active deals
        List<Restaurant> restaurants = dealDao.getRestaurants();
        // This array indicates in every minute of the day, the change of the deal's number.
        int[] change = new int[24 * 60];

        // 1.1) Go through every deal in each restaurant, and set up the change array.
        for (Restaurant restaurant : restaurants) {
            if (restaurant == null || restaurant.getDeals() == null) continue;

            // 1.2) Go through deals.
            for (int j = 0; j < restaurant.getDeals().size(); j++) {
                Deal deal = restaurant.getDeals().get(j);
                if (deal == null) continue;

                // 1.3) Get the active period of each deal
                TimeWindow window = DealUtils.getActiveWindowOfDeal(restaurant, deal);

                // 1.4) Add to the change array
                if (window == null) continue;

                int startMinute = window.start().getHour() * 60 + window.start().getMinute();
                int endMinute = window.end().getHour() * 60 + window.end().getMinute();
                change[startMinute] += 1; // Means at that minute, one more deal
                change[endMinute] -= 1; // Means at that minute, one less deal
            }
        }

        // 2) Calculate the max deal, the start time and end time
        int currentDealNo = 0;
        int maxDealNo = 0;
        int startMinute = -1;
        int endMinute = -1;
        // 2.1) Get the max deal number.
        for (int i = 0; i < 1440; i++) {
            currentDealNo += change[i];
            if (currentDealNo > maxDealNo){
                maxDealNo = currentDealNo;
            }
        }

        // 2.2) If the max deal number is 0, means no available deal, return an empty response.
        if (maxDealNo == 0) return new PeakTimeResponse();

        // 2.3) Use the max deal number to calculate the peak start time and end time.
        currentDealNo = 0;
        for (int i = 0; i < 1440; i++) {
            currentDealNo += change[i];
            boolean isPeak = currentDealNo == maxDealNo;
            // Means now is the peak time, and the startMinutes never been set before.
            if (isPeak && startMinute == -1){
                startMinute = i;
            }
            //Means now is not the peak time, the startMinutes has been set before but endMinutes not.
            if (!isPeak && startMinute >= 0){
                endMinute = i;
                break;
            }
        }

        // 4) Wrap in PeakTimeResponse and return.
        return new PeakTimeResponse(TimeUtils.parseTime(startMinute), TimeUtils.parseTime(endMinute));
    }

}
