package com.tripwego.api.entity;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.tripwego.api.Constants;
import com.tripwego.api.request.mapper.UserEntityMapper;
import com.tripwego.api.response.mapper.UserMapper;
import com.tripwego.domain.MyUser;

import java.util.logging.Logger;

/**
 * Created by JG on 05/06/16.
 */
public class UserRepository {
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private UserEntityMapper userEntityMapper = new UserEntityMapper();
    private UserMapper userMapper = new UserMapper();

    public MyUser create(MyUser myUser) {
        final String userId = myUser.getUserId();
        LOGGER.info("--> userId" + userId + ".");
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        final Entity entity = new Entity(Constants.KIND_USER, userId);
        userEntityMapper.map(entity, myUser);
        datastore.put(entity);
        return userMapper.map(entity);
    }
}
