/*
 * Created on Fri Oct 09 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.impl.event.entity.EntityAddLevelEvent;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    // ugly
    private EntityAddLevelEvent addEvent;

    @ModifyVariable(method = "addEntity", at = @At("HEAD"), argsOnly = true)
    public Entity onAddEntityPre(Entity entity) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        addEvent = new EntityAddLevelEvent(entity);

        pancakeServer.getEventManager().callEvent(addEvent);

        return addEvent.getEntity();
    }

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    public void onAddEntity(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (info.isCancelled()) return;

        if (addEvent != null && (addEvent.isCancelled() || entity == null)) {
            addEvent = null;

            info.setReturnValue(false);
            return;
        }
    }

}
