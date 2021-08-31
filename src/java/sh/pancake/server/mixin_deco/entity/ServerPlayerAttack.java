/*
 * Created on Tue Aug 31 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin_deco.entity;

import net.minecraft.world.entity.Entity;

public interface ServerPlayerAttack {
    
    void attack(Entity entity);

}
