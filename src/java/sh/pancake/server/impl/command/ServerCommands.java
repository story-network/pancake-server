/*
 * Created on Tue Aug 24 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.impl.command;

import java.util.Map;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.commands.SharedSuggestionProvider;
import sh.pancake.server.PancakeServer;
import sh.pancake.server.command.BrigadierUtil;
import sh.pancake.server.command.CommandAdvisor;
import sh.pancake.server.command.CommandExecutor;
import sh.pancake.server.command.CommandResult;
import sh.pancake.server.command.PancakeCommandDispatcher;
import sh.pancake.server.command.PancakeCommandStack;

public class ServerCommands implements CommandAdvisor, CommandExecutor {

    private final PancakeCommandDispatcher<PancakeCommandStack> dispatcher;

    public ServerCommands(String namespace, PancakeServer server) {
        this.dispatcher = new PancakeCommandDispatcher<>(namespace);

        StopCommand.register(server, dispatcher);
    }

    public PancakeCommandDispatcher<PancakeCommandStack> getDispatcher() {
        return dispatcher;
    }

    @Override
    public CommandResult executeCommand(StringReader reader, PancakeCommandStack stack) throws CommandSyntaxException {
        return BrigadierUtil.executeCommand(dispatcher, reader, stack);
    }

    @Override
    public void fillSuggestion(
        CommandNode<SharedSuggestionProvider> suggestion,
        PancakeCommandStack stack,
        Map<CommandNode<PancakeCommandStack>, CommandNode<SharedSuggestionProvider>> redirectMap
    ) {
        BrigadierUtil.addSuggestion(suggestion, dispatcher.getRoot(), stack, redirectMap);
    }
    
}
