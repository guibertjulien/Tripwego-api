package com.tripwego.api.response.mapper.datastore;

import com.google.appengine.api.datastore.PostalAddress;

/**
 * Created by JG on 17/11/16.
 */
public class PostalAddressMapper {

    public String map(Object property) {
        String result = "";
        if (property != null) {
            final PostalAddress postalAddress = (PostalAddress) property;
            result = postalAddress.getAddress();
        }
        return result;
    }
}
