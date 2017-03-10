package com.tripwego.api.placeresult.addresscomponent;

import com.google.appengine.api.datastore.Entity;
import com.tripwego.api.common.AbstractRepository;
import com.tripwego.dto.placeresult.AddressComponentDto;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by JG on 19/02/17.
 */
public class AddressComponentRepository extends AbstractRepository<AddressComponentDto> {

    private static final Logger LOGGER = Logger.getLogger(AddressComponentRepository.class.getName());

    private AddressComponentEntityMapper entityMapper = new AddressComponentEntityMapper();

    @Override
    public void createCollection(Entity parent, List<AddressComponentDto> addressComponents) {
        final List<Entity> entities = new ArrayList<>();
        for (AddressComponentDto addressComponentDto : addressComponents) {
            entities.add(entityToCreate(parent, addressComponentDto));
        }
        datastore.put(entities);
    }

    @Override
    public Entity entityToCreate(Entity parent, AddressComponentDto addressComponentDto) {
        final Entity entity = entityMapper.map(addressComponentDto, parent);
        return entity;
    }
}
