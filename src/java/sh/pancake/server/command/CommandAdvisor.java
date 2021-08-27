/*
 * Created on Fri Aug 20 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.commands.SharedSuggestionProvider;

public interface CommandAdvisor {

    default CompletableFuture<Suggestions> getCompletionSuggestions(String command, PancakeCommandStack stack) {
        return getCompletionSuggestions(new StringReader(command), stack);
    }

    CompletableFuture<Suggestions> getCompletionSuggestions(StringReader reader, PancakeCommandStack stack);

    default void fillSuggestion(CommandNode<SharedSuggestionProvider> suggestion, PancakeCommandStack stack) {
        Map<CommandNode<PancakeCommandStack>, CommandNode<SharedSuggestionProvider>> map = new HashMap<>();

        fillSuggestion(suggestion, stack, map);
    }

    void fillSuggestion(
        CommandNode<SharedSuggestionProvider> suggestion,
        PancakeCommandStack stack,
        Map<CommandNode<PancakeCommandStack>, CommandNode<SharedSuggestionProvider>> redirectMap
    );

}
