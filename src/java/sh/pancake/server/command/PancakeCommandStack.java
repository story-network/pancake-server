/*
 * Created on Mon Aug 16 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.function.Function;

import net.minecraft.commands.CommandSourceStack;
import sh.pancake.server.PancakeServer;

/**
 * Wrapping minecraft CommandSourceStack and provides additional datas.
 */
public class PancakeCommandStack {

    private final PancakeServer server;

    private final CommandSourceStack innerStack;
    
    public PancakeCommandStack(PancakeServer server, CommandSourceStack innerStack) {
        this.server = server;
        this.innerStack = innerStack;
    }

    /**
     * Compute inner CommandSourceStack
     *
     * @param function
     * @return New PancakeCommandStack Object with computed CommandSourceStack
     */
    public PancakeCommandStack compute(Function<CommandSourceStack, CommandSourceStack> function) {
        return new PancakeCommandStack(server, function.apply(innerStack));
    }

    public PancakeServer getServer() {
        return server;
    }

    public CommandSourceStack getInnerStack() {
        return innerStack;
    }
}
