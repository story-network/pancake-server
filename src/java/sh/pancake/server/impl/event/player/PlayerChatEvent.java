/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;
import sh.pancake.server.impl.network.Chat;

public class PlayerChatEvent extends ServerPlayerEventImpl {

    private final String message;
    private Chat chat;

    public PlayerChatEvent(ServerPlayer player, String message, Chat chat) {
        super(player);

        this.message = message;
        this.chat = chat;
    }
    
    public String getMessage() {
        return message;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
    
}
