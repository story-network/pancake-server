/*
 * Created on Thu Aug 26 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.entity;

import net.minecraft.world.entity.Entity;

public interface EntityEvent<T extends Entity> {

    T getEntity();
    
}
