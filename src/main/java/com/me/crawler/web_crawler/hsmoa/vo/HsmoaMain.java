package com.me.crawler.web_crawler.hsmoa.vo;

public class HsmoaMain {
    private String link;
    private String mainImage;
    private String shopMark;
    private String startTime;
    private String endTime;
    private String title;
    private String orgPrice;
    private String salesPrice;

    private HsmoaDetail hsmoaDetail;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getShopMark() {
        return shopMark;
    }

    public void setShopMark(String shopMark) {
        this.shopMark = shopMark;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrgPrice() {
        return orgPrice;
    }

    public void setOrgPrice(String orgPrice) {
        this.orgPrice = orgPrice;
    }

    public String getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(String salesPrice) {
        this.salesPrice = salesPrice;
    }

    public HsmoaDetail getHsmoaDetail() {
        return hsmoaDetail;
    }

    public void setHsmoaDetail(HsmoaDetail hsmoaDetail) {
        this.hsmoaDetail = hsmoaDetail;
    }

    public String toString() {
        return "title : " + this.title + ", " + "salesPrice : " + this.hsmoaDetail.getSalesPrice() + ", " + "realTime : " + this.hsmoaDetail.getRealTime();


    }
}
