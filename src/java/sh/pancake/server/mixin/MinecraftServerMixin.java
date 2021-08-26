/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.server.ServerTickEvent;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    /**
     * Modify brand name
     */
    @ModifyConstant(method = "getServerModName", constant = @Constant(stringValue = "vanilla"), require = 0)
    public String getServerModName(String prev) {
        return "Pancake";
    }

    @Inject(method = "tickServer", at = @At("HEAD"), cancellable = true)
    void tickServerPre(BooleanSupplier haveTime, CallbackInfo info) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) return;

        ServerTickEvent event = new ServerTickEvent(((MinecraftServer) (Object) this).getTickCount(), haveTime);
        server.dispatchEvent(event);

        if (event.isCancelled()) {
            info.cancel();
        }
    }

}
