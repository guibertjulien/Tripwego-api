package com.tripwego.api.placeresult;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Rating;

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
        final Rating rating1 = (Rating) o1.getProperty(RATING);
        final Rating rating2 = (Rating) o2.getProperty(RATING);
        if (counter1 > counter2) {
            return 1;
        } else {
            return rating1.compareTo(rating2);
        }
    }
}
