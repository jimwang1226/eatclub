package com.eatclub.api.model.domain;

import java.util.List;

public class Restaurant {
    private String objectId;
    private String name;
    private String address1;
    private String suburb;
    private String open;
    private String close;
    private List<Deal> deals;

    public String getObjectId() { return objectId; }
    public void setObjectId(String objectId) { this.objectId = objectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getSuburb() { return suburb; }
    public void setSuburb(String suburb) { this.suburb = suburb; }

    public String getOpen() { return open; }
    public void setOpen(String open) { this.open = open; }

    public String getClose() { return close; }
    public void setClose(String close) { this.close = close; }

    public List<Deal> getDeals() { return deals; }
    public void setDeals(List<Deal> deals) { this.deals = deals; }
}

