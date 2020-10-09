/*
 * Created on Fri Oct 09 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import net.minecraft.world.entity.Entity;

// Note unlike bukkit called even on initializing
public class EntityAddLevelEvent extends EntityEvent<Entity> {

    public EntityAddLevelEvent(Entity entity) {
        super(entity);
    }

    public void setEntity(Entity entity) {
        super.setEntity(entity);
    }
    
}
