/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import sh.pancake.common.util.AsyncTask;
import sh.pancake.server.IPancakeExtra;

public class CommandManager {

    private static final Logger LOGGER = LogManager.getLogger("CommandManager");

    // Separate by IPancakeExtra so commands can unload when they are unloaded
    private WeakHashMap<IPancakeExtra, CommandDispatcher<CommandSourceStack>> extraMap;

    private CommandDispatcher<CommandSourceStack> serverDispatcher;

    public CommandManager() {
        this.extraMap = new WeakHashMap<>();

        this.serverDispatcher = new CommandDispatcher<>();
    }

    public CommandDispatcher<CommandSourceStack> getServerDispatcher() {
        return serverDispatcher;
    }

    public CommandDispatcher<CommandSourceStack> getDispatcherFor(IPancakeExtra extra) {
        return extraMap.computeIfAbsent(extra, (ex) -> new CommandDispatcher<CommandSourceStack>());
    }

    public void onMCCommandInit(CommandDispatcher<CommandSourceStack> mcDispatcher) {
        LOGGER.info("Initializing commands for " + mcDispatcher);
    }

    public List<CommandDispatcher<CommandSourceStack>> getDispatcherList() {
        List<CommandDispatcher<CommandSourceStack>> list = new ArrayList<>();

        list.add(serverDispatcher);

        list.addAll(extraMap.values());

        return list;
    }

    public int performCommand(StringReader reader, CommandSourceStack stack) throws CommandSyntaxException, CommandRuntimeException {
        int executionCount = 0;

        Iterator<CommandDispatcher<CommandSourceStack>> dispatcherIter = getDispatcherList().iterator();

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

    public AsyncTask<Suggestions[]> createSuggestionsListAsync(CommandSourceStack stack, StringReader reader) {
        List<CommandDispatcher<CommandSourceStack>> dispatcherList = getDispatcherList();
        Iterator<CommandDispatcher<CommandSourceStack>> iter = dispatcherList.iterator();

        List<AsyncTask<Suggestions>> taskList = new ArrayList<>(dispatcherList.size());
        while (iter.hasNext()) {
            CommandDispatcher<CommandSourceStack> dispatcher = iter.next();

            ParseResults<CommandSourceStack> res = dispatcher.parse(reader, stack);

            taskList.add(new AsyncTask<Suggestions>(dispatcher.getCompletionSuggestions(res)::join));
        }

        return (AsyncTask<Suggestions[]>) AsyncTask.all(taskList.toArray(new AsyncTask[0]));

    }

}
