package com.tripwego.api.common;

import com.google.appengine.api.datastore.*;
import com.google.appengine.repackaged.com.google.common.base.Function;
import com.google.appengine.repackaged.com.google.common.collect.FluentIterable;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableCollection;

import java.util.List;

public abstract class AbstractRepository<DTO> {

    protected final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    protected abstract void createCollection(Entity parent, List<DTO> collection);

    public abstract Entity entityToCreate(Entity parent, DTO dto);

    public void updateCollection(String kind, Entity parent, List<DTO> collection) {
        deleteCollection(kind, parent);
        createCollection(parent, collection);
    }

    public void deleteCollection(String kind, Entity parent) {
        datastore.delete(keysToDelete(kind, parent));
    }

    protected ImmutableCollection<Key> keysToDelete(String kind, Entity parent) {
        final Query query = new Query(kind).setAncestor(parent.getKey());
        final List<Entity> entitiesKeysOnly = datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
        return extractKeys(entitiesKeysOnly);
    }

    protected ImmutableCollection<Key> extractKeys(final List<Entity> entities) {
        final ImmutableCollection<Key> keys = FluentIterable.from(entities).toMap(new Function<Entity, Key>() {
            @Override
            public Key apply(Entity entity) {
                return entity.getKey();
            }
        }).values();
        return keys;
    }
}
