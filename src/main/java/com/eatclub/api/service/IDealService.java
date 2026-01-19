package com.eatclub.api.service;

import com.eatclub.api.dto.DealResponse;

public interface IDealService {
    DealResponse queryActiveDealsByTime(String time);
}
