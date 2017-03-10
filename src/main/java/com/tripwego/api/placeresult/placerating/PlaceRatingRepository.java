package com.tripwego.api.placeresult.placerating;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Rating;
import com.tripwego.api.common.AbstractRepository;
import com.tripwego.dto.placeresult.PlaceRatingDto;

import java.util.Date;
import java.util.List;

import static com.tripwego.api.Constants.*;

/**
 * Created by JG on 08/03/17.
 */
public class PlaceRatingRepository extends AbstractRepository<PlaceRatingDto> {

    private static final String NO_COMMENT = "";
    private static final int DEFAULT_RATING = 0;

    @Override
    protected void createCollection(Entity parent, List<PlaceRatingDto> collection) {

    }

    @Override
    public Entity entityToCreate(Entity parent, PlaceRatingDto placeRatingDto) {
        final String userId = placeRatingDto.getUser().getUserId();
        final Entity entity = new Entity(KIND_PLACE_RATING, parent.getKey());
        entity.setProperty(RATING, new Rating(DEFAULT_RATING));
        entity.setProperty(IS_EVALUATED, false);
        entity.setProperty(USER_ID, userId);
        entity.setProperty(COMMENT, NO_COMMENT);
        entity.setProperty(CREATED_AT, new Date());
        //entity.setProperty(UPDATED_AT, new Date());// TODO
        return entity;
    }
}
