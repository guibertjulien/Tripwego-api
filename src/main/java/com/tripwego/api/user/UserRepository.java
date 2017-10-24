package com.tripwego.api.user;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.tripwego.api.Constants;
import com.tripwego.dto.user.MyUser;

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
        final Entity entity = new Entity(Constants.KIND_USER, userId);
        userEntityMapper.map(entity, myUser);
        datastore.put(entity);
        return userMapper.map(entity);
    }
}
