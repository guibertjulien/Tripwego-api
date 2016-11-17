package com.tripwego.api.entity;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Optional;
import com.tripwego.api.Constants;
import com.tripwego.api.TripStatus;
import com.tripwego.api.query.TripQueries;
import com.tripwego.api.query.UserQueries;
import com.tripwego.api.request.mapper.trip.TripEntityMapper;
import com.tripwego.api.request.mapper.trip.TripEntityMapperFactory;
import com.tripwego.api.response.mapper.trip.TripMapper;
import com.tripwego.api.response.mapper.trip.TripMapperFactory;
import com.tripwego.dto.MyUser;
import com.tripwego.dto.Step;
import com.tripwego.dto.Trip;
import com.tripwego.dto.TripProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class TripRepository {

    private static final Logger LOGGER = Logger.getLogger(TripRepository.class.getName());
    private static final int NUMBER_VERSION_DEFAULT = 1;

    private TripEntityMapper tripEntityMapper = new TripEntityMapperFactory().create();
    private TripMapper tripMapper = new TripMapperFactory().create();
    private StepRepository stepRepository = new StepRepository();
    private AccommodationRepository accommodationRepository = new AccommodationRepository();
    private TripQueries tripQueries = new TripQueries();
    private UserRepository userRepository = new UserRepository();
    private UserQueries userQueries = new UserQueries();

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public Trip create(Trip trip) {
        Optional<MyUser> user = Optional.fromNullable(userRepository.create(trip.getUser()));
        final Entity entity = new Entity(Constants.KIND_TRIP);
        tripEntityMapper.map(entity, trip, user);
        entity.setProperty(Constants.IS_DEFAULT, true);
        entity.setProperty(Constants.STATUS, TripStatus.NOT_SAVED.name());
        entity.setProperty(Constants.IS_CANCELLED, false);
        entity.setProperty(Constants.CREATED_AT, new Date());
        entity.setProperty(Constants.UPDATED_AT, new Date());
        entity.setProperty(Constants.IS_PUBLISHED, true); // TODO false ?
        updateTripVersion(trip, entity, NUMBER_VERSION_DEFAULT);
        datastore.put(entity);
        // no step
        // no user
        // TODO enhance
        return tripMapper.map(entity, Optional.of(trip.getSteps()), user);
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
                entity = new Entity(Constants.KIND_TRIP, parent.getKey());
                tripEntityMapper.map(entity, trip, Optional.of(user));
                entity.setProperty(Constants.IS_DEFAULT, true);
                entity.setProperty(Constants.CREATED_AT, new Date());
                updateTripVersion(trip, entity, number);
                // TODO make other trip default
                // create steps
                stepRepository.createAll(trip.getSteps(), entity);
                accommodationRepository.createAll(trip.getAccommodations(), entity);
            }
            // update trip
            else {
                LOGGER.info("--> update trip");
                entity = parent;
                tripEntityMapper.map(parent, trip, Optional.of(user));
                // update steps
                stepRepository.updateAll(trip.getSteps(), entity);
                accommodationRepository.updateAll(trip.getAccommodations(), entity);
            }
            entity.setProperty(Constants.STATUS, TripStatus.SAVED.name());
            entity.setProperty(Constants.UPDATED_AT, new Date());
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
            final Entity entity = new Entity(Constants.KIND_TRIP);
            tripEntityMapper.map(entity, trip, user);
            entity.setProperty(Constants.IS_DEFAULT, true);
            entity.setProperty(Constants.STATUS, TripStatus.SAVED.name());
            entity.setProperty(Constants.IS_CANCELLED, false);
            entity.setProperty(Constants.CREATED_AT, new Date());
            entity.setProperty(Constants.UPDATED_AT, new Date());
            entity.setProperty(Constants.IS_PUBLISHED, trip.isPublished());
            updateTripVersion(trip, entity, NUMBER_VERSION_DEFAULT);
            entity.setProperty(Constants.IS_COPY, true);
            datastore.put(entity);
            //
            stepRepository.updateAll(trip.getSteps(), entity);
            accommodationRepository.updateAll(trip.getAccommodations(), entity);
            result = tripMapper.map(entity, Optional.of(trip.getSteps()), user);
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
            trip = tripMapper.map(entity, Optional.<List<Step>>absent(), Optional.<MyUser>absent());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return trip;
    }

    /**
     * lazy
     */
    public Trip retrieveEager(String id) {
        Trip trip = null;
        try {
            final Entity entity = datastore.get(KeyFactory.stringToKey(id));
            final List<Step> steps = tripQueries.findStepsByTrip(entity);
            Optional<MyUser> user = userQueries.findByUserId(String.valueOf(entity.getProperty(Constants.USER_ID)));
            trip = tripMapper.map(entity, Optional.of(steps), user);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return trip;
    }

    public void deleteOrRestore(Trip trip) throws EntityNotFoundException {
        final Entity entity = datastore.get(KeyFactory.stringToKey(trip.getId()));
        entity.setProperty(Constants.IS_CANCELLED, trip.isCancelled());
        if (trip.isCancelled()) {
            entity.setProperty(Constants.CANCELLATION_DATE, new Date());
        } else {
            entity.setProperty(Constants.CANCELLATION_DATE, null);
        }
        datastore.put(entity);
    }

    public void publishOrPrivate(Trip trip) throws EntityNotFoundException {
        final Entity entity = datastore.get(KeyFactory.stringToKey(trip.getId()));
        entity.setProperty(Constants.IS_PUBLISHED, trip.isPublished());
        datastore.put(entity);
    }

    private void makeTripDefault(String id) {
        final Transaction txn = datastore.beginTransaction();
        try {
            // BEGIN TRANSACTION
            final Entity entity = datastore.get(KeyFactory.stringToKey(id));
            entity.setProperty(Constants.IS_DEFAULT, true);
            datastore.put(entity);
            final List<Entity> tripsToUpdate = new ArrayList<>();
            final Entity parent = datastore.get(entity.getParent());
            final Query query = new Query(Constants.KIND_STEP).setAncestor(parent.getKey());
            final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
            parent.setProperty(Constants.IS_DEFAULT, false);
            tripsToUpdate.add(parent);
            for (Entity child : entities) {
                child.setProperty(Constants.IS_DEFAULT, false);
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
        embeddedVersion.setProperty(Constants.VERSION_NUMBER, number);
        embeddedVersion.setProperty(Constants.VERSION_USER, trip.getUser().getUserId());
        embeddedVersion.setProperty(Constants.VERSION_CREATED_AT, new Date());
        embeddedVersion.setProperty(Constants.VERSION_UPDATED_AT, new Date());
        entity.setProperty(Constants.EMBEDDED_VERSION, embeddedVersion);
        LOGGER.info("--> updateTripVersion - END");
    }

    private void updateTripProvider(Trip trip, Entity entity) {
        LOGGER.info("--> updateTripProvider - DEB");
        final EmbeddedEntity embeddedProvider = new EmbeddedEntity();
        TripProvider tripProvider = trip.getTripProvider();
        embeddedProvider.setProperty(Constants.NAME, tripProvider.getName());
        embeddedProvider.setProperty(Constants.TYPE, tripProvider.getType());
        embeddedProvider.setProperty(Constants.URL_SITE, tripProvider.getUrl());
        embeddedProvider.setProperty(Constants.EMAIL, tripProvider.getEmail());
        entity.setProperty(Constants.EMBEDDED_PROVIDER, embeddedProvider);
        LOGGER.info("--> updateTripProvider - END");
    }

    public void deleteTripsWithUserUnknown() {
        /*final Transaction txn = datastore.beginTransaction();
        try {*/
        // BEGIN TRANSACTION
        final List<Key> keysToKill = new ArrayList<>();
        final List<Entity> tripsToDelete = tripQueries.findTripEntitiesWithUserUnknown();
        for (Entity entity : tripsToDelete) {
            deleteAllStepsForTrip(entity);
            keysToKill.add(entity.getKey());
        }
        datastore.delete(keysToKill);
        // END TRANSACTION
        /*    txn.commit();
        } catch (Exception e) {
            LOGGER.warning("" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }*/
    }

    public void deleteTripsCancelled() {
        LOGGER.info("--> deleteTripsCancelled - DEB");
        // BEGIN TRANSACTION
        final List<Key> keysToKill = new ArrayList<>();
        final List<Entity> tripsToDelete = tripQueries.findTripEntitiesCancelled();
        for (Entity entity : tripsToDelete) {
            deleteAllStepsForTrip(entity);
            keysToKill.add(entity.getKey());
        }
        LOGGER.info("--> deleteTripsCancelled : " + keysToKill.size());
        datastore.delete(keysToKill);
        LOGGER.info("--> deleteTripsCancelled - END");
    }

    private void deleteAllStepsForTrip(Entity parent) {
        final List<Key> keysToKill = new ArrayList<>();
        final List<Entity> stepsByTrip = tripQueries.findStepEntitiesKeyByTrip(parent);
        for (Entity entity : stepsByTrip) {
            keysToKill.add(entity.getKey());
        }
        datastore.delete(keysToKill);
    }

}