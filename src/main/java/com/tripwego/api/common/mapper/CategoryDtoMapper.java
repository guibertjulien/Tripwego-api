package com.tripwego.api.common.mapper;


import com.google.appengine.api.datastore.Category;

/**
 * Created by JG on 17/11/16.
 */
public class CategoryDtoMapper {

    public String map(Object property) {
        String result = "";
        if (property != null) {
            final Category category = (Category) property;
            result = category.getCategory();
        }
        return result;
    }
}
