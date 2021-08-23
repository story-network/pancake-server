/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.network;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerStatusPacketListenerImpl;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.status.ServerPingRequestEvent;
import sh.pancake.server.impl.event.status.ServerStatusEvent;

@Mixin(ServerStatusPacketListenerImpl.class)
public abstract class ServerStatusPacketListenerImplMixin {

    @Final
    @Shadow
    private MinecraftServer server;

    @Final
    @Shadow
    private Connection connection;

    @Redirect(method = "handleStatusRequest", at = @At(value = "INVOKE", target = "net/minecraft/network/Connection.send(Lnet/minecraft/network/protocol/Packet;)V"))
    public void handleStatusRequest_send(Connection connection) {
        PancakeServer pancakeServer = PancakeServerService.getService().getServer();
        if (pancakeServer == null) {
            connection.send(new ClientboundStatusResponsePacket(server.getStatus()));
            return;
        }

        ServerStatus origin = server.getStatus();

        ServerStatusEvent event = new ServerStatusEvent(connection, origin);
        pancakeServer.dispatchEvent(event);

        if (event.isCancelled()) return;
        
        connection.send(new ClientboundStatusResponsePacket(event.getStatus()));
    }

    @Inject(method = "handlePingRequest", at = @At("HEAD"), cancellable = true)
    public void handlePingRequestPre(ServerboundPingRequestPacket packet, CallbackInfo info) {
        PancakeServer pancakeServer = PancakeServerService.getService().getServer();
        if (pancakeServer == null) {
            return;
        }

        ServerPingRequestEvent event = new ServerPingRequestEvent(connection, packet.getTime());
        pancakeServer.dispatchEvent(event);

        if (event.isCancelled()) {
            info.cancel();
            connection.disconnect(new TranslatableComponent("multiplayer.status.request_handled"));
        }
    }
    
}
