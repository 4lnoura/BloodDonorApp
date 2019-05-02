package com.example.noura.riyadh_tb.model;

public class Comment {

private String IssuedByName;
private String IssuedUsername;
private String imageURL;
private String TextComment;

    public Comment() {

      this.IssuedByName="";
      this.IssuedUsername="";
      this.TextComment="";
      this.imageURL="";


    }

    public Comment(String issuedByName, String issuedUsername, String imageURL, String textComment) {
        IssuedByName = issuedByName;
        IssuedUsername = issuedUsername;
        this.imageURL = imageURL;
        TextComment = textComment;
    }

    public String getIssuedByName() {
        return IssuedByName;
    }

    public void setIssuedByName(String issuedByName) {
        IssuedByName = issuedByName;
    }

    public String getIssuedUsername() {
        return IssuedUsername;
    }

    public void setIssuedUsername(String issuedUsername) {
        IssuedUsername = issuedUsername;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTextComment() {
        return TextComment;
    }

    public void setTextComment(String textComment) {
        TextComment = textComment;
    }
}
