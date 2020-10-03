/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */
package sh.pancake.server.mixin;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.dedicated.DedicatedServer;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;

@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin {

    @Inject(method = "initServer", at = @At("HEAD"))
    void onPreInitServer(CallbackInfoReturnable<Boolean> info) {
        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        server.onPreMCServerInit((DedicatedServer) (Object) this);
    }

    @Inject(method = "initServer", at = @At("RETURN"))
    void onPostInitServer(CallbackInfoReturnable<Boolean> info) {
        PancakeServer server = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        server.onPostMCServerInit((DedicatedServer) (Object) this);
    }

}
