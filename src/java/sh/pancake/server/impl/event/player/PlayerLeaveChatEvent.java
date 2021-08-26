/*
 * Created on Mon Aug 23 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;
import sh.pancake.server.impl.network.Chat;

public class PlayerLeaveChatEvent extends ServerPlayerEventImpl {

    private Chat leaveChat;

    public PlayerLeaveChatEvent(ServerPlayer player, Chat leaveChat) {
        super(player);

        this.leaveChat = leaveChat;
    }
    
    public Chat getLeaveChat() {
        return leaveChat;
    }

    public void setLeaveChat(Chat leaveChat) {
        this.leaveChat = leaveChat;
    }
    
}
