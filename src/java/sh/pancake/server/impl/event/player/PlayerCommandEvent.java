/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.player;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class PlayerCommandEvent extends PlayerEvent {

    private CommandSourceStack source;

    private String command;

    public PlayerCommandEvent(ServerPlayer player, String command, CommandSourceStack source) {
        super(player);

        this.command = command;
        this.source = source;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public CommandSourceStack getSource() {
        return source;
    }

    public void setSource(CommandSourceStack source) {
        this.source = source;
    }
    
}
