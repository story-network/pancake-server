/*
 * Created on Fri Oct 09 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

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
    
}
