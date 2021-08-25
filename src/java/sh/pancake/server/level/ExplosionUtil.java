/*
 * Created on Mon Aug 23 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.level;

import net.minecraft.world.level.Explosion;
import sh.pancake.server.impl.level.ExplosionInfo;
import sh.pancake.server.mixin.accessor.ExplosionAccessor;

public class ExplosionUtil {

    public static ExplosionInfo getInfo(Explosion explosion) {
        ExplosionAccessor accessor = ((ExplosionAccessor) explosion);

        return new ExplosionInfo(
            accessor.getX(),
            accessor.getY(),
            accessor.getZ(),
            accessor.getRadius(),
            accessor.getFire(),
            accessor.getDamageSource(),
            accessor.getSource(),
            accessor.getBlockInteraction()
        );
    }

    public static void applyInfo(Explosion explosion, ExplosionInfo info) {
        ExplosionAccessor accessor = ((ExplosionAccessor) explosion);

        if (accessor.getX() != info.getX()) accessor.setX(info.getX());
        if (accessor.getY() != info.getY()) accessor.setY(info.getY());
        if (accessor.getZ() != info.getZ()) accessor.setZ(info.getZ());

        if (accessor.getRadius() != info.getRadius()) accessor.setRadius(info.getRadius());
        if (accessor.getFire() != info.getFire()) accessor.setFire(info.getFire());
        if (accessor.getDamageSource() != info.getDamageSource()) accessor.setDamageSource(info.getDamageSource());

        if (accessor.getSource() != info.getEntity()) accessor.setSource(info.getEntity());

        if (accessor.getBlockInteraction() != info.getInteraction()) accessor.setBlockInteraction(info.getInteraction());
    }
    
}
