/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.accessor;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.Vec3;

@Mixin(Explosion.class)
public interface ExplosionAccessor {

    @Accessor("fire")
    boolean getFire();
    
    @Mutable
    @Accessor("fire")
    void setFire(boolean fire);

    @Accessor("blockInteraction")
    BlockInteraction getBlockInteraction();
    
    @Mutable
    @Accessor("blockInteraction")
    void setBlockInteraction(BlockInteraction blockInteraction);

    @Accessor("level")
    Level getLevel();

    @Accessor("x")
    double getX();
    
    @Mutable
    @Accessor("x")
    void setX(double x);

    @Accessor("y")
    double getY();
    
    @Mutable
    @Accessor("y")
    void setY(double y);

    @Accessor("z")
    double getZ();
    
    @Mutable
    @Accessor("z")
    void setZ(double z);
   
    @Nullable
    @Accessor("source")
    Entity getSource();
    
    @Mutable
    @Accessor("source")
    void setSource(@Nullable Entity entity);

    @Accessor("radius")
    float getRadius();
    
    @Mutable
    @Accessor("radius")
    void setRadius(float radius);

    @Accessor("damageSource")
    DamageSource getDamageSource();
    
    @Mutable
    @Accessor("damageSource")
    void setDamageSource(DamageSource damageSource);

    @Accessor("damageCalculator")
    ExplosionDamageCalculator getDamageCalculator();
    
    @Mutable
    @Accessor("damageCalculator")
    void setDamageCalculator(ExplosionDamageCalculator damageCalculator);

    @Accessor("toBlow")
    List<BlockPos> getToBlow();
    
    @Mutable
    @Accessor("toBlow")
    void setToBlow(List<BlockPos> toBlow);

    @Accessor("hitPlayers")
    Map<Player, Vec3> getHitPlayers();
    
    @Mutable
    @Accessor("hitPlayers")
    void setHitPlayers(Map<Player, Vec3> hitPlayers);
}
