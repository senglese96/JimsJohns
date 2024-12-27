package com.example.jimsjohns;

import java.io.Serializable;

public class Bathroom implements Serializable {
    private String name;
    private String description;
    private boolean genderNeutral;
    private boolean purchaseNecessary;
    private int rating;
    private double latitude;
    private double longitude;
    private String createdBy;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGenderNeutral() {
        return genderNeutral;
    }

    public void setGenderNeutral(boolean genderNeutral) {
        this.genderNeutral = genderNeutral;
    }

    public boolean isPurchaseNecessary() {
        return purchaseNecessary;
    }

    public void setPurchaseNecessary(boolean purchaseNecessary) {
        this.purchaseNecessary = purchaseNecessary;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSnippet() {
        String snippet = "";
        if(!description.isEmpty()) {
            snippet += description + "\n";
        }
        if(genderNeutral) {
            snippet += "Gender Neutral Available\n";
        } else {
            snippet += "Gender Neutral Unavailable\n";
        }
        if(purchaseNecessary) {
            snippet += "Purchase Necessary\n";
        } else {
            snippet += "Free Access\n";
        }
        return snippet + "Cleanliness Rating: " + rating;
    }
}
