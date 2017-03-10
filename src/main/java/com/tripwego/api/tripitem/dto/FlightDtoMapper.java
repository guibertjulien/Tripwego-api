package com.tripwego.api.tripitem.dto;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.tripwego.dto.step.Step;
import com.tripwego.dto.tripitem.Flight;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by JG on 25/12/16.
 */
public class FlightDtoMapper {

    private static final Logger LOGGER = Logger.getLogger(FlightDtoMapper.class.getName());

    public Flight map(Entity entity, List<Step> steps) {
        LOGGER.info("--> FlightMapper.map - START");
        final Flight flight = new Flight();
        flight.setId(KeyFactory.keyToString(entity.getKey()));
        flight.getSteps().addAll(steps);
        LOGGER.info("--> FlightMapper.map - END");
        return flight;
    }
}
