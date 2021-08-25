/*
 * Created on Tue Aug 24 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.network;

import java.net.SocketAddress;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.server.ServerLoginEvent;
import sh.pancake.server.mixin.accessor.ConnectionAccessor;
import sh.pancake.server.network.ServerNetworkManager;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin {

    @Shadow
    private MinecraftServer server;

    @Shadow
    private GameProfile gameProfile;

    @Final
    @Shadow
    private Connection connection;

    @Inject(
        method = "handleAcceptedLogin",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/server/network/ServerLoginPacketListenerImpl.server",
            shift = Shift.BEFORE
        )
    )
    public void handleAcceptedLoginPre(CallbackInfo info) {
        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null || server.getNetworkManager() == null) return;

        ServerNetworkManager networkManager = server.getNetworkManager();
        networkManager.markProfile(((ConnectionAccessor) connection).getChannel(), gameProfile);
    }

    @Redirect(
        method = "handleAcceptedLogin",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/players/PlayerList.canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;"
        )
    )
    public Component handleAcceptedLogin_canPlayerLogin(PlayerList list, SocketAddress addr, GameProfile profile) {
        Component kickMessage = list.canPlayerLogin(addr, profile);

        PancakeServer server = PancakeServerService.getService().getServer();
        if (server == null) return kickMessage;

        ServerLoginEvent event = new ServerLoginEvent(
            ((ConnectionAccessor) connection).getChannel(),
            profile,
            kickMessage != null ? ServerLoginEvent.State.DENY : ServerLoginEvent.State.ALLOW,
            kickMessage
        );

        server.dispatchEvent(event);

        if (event.isCancelled()) {
            return new TranslatableComponent("disconnect.disconnected");
        } else if (event.getState() == ServerLoginEvent.State.ALLOW) {
            return null;
        } else if (event.getState() == ServerLoginEvent.State.DENY && event.getKickMessage() == null) {
            return new TranslatableComponent("disconnect.disconnected");
        }

        return event.getKickMessage();
    }
}
