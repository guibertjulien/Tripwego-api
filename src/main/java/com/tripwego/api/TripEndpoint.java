package com.tripwego.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.tripwego.api.entity.TripRepository;
import com.tripwego.api.query.TripQueries;
import com.tripwego.domain.Trip;

import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;


/**
 * Defines v1 of a helloworld API, which provides simple "greeting" methods.
 */
/*
@Api(name = "helloworld",
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE}
)
*/
@Api(name = "tripendpoint", namespace = @ApiNamespace(ownerDomain = "tripwego.com", ownerName = "tripwego.com", packagePath = "endpoints"))
public class TripEndpoint {

    private static final Logger _logger = Logger.getLogger(TripEndpoint.class.getName());

    private TripRepository tripRepository = new TripRepository();
    private TripQueries tripQueries = new TripQueries();

    /**
     * This inserts a new entity into App Engine datastore. If the entity
     * already exists in the datastore, an exception is thrown. It uses HTTP
     * POST method.
     *
     * @param trip the entity to be inserted.
     * @return The inserted entity.
     */
    @ApiMethod(name = "insertTrip")
    public Trip insertTrip(Trip trip) {
        return tripRepository.create(trip);
    }

    /**
     * This method gets the entity having primary key id. It uses HTTP GET
     * method.
     *
     * @param id the primary key of the java bean.
     * @return The entity with primary key id.
     */
    @ApiMethod(name = "getTrip")
    public Trip getTrip(@Named("id") String id) {
        return tripRepository.retrieveEager(id);
    }

    /**
     * This method is used for updating an existing entity. If the entity does
     * not exist in the datastore, an exception is thrown. It uses HTTP PUT
     * method.
     *
     * @param trip the entity to be updated.
     * @return The updated entity.
     */
    @ApiMethod(name = "updateTrip")
    public Trip updateTrip(Trip trip) {
        return tripRepository.update(trip);
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "findTripsByUser", path = "findTripsByUser", httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Trip> findTripsByUser(@Named("userId") String userId) {
        final List<Trip> trips = tripQueries.findTripsByUser(userId);
        return CollectionResponse.<Trip>builder().setItems(trips).build();
    }

    @SuppressWarnings({"unchecked", "unused"})
    @ApiMethod(name = "findAllTrips", path = "findAllTrips", httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Trip> findAllTrips(@Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {
        // TODO cursor
        final List<Trip> trips = tripQueries.findAllTrips();
        return CollectionResponse.<Trip>builder().setItems(trips).setNextPageToken(cursorString).build();
    }

    @ApiMethod(name = "copyTrip", path = "copyTrip", httpMethod = ApiMethod.HttpMethod.POST)
    public Trip copyTrip(Trip trip) {
        return tripRepository.copy(trip);
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "deleteOrRestoreTrip", path = "deleteOrRestoreTrip", httpMethod = ApiMethod.HttpMethod.PUT)
    public void deleteOrRestoreTrip(Trip trip) {
        try {
            tripRepository.deleteOrRestore(trip);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "publishOrPrivateTrip", path = "publishOrPrivateTrip", httpMethod = ApiMethod.HttpMethod.PUT)
    public void publishOrPrivateTrip(Trip trip) {
        try {
            tripRepository.publishOrPrivate(trip);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

}
