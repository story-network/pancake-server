/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */
package sh.pancake.server.mixin;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.dedicated.DedicatedServer;
import sh.pancake.server.PancakeServerService;

@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin {

    /**
     * Switch console output to JLine
     */
    @ModifyVariable(method = "initServer", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
    Thread overrideConsoleThread(Thread thread) {
        Thread consoleThread = new Thread(() -> PancakeServerService.getService().startConsole());
        consoleThread.setDaemon(true);
        
        return consoleThread;
    }

    /**
     * Fire initialization
     */
    @Inject(method = "initServer", at = @At("HEAD"))
    void initServerPre(CallbackInfoReturnable<Boolean> info) {
        PancakeServerService service = PancakeServerService.getService();

        service.doInit((DedicatedServer) (Object) this);
    }

    /**
     * Fire post initialization
     */
    @Inject(method = "initServer", at = @At("TAIL"))
    void initServerPost(CallbackInfoReturnable<Boolean> info) {
        PancakeServerService service = PancakeServerService.getService();

        service.doPostInit();
    }

}
