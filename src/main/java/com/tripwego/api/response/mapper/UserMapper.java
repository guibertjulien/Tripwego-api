package com.tripwego.api.response.mapper;

import com.google.appengine.api.datastore.Entity;
import com.tripwego.api.Constants;
import com.tripwego.domain.MyUser;

import java.util.logging.Logger;


public class UserMapper {

    private static final Logger LOGGER = Logger.getLogger(UserMapper.class.getName());

    public MyUser map(Entity entity) {
        LOGGER.info("--> UserMapper.map - START");
        final MyUser myUser = new MyUser();
        myUser.setUserId(String.valueOf(entity.getProperty(Constants.USER_ID)));
        myUser.setAuthDomain(String.valueOf(entity.getProperty(Constants.AUTH_DOMAIN)));
        myUser.setEmail(String.valueOf(entity.getProperty(Constants.EMAIL)));
        myUser.setFederatedIdentity(String.valueOf(entity.getProperty(Constants.FEDERATED_IDENTITY)));
        myUser.setNickname(String.valueOf(entity.getProperty(Constants.NICKNAME)));
        myUser.setPicture(String.valueOf(entity.getProperty(Constants.PICTURE)));
        LOGGER.info("--> UserMapper.map - END");
        return myUser;
    }
}
