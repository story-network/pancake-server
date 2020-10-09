/*
 * Created on Fri Oct 09 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import sh.pancake.server.impl.event.level.LevelEvent;

public class EntityEvent<T extends Entity> extends LevelEvent {

    private T entity;

    public EntityEvent(T entity) {
        super(entity.level);
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    protected void setEntity(T entity) {
        this.entity = entity;
    }

    // Returns null when it can't be casted
    @Nullable
    public <S extends T>S getEntityAs(Class<S> cl) {
        if (!cl.isInstance(entity)) return null;

        return (S) entity;
    }
    
}
