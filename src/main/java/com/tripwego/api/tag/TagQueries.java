package com.tripwego.api.tag;

import com.google.appengine.api.datastore.*;
import com.tripwego.api.Constants;
import com.tripwego.dto.tag.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JG on 05/06/16.
 */
public class TagQueries {

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private TagDtoMapper tagDtoMapper = new TagDtoMapper();

    public List<Tag> findAll() {
        final List<Tag> result = new ArrayList<>();
        // TODO filter by TYPE
        final Query query = new Query(Constants.KIND_TAG);
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        for (Entity entity : entities) {
            final Tag tag = tagDtoMapper.map(entity);
            result.add(tag);
        }
        return result;
    }

    public void updateAllEntities() {
        final Query query = new Query(Constants.KIND_TAG);
        final List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        for (Entity entity : entities) {
            entity.setProperty(Constants.COLOR, Constants.DEFAULT_COLOR);
            entity.setProperty(Constants.ICON_TYPE, "");
            datastore.put(entity);
        }
    }

}
