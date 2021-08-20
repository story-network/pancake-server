/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.commands.SharedSuggestionProvider;
import sh.pancake.server.command.CommandResult;
import sh.pancake.server.command.PancakeCommandStack;
import sh.pancake.server.extension.ExtensionStore;

public class ExtensionUtil {

    public static void dispatchEvent(ExtensionStore<?> store, Object event) {
        var iterator = store.iterator();
        while (iterator.hasNext()) {
            iterator.next().getEventManager().dispatchEvent(event);
        }
    }

    public static CommandResult dispatchCommand(ExtensionStore<?> store, StringReader reader, PancakeCommandStack stack) throws CommandSyntaxException {
        int lastCursor = reader.getCursor();

        var iterator = store.iterator();
        while (iterator.hasNext()) {
            CommandDispatcher<PancakeCommandStack> dispatcher = iterator.next().getCommandDispatcher();

            ParseResults<PancakeCommandStack> result = dispatcher.parse(reader, stack);

            if (!result.getContext().getRange().isEmpty()) {
                return new CommandResult(true, dispatcher.execute(result));
            }

            reader.setCursor(lastCursor);
        }

        return new CommandResult(false, 0);
    }

    public static void fillSuggestion(ExtensionStore<?> store, CommandNode<SharedSuggestionProvider> suggestion, PancakeCommandStack stack) {
        var iterator = store.iterator();
        while (iterator.hasNext()) {
            BrigadierUtil.addSuggestion(suggestion, iterator.next().getCommandDispatcher().getRoot(), stack);
        }
    }
}
