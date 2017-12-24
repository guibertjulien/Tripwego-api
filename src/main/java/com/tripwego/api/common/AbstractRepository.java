package com.tripwego.api.common;

import com.google.appengine.api.datastore.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    protected Collection<Key> keysToDelete(String kind, Entity parent) {
        final Query query = new Query(kind).setAncestor(parent.getKey());
        final List<Entity> entitiesKeysOnly = datastore.prepare(query.setKeysOnly()).asList(FetchOptions.Builder.withDefaults());
        return extractKeys(entitiesKeysOnly);
    }

    protected Collection<Key> extractKeys(final List<Entity> entities) {
        return entities.stream().map(new Function<Entity, Key>() {
            @Override
            public Key apply(Entity entity) {
                return entity.getKey();
            }
        }).collect(Collectors.toList());
    }
}
