package com.example.tripwegoapi.dto;

import com.example.tripwegoapi.dto.tripitem.Accommodation;
import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.Rating;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JG on 04/06/16.
 */
public class Trip {
    private String id = null;

    private String parentTripId = null;

    private String name = null;

    private String description = null;

    private String countryCode = null;

    private String countryName = null;

    private Date startDate = null;

    private Date endDate = null;

    private long duration = 0;

    private Date createdAt = null;

    private Date updatedAt = null;

    private Link urlStaticMap = null;

    private Link urlPhoto = null;

    private Category category = null;

    private Rating rating = null;

    private List<Step> steps = new ArrayList<Step>();
    private final List<Accommodation> accommodations = new ArrayList<>();

    private MyUser user = null;

    private List<Traveler> travelers = new ArrayList<Traveler>();

    private List<Tag> manualTags = new ArrayList<Tag>();

    private List<Tag> autoTags = new ArrayList<Tag>();

    private TripVersion tripVersion = null;

    private TripProvider tripProvider = null;

    private boolean privateTrip = false;

    private boolean published = true;

    private boolean anonymous = false;

    private boolean cancelled = false;

    private Link urlSite;

    private Link urlPhotoAlbum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public List<Traveler> getTravelers() {
        return travelers;
    }

    public void setTravelers(List<Traveler> travelers) {
        this.travelers = travelers;
    }

    public List<Tag> getManualTags() {
        return manualTags;
    }

    public void setManualTags(List<Tag> manualTags) {
        this.manualTags = manualTags;
    }

    public List<Tag> getAutoTags() {
        return autoTags;
    }

    public void setAutoTags(List<Tag> autoTags) {
        this.autoTags = autoTags;
    }

    public TripVersion getTripVersion() {
        return tripVersion;
    }

    public void setTripVersion(TripVersion tripVersion) {
        this.tripVersion = tripVersion;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isPrivateTrip() {
        return privateTrip;
    }

    public void setPrivateTrip(boolean privateTrip) {
        this.privateTrip = privateTrip;
    }

    public TripProvider getTripProvider() {
        return tripProvider;
    }

    public void setTripProvider(TripProvider tripProvider) {
        this.tripProvider = tripProvider;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Link getUrlSite() {
        return urlSite;
    }

    public void setUrlSite(Link urlSite) {
        this.urlSite = urlSite;
    }

    public Link getUrlPhotoAlbum() {
        return urlPhotoAlbum;
    }

    public void setUrlPhotoAlbum(Link urlPhotoAlbum) {
        this.urlPhotoAlbum = urlPhotoAlbum;
    }

    public Link getUrlStaticMap() {
        return urlStaticMap;
    }

    public void setUrlStaticMap(Link urlStaticMap) {
        this.urlStaticMap = urlStaticMap;
    }

    public Link getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(Link urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getParentTripId() {
        return parentTripId;
    }

    public void setParentTripId(String parentTripId) {
        this.parentTripId = parentTripId;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public List<Accommodation> getAccommodations() {
        return accommodations;
    }
}