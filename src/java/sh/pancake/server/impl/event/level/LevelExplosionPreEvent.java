/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ExplosionDamageCalculator;
import sh.pancake.server.impl.level.ExplosionInfo;

public class LevelExplosionPreEvent extends ServerLevelEventImpl {

    private ExplosionInfo info;

    private ExplosionDamageCalculator damageCalculator;

    public LevelExplosionPreEvent(ServerLevel level, ExplosionInfo info, @Nullable ExplosionDamageCalculator damageCalculator) {
        super(level);

        this.info = info;

        this.damageCalculator = damageCalculator;
    }

    public ExplosionInfo getInfo() {
        return info;
    }

    public void setInfo(ExplosionInfo info) {
        this.info = info;
    }

    @Nullable
    public ExplosionDamageCalculator getDamageCalculator() {
        return damageCalculator;
    }

    public void setDamageCalculator(ExplosionDamageCalculator damageCalculator) {
        this.damageCalculator = damageCalculator;
    }

}
