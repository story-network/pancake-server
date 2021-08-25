/*
 * Created on Fri Oct 09 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EntityEvent<T extends Entity> {

    private final T entity;

    public EntityEvent(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }
    
    public Level getLevel() {
        return entity.level;
    }

    /**
     * Try to cast entity
     *
     * @param <S> Entity type
     * @param cl Entity class
     * @return null if entity cannot be casted
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <S extends T>S castEntityAs(Class<S> cl) {
        if (!cl.isInstance(entity)) return null;

        return (S) entity;
    }
    
}
