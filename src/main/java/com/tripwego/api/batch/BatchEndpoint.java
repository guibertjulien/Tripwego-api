package com.tripwego.api.batch;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.tripwego.api.trip.TripRepository;

import static com.tripwego.api.ConfigurationConstants.NB_DAYS_BEFORE_REMOVE;
import static com.tripwego.api.ConfigurationConstants.NB_DAYS_BEFORE_REMOVE_USER_UNKNOWN;

/**
 * Created by JG on 19/02/17.
 */
@Api(name = "batchendpoint", namespace = @ApiNamespace(ownerDomain = "tripwego.com", ownerName = "tripwego.com", packagePath = "endpoints"))
public class BatchEndpoint {

    private TripRepository tripRepository = new TripRepository();

    @ApiMethod(name = "delete_trips_cancelled", path = "delete_trips_cancelled", httpMethod = ApiMethod.HttpMethod.GET, apiKeyRequired = AnnotationBoolean.TRUE)
    public void delete_trips_cancelled() {
        tripRepository.deleteTripsCancelled(NB_DAYS_BEFORE_REMOVE);
    }

    @ApiMethod(name = "clean_trips", path = "clean_trips", httpMethod = ApiMethod.HttpMethod.GET, apiKeyRequired = AnnotationBoolean.TRUE)
    public void clean_trips() {
        tripRepository.deleteTripsNotSaved(NB_DAYS_BEFORE_REMOVE_USER_UNKNOWN);
        tripRepository.deleteTripsWithUserUnknown(NB_DAYS_BEFORE_REMOVE_USER_UNKNOWN);
    }

    @ApiMethod(name = "update_continent_in_trip", path = "update_continent_in_trip", httpMethod = ApiMethod.HttpMethod.GET, apiKeyRequired = AnnotationBoolean.TRUE)
    public void update_continent_in_trip() {
        //tripRepository.updateContinent();
        tripRepository.updateSeo();
    }
}