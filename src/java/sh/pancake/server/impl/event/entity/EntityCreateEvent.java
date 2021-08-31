/*
 * Created on Tue Aug 31 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import sh.pancake.server.impl.event.CancellableEvent;

public class EntityCreateEvent extends CancellableEvent {
    
    private final Entity entity;

    public EntityCreateEvent(@Nullable Entity entity) {
        this.entity = entity;
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }

}
