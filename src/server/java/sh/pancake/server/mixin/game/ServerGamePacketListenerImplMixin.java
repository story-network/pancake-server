/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin.game;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import sh.pancake.launcher.PancakeLauncher;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.impl.event.player.PlayerChatEvent;
import sh.pancake.server.impl.event.player.PlayerVehicleInputEvent;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Shadow
    private MinecraftServer server;
    
    @Redirect(method = "handlePlayerInput", at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.setPlayerInput(FFZZ)V"))
    public void onPlayerInput(ServerPlayer player, float xxa, float zza, boolean jumping, boolean sneaking) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PlayerVehicleInputEvent event = new PlayerVehicleInputEvent(player, xxa, zza, jumping, sneaking);

        pancakeServer.getEventManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        player.setPlayerInput(event.getXxa(), event.getZza(), event.isJumping(), event.isSneaking());
    }

    @Redirect(method = "handleChat", at = @At(value = "INVOKE", target = "net/minecraft/server/players/PlayerList.broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"))
    public void onPlayerChat(PlayerList playerList, Component component, ChatType type, UUID uuid) {
        PancakeServer pancakeServer = (PancakeServer) PancakeLauncher.getLauncher().getServer();

        PlayerChatEvent event = new PlayerChatEvent(player, component, type, uuid);

        pancakeServer.getEventManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        server.getPlayerList().broadcastMessage(event.getComponent(), event.getChatType(), event.getSenderUUID());
    }

}
