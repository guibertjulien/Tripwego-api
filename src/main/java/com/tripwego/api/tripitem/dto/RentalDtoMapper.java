package com.tripwego.api.tripitem.dto;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.tripwego.dto.step.Step;
import com.tripwego.dto.tripitem.Rental;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by JG on 25/12/16.
 */
public class RentalDtoMapper {

    private static final Logger LOGGER = Logger.getLogger(RentalDtoMapper.class.getName());

    public Rental map(Entity entity, List<Step> steps) {
        LOGGER.info("--> RentalMapper.map - START");
        final Rental rental = new Rental();
        rental.setId(KeyFactory.keyToString(entity.getKey()));
        rental.getSteps().addAll(steps);
        LOGGER.info("--> RentalMapper.map - END");
        return rental;
    }
}
