/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.status;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.impl.event.status.ServerPingRequestEvent;
import sh.pancake.server.impl.event.status.ServerStatusEvent;

@Mixin(ServerStatusPacketListenerImpl.class)
public abstract class ServerStatusPacketListenerImplMixin {

    @Shadow
    private Connection connection;

    @Redirect(method = "handleStatusRequest", at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.getStatus()Lnet/minecraft/network/protocol/status/ServerStatus;"))
    public ServerStatus onGetStatus(MinecraftServer server) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();
        
        ServerStatus origin = server.getStatus();

        ServerStatusEvent event = new ServerStatusEvent(connection, origin);

        pancakeServer.getEventManager().callEvent(event);

        return event.getStatus();
    }

    @Redirect(method = "handlePingRequest", at = @At(value = "INVOKE", target = "net/minecraft/network/protocol/status/ServerboundPingRequestPacket.getTime()J"))
    public long onGetTime(ServerboundPingRequestPacket packet) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        long time = packet.getTime();

        ServerPingRequestEvent event = new ServerPingRequestEvent(connection, time);

        pancakeServer.getEventManager().callEvent(event);

        return time;
    }
    
}
