package com.example.noura.riyadh_tb.model;

import java.net.IDN;

public class Service {

    private String Title;
    private String IssuedBy;
    private String Type;
    private String Category;
    private String description;
    private String IssuedTime;
    //   private String Location;
    private String ResponseBy;
    private String state;
    private boolean isConfirm;
    private int Rate;
    private String Date;
    private String ID;

    public Service() {

        this.Title = "";
        this.IssuedBy = "";
        this.Type = "";
        this.Category = "";
        this.description = "";
        this.IssuedTime = "";
        this.ResponseBy = "";
        this.state = "Published";
        this.isConfirm = false;
        this.Rate = 0;
        this.Date="";
        this.ID= "";

    }

    public Service(String title, String issuedBy, String type, String category, String description,
                   String issuedTime, String responseBy,String Date ,String ID) {

        this.Title = title;
        this.IssuedBy = issuedBy;
        this.Type = type;
        this.Category = category;
        this.description = description;
        this.IssuedTime = issuedTime;
        this.ResponseBy = responseBy;
        this.state = "Published";
        this.isConfirm = false;
        this.Rate = 0;
        this.Date=Date;
        this.ID= ID;

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getIssuedBy() {
        return IssuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        IssuedBy = issuedBy;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDescreption() {
        return description;
    }

    public void setDescreption(String descreption) {
        this.description = descreption;
    }

    public String getIssuedTime() {
        return IssuedTime;
    }

    public void setIssuedTime(String issuedTime) {
        IssuedTime = issuedTime;
    }

    public String getResponseBy() {
        return ResponseBy;
    }

    public void setResponseBy(String responseBy) {
        ResponseBy = responseBy;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isConfirm() {
        return isConfirm;
    }

    public void setConfirm(boolean confirm) {
        isConfirm = confirm;
    }

    public int getRate() {
        return Rate;
    }

    public void setRate(int rate) {
        Rate = rate;
    }

    public String getDate() {
        return Date;
    }

    public String getID() {
        return ID;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
