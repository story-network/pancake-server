/*
 * Created on Mon Aug 30 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.player;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;

public class EntityInteractInfo {
    
    private int entityId;
    private Entity entity;

    public EntityInteractInfo(int entityId, @Nullable Entity entity) {
        this.entityId = entityId;
        this.entity = entity;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }

    public void setEntity(@Nullable Entity entity) {
        this.entity = entity;
    }

}
