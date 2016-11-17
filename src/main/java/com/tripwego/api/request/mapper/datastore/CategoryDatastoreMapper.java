package com.tripwego.api.request.mapper.datastore;

import com.google.appengine.api.datastore.Category;

/**
 * Created by JG on 17/11/16.
 */
public class CategoryDatastoreMapper {

    public Category map(com.tripwego.dto.datastore.Category category) {
        return new Category(category.getCategory());
    }
}
