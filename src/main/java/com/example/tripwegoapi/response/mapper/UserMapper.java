package com.example.tripwegoapi.response.mapper;

import com.example.tripwegoapi.Constants;
import com.example.tripwegoapi.dto.MyUser;
import com.google.appengine.api.datastore.Entity;


public class UserMapper {

    public MyUser map(Entity entity) {
        MyUser myUser = new MyUser();
        myUser.setUserId(String.valueOf(entity.getProperty(Constants.USER_ID)));
        myUser.setAuthDomain(String.valueOf(entity.getProperty(Constants.AUTH_DOMAIN)));
        myUser.setEmail(String.valueOf(entity.getProperty(Constants.EMAIL)));
        myUser.setFederatedIdentity(String.valueOf(entity.getProperty(Constants.FEDERATED_IDENTITY)));
        myUser.setNickname(String.valueOf(entity.getProperty(Constants.NICKNAME)));
        myUser.setPicture(String.valueOf(entity.getProperty(Constants.PICTURE)));
        return myUser;
    }
}
