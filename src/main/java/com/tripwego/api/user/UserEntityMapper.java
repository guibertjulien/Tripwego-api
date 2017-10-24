package com.tripwego.api.user;

import com.google.appengine.api.datastore.Entity;
import com.tripwego.api.Constants;
import com.tripwego.dto.user.MyUser;

import java.util.Date;
import java.util.logging.Logger;


public class UserEntityMapper {

    private static final Logger LOGGER = Logger.getLogger(UserEntityMapper.class.getName());

    public void map(final Entity entity, MyUser myUser) {
        LOGGER.info("--> UserEntityMapper.map - START");
        entity.setProperty(Constants.USER_ID, myUser.getUserId());
        entity.setProperty(Constants.AUTH_DOMAIN, myUser.getAuthDomain());
        entity.setProperty(Constants.EMAIL, myUser.getEmail());
        entity.setProperty(Constants.FEDERATED_IDENTITY, myUser.getFederatedIdentity());
        entity.setProperty(Constants.NICKNAME, myUser.getNickname());
        entity.setProperty(Constants.PICTURE, myUser.getPicture());
        entity.setProperty(Constants.CREATED_AT, new Date());
        LOGGER.info("--> UserEntityMapper.map - END");
    }
}
