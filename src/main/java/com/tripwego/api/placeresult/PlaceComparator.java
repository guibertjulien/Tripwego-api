package com.tripwego.api.placeresult;

import com.google.appengine.api.datastore.Entity;

import java.util.Comparator;

import static com.tripwego.api.Constants.COUNTER;
import static com.tripwego.api.Constants.RATING;

/**
 * Created by JG on 08/03/17.
 */
public class PlaceComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity o1, Entity o2) {
        final long counter1 = (long) o1.getProperty(COUNTER);
        final long counter2 = (long) o2.getProperty(COUNTER);
        final double rating1 = (double) o1.getProperty(RATING);
        final double rating2 = (double) o2.getProperty(RATING);
        if (rating1 < rating2) {
            return 1;
        } else if (counter1 < counter2) {
            return 1;
        } else {
            return 0;
        }
    }
}
