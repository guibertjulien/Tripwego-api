package com.tripwego.api.common.mapper;

import com.google.appengine.api.datastore.PostalAddress;

/**
 * Created by JG on 17/11/16.
 */
public class PostalAddressDtoMapper {

    public String map(Object property) {
        String result = "";
        if (property != null) {
            final PostalAddress postalAddress = (PostalAddress) property;
            result = postalAddress.getAddress();
        }
        return result;
    }
}
