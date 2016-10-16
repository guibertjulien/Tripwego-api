package com.example.tripwegoapi.entity;

import com.example.tripwegoapi.Constants;
import com.example.tripwegoapi.dto.MyUser;
import com.example.tripwegoapi.request.mapper.UserEntityMapper;
import com.example.tripwegoapi.response.mapper.UserMapper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import java.util.logging.Logger;

/**
 * Created by JG on 05/06/16.
 */
public class UserRepository {
    private static final Logger _logger = Logger.getLogger(UserRepository.class.getName());

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private UserEntityMapper userEntityMapper = new UserEntityMapper();
    private UserMapper userMapper = new UserMapper();

    public MyUser create(MyUser myUser) {
        final String userId = myUser.getUserId();
        _logger.info("--> userId" + userId + ".");
        if (userId == null || userId.isEmpty()) {
            return null;
        }
        final Entity entity = new Entity(Constants.KIND_USER, userId);
        userEntityMapper.map(entity, myUser);
        datastore.put(entity);
        return userMapper.map(entity);
    }
}
