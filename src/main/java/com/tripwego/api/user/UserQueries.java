package com.tripwego.api.user;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.Constants;
import com.tripwego.dto.common.Counter;
import com.tripwego.dto.user.MyUser;

import java.util.Optional;

import static com.tripwego.api.Constants.CREATED_AT;
import static com.tripwego.api.Constants.KIND_USER;


/**
 * Created by JG on 04/06/16.
 */
public class UserQueries {

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private UserMapper userMapper = new UserMapper();

    public Optional<MyUser> findByUserId(String userId) {
        Optional<MyUser> user = Optional.empty();
        final Query.Filter byUser = new Query.FilterPredicate(Constants.USER_ID, Query.FilterOperator.EQUAL, userId);
        final Query query = new Query(Constants.KIND_USER).setFilter(byUser);
        final Entity entity = datastore.prepare(query).asSingleEntity();
        if (entity != null) {
            user = Optional.of(userMapper.map(entity));
        }
        return user;
    }

    public Counter count() {
        final Counter counter = new Counter();
        final Query query = new Query(KIND_USER).addSort(CREATED_AT, Query.SortDirection.DESCENDING).setKeysOnly();
        counter.setCount(Long.valueOf(datastore.prepare(query).asList(FetchOptions.Builder.withDefaults()).size()));
        return counter;
    }
}
