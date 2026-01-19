package com.eatclub.api.dto;

import java.util.List;

public class DealResponse {
    private List<DealDto> deals;

    public DealResponse() {}

    public DealResponse(List<DealDto> deals) {
        this.deals = deals;
    }

    public List<DealDto> getDeals() {
        return deals;
    }

    public void setDeals(List<DealDto> deals) {
        this.deals = deals;
    }
}
