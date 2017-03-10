package com.tripwego.api.placeresult.addresscomponent;

import com.google.appengine.api.datastore.Entity;
import com.tripwego.dto.placeresult.AddressComponentDto;

import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;

/**
 * Created by JG on 19/02/17.
 */
public class AddressComponentEntityMapper {

    private static final Logger LOGGER = Logger.getLogger(AddressComponentEntityMapper.class.getName());

    public Entity map(AddressComponentDto source, Entity parent) {
        LOGGER.info("--> AddressComponentEntityMapper.map - START");
        final Entity entity = new Entity(KIND_ADDRESS_COMPONENT, parent.getKey());
        entity.setProperty(SHORT_NAME, source.getShort_name());
        entity.setProperty(LONG_NAME, source.getLong_name());
        entity.setProperty(TYPES, source.getTypes());
        LOGGER.info("--> AddressComponentEntityMapper.map - END");
        return entity;
    }
}
