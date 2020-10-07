/*
 * Created on Wed Oct 07 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import sh.pancake.common.util.AsyncTask;
import sh.pancake.server.PancakeServer;

public class NestedCommandDispatcher extends CommandDispatcher<CommandSourceStack> {

    private PancakeServer pancakeServer;
    private IDispatcherSupplier<ICommandStack> dispatcherSupplier;

    public NestedCommandDispatcher(RootCommandNode<CommandSourceStack> root, PancakeServer pancakeServer, IDispatcherSupplier<ICommandStack> dispatcherSupplier) {
        super(root);
        
        this.pancakeServer = pancakeServer;
        this.dispatcherSupplier = dispatcherSupplier;
    }

    public NestedCommandDispatcher(PancakeServer pancakeServer, IDispatcherSupplier<ICommandStack> dispatcherSupplier) {
        this(new RootCommandNode<CommandSourceStack>(), pancakeServer, dispatcherSupplier);
    }

    public PancakeServer getPancakeServer() {
        return pancakeServer;
    }

    public IDispatcherSupplier<ICommandStack> getDispatcherSupplier() {
        return dispatcherSupplier;
    }

    public ICommandStack createFrom(CommandSourceStack sourceStack) {
        return new CommandStack(pancakeServer, sourceStack);
    }

    @Override
    public int execute(final ParseResults<CommandSourceStack> parse) throws CommandSyntaxException {
        ICommandStack stack = createFrom(parse.getContext().getSource());

        Iterator<CommandDispatcher<ICommandStack>> iter = dispatcherSupplier.getIterator();

        StringReader extraReader = new StringReader(parse.getReader().getString());

        while(iter.hasNext()) {
            CommandDispatcher<ICommandStack> dispatcher = iter.next();

            ParseResults<ICommandStack> parseRes = dispatcher.parse(extraReader, stack);
            if (parseRes.getReader().canRead() && parseRes.getContext().getRange().isEmpty()) continue;

            try {
                return dispatcher.execute(parseRes);
            } catch (CommandSyntaxException e) {
                // Skip only if command does not exists
                if (e.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()) {
                    continue;
                }

                throw e;
            }
        }

        return super.execute(parse);
    }

    @Override
    public CompletableFuture<Suggestions> getCompletionSuggestions(final ParseResults<CommandSourceStack> parse, int cursor) {
        ICommandStack stack = new CommandStack(pancakeServer, parse.getContext().getSource());

        String command = parse.getReader().getString();

        StringReader extraReader = new StringReader(parse.getReader().getString());

        AsyncTask<Suggestions[]> suggestionsTask = createExtraSuggestionsListAsync(stack, extraReader);

        List<Suggestions> list = new ArrayList<>();
        try {
            list.addAll(Arrays.asList(suggestionsTask.join()));
        } catch(Throwable throwable) {
            // This never happens and should not happen
        }

        return super.getCompletionSuggestions(parse, cursor).thenApply((mcSuggestions) -> {
            list.add(mcSuggestions);

            return Suggestions.merge(command, list);
        });
    }

    public AsyncTask<Suggestions[]> createExtraSuggestionsListAsync(ICommandStack stack, StringReader reader) {
        Iterator<CommandDispatcher<ICommandStack>> iter = getDispatcherSupplier().getIterator();

        List<AsyncTask<Suggestions>> taskList = new ArrayList<>();
        while (iter.hasNext()) {
            CommandDispatcher<ICommandStack> dispatcher = iter.next();

            ParseResults<ICommandStack> res = dispatcher.parse(reader, stack);

            taskList.add(new AsyncTask<Suggestions>(dispatcher.getCompletionSuggestions(res)::join));
        }
        
        return AsyncTask.all(taskList.toArray(new AsyncTask[0]));
    }

    public void fillUsableCommandList(CommandNode<SharedSuggestionProvider> suggestion, ICommandStack stack, Map<CommandNode<?>, CommandNode<SharedSuggestionProvider>> redirectMap) {
        CommandSourceStack source = stack.getSourceStack();

        Iterator<CommandDispatcher<ICommandStack>> iter = dispatcherSupplier.getIterator();

        while (iter.hasNext()) {
            CommandDispatcher<ICommandStack> dispatcher = iter.next();

            fillUsableCommand(dispatcher.getRoot(), suggestion, stack, redirectMap);
        }

        fillUsableCommand(getRoot(), suggestion, source, redirectMap);
    }

    private <T extends SharedSuggestionProvider>void fillUsableCommand(CommandNode<T> root, CommandNode<SharedSuggestionProvider> suggestion, T stack, Map<CommandNode<?>, CommandNode<SharedSuggestionProvider>> redirectMap) {
        Iterator<CommandNode<T>> iter = root.getChildren().iterator();

        while (iter.hasNext()) {
            CommandNode<T> node = iter.next();

            if (!node.canUse(stack))
                continue;

            ArgumentBuilder<SharedSuggestionProvider, ?> builder = (ArgumentBuilder<SharedSuggestionProvider, ?>) (ArgumentBuilder<?, ?>) node.createBuilder();
            builder.requires((s) -> true);
            if (builder.getCommand() != null)
                builder.executes((s) -> 0);

            if (builder instanceof RequiredArgumentBuilder) {
                RequiredArgumentBuilder<SharedSuggestionProvider, ?> requiredBuilder = (RequiredArgumentBuilder<SharedSuggestionProvider, ?>) builder;
                if (requiredBuilder.getSuggestionsProvider() != null) {
                    requiredBuilder.suggests(SuggestionProviders.safelySwap((SuggestionProvider<SharedSuggestionProvider>) requiredBuilder.getSuggestionsProvider()));
                }
            }

            if (builder.getRedirect() != null) {
                builder.redirect(redirectMap.get(builder.getRedirect()));
            }

            CommandNode<SharedSuggestionProvider> suggestionNode = builder.build();
            redirectMap.put(node, suggestionNode);
            suggestion.addChild(suggestionNode);
            if (!node.getChildren().isEmpty())
                fillUsableCommand(node, suggestionNode, stack, redirectMap);
        }
    }

}
