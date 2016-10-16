package com.example.tripwegoapi.dto;

import com.google.appengine.api.users.User;

import java.util.Date;

/**
 * Created by JG on 04/06/16.
 */
public class TripVersion {

    private String parentTripId = null;

    private long number = 0;

    private boolean isDefault = true;

    // TODO change
    private User userUpdater = null;

    private Date createdAt = null;

    public String getParentTripId() {
        return parentTripId;
    }

    public void setParentTripId(String parentTripId) {
        this.parentTripId = parentTripId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public User getUserUpdater() {
        return userUpdater;
    }

    public void setUserUpdater(User userUpdater) {
        this.userUpdater = userUpdater;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }
}
