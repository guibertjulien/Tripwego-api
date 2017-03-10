package com.tripwego.api.trip;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TripQueriesTest {

    // Kind = table
    // Entity = ligne

    public static final String TRIP_NAME = "California trip 2016";
    public static final String TABLE_TRIP = "Trip";
    public static final String TABLE_STEP = "Step";

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(
                    // Set no eventual consistency, that way queries return all results.
                    // https://cloud.google.com/appengine/docs/java/tools/localunittesting#Java_Writing_High_Replication_Datastore_tests
                    new LocalDatastoreServiceTestConfig()
                            .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

    private DatastoreService datastore;
    private TripQueries tested;

    @Before
    public void setUp() throws Exception {
        helper.setUp();
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown();
    }

    @Test
    public void testFindStepsByTrip() throws Exception {
        Entity trip = new Entity(TABLE_TRIP, "trip1");
        trip.setProperty("name", TRIP_NAME);
        Entity step1 = new Entity(TABLE_STEP, "step1", trip.getKey());
        Entity step2 = new Entity(TABLE_STEP, "step2", trip.getKey());
        datastore.put(ImmutableList.<Entity>of(trip, step1, step2));

        // retrieve trip
        Entity entity = datastore.get(trip.getKey());
        assertThat((String) entity.getProperty("name"), is(TRIP_NAME));

        // retrieve steps
        Query query = new Query(TABLE_STEP).setAncestor(trip.getKey());
        List<Entity> results = datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
        assertThat(results.get(0), is(step1));
        assertThat(results.get(1), is(step2));
    }
}