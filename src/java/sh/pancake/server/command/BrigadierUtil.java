/*
 * Created on Thu Aug 19 2021
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.HashMap;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import sh.pancake.server.mixin.accessor.CommandNodeAccessor;

public class BrigadierUtil {
    
    @SuppressWarnings("unchecked")
    public static <S> void unregisterCommand(CommandNode<S> node, String literal) {
        CommandNodeAccessor<S> nodeAccessor = (CommandNodeAccessor<S>) node;

        nodeAccessor.getChildren().remove(literal);
        nodeAccessor.getLiterals().remove(literal);
        nodeAccessor.getArguments().remove(literal);
    }

    public static <T> void addSuggestion(
        CommandNode<SharedSuggestionProvider> suggestion,
        CommandNode<T> root,
        T source
    ) {
        Map<CommandNode<T>, CommandNode<SharedSuggestionProvider>> redirectMap = new HashMap<>();

        addSuggestion(suggestion, root, source, redirectMap);
    }

    @SuppressWarnings("unchecked")
    public static <T> void addSuggestion(
        CommandNode<SharedSuggestionProvider> suggestion,
        CommandNode<T> root,
        T source,
        Map<CommandNode<T>, CommandNode<SharedSuggestionProvider>> redirectMap
    ) {
        redirectMap.put(root, suggestion);

        var iterator = root.getChildren().iterator();

        while (iterator.hasNext()) {
            var node = iterator.next();

            if (node.canUse(source)) {
                if (node instanceof LiteralCommandNode) {
                    LiteralCommandNode<T> literal = (LiteralCommandNode<T>) node;
                    if (!suggestion.getRelevantNodes(new StringReader(literal.getLiteral())).isEmpty()) {
                        continue;
                    }
                }

                ArgumentBuilder<SharedSuggestionProvider, ?> arg = (ArgumentBuilder<SharedSuggestionProvider, ?>) node.createBuilder();

                // Invalidate copied requirement
                arg.requires((stack) -> true);
                if (arg.getCommand() != null) {
                    arg.executes((ctx) -> 0);
                }

                if (arg instanceof RequiredArgumentBuilder) {
                    RequiredArgumentBuilder<SharedSuggestionProvider, ?> requiredArg = (RequiredArgumentBuilder<SharedSuggestionProvider, ?>) arg;
                    if (requiredArg.getSuggestionsProvider() != null) {
                        requiredArg.suggests(SuggestionProviders.safelySwap(requiredArg.getSuggestionsProvider()));
                    }
                }

                if (arg.getRedirect() != null) {
                    arg.redirect(redirectMap.get(arg.getRedirect()));
                }

                CommandNode<SharedSuggestionProvider> child = arg.build();

                redirectMap.put(node, child);

                suggestion.addChild(child);

                if (!node.getChildren().isEmpty()) {
                    addSuggestion(child, node, source, redirectMap);
                }
            }
        }
    }

    public static <T> CommandResult executeCommand(CommandDispatcher<T> dispatcher, StringReader reader, T stack) throws CommandSyntaxException {
        int lastCursor = reader.getCursor();

        ParseResults<T> parsed = dispatcher.parse(reader, stack);
        if (!parsed.getContext().getRange().isEmpty()) {
            return new CommandResult(true, dispatcher.execute(parsed));
        }

        reader.setCursor(lastCursor);

        return new CommandResult(false, 0);
    }

}
