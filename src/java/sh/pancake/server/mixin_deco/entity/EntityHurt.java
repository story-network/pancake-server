/*
 * Created on Thu Aug 26 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin_deco.entity;

import net.minecraft.world.damagesource.DamageSource;

public interface EntityHurt {
    
    boolean hurt(DamageSource source, float amount);

}
