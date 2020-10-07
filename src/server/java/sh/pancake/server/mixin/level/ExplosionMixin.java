/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.level;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.Vec3;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.impl.event.level.PostExplosionEvent;
import sh.pancake.server.impl.event.level.PreExplosionEvent;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Mutable
    @Shadow
    private boolean fire;

    @Mutable
    @Shadow
    private BlockInteraction blockInteraction;

    @Mutable
    @Shadow
    private Level level;

    @Mutable
    @Shadow
    private double x;

    @Mutable
    @Shadow
    private double y;

    @Mutable
    @Shadow
    private double z;

   
    @Mutable
    @Shadow
    private Entity source;

    @Mutable
    @Shadow
    private float radius;

    @Mutable
    @Shadow
    private DamageSource damageSource;
    
    @Mutable
    @Shadow
    private ExplosionDamageCalculator damageCalculator;

    @Shadow
    private List<BlockPos> toBlow;

    @Shadow
    private Map<Player, Vec3> hitPlayers;
    
    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    public void onExplode(CallbackInfo info) {
        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();
        
        PreExplosionEvent event = new PreExplosionEvent(level, (Explosion) (Object) this, x, y, z, radius, fire, damageSource, source, blockInteraction, damageCalculator);

        server.getEventManager().callEvent(event);

        if (event.isCancelled()) {
            info.cancel();
            return;
        }

        if (x != event.getX()) x = event.getX();
        if (y != event.getY()) y = event.getY();
        if (z != event.getZ()) z = event.getZ();

        if (radius != event.getRadius()) radius = event.getRadius();
        if (fire != event.isFire()) fire = event.isFire();
        if (damageSource != event.getDamageSource()) damageSource = event.getDamageSource();

        if (source != event.getEntity()) source = event.getEntity();

        if (blockInteraction != event.getInteraction()) blockInteraction = event.getInteraction();
        if (damageCalculator != event.getDamageCalculator()) damageCalculator = event.getDamageCalculator();
    }

    @Inject(method = "finalizeExplosion", at = @At("HEAD"))
    public void onFinalizeExplosion(boolean showParticle, CallbackInfo info) {
        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PostExplosionEvent event = new PostExplosionEvent(level, (Explosion) (Object) this, x, y, z, radius, fire, damageSource, source, blockInteraction, toBlow, hitPlayers);
    
        server.getEventManager().callEvent(event);

        if (radius != event.getRadius()) radius = event.getRadius();
        if (fire != event.isFire()) fire = event.isFire();
        if (damageSource != event.getDamageSource()) damageSource = event.getDamageSource();

        if (source != event.getEntity()) source = event.getEntity();

        if (blockInteraction != event.getInteraction()) blockInteraction = event.getInteraction();

        if (event.isCancelled()) {
            info.cancel();
            return;
        }
    }

}
