/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import sh.pancake.server.command.BrigadierUtil;
import sh.pancake.server.command.CommandResult;
import sh.pancake.server.command.PancakeCommandStack;

public class ExtensionUtil {

    public static void dispatchEvent(ExtensionStore<?> store, Object event) {
        var iterator = store.iterator();
        while (iterator.hasNext()) {
            iterator.next().getEventManager().dispatchEvent(event);
        }
    }

    public static CommandResult dispatchCommand(ExtensionStore<?> store, StringReader reader, PancakeCommandStack stack) throws CommandSyntaxException {
        var iterator = store.iterator();
        while (iterator.hasNext()) {
            CommandResult res = iterator.next().executeCommand(reader, stack);

            if (res.isExecuted()) return res;
        }

        return new CommandResult(false, 0);
    }

    public static void fillSuggestion(
        ExtensionStore<?> store,
        CommandNode<SharedSuggestionProvider> suggestion,
        PancakeCommandStack stack,
        Map<CommandNode<PancakeCommandStack>, CommandNode<SharedSuggestionProvider>> redirectMap
    ) {
        var iterator = store.iterator();
        while (iterator.hasNext()) {
            iterator.next().fillSuggestion(suggestion, stack);
        }
    }

    public static void fillPayloadChannels(ExtensionStore<?> store, Set<ResourceLocation> set) {
        var iterator = store.iterator();
        while (iterator.hasNext()) {
            set.addAll(iterator.next().getPayloadChannels());
        }
    }

    public static void processPayload(ExtensionStore<?> store, ResourceLocation identifier, Channel channel, ByteBuf buf) {
        var iterator = store.iterator();
        while (iterator.hasNext()) {
            iterator.next().processPayload(identifier, channel, buf);
        }
    }

    public static CompletableFuture<Suggestions> getCompletionSuggestions(ExtensionStore<?> store, StringReader reader, PancakeCommandStack stack) {
        List<CompletableFuture<Suggestions>> taskList = new ArrayList<>();

        var iterator = store.iterator();
        while (iterator.hasNext()) {
            var extension = iterator.next();

            taskList.add(extension.getCompletionSuggestions(new StringReader(reader), stack));
        }

        return BrigadierUtil.mergeSuggestionTasks(reader.getString(), taskList);
    }
}
