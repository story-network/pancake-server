/*
 * Created on Tue Aug 24 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.level.ServerPlayer;
import sh.pancake.server.impl.network.Chat;

public class PlayerJoinChatEvent extends PlayerEvent {

    private Chat joinChat;

    private final GameProfile oldProfile;

    public PlayerJoinChatEvent(ServerPlayer player, Chat joinChat, @Nullable GameProfile oldProfile) {
        super(player);

        this.joinChat = joinChat;

        this.oldProfile = oldProfile;
    }
    
    public Chat getJoinChat() {
        return joinChat;
    }

    public void setJoinChat(Chat joinChat) {
        this.joinChat = joinChat;
    }

    @Nullable
    public GameProfile getOldProfile() {
        return oldProfile;
    }
    
}
