package com.example.noura.riyadh_tb.model;

public class RecommendationServices {
    private String Title;
    private String Category;
    private String Description;
    private double XLocation;
    private double YLocation;


    public RecommendationServices(String Title, String Category, String Description, double XLocation, double YLocation){
        this.Title=Title;
        this.Category=Category;
        this.Description=Description;
        this.XLocation=XLocation;
        this.YLocation=YLocation;

    }
    public String getTitle() {
        return Title;
    }

    public double getYLocation() {
        return YLocation;
    }

    public double getXLocation() {
        return XLocation;
    }

    public String getCategory() {
        return Category;
    }

    public String getDescription() {
        return Description;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setYLocation(double YLocation) {
        this.YLocation = YLocation;
    }

    public void setXLocation(double XLocation) {
        this.XLocation = XLocation;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
