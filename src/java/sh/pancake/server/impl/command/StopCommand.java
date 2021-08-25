/*
 * Created on Tue Aug 24 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.command;

import net.minecraft.network.chat.TranslatableComponent;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.command.PancakeCommandDispatcher;
import sh.pancake.server.command.PancakeCommandStack;

public class StopCommand {
    
    public static void register(PancakeServer server, PancakeCommandDispatcher<PancakeCommandStack> dispatcher) {
        dispatcher.register(
            PancakeCommandStack.literal("stop")
            .requires((stack) -> stack.getInnerStack().hasPermission(4))
            .executes((ctx) -> {
                ctx.getSource().getInnerStack().sendSuccess(new TranslatableComponent("commands.stop.stopping"), true);

                server.getService().stopServer();
                return 1;
            })
        );
    }

}