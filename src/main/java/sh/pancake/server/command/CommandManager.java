/*
 * Created on Sun Oct 04 2020
 *
 * Copyright (c) storycraft. Licensed under the Apache Licence 2.0.
 */

package sh.pancake.server.command;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import com.mojang.brigadier.CommandDispatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.commands.CommandSourceStack;
import sh.pancake.server.IPancakeExtra;

public class CommandManager {

    private static final Logger LOGGER = LogManager.getLogger("CommandManager");

    // Separate by IPancakeExtra so commands can unload when they are unloaded
    private WeakHashMap<IPancakeExtra, CommandDispatcher<CommandSourceStack>> extraMap;

    public CommandManager() {
        this.extraMap = new WeakHashMap<>();
    }

    public CommandDispatcher<CommandSourceStack> getDispatcherFor(IPancakeExtra extra) {
        return extraMap.computeIfAbsent(extra, (ex) -> new CommandDispatcher<CommandSourceStack>());
    }

    public void onMCCommandInit(CommandDispatcher<CommandSourceStack> serverDispatcher) {
        LOGGER.info("Initializing commands for " + serverDispatcher);
    }

    public List<CommandDispatcher<CommandSourceStack>> getDispatcherList() {
        List<CommandDispatcher<CommandSourceStack>> list = new ArrayList<>();

        list.addAll(extraMap.values());

        return list;
    }

}
