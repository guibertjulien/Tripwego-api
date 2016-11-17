package com.tripwego.api.response.mapper.datastore;

import com.google.appengine.api.datastore.PostalAddress;

/**
 * Created by JG on 17/11/16.
 */
public class PostalAddressMapper {

    public com.tripwego.dto.datastore.PostalAddress map(PostalAddress postalAddress) {
        final com.tripwego.dto.datastore.PostalAddress result = new com.tripwego.dto.datastore.PostalAddress();
        result.setAddress(postalAddress.getAddress());
        return result;
    }
}
