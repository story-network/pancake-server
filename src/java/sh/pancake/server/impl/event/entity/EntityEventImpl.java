/*
 * Created on Fri Oct 09 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import net.minecraft.world.entity.Entity;
import sh.pancake.server.impl.event.CancellableEvent;

public abstract class EntityEventImpl<T extends Entity> extends CancellableEvent implements EntityEvent<T> {

    private final T entity;

    public EntityEventImpl(T entity) {
        this.entity = entity;
    }

    @Override
    public T getEntity() {
        return entity;
    }
    
}
