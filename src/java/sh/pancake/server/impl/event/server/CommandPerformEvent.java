/*
 * Created on Thu Oct 08 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.event.server;

import net.minecraft.commands.CommandSourceStack;
import sh.pancake.server.impl.event.CancellableEvent;

public class CommandPerformEvent extends CancellableEvent {

    private CommandSourceStack source;

    private String command;

    public CommandPerformEvent(String command, CommandSourceStack source) {
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
