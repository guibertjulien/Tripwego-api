package com.example.tripwegoapi.dto;

import com.google.appengine.api.datastore.*;

import java.util.Date;

/**
 * Created by JG on 04/06/16.
 */
public class Step {

    private String id = null;

    private String parentId = null;

    private String parentStepId = null;

    private String name = null;

    private String description = null;

    private PostalAddress address = null;

    private GeoPt geoPt = null;

    private long dayIn = 0;

    private long dayOut = 0;

    private Link urlPhoto = null;

    private Link infoLink = null;

    private Category category = null;

    private Rating rating = null;

    private long indexOnRoadMap = 0;

    private Date createdAt = null;

    private Date updatedAt = null;

    private String placeCategory = null;

    private String placeType = null;

    private String periodCategory = null;

    private String periodType = null;

    private String wayType = null;

    private boolean avoidHighways = false;

    private boolean avoidTolls = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

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

    public PostalAddress getAddress() {
        return address;
    }

    public void setAddress(PostalAddress address) {
        this.address = address;
    }

    public GeoPt getGeoPt() {
        return geoPt;
    }

    public void setGeoPt(GeoPt geoPt) {
        this.geoPt = geoPt;
    }

    public Link getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(Link infoLink) {
        this.infoLink = infoLink;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public long getIndexOnRoadMap() {
        return indexOnRoadMap;
    }

    public void setIndexOnRoadMap(long indexOnRoadMap) {
        this.indexOnRoadMap = indexOnRoadMap;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPlaceCategory() {
        return placeCategory;
    }

    public void setPlaceCategory(String placeCategory) {
        this.placeCategory = placeCategory;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public String getWayType() {
        return wayType;
    }

    public void setWayType(String wayType) {
        this.wayType = wayType;
    }

    public boolean isAvoidHighways() {
        return avoidHighways;
    }

    public void setAvoidHighways(boolean avoidHighways) {
        this.avoidHighways = avoidHighways;
    }

    public boolean isAvoidTolls() {
        return avoidTolls;
    }

    public void setAvoidTolls(boolean avoidTolls) {
        this.avoidTolls = avoidTolls;
    }

    public long getDayIn() {
        return dayIn;
    }

    public void setDayIn(long dayIn) {
        this.dayIn = dayIn;
    }

    public long getDayOut() {
        return dayOut;
    }

    public void setDayOut(long dayOut) {
        this.dayOut = dayOut;
    }

    public Link getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(Link urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getParentStepId() {
        return parentStepId;
    }

    public void setParentStepId(String parentStepId) {
        this.parentStepId = parentStepId;
    }

    public String getPeriodCategory() {
        return periodCategory;
    }

    public void setPeriodCategory(String periodCategory) {
        this.periodCategory = periodCategory;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }
}
