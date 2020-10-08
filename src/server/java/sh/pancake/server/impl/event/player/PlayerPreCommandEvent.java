/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.server.level.ServerPlayer;

public class PlayerPreCommandEvent extends PlayerEvent {

    // Will starts with '/' in most case
    private String command;

    public PlayerPreCommandEvent(ServerPlayer player, String command) {
        super(player);

        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
    
}
