/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import java.util.UUID;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PlayerChatEvent extends PlayerEvent {

    private Component component;
    private ChatType chatType;
    private UUID senderUUID;

    public PlayerChatEvent(ServerPlayer player, Component component, ChatType chatType, UUID senderUUID) {
        super(player);
        this.component = component;
        this.chatType = chatType;
        this.senderUUID = senderUUID;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public ChatType getChatType() {
        return chatType;
    }
    
    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public UUID getSenderUUID() {
        return senderUUID;
    }

    public void setSenderUUID(UUID senderUUID) {
        this.senderUUID = senderUUID;
    }
    
}
