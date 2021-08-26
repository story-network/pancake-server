/*
 * Created on Thu Aug 26 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import sh.pancake.server.impl.event.CancellableEvent;
import sh.pancake.server.impl.event.level.ServerLevelEvent;

public class EntitySpawnLevelEvent extends CancellableEvent implements EntityEvent<Entity>, ServerLevelEvent {

    private final ServerLevel level;
    private Entity entity;

    public EntitySpawnLevelEvent(ServerLevel level, Entity entity) {
        this.level = level;
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }
    
}
