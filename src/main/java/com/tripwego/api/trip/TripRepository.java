package com.tripwego.api.trip;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.tripwego.api.placeresult.PlaceResultRepository;
import com.tripwego.api.placeresult.addresscomponent.AddressComponentDtoMapper;
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
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.tripwego.api.Constants.*;

public class TripRepository {

    private static final Logger LOGGER = Logger.getLogger(TripRepository.class.getName());
    private static final int NUMBER_VERSION_DEFAULT = 1;

    private TripEntityMapper tripEntityMapper = new TripEntityMapper();
    private ActivityDtoMapper activityDtoMapper = new ActivityDtoMapper();
    private AccommodationDtoMapper accommodationDtoMapper = new AccommodationDtoMapper();
    private FlightDtoMapper flightDtoMapper = new FlightDtoMapper();
    private RailDtoMapper railDtoMapper = new RailDtoMapper();
    private RentalDtoMapper rentalDtoMapper = new RentalDtoMapper();
    private AddressComponentDtoMapper addressComponentMapper = new AddressComponentDtoMapper();
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
        Optional<MyUser> user = Optional.fromNullable(userRepository.create(trip.getUser()));
        final Entity entity = new Entity(KIND_TRIP);
        tripEntityMapper.map(entity, trip, user);
        entity.setProperty(IS_DEFAULT, true);
        entity.setProperty(STATUS, TripStatus.NOT_SAVED.name());
        entity.setProperty(IS_CANCELLED, false);
        entity.setProperty(CREATED_AT, new Date());
        entity.setProperty(UPDATED_AT, new Date());
        entity.setProperty(IS_PUBLISHED, true); // TODO false ?
        entity.setProperty(TAGS, trip.getTags());
        updateTripVersion(trip, entity, NUMBER_VERSION_DEFAULT);
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
                updateTripVersion(trip, entity, number);
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
            entity.setProperty(STATUS, TripStatus.SAVED.name());
            entity.setProperty(UPDATED_AT, new Date());
            if (trip.getTripProvider() != null) {
                updateTripProvider(trip, entity);
            }
            datastore.put(entity);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }


    public Trip copy(Trip trip) {
        Trip result = null;
        LOGGER.info("--> copy - DEB");
        Optional<MyUser> user = Optional.fromNullable(userRepository.create(trip.getUser()));
        try {
            final Entity copyFrom = datastore.get(KeyFactory.stringToKey(trip.getParentTripId()));
            //final Entity entity = new Entity(KIND_TRIP, copyFrom.getKey());
            final Entity entity = new Entity(KIND_TRIP);
            tripEntityMapper.map(entity, trip, user);
            entity.setProperty(IS_DEFAULT, true);
            entity.setProperty(STATUS, TripStatus.SAVED.name());
            entity.setProperty(IS_CANCELLED, false);
            entity.setProperty(CREATED_AT, new Date());
            entity.setProperty(UPDATED_AT, new Date());
            entity.setProperty(IS_PUBLISHED, trip.isPublished());
            updateTripVersion(trip, entity, NUMBER_VERSION_DEFAULT);
            entity.setProperty(IS_COPY, true);
            datastore.put(entity);
            updateChild(trip, entity);

            result = tripDtoMapper.map(entity, user);
            // TODO copy steps
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        LOGGER.info("--> copy - END");
        return result;
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
     * lazy
     */
    public Trip retrieveEager(String id) {
        LOGGER.info("--> retrieveEager - START");
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
            trip.setPlaceResultDto(placeResultRepository.retrieveEager(trip.getPlaceResultId()));
            LOGGER.info("--> placeResult - OK");
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        LOGGER.info("--> retrieveEager - END");
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

    public void publishOrPrivate(Trip trip) throws EntityNotFoundException {
        final Entity entity = datastore.get(KeyFactory.stringToKey(trip.getId()));
        entity.setProperty(IS_PUBLISHED, trip.isPublished());
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

    private void updateTripVersion(Trip trip, Entity entity, long number) {
        LOGGER.info("--> updateTripVersion - DEB : " + number);
        // no parent id
        // version = 1
        final EmbeddedEntity embeddedVersion = new EmbeddedEntity();
        embeddedVersion.setProperty(VERSION_NUMBER, number);
        embeddedVersion.setProperty(VERSION_USER, trip.getUser().getUserId());
        embeddedVersion.setProperty(VERSION_CREATED_AT, new Date());
        embeddedVersion.setProperty(VERSION_UPDATED_AT, new Date());
        entity.setProperty(EMBEDDED_VERSION, embeddedVersion);
        LOGGER.info("--> updateTripVersion - END");
    }

    private void updateTripProvider(Trip trip, Entity entity) {
        LOGGER.info("--> updateTripProvider - DEB");
        final EmbeddedEntity embeddedProvider = new EmbeddedEntity();
        TripProvider tripProvider = trip.getTripProvider();
        embeddedProvider.setProperty(NAME, tripProvider.getName());
        embeddedProvider.setProperty(TYPE, tripProvider.getType());
        embeddedProvider.setProperty(URL_SITE, tripProvider.getUrl());
        embeddedProvider.setProperty(EMAIL, tripProvider.getEmail());
        entity.setProperty(EMBEDDED_PROVIDER, embeddedProvider);
        LOGGER.info("--> updateTripProvider - END");
    }

    public void deleteTripsWithUserUnknown() {
        deleteTripEntities(tripQueries.findTripEntitiesWithUserUnknown());
    }

    public void deleteTripsCancelled() {
        LOGGER.info("--> deleteTripsCancelled - DEB");
        // BEGIN TRANSACTION
        final List<Key> keysToKill = new ArrayList<>();
        final List<Entity> tripsToDelete = tripQueries.findTripEntitiesCancelled();
        for (Entity entity : tripsToDelete) {
            deleteChild(entity);
            keysToKill.add(entity.getKey());
        }
        LOGGER.info("--> deleteTripsCancelled : " + keysToKill.size());
        datastore.delete(keysToKill);
        LOGGER.info("--> deleteTripsCancelled - END");
    }

    public void deleteTripsCancelledFromUser(String userId) {
        deleteTripEntities(tripQueries.findTripEntitiesCancelledByUser(userId));
    }

    private void deleteTripEntities(List<Entity> tripsToDelete) {
        final List<Key> keysToKill = new ArrayList<>();
        for (Entity entity : tripsToDelete) {
            deleteChild(entity);
            keysToKill.add(entity.getKey());
        }
        datastore.delete(keysToKill);
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
}
