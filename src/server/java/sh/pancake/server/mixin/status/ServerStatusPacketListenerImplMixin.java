/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.status;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.impl.event.status.ServerStatusEvent;

@Mixin(ServerStatusPacketListenerImpl.class)
public abstract class ServerStatusPacketListenerImplMixin {

    @Redirect(method = "handleStatusRequest", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.getStatus()Lnet/minecraft/network/protocol/status/ServerStatus;"))
    public ServerStatus onGetStatus(MinecraftServer server) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();
        
        ServerStatus origin = server.getStatus();

        ServerStatusEvent event = new ServerStatusEvent(origin);

        pancakeServer.getEventManager().callEvent(event);

        return event.getStatus();
    }
    
}
