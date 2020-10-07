/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;

public class PreExplosionEvent extends AbstractExplosionEvent {

    private ExplosionDamageCalculator damageCalculator;

    public PreExplosionEvent(Level level, Explosion explosion, double x, double y, double z, float radius, boolean fire, DamageSource damageSource,
            Entity entity, BlockInteraction interaction, ExplosionDamageCalculator damageCalculator) {
        super(level, explosion, x, y, z, radius, fire, damageSource, entity, interaction);

        this.damageCalculator = damageCalculator;
    }

    public ExplosionDamageCalculator getDamageCalculator() {
        return damageCalculator;
    }

    public void setDamageCalculator(ExplosionDamageCalculator damageCalculator) {
        this.damageCalculator = damageCalculator;
    }

    @Override
    public void setRadius(float radius) {
        super.setRadius(radius);
    }

    @Override
    public void setX(double x) {
        super.setX(x);
    }

    @Override
    public void setY(double y) {
        super.setY(y);
    }

    @Override
    public void setZ(double z) {
        super.setZ(z);
    }

}
