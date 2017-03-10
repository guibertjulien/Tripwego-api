package com.tripwego.api.placeresult.addresscomponent;

import com.google.appengine.api.datastore.Entity;
import com.tripwego.dto.placeresult.AddressComponentDto;

import java.util.ArrayList;
import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;

/**
 * Created by JG on 19/02/17.
 */
public class AddressComponentDtoMapper {

    private static final Logger LOGGER = Logger.getLogger(AddressComponentDtoMapper.class.getName());

    public AddressComponentDto map(Entity entity) {
        LOGGER.info("--> AddressComponentDtoMapper.map - START");
        final AddressComponentDto addressComponentDto = new AddressComponentDto();
        addressComponentDto.setShort_name(String.valueOf(entity.getProperty(SHORT_NAME)));
        addressComponentDto.setLong_name(String.valueOf(entity.getProperty(LONG_NAME)));
        addressComponentDto.getTypes().addAll((ArrayList<String>) entity.getProperty(TYPES));
        LOGGER.info("--> AddressComponentDtoMapper.map - START");
        return addressComponentDto;
    }
}
