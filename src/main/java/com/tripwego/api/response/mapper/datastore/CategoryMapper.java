package com.tripwego.api.response.mapper.datastore;

import com.google.appengine.api.datastore.Category;

/**
 * Created by JG on 17/11/16.
 */
public class CategoryMapper {

    public com.tripwego.dto.datastore.Category map(Category category) {
        final com.tripwego.dto.datastore.Category result = new com.tripwego.dto.datastore.Category();
        result.setCategory(category.getCategory());
        return result;
    }
}
