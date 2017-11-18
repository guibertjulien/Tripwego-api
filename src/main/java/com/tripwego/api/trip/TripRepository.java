package com.tripwego.api.trip;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.Days;
import com.tripwego.api.common.AbstractRepository;
import com.tripwego.api.placeresult.PlaceResultRepository;
import com.tripwego.api.trip.status.TripAdminStatus;
import com.tripwego.api.tripitem.TripItemQueries;
import com.tripwego.api.tripitem.dto.*;
import com.tripwego.api.tripitem.repository.*;
import com.tripwego.api.user.UserQueries;
import com.tripwego.api.user.UserRepository;
import com.tripwego.dto.step.Step;
import com.tripwego.dto.trip.Trip;
import com.tripwego.dto.trip.TripProvider;
import com.tripwego.dto.tripitem.*;
import com.tripwego.dto.user.MyUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;
import static com.tripwego.api.trip.status.TripAdminStatus.*;
import static com.tripwego.api.trip.status.TripCertificate.NONE;
import static com.tripwego.api.trip.status.TripPlanStatus.TO_PLAN;
import static com.tripwego.api.trip.status.TripUserStatus.PUBLISHED;
import static com.tripwego.api.trip.status.TripVisibility.PUBLIC;

public class TripRepository extends AbstractRepository<Trip> {

    private static final Logger LOGGER = Logger.getLogger(TripRepository.class.getName());
    private static final int NUMBER_VERSION_DEFAULT = 1;

    private TripEntityMapper tripEntityMapper = new TripEntityMapper();
    private ActivityDtoMapper activityDtoMapper = new ActivityDtoMapper();
    private AccommodationDtoMapper accommodationDtoMapper = new AccommodationDtoMapper();
    private FlightDtoMapper flightDtoMapper = new FlightDtoMapper();
    private RailDtoMapper railDtoMapper = new RailDtoMapper();
    private RentalDtoMapper rentalDtoMapper = new RentalDtoMapper();
    private TripDtoMapper tripDtoMapper = new TripDtoMapperFactory().create();
    private ActivityRepository activityRepository = new ActivityRepository();
    private AccommodationRepository accommodationRepository = new AccommodationRepository();
    private FlightRepository flightRepository = new FlightRepository();
    private RailRepository railRepository = new RailRepository();
    private RentalRepository rentalRepository = new RentalRepository();
    private TripQueries tripQueries = new TripQueries();
    private TripItemQueries tripItemQueries = new TripItemQueries();
    private UserRepository userRepository = new UserRepository();
    private UserQueries userQueries = new UserQueries();
    private PlaceResultRepository placeResultRepository = new PlaceResultRepository();

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public Trip create(Trip trip) {
        Optional<MyUser> user = Optional.absent();
        LOGGER.info("--> user : " + trip.getUser().getUserId());
        if (trip.getUser() != null && !Strings.isNullOrEmpty(trip.getUser().getUserId())) {
            user = Optional.fromNullable(userRepository.create(trip.getUser()));
        }
        final Entity entity = new Entity(KIND_TRIP);
        tripEntityMapper.map(entity, trip, user);
        entity.setProperty(IS_DEFAULT, true);
        entity.setProperty(CREATED_AT, new Date());
        entity.setProperty(UPDATED_AT, new Date());
        entity.setProperty(TAGS, trip.getTags());
        entity.setProperty(IS_STORE_IN_DOCUMENT, false);
        entity.setProperty(IS_CANCELLED, false);
        entity.setProperty(IS_TRIP_AUTOMATIC, trip.isTripAutomatic());
        entity.setProperty(TRIP_ADMIN_STATUS, CREATED.name());
        entity.setProperty(TRIP_USER_STATUS, PUBLISHED.name());
        entity.setProperty(TRIP_PLAN_STATUS, TO_PLAN.name());
        entity.setProperty(TRIP_VISIBILITY, PUBLIC.name());
        entity.setProperty(TRIP_CERTIFICATE, NONE.name());
        updateVersion(trip, entity, NUMBER_VERSION_DEFAULT);
        final Entity placeResultEntity = placeResultRepository.create(trip.getPlaceResultDto());
        entity.setProperty(PLACE_RESULT_ID, KeyFactory.keyToString(placeResultEntity.getKey()));
        datastore.put(entity);
        // no step
        // no user
        // TODO enhance
        return tripDtoMapper.map(entity, user);
    }

