/*
 * Created on Wed Aug 25 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.network;

import java.util.UUID;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

public class Chat {
    
    private Component component;
    private ChatType type;
    private UUID uuid;

    public Chat(Component component, ChatType type, UUID uuid) {
        this.component = component;
        this.type = type;
        this.uuid = uuid;
    }

    public Component getComponent() {
        return component;
    }

    public ChatType getType() {
        return type;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }


}
