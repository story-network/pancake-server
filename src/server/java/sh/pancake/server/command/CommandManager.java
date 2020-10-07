/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.server.level.ServerPlayer;
import sh.pancake.common.util.AsyncTask;

public class CommandManager<T> {

    private static final Logger LOGGER = LogManager.getLogger("CommandManager");

    // Separate by T so commands can unload when they are unloaded
    private WeakHashMap<T, CommandDispatcher<ICommandStack>> extraMap;

    private CommandDispatcher<ICommandStack> serverDispatcher;

    public CommandManager() {
        this.extraMap = new WeakHashMap<>();

        this.serverDispatcher = new CommandDispatcher<>();
    }

    public CommandDispatcher<ICommandStack> getServerDispatcher() {
        return serverDispatcher;
    }

    public CommandDispatcher<ICommandStack> getDispatcherFor(T extra) {
        return extraMap.computeIfAbsent(extra, (ex) -> new CommandDispatcher<ICommandStack>());
    }

    public void onMCCommandInit(CommandDispatcher<CommandSourceStack> mcDispatcher) {
        LOGGER.info("Initializing commands for " + mcDispatcher);
    }

    public List<CommandDispatcher<ICommandStack>> getDispatcherList() {
        List<CommandDispatcher<ICommandStack>> list = new ArrayList<>();

        list.add(serverDispatcher);

        list.addAll(extraMap.values());

        return list;
    }

    public int performCommand(StringReader reader, ICommandStack stack) throws CommandSyntaxException, CommandRuntimeException {
        int executionCount = 0;

        Iterator<CommandDispatcher<ICommandStack>> dispatcherIter = getDispatcherList().iterator();

        CommandSyntaxException commandEx = null;
        CommandRuntimeException commandRuntimeEx = null;

        boolean hasCommand = false;

        while (dispatcherIter.hasNext() && executionCount < 1) {
            try {
                executionCount = dispatcherIter.next().execute(reader, stack);
                hasCommand = true;
            } catch (CommandRuntimeException runtimeEx) {
                commandRuntimeEx = runtimeEx;
            } catch (CommandSyntaxException syntaxEx) {
                commandEx = syntaxEx;
            }
        }

        if (!hasCommand && commandEx != null) throw commandEx;
        if (!hasCommand && commandRuntimeEx != null) throw commandRuntimeEx;

        return executionCount;
    }

    public AsyncTask<Suggestions[]> createSuggestionsListAsync(ICommandStack stack, StringReader reader) {
        List<CommandDispatcher<ICommandStack>> dispatcherList = getDispatcherList();
        Iterator<CommandDispatcher<ICommandStack>> iter = dispatcherList.iterator();

        List<AsyncTask<Suggestions>> taskList = new ArrayList<>(dispatcherList.size());
        while (iter.hasNext()) {
            CommandDispatcher<ICommandStack> dispatcher = iter.next();

            ParseResults<ICommandStack> res = dispatcher.parse(reader, stack);

            taskList.add(new AsyncTask<Suggestions>(dispatcher.getCompletionSuggestions(res)::join));
        }

        return AsyncTask.all(taskList.toArray(new AsyncTask[0]));
    }

    public void fillUsableCommandList(CommandNode<SharedSuggestionProvider> suggestion, ICommandStack stack, Map<CommandNode<ICommandStack>, CommandNode<SharedSuggestionProvider>> redirectMap) {
        List<CommandDispatcher<ICommandStack>> list = getDispatcherList();
        Iterator<CommandDispatcher<ICommandStack>> iter = list.iterator();

        while (iter.hasNext()) {
            CommandDispatcher<ICommandStack> dispatcher = iter.next();
            CommandNode<ICommandStack> node = dispatcher.getRoot();

            redirectMap.put(node, suggestion);

            fillUsableCommand(node, suggestion, stack, redirectMap);
        }
    }

    public void fillUsableCommand(CommandNode<ICommandStack> root, CommandNode<SharedSuggestionProvider> suggestion, ICommandStack stack, Map<CommandNode<ICommandStack>, CommandNode<SharedSuggestionProvider>> redirectMap) {
        Iterator<CommandNode<ICommandStack>> iter = root.getChildren().iterator();

        while (iter.hasNext()) {
            CommandNode<ICommandStack> node = iter.next();

            if (!node.canUse(stack)) continue;

            ArgumentBuilder<SharedSuggestionProvider, ?> builder = (ArgumentBuilder<SharedSuggestionProvider, ?>) (ArgumentBuilder<?, ?>) node.createBuilder();
            builder.requires((s) -> true);
            if (builder.getCommand() != null) builder.executes((s) -> 0);

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
             if (!node.getChildren().isEmpty()) fillUsableCommand(node, suggestionNode, stack, redirectMap);
        }
    }

	public void sendAllCommandList(ServerPlayer player) {
        player.getServer().getCommands().sendCommands(player);
	}

}
