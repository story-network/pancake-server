/*
 * Created on Mon Aug 16 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerConnectionListener;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.network.ServerNetworkManager;

@Mixin(ServerConnectionListener.class)
public abstract class ServerConnectionListenerMixin {
    
    /**
     * Update server hook when new listener added
     */
    @Inject(method = "startTcpServerListener", at = @At("RETURN"))
    void startTcpServerListenerPost(CallbackInfo info) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) return;

        ServerNetworkManager manager = server.getNetworkManager();
        if (manager == null) return;

        manager.getGlobalHook().updateHandler();
    }

}
