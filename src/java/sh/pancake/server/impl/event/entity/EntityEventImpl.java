/*
 * Created on Fri Oct 09 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import sh.pancake.server.impl.event.CancellableEvent;
import sh.pancake.server.impl.event.level.LevelEvent;

public abstract class EntityEventImpl<T extends Entity> extends CancellableEvent implements EntityEvent<T>, LevelEvent {

    private final T entity;

    public EntityEventImpl(T entity) {
        this.entity = entity;
    }

    @Override
    public T getEntity() {
        return entity;
    }

    @Override
    public Level getLevel() {
        return entity.level;
    }
    
}
