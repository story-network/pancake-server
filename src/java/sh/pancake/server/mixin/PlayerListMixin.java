/*
 * Created on Wed Aug 25 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.mixin;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.PancakeServerService;
import sh.pancake.server.impl.event.player.PlayerJoinChatEvent;
import sh.pancake.server.impl.network.Chat;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Redirect(
        method = "placeNewPlayer",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/players/PlayerList.broadcastMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/ChatType;Ljava/util/UUID;)V"
        )
    )
    public void placeNewPlayer_broadcastMessage(
        PlayerList list,
        Component component,
        ChatType type,
        UUID uuid,
        Connection connection,
        ServerPlayer player
    ) {
        PancakeServer pancakeServer = PancakeServerService.getService().getServer();
        if (pancakeServer == null) {
            list.broadcastMessage(component, type, uuid);
            return;
        }

        GameProfile oldProfile = server.getProfileCache().get(player.getGameProfile().getId()).orElse(null);

        PlayerJoinChatEvent event = new PlayerJoinChatEvent(player, new Chat(component, type, uuid), oldProfile);
        pancakeServer.dispatchEvent(event);

        if (event.isCancelled()) return;

        Chat chat = event.getJoinChat();
        list.broadcastMessage(chat.getComponent(), chat.getType(), chat.getUUID());
    }   
}
