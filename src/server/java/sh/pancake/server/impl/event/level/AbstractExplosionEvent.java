/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.level;

import javax.annotation.Nullable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;

public class AbstractExplosionEvent extends LevelEvent {

    private Explosion explosion;

    private double x;
    private double y;
    private double z;

    private float radius;
    private boolean fire;

    private DamageSource damageSource;

    private Entity entity;

    private BlockInteraction interaction;

    public AbstractExplosionEvent(Level level, Explosion explosion,
        double x, double y, double z, float radius, boolean fire, DamageSource damageSource, Entity entity, BlockInteraction interaction) {
        super(level);

        this.explosion = explosion;

        this.x = x;
        this.y = y;
        this.z = z;

        this.radius = radius;
        this.fire = fire;
        
        this.damageSource = damageSource;

        this.entity = entity;
    }

    public Explosion getExplosion() {
        return explosion;
    }

    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    protected void setX(double x) {
        this.x = x;
    }

    protected void setY(double y) {
        this.y = y;
    }

    protected void setZ(double z) {
        this.z = z;
    }

    @Nullable
    public Entity getEntity() {
        return entity;
    }
    
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public void setDamageSource(DamageSource damageSource) {
        this.damageSource = damageSource;
    }

    public float getRadius() {
        return radius;
    }

    protected void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }

    public BlockInteraction getInteraction() {
        return interaction;
    }
    
    public void setInteraction(BlockInteraction interaction) {
        this.interaction = interaction;
    }

}
