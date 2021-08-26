/*
 * Created on Mon Aug 23 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.world;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Explosion.BlockInteraction;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.entity.EntitySpawnLevelEvent;
import sh.pancake.server.impl.event.level.LevelExplosionPostEvent;
import sh.pancake.server.impl.event.level.LevelExplosionPreEvent;
import sh.pancake.server.impl.level.ExplosionInfo;
import sh.pancake.server.level.ExplosionUtil;
import sh.pancake.server.mixin.accessor.ExplosionAccessor;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(
        method = "explode",
        at = @At(value = "INVOKE", target = "net/minecraft/world/level/Explosion.explode()V", ordinal = 0, shift = Shift.BEFORE),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void explode_explodePre(
        @Nullable Entity var1,
        @Nullable DamageSource var2,
        @Nullable ExplosionDamageCalculator var3,
        double var4,
        double var5,
        double var6,
        float var7,
        boolean var8,
        BlockInteraction var9,
        CallbackInfoReturnable<Explosion> callbackInfo,
        Explosion explosion
    ) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) return;

        ExplosionAccessor accessor = (ExplosionAccessor) explosion;
        
        LevelExplosionPreEvent event = new LevelExplosionPreEvent(
            (ServerLevel) (Object) this,
            ExplosionUtil.getInfo(explosion),
            accessor.getDamageCalculator()
        );

        server.dispatchEvent(event);

        if (event.isCancelled()) {
            callbackInfo.setReturnValue(explosion);
            return;
        }

        ExplosionInfo info = event.getInfo();
        ExplosionUtil.applyInfo(explosion, info);

        if (accessor.getDamageCalculator() != event.getDamageCalculator()) accessor.setDamageCalculator(event.getDamageCalculator());
    }

    @Inject(
        method = "explode",
        at = @At(value = "INVOKE", target = "net/minecraft/world/level/Explosion.finalizeExplosion(Z)V", ordinal = 0, shift = Shift.BEFORE),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void explode_finalizeExplosionPre(
        @Nullable Entity var1,
        @Nullable DamageSource var2,
        @Nullable ExplosionDamageCalculator var3,
        double var4,
        double var5,
        double var6,
        float var7,
        boolean var8,
        BlockInteraction var9,
        CallbackInfoReturnable<Explosion> callbackInfo,
        Explosion explosion
    ) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) return;

        ExplosionAccessor accessor = ((ExplosionAccessor) explosion);
        
        LevelExplosionPostEvent event = new LevelExplosionPostEvent(
            (ServerLevel) (Object) this,
            ExplosionUtil.getInfo(explosion),
            accessor.getToBlow(),
            accessor.getHitPlayers()
        );

        server.dispatchEvent(event);

        if (event.isCancelled()) {
            callbackInfo.setReturnValue(explosion);
            return;
        }

        ExplosionInfo info = event.getInfo();
        ExplosionUtil.applyInfo(explosion, info);

        if (accessor.getToBlow() != event.getBlockList()) accessor.setToBlow(event.getBlockList());
        if (accessor.getHitPlayers() != event.getHitPlayerMap()) accessor.setHitPlayers(event.getHitPlayerMap());
    }

    @Shadow
    protected abstract boolean addEntity(Entity entity);

    @Redirect(
        method = "addFreshEntity",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/level/ServerLevel.addEntity(Lnet/minecraft/world/entity/Entity;)Z"
        )
    )
    public boolean addFreshEntity_addEntity(ServerLevel level, Entity entity) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) return addEntity(entity);

        EntitySpawnLevelEvent event = new EntitySpawnLevelEvent(level, entity);
        server.dispatchEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        return addEntity(event.getEntity());
    }
    
}
