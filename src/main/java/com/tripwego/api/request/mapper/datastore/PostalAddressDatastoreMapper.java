package com.tripwego.api.request.mapper.datastore;

import com.google.appengine.api.datastore.PostalAddress;

/**
 * Created by JG on 17/11/16.
 */
public class PostalAddressDatastoreMapper {

    public PostalAddress map(com.tripwego.dto.datastore.PostalAddress postalAddress) {
        return new PostalAddress(postalAddress.getAddress());
    }
}
