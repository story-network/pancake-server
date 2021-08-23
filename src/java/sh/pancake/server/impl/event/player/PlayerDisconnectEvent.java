/*
 * Created on Mon Aug 23 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import java.util.UUID;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PlayerDisconnectEvent extends PlayerEvent {

    private Component leaveText;
    private ChatType type;
    private UUID broadcastUUID;

    public PlayerDisconnectEvent(ServerPlayer player, Component leaveText, ChatType type, UUID broadcastUUID) {
        super(player);

        this.leaveText = leaveText;
        this.type = type;
        this.broadcastUUID = broadcastUUID;
    }

    public Component getLeaveText() {
        return leaveText;
    }

    public ChatType getType() {
        return type;
    }

    public UUID getBroadcastUUID() {
        return broadcastUUID;
    }

    public void setLeaveText(Component leaveText) {
        this.leaveText = leaveText;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public void setBroadcastUUID(UUID broadcastUUID) {
        this.broadcastUUID = broadcastUUID;
    }
    
}