    public Trip update(Trip trip) {
        Trip result = null;
        try {
            final Entity parent = datastore.get(KeyFactory.stringToKey(trip.getId()));
            Entity entity;
            final long number = trip.getTripVersion().getNumber();
            final MyUser user = userRepository.create(trip.getUser());
            // insert trip with parent
            if (number > NUMBER_VERSION_DEFAULT) {
                LOGGER.info("--> new trip");
                entity = new Entity(KIND_TRIP, parent.getKey());
                tripEntityMapper.map(entity, trip, Optional.of(user));
                entity.setProperty(IS_DEFAULT, true);
                entity.setProperty(CREATED_AT, new Date());
                updateVersion(trip, entity, number);
                // TODO make other trip default
                // create steps
                activityRepository.createAll(trip.getActivities(), entity);
                accommodationRepository.createAll(trip.getAccommodations(), entity);
                flightRepository.createAll(trip.getFlights(), entity);
                railRepository.createAll(trip.getRails(), entity);
                rentalRepository.createAll(trip.getRentals(), entity);
                final Entity placeResultEntity = placeResultRepository.create(trip.getPlaceResultDto());
                entity.setProperty(PLACE_RESULT_ID, KeyFactory.keyToString(placeResultEntity.getKey()));
            }
            // update trip
            else {
                LOGGER.info("--> update trip");
                entity = parent;
                tripEntityMapper.map(parent, trip, Optional.of(user));
                // !!! first order for addressComponentRepository.updateCollection !!!
                updateChild(trip, entity);
            }
            if (CREATED.name().equals(trip.getTripAdminStatus())) {
                entity.setProperty(TRIP_ADMIN_STATUS, UPDATED.name());
            }
            entity.setProperty(UPDATED_AT, new Date());
            if (trip.getTripProvider() != null) {
                updateProvider(trip, entity);
            }
            datastore.put(entity);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }


    public Trip copy(Trip trip) {
        LOGGER.info("--> copy - START : " + trip.getParentTripId());
        final Optional<MyUser> user = Optional.fromNullable(userRepository.create(trip.getUser()));
        final Entity entity = new Entity(KIND_TRIP);
        tripEntityMapper.map(entity, trip, user);
        entity.setProperty(IS_DEFAULT, true);
        entity.setProperty(TRIP_ADMIN_STATUS, FORKED.name());
        entity.setProperty(IS_CANCELLED, false);
        entity.setProperty(CREATED_AT, new Date());
        entity.setProperty(UPDATED_AT, new Date());
        updateVersion(trip, entity, NUMBER_VERSION_DEFAULT);
        //
        entity.setProperty(IS_COPY, true);
        final Entity placeResultEntity = placeResultRepository.create(trip.getPlaceResultDto());
        entity.setProperty(PLACE_RESULT_ID, KeyFactory.keyToString(placeResultEntity.getKey()));
        //
        datastore.put(entity);
        updateChild(trip, entity);
        LOGGER.info("--> copy - END");
        return tripDtoMapper.map(entity, user);
    }

    /**
     * lazy
     */
    public Trip retrieveLazy(String id) {
        Trip trip = null;
        try {
            Entity entity = datastore.get(KeyFactory.stringToKey(id));
            trip = tripDtoMapper.map(entity, Optional.<MyUser>absent());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return trip;
    }

    /**
     * eager
     */
    public Trip retrieveEager(String id) {
        LOGGER.info("--> retrieve - START");
        Trip trip = null;
        try {
            final Entity tripEntity = datastore.get(KeyFactory.stringToKey(id));
            Optional<MyUser> user = userQueries.findByUserId(String.valueOf(tripEntity.getProperty(USER_ID)));
            trip = tripDtoMapper.map(tripEntity, user);
            LOGGER.info("--> items");
            // activities
            for (Entity entity : tripItemQueries.findTripItem(tripEntity, KIND_ACTIVITY)) {
                final List<Step> steps = tripQueries.findStepsByTripItem(entity);
                final Activity activity = activityDtoMapper.map(entity, steps);
                trip.getActivities().add(activity);
            }
            LOGGER.info("--> activities - OK");
            // accommodations
            for (Entity entity : tripItemQueries.findTripItem(tripEntity, KIND_ACCOMMODATION)) {
                final List<Step> steps = tripQueries.findStepsByTripItem(entity);
                final Accommodation accommodation = accommodationDtoMapper.map(entity, steps);
                trip.getAccommodations().add(accommodation);
            }
            LOGGER.info("--> accommodations - OK");
            // flights
            for (Entity entity : tripItemQueries.findTripItem(tripEntity, KIND_FLIGHT)) {
                final List<Step> steps = tripQueries.findStepsByTripItem(entity);
                final Flight flight = flightDtoMapper.map(entity, steps);
                trip.getFlights().add(flight);
            }
            LOGGER.info("--> flights - OK");
            // rails
            for (Entity entity : tripItemQueries.findTripItem(tripEntity, KIND_RAIL)) {
                final List<Step> steps = tripQueries.findStepsByTripItem(entity);
                final Rail rail = railDtoMapper.map(entity, steps);
                trip.getRails().add(rail);
            }
            LOGGER.info("--> rails - OK");
            // rentals
            for (Entity entity : tripItemQueries.findTripItem(tripEntity, KIND_RENTAL)) {
                final List<Step> steps = tripQueries.findStepsByTripItem(entity);
                final Rental rental = rentalDtoMapper.map(entity, steps);
                trip.getRentals().add(rental);
            }
            LOGGER.info("--> rentals - OK");
            trip.setPlaceResultDto(placeResultRepository.retrieve(trip.getPlaceResultId()));
            LOGGER.info("--> placeResult - OK");
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        LOGGER.info("--> retrieve - END");
        return trip;
    }

    public void deleteOrRestore(Trip trip) throws EntityNotFoundException {
        final Entity entity = datastore.get(KeyFactory.stringToKey(trip.getId()));
        entity.setProperty(IS_CANCELLED, trip.isCancelled());
        if (trip.isCancelled()) {
            entity.setProperty(CANCELLATION_DATE, new Date());
        } else {
            entity.setProperty(CANCELLATION_DATE, null);
        }
        datastore.put(entity);
    }

    public void updateAdminStatus(Trip trip) throws EntityNotFoundException {
        final Entity entity = datastore.get(KeyFactory.stringToKey(trip.getId()));
        entity.setProperty(TRIP_ADMIN_STATUS, trip.getTripAdminStatus());
        datastore.put(entity);
    }

    public void updateAdminCertificate(Trip trip) throws EntityNotFoundException {
        final Entity entity = datastore.get(KeyFactory.stringToKey(trip.getId()));
        entity.setProperty(TRIP_CERTIFICATE, trip.getTripCertificate());
        datastore.put(entity);
    }

    public void updateVisibility(Trip trip) throws EntityNotFoundException {
        final Entity entity = datastore.get(KeyFactory.stringToKey(trip.getId()));
        entity.setProperty(TRIP_VISIBILITY, trip.getTripVisibility());
        datastore.put(entity);
    }

    private void makeTripDefault(String id) {
        final Transaction txn = datastore.beginTransaction();
        try {
            // BEGIN TRANSACTION
            final Entity entity = datastore.get(KeyFactory.stringToKey(id));
            entity.setProperty(IS_DEFAULT, true);
            datastore.put(entity);
            final List<Entity> tripsToUpdate = new ArrayList<>();
            final Entity parent = datastore.get(entity.getParent());
            final Query query = new Query(KIND_STEP).setAncestor(parent.getKey());
            final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
            parent.setProperty(IS_DEFAULT, false);
            tripsToUpdate.add(parent);
            for (Entity child : entities) {
                child.setProperty(IS_DEFAULT, false);
                tripsToUpdate.add(child);
            }
            datastore.put(tripsToUpdate);
            // END TRANSACTION
            txn.commit();
        } catch (Exception e) {
            LOGGER.warning("" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private void updateVersion(Trip trip, Entity entity, long number) {
        LOGGER.info("--> updateVersion - START : " + number);
        // no parent id
        // version = 1
        final EmbeddedEntity embeddedVersion = new EmbeddedEntity();
        embeddedVersion.setProperty(VERSION_NUMBER, number);
        embeddedVersion.setProperty(VERSION_USER, trip.getUser().getUserId());
        embeddedVersion.setProperty(VERSION_CREATED_AT, new Date());
        embeddedVersion.setProperty(VERSION_UPDATED_AT, new Date());
        entity.setProperty(EMBEDDED_VERSION, embeddedVersion);
        LOGGER.info("--> updateVersion - END");
    }

    private void updateProvider(Trip trip, Entity entity) {
        LOGGER.info("--> updateProvider - START");
        final EmbeddedEntity embeddedProvider = new EmbeddedEntity();
        TripProvider tripProvider = trip.getTripProvider();
        embeddedProvider.setProperty(NAME, tripProvider.getName());
        embeddedProvider.setProperty(TYPE, tripProvider.getType());
        embeddedProvider.setProperty(URL_SITE, tripProvider.getUrl());
        embeddedProvider.setProperty(EMAIL, tripProvider.getEmail());
        entity.setProperty(EMBEDDED_PROVIDER, embeddedProvider);
        LOGGER.info("--> updateProvider - END");
    }

    public void deleteTripsWithUserUnknown(int delay) {
        LOGGER.info("--> deleteTripsWithUserUnknown - START");
        final Date today = Calendar.getInstance().getTime();
        final List<Entity> tripWithUserUnknown = tripQueries.findTripEntitiesWithUserUnknown();
        final List<Entity> tripsToDelete = new ArrayList<>();
        for (Entity entity : tripWithUserUnknown) {
            final Date createdDate = (Date) entity.getProperty(CREATED_AT);
            final int days = Days.daysBetween(new DateTime(createdDate), new DateTime(today)).getDays();
            if (days >= delay) {
                tripsToDelete.add(entity);
            }
        }
        if (tripsToDelete.size() > 0) {
            deleteTripEntities(tripsToDelete);
        }
        LOGGER.info("--> deleteTripsWithUserUnknown - END");
    }

    public void deleteTripsCancelled(int delay) {
        LOGGER.info("--> deleteTripsCancelled - START");
        final Date today = Calendar.getInstance().getTime();
        final List<Entity> tripsCancelled = tripQueries.findTripEntitiesCancelled();
        final List<Entity> tripsToDelete = new ArrayList<>();
        for (Entity entity : tripsCancelled) {
            final Date cancellationDate = (Date) entity.getProperty(CANCELLATION_DATE);
            final String tripAdminStatus = String.valueOf(entity.getProperty(TRIP_ADMIN_STATUS));
            final int days = Days.daysBetween(new DateTime(cancellationDate), new DateTime(today)).getDays();
            if (TripAdminStatus.valueOf(tripAdminStatus) == CANCELLED || days >= delay) {
                tripsToDelete.add(entity);
            }
        }
        if (tripsToDelete.size() > 0) {
            deleteTripEntities(tripsToDelete);
        }
        LOGGER.info("--> deleteTripsCancelled - END");
    }

    public void deleteTripsCancelledFromUser(String userId) {
        deleteTripEntities(tripQueries.findTripEntitiesCancelledByUser(userId));
    }

    private void deleteTripEntities(List<Entity> tripsToDelete) {
        final List<Key> keysToKill = new ArrayList<>();
        keysToKill.addAll(extractKeys(tripsToDelete));
        for (Entity entity : tripsToDelete) {
            deleteChild(entity);
        }
        if (keysToKill.size() > 0) {
            datastore.delete(keysToKill);
            // after trip deletion
            placeResultRepository.deletePlaceAssociated(tripsToDelete, KIND_TRIP, PLACE_RESULT_ID);
        }
    }

    private void deleteChild(Entity parent) {
        activityRepository.deleteAll(parent);
        accommodationRepository.removeAll(parent);
        flightRepository.removeAll(parent);
        railRepository.removeAll(parent);
        rentalRepository.removeAll(parent);
    }

    private void updateChild(Trip trip, Entity entity) {
        // update steps
        activityRepository.updateAll(trip.getActivities(), entity);
        accommodationRepository.updateAll(trip.getAccommodations(), entity);
        flightRepository.updateAll(trip.getFlights(), entity);
        railRepository.updateAll(trip.getRails(), entity);
        rentalRepository.updateAll(trip.getRentals(), entity);
    }

    @Override
    protected void createCollection(Entity parent, List<Trip> collection) {

    }

    @Override
    public Entity entityToCreate(Entity parent, Trip trip) {
        return null;
    }

    // TODO remove
    public void testQuery() {
        LOGGER.info("--> testQuery - START");
        LOGGER.info("=====================> query 1");
        final Query query = new Query(KIND_TRIP).addProjection(new PropertyProjection(PLACE_RESULT_ID, String.class));
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        LOGGER.info("--> " + entities.size());
        for (Entity entity : entities) {
            LOGGER.info("key : " + entity.getKind() + " / " + entity.getKey());
            LOGGER.info("place id : " + String.valueOf(entity.getProperty(PLACE_RESULT_ID)));
        }

        LOGGER.info("=====================> query 2");
        final Query query2 = new Query(KIND_TRIP);
        final List<Entity> entities2 = datastore.prepare(query2).asList(FetchOptions.Builder.withDefaults());
        LOGGER.info("--> " + entities.size());
        for (Entity entity : entities2) {
            LOGGER.info("key : " + entity.getKind() + " / " + entity.getKey());
            LOGGER.info("place id : " + String.valueOf(entity.getProperty(PLACE_RESULT_ID)));
        }
    }
}
