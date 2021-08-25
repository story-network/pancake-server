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
        addSuggestion(suggestion, root, source, new HashMap<>());
    }

    public static <T> void addSuggestion(
        CommandNode<SharedSuggestionProvider> suggestion,
        CommandNode<T> root,
        T source,
        Map<CommandNode<T>, CommandNode<SharedSuggestionProvider>> redirectMap
    ) {
        redirectMap.put(root, suggestion);

        addSuggestionInner(suggestion, root, source, redirectMap);
    }

    private static <T> void addSuggestionInner(
        CommandNode<SharedSuggestionProvider> suggestion,
        CommandNode<T> parent,
        T source,
        Map<CommandNode<T>, CommandNode<SharedSuggestionProvider>> redirectMap
    ) {
        var iterator = parent.getChildren().iterator();

        while (iterator.hasNext()) {
            var node = iterator.next();

            if (node instanceof LiteralCommandNode) {
                LiteralCommandNode<T> literalNode = (LiteralCommandNode<T>) node;

                if (suggestion.getRelevantNodes(new StringReader(literalNode.getLiteral())).size() == 1) {
                    continue;
                }
            }

            if (node.canUse(source)) {
                CommandNode<SharedSuggestionProvider> child = toSuggestion(node, redirectMap);

                suggestion.addChild(child);

                if (!node.getChildren().isEmpty()) {
                    addSuggestionInner(child, node, source, redirectMap);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> CommandNode<SharedSuggestionProvider> toSuggestion(
        CommandNode<T> node,
        Map<CommandNode<T>, CommandNode<SharedSuggestionProvider>> redirectMap
    ) {
        if (redirectMap.containsKey(node)) {
            return redirectMap.get(node);
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

        if (node.getRedirect() != null) {
            CommandNode<SharedSuggestionProvider> redirect = redirectMap.get(node.getRedirect());
            if (redirect == null) {
                redirect = toSuggestion(node.getRedirect(), redirectMap);
            }

            arg.redirect(redirect);
        }

        CommandNode<SharedSuggestionProvider> child = arg.build();
        
        redirectMap.put(node, child);

        return child;
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
