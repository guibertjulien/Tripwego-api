package com.tripwego.api.trip;

import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.tripwego.dto.common.Counter;
import com.tripwego.dto.statistics.Statistics;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.trip.TripSearchCriteria;

import java.util.List;
import java.util.logging.Logger;

/**
 * The Echo API which Endpoints will be exposing.
 */
// [START echo_api_annotation]
@Api(
        name = "tripendpoint",
        version = "v1",
        namespace =
        @ApiNamespace(
                ownerDomain = "tripwego.com",
                ownerName = "tripwego.com",
                packagePath = ""
        ),
        // [START_EXCLUDE]
        issuers = {
                @ApiIssuer(
                        name = "firebase",
                        issuer = "https://securetoken.google.com/tripwego-api",
                        jwksUri = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com")
        }
        // [END_EXCLUDE]
)
// [END echo_api_annotation]
public class TripEndpoint {

    private static final Logger _logger = Logger.getLogger(TripEndpoint.class.getName());

    private TripRepository tripRepository = new TripRepository();
    private TripQueries queries = new TripQueries();

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

    @ApiMethod(name = "get_trip", path = "get_trip", httpMethod = ApiMethod.HttpMethod.GET, apiKeyRequired = AnnotationBoolean.TRUE)
    public Trip retrieveTrip(@Named("id") String id) {
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
    public CollectionResponse<Trip> findTripsByUser(@Named("userId") String userId, @Nullable @Named("cursor") String cursorString, @Nullable @Named("limit") Integer limit) {
        final List<Trip> trips = queries.findTripsByUser(userId);
        return CollectionResponse.<Trip>builder().setItems(trips).setNextPageToken(cursorString).build();
    }

    @SuppressWarnings({"unchecked", "unused"})
    @ApiMethod(name = "findAllTripsAlive", path = "findAllTripsAlive", httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Trip> findAllTripsAlive(@Nullable @Named("cursor") String cursorString, @Nullable @Named("offset") Integer offset, @Nullable @Named("limit") Integer limit) {
        final List<Trip> trips = queries.findAllTripsAlive(offset, limit);
        return CollectionResponse.<Trip>builder().setItems(trips).setNextPageToken(cursorString).build();
    }

    @SuppressWarnings({"unchecked", "unused"})
    @ApiMethod(name = "findAllTripsForSeo", path = "findAllTripsForSeo", httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Trip> findAllTripsForSeo(@Nullable @Named("cursor") String cursorString, @Nullable @Named("offset") Integer offset, @Nullable @Named("limit") Integer limit) {
        final List<Trip> trips = queries.findAllTripsForSeo();
        return CollectionResponse.<Trip>builder().setItems(trips).build();
    }

    @SuppressWarnings({"unchecked", "unused"})
    @ApiMethod(name = "findAllTripsForAdmin", path = "findAllTripsForAdmin", httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Trip> findAllTripsForAdmin(@Nullable @Named("cursor") String cursorString, @Nullable @Named("offset") Integer offset, @Nullable @Named("limit") Integer limit, @Nullable @Named("categoryNames") List<String> categoryNames) {
        final List<Trip> trips = queries.findAllTripsForAdmin(offset, limit, categoryNames);
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
    @ApiMethod(name = "updateVisibility", path = "updateVisibility", httpMethod = ApiMethod.HttpMethod.PUT)
    public void updateVisibility(Trip trip) {
        try {
            tripRepository.updateVisibility(trip);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "updateAdminStatus", path = "updateAdminStatus", httpMethod = ApiMethod.HttpMethod.PUT)
    public void updateAdminStatus(Trip trip) {
        try {
            tripRepository.updateAdminStatus(trip);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "updateAdminCertificate", path = "updateAdminCertificate", httpMethod = ApiMethod.HttpMethod.PUT)
    public void updateAdminCertificate(Trip trip) {
        try {
            tripRepository.updateAdminCertificate(trip);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "deleteTripsCancelled", path = "deleteTripsCancelled", httpMethod = ApiMethod.HttpMethod.GET)
    public void deleteTripsCancelled(@Named("userId") String userId) {
        tripRepository.deleteTripsCancelledFromUser(userId);
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "ping", path = "ping", httpMethod = ApiMethod.HttpMethod.GET)
    public void ping() {
        _logger.info("ping");
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "testQuery", path = "testQuery", httpMethod = ApiMethod.HttpMethod.GET)
    public void testQuery() {
        tripRepository.testQuery();
    }

    @SuppressWarnings("unchecked")
    @ApiMethod(name = "statistics", path = "statistics", httpMethod = ApiMethod.HttpMethod.GET)
    public Statistics statistics() {
        return queries.statistics();
    }

    @ApiMethod(name = "find", path = "find", httpMethod = ApiMethod.HttpMethod.POST)
    public CollectionResponse<Trip> find(TripSearchCriteria criteria) {
        final List<Trip> trips = queries.find(criteria);
        return CollectionResponse.<Trip>builder().setItems(trips).build();
    }

    @ApiMethod(name = "count", path = "count", httpMethod = ApiMethod.HttpMethod.GET)
    public Counter count() {
        final Counter counter = queries.count();
        return counter;
    }

}
