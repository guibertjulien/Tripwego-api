package com.example.tripwegoapi.dto;

import com.google.appengine.api.datastore.Link;

/**
 * Created by JG on 18/06/16.
 */
public class TripProvider {

    private String type;

    private Link url;

    private String name;

    private String email;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Link getUrl() {
        return url;
    }

    public void setUrl(Link url) {
        this.url = url;
    }
}
