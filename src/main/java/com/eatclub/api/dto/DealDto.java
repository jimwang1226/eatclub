package com.eatclub.api.dto;

/**
 * DealDto
 */
public class DealDto {

    private String restaurantObjectId;
    private String restaurantName;
    private String restaurantAddress1;
    private String restaurantSuburb;
    private String restaurantOpen;
    private String restaurantClose;
    private String dealObjectId;
    private String discount;
    private String dineIn;
    private String lightning;
    private String qtyLeft;

    public DealDto() {}

    public DealDto(String restaurantObjectId,
                   String restaurantName,
                   String restaurantAddress1,
                   String restaurantSuburb,
                   String restaurantOpen,
                   String restaurantClose,
                   String dealObjectId,
                   String discount,
                   String dineIn,
                   String lightning,
                   String qtyLeft) {
        this.restaurantObjectId = restaurantObjectId;
        this.restaurantName = restaurantName;
        this.restaurantAddress1 = restaurantAddress1;
        this.restaurantSuburb = restaurantSuburb;
        this.restaurantOpen = restaurantOpen;
        this.restaurantClose = restaurantClose;
        this.dealObjectId = dealObjectId;
        this.discount = discount;
        this.dineIn = dineIn;
        this.lightning = lightning;
        this.qtyLeft = qtyLeft;
    }

    public String getRestaurantObjectId() {
        return restaurantObjectId;
    }

    public void setRestaurantObjectId(String restaurantObjectId) {
        this.restaurantObjectId = restaurantObjectId;
    }

    public String getQtyLeft() {
        return qtyLeft;
    }

    public void setQtyLeft(String qtyLeft) {
        this.qtyLeft = qtyLeft;
    }

    public String getDineIn() {
        return dineIn;
    }

    public void setDineIn(String dineIn) {
        this.dineIn = dineIn;
    }

    public String getLightning() {
        return lightning;
    }

    public void setLightning(String lightning) {
        this.lightning = lightning;
    }

    public String getDealObjectId() {
        return dealObjectId;
    }

    public void setDealObjectId(String dealObjectId) {
        this.dealObjectId = dealObjectId;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getRestaurantClose() {
        return restaurantClose;
    }

    public void setRestaurantClose(String restaurantClose) {
        this.restaurantClose = restaurantClose;
    }

    public String getRestaurantOpen() {
        return restaurantOpen;
    }

    public void setRestaurantOpen(String restaurantOpen) {
        this.restaurantOpen = restaurantOpen;
    }

    public String getRestaurantSuburb() {
        return restaurantSuburb;
    }

    public void setRestaurantSuburb(String restaurantSuburb) {
        this.restaurantSuburb = restaurantSuburb;
    }

    public String getRestaurantAddress1() {
        return restaurantAddress1;
    }

    public void setRestaurantAddress1(String restaurantAddress1) {
        this.restaurantAddress1 = restaurantAddress1;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    @Override
    public String toString() {
        return "DealDto{" +
                "restaurantObjectId='" + restaurantObjectId + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", restaurantAddress1='" + restaurantAddress1 + '\'' +
                ", restaurantSuburb='" + restaurantSuburb + '\'' +
                ", restaurantOpen='" + restaurantOpen + '\'' +
                ", restaurantClose='" + restaurantClose + '\'' +
                ", dealObjectId='" + dealObjectId + '\'' +
                ", discount='" + discount + '\'' +
                ", dineIn='" + dineIn + '\'' +
                ", lightning='" + lightning + '\'' +
                ", qtyLeft='" + qtyLeft + '\'' +
                '}';
    }
}

