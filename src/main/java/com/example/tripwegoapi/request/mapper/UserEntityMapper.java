package com.example.tripwegoapi.request.mapper;

import com.example.tripwegoapi.Constants;
import com.example.tripwegoapi.dto.MyUser;
import com.google.appengine.api.datastore.Entity;


public class UserEntityMapper {

    public void map(final Entity entity, MyUser myUser) {
        entity.setProperty(Constants.USER_ID, myUser.getUserId());
        entity.setProperty(Constants.AUTH_DOMAIN, myUser.getAuthDomain());
        entity.setProperty(Constants.EMAIL, myUser.getEmail());
        entity.setProperty(Constants.FEDERATED_IDENTITY, myUser.getFederatedIdentity());
        entity.setProperty(Constants.NICKNAME, myUser.getNickname());
        entity.setProperty(Constants.PICTURE, myUser.getPicture());
    }
}
