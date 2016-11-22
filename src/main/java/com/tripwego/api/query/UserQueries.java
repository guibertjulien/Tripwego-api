package com.tripwego.api.query;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.tripwego.api.Constants;
import com.tripwego.api.response.mapper.UserMapper;
import com.tripwego.domain.MyUser;


/**
 * Created by JG on 04/06/16.
 */
public class UserQueries {

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private UserMapper userMapper = new UserMapper();

    public Optional<MyUser> findByUserId(String userId) {
        Optional<MyUser> user = Optional.absent();
        final Query.Filter byUser = new Query.FilterPredicate(Constants.USER_ID, Query.FilterOperator.EQUAL, userId);
        final Query query = new Query(Constants.KIND_USER).setFilter(byUser);
        final Entity entity = datastore.prepare(query).asSingleEntity();
        if (entity != null) {
            user = Optional.of(userMapper.map(entity));
        }
        return user;
    }
}
